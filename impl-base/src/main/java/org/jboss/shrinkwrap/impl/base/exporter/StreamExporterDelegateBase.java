/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.io.StreamErrorHandler;
import org.jboss.shrinkwrap.impl.base.io.StreamTask;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.jboss.shrinkwrap.spi.Configurable;

/**
 * Base for exporters capable of writing to some implementation of {@link OutputStream}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public abstract class StreamExporterDelegateBase<O extends OutputStream> extends AbstractExporterDelegate<InputStream> {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(StreamExporterDelegateBase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@link OutputStream} used to write the individual entries
     */
    protected O outputStream;

    /**
     * {@link InputStream} to be returned to the caller
     */
    private InputStream inputStream;

    /**
     * Used to see if we have exported at least one node
     */
    private Set<ArchivePath> pathsExported = new HashSet<ArchivePath>();

    /**
     * Synchronization point where the encoding process will wait until all streams have been set up
     */
    protected final CountDownLatch latch = new CountDownLatch(1);

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new exporter delegate for exporting archives
     */
    public StreamExporterDelegateBase(final Archive<?> archive) throws IllegalArgumentException {
        // Delegate to super
        super(archive);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Returns the task used to run the export operation in another Thread. Exposed such that the specified task (which
     * handles the export) may be wrapped in some error handling logic specific to the export process.
     *
     * @param wrappedTask
     *            The export task to be wrapped in more specific handling logic
     */
    protected abstract Callable<Void> getExportTask(Callable<Void> wrappedTask);

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#doExport()
     */
    protected void doExport() {
        // Define the task to operate in another Thread so we can pipe the output to an InStream
        final Callable<Void> exportTask = this.getExportTask(new Callable<Void>() {

            // Wrapped task is the super implementation
            @Override
            public Void call() throws Exception {
                StreamExporterDelegateBase.super.doExport();
                return null;
            }

        });

        // Get an ExecutorService to which we may submit jobs. This is either supplied by the user
        // in a custom domain, or if one has not been specified, we'll make one and shut it down right
        // here. ExecutorServices supplied by the user are under the user's lifecycle, therefore it's
        // user responsibility to shut it down appropriately.
        boolean executorServiceIsOurs = false;
        ExecutorService service = this.getArchive().as(Configurable.class).getConfiguration().getExecutorService();
        if (service == null) {
            service = Executors.newSingleThreadExecutor();
            executorServiceIsOurs = true;
        }

        // Get a handle and return it to the caller
        final Future<Void> job = service.submit(exportTask);

        // If we've created the ES
        if (executorServiceIsOurs) {
            // Tell the service to shut down after the job has completed, and accept no new jobs
            service.shutdown();
        }

        /*
         * At this point the job will start, but hit the latch until we set up the streams and tell it to proceed.
         */

        // Stream to return to the caller
        final FutureCompletionInputStream input = new FutureCompletionInputStream(job);
        inputStream = input;

        /**
         * OutputStream which will be associated with the returned InStream, and the chained IO point for the final
         * OutStream
         */
        final OutputStream output;
        try {
            output = new PipedOutputStream(input);
        } catch (final IOException e) {
            throw new RuntimeException("Error in setting up output stream", e);
        }

        // Set up the stream to which we'll write entries, backed by the piped stream
        try {
            outputStream = StreamExporterDelegateBase.this.createOutputStream(output);
        } catch (final IOException e) {
            throw new ArchiveExportException("Could not create the underlying stream to export: "
                + this.getArchive().toString(), e);
        }

        /*
         * The job is now waiting on us to signal that we've set up the streams; let it continue
         */
        latch.countDown();
    }

    /**
     * Writes the next entry (demarcates a new file/folder is to be written)
     *
     * @param outputStream
     * @param context
     * @throws IOException
     *             If an error occurred writing the entry
     */
    protected abstract void putNextExtry(O outputStream, String context) throws IOException;

    /**
     * Closes the current entry context for the specified {@link OutputStream}
     *
     * @param outputStream
     */
    protected abstract void closeEntry(O outputStream) throws IOException;

    /**
     * Creates the real {@link OutputStream} to which we'll write, wrapping the provided target.
     *
     * @param out
     * @return
     * @throws IOException
     *             If an error occurred in creating the stream
     */
    protected abstract O createOutputStream(OutputStream out) throws IOException;

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#processNode(ArchivePath, Node)
     */
    @Override
    protected void processNode(final ArchivePath path, final Node node) {
        // Precondition checks
        if (path == null) {
            throw new IllegalArgumentException("Path must be specified");
        }
        if (node == null) {
            throw new IllegalArgumentException("asset must be specified");
        }

        // Mark if we're writing a directory
        final boolean isDirectory = node.getAsset() == null;

        InputStream stream = null;
        if (!isDirectory) {
            stream = node.getAsset().openStream();
        }

        final String pathName = PathUtil.optionallyRemovePrecedingSlash(path.get());

        // Make a task for this stream and close when done
        IOUtil.closeOnComplete(stream, new StreamTask<InputStream>() {

            @Override
            public void execute(final InputStream stream) throws Exception {
                String resolvedPath = pathName;
                if (isDirectory) {
                    resolvedPath = PathUtil.optionallyAppendSlash(resolvedPath);
                }

                /*
                 * Wait until all streams have been set up for encoding, or do nothing if everything's set up already
                 */
                latch.await();

                // Write the Asset under the same Path name in the output
                try {
                    putNextExtry(outputStream, resolvedPath);
                } catch (final IOException ze) {
                    log.log(Level.SEVERE, pathsExported.toString());
                    throw new RuntimeException(ze);
                }

                // Mark that we've written this Path
                pathsExported.add(path);

                // Read the contents of the asset and write to the JAR,
                // if we're not just a directory
                if (!isDirectory) {
                    IOUtil.copy(stream, outputStream);
                }

                // Close up the instream and the entry
                StreamExporterDelegateBase.this.closeEntry(outputStream);
            }

        }, new StreamErrorHandler() {

            @Override
            public void handle(Throwable t) {
                throw new ArchiveExportException("Failed to write asset to output: " + path.get(), t);
            }

        });
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#getResult()
     */
    @Override
    protected InputStream getResult() {
        return inputStream;
    }

    /**
     * Returns an immutable view of all {@link ArchivePath}s currently exported
     *
     * @return
     */
    protected final Set<ArchivePath> getExportedPaths() {
        return Collections.unmodifiableSet(this.pathsExported);
    }

}

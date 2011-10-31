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
package org.jboss.shrinkwrap.impl.base.exporter.zip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.impl.base.exporter.StreamExporterDelegateBase;

/**
 * JDK-based implementation of a ZIP exporter. Cannot handle archives with no content (as there'd be no {@link ZipEntry}
 * s to write to the {@link ZipOutputStream}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class JdkZipExporterDelegate extends StreamExporterDelegateBase<ZipOutputStream> {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(JdkZipExporterDelegate.class.getName());

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new exporter delegate for exporting archives as ZIP
     *
     * @throws IllegalArgumentException
     *             If the archive has no {@link Asset}s; JDK ZIP handling cannot support writing out to a
     *             {@link ZipOutputStream} with no {@link ZipEntry}s.
     */
    public JdkZipExporterDelegate(final Archive<?> archive) throws IllegalArgumentException {
        super(archive);

        // Precondition check
        if (archive.getContent().isEmpty()) {
            throw new IllegalArgumentException(
                "[SHRINKWRAP-93] Cannot use this JDK-based implementation to export as ZIP an archive with no content: "
                    + archive.toString());
        }
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterDelegateBase#closeEntry(java.io.OutputStream)
     */
    @Override
    protected final void closeEntry(final ZipOutputStream outputStream) throws IOException {
        // Close the entry
        outputStream.closeEntry();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterDelegateBase#createOutputStream(java.io.OutputStream)
     */
    @Override
    protected final ZipOutputStream createOutputStream(final OutputStream out) throws IOException {
        // Create and return
        return new ZipOutputStream(out);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterDelegateBase#putNextExtry(java.io.OutputStream,
     *      java.lang.String)
     */
    @Override
    protected final void putNextExtry(final ZipOutputStream outputStream, final String context) throws IOException {
        // Put
        outputStream.putNextEntry(new ZipEntry(context));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterDelegateBase#getExportTask()
     */
    @Override
    protected Callable<Void> getExportTask(final Callable<Void> wrappedTask) {
        assert wrappedTask != null : "Wrapped task must be specified";
        return new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                try {
                    // Attempt the wrapped task
                    wrappedTask.call();
                } catch (final Exception e) {

                    // Log this and rethrow; otherwise if we go into deadlock we won't ever
                    // be able to get the underlying cause from the Future
                    log.log(Level.WARNING, "Exception encountered during export of archive", e);

                    // SHRINKWRAP-133 - if the output is empty, it won't close and a deadlock is triggered
                    final Set<ArchivePath> pathsExported = JdkZipExporterDelegate.this.getExportedPaths();
                    if (pathsExported.isEmpty()) {
                        // Ensure the streams are set up before we do any work on them;
                        // it's possible that we encountered an exception before
                        // everything has been initialized by the main Thread
                        // SHRINKWRAP-137
                        latch.await();

                        // Write a dummy entry just so the JDK ZIP impl can close cleanly
                        putNextExtry(outputStream, "dummy.txt");
                    }

                    throw e;
                } finally {

                    try {
                        outputStream.close();
                    } catch (final IOException ioe) {
                        // Ignore, but warn of danger
                        log.log(Level.WARNING,
                            "[SHRINKWRAP-120] Possible deadlock scenario: Got exception on closing the ZIP out stream: "
                                + ioe.getMessage(), ioe);
                    }
                }

                return null;
            }
        };
    }
}

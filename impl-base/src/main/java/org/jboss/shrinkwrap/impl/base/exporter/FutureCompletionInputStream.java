/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
import java.io.PipedInputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;

/**
 * {@link PipedInputStream} which, when fully-read, will block upon a {@link Future} and report any exceptional
 * circumstances to the owning Thread.
 *
 * @param <T>
 *            Response type of the {@link Future}
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class FutureCompletionInputStream extends PipedInputStream {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(FutureCompletionInputStream.class.getName());

    /**
     * Number of bytes read signaling the end has been reached
     */
    private static final int EOF = -1;

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * The job upon which we'll block and obtain any exceptions from when we're done reading
     */
    private final Future<?> job;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new Stream
     */
    public FutureCompletionInputStream(final Future<?> job) {
        super();
        this.job = job;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.PipedInputStream#read()
     */
    @Override
    public synchronized int read() throws IOException {
        final int bytesRead = super.read();
        this.awaitOnFutureOnDone(bytesRead);
        return bytesRead;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.PipedInputStream#read(byte[], int, int)
     */
    @Override
    public synchronized int read(byte[] b, int off, int len) throws IOException {
        final int bytesRead = super.read(b, off, len);
        this.awaitOnFutureOnDone(bytesRead);
        return bytesRead;
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * If we've read the full stream, awaits on {@link FutureCompletionInputStream#job}, reporting any exceptions
     * wrapped in an {@link ArchiveExportException}.
     *
     * @param bytesRead
     * @throws ArchiveExportException
     */
    private void awaitOnFutureOnDone(final int bytesRead) throws ArchiveExportException {
        if (bytesRead == EOF) {
            try {
                // Block until the streams have been closed in the underlying job
                job.get();
            } catch (final InterruptedException e) {
                Thread.interrupted();
                log.log(Level.WARNING, "We've been interrupted while waiting for the export process to complete", e);
            }
            // Some error
            catch (final ExecutionException ee) {
                // Unwrap and rethrow
                final Throwable cause = ee.getCause();
                if (cause == null) {
                    throw new IllegalStateException("Cause of execution failure not specified: ", ee);
                }
                // Wrap as our exception type and rethrow
                throw new ArchiveExportException(cause);
            }
        }
    }
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Iterator;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;

/**
 * Base for on-demand input streams. Encodes data on the fly, when read method is executed.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public abstract class AbstractOnDemandInputStream<T extends OutputStream> extends InputStream {

    /**
     * Number of bytes kept in buffer.
     */
    private static final int BUFFER_LENGTH = 4096;

    /**
     * Iterator over nodes contained in base archive.
     */
    private final Iterator<Node> nodesIterator;

    /**
     * Created by abstract method.
     */
    protected T outputStream;

    /**
     * Base for outputStream.
     */
    private final ByteArrayOutputStream bufferedOutputStream = new ByteArrayOutputStream();

    /**
     * Stream of currently processed Node.
     */
    private InputStream currentNodeStream;

    /**
     * Stream to the buffer.
     */
    private ByteArrayInputStream bufferInputStream;

    /**
     * If output stream was closed - we should finish.
     */
    private boolean outputStreamClosed = false;

    /**
     * Currently processed archive path - for displaying exception.
     */
    private ArchivePath currentPath = null;

    /**
     * Creates stream directly from archive.
     *
     * @param archive
     */
    public AbstractOnDemandInputStream(final Archive<?> archive) {
        final Collection<Node> nodes = archive.getContent().values();
        this.nodesIterator = nodes.iterator();
    }

    @Override
    public int read() throws IOException {

        if (outputStream == null && !outputStreamClosed) {
            // first run
            outputStream = createOutputStream(bufferedOutputStream);
        }

        int value = bufferInputStream != null ? bufferInputStream.read() : -1;
        if (value == -1) {
            if (currentNodeStream != null) {
                // current node was not processed completely
                try {
                    doCopy();
                    bufferInputStream = new ByteArrayInputStream(bufferedOutputStream.toByteArray());
                    bufferedOutputStream.reset();
                    return this.read();
                } catch (final Throwable t) {
                    throw new ArchiveExportException("Failed to write asset to output: " + currentPath.get(), t);
                }
            } else if (nodesIterator.hasNext()) {
                // current node was processed completely, process next one
                final Node currentNode = nodesIterator.next();

                currentPath = currentNode.getPath();
                final String pathName = PathUtil.optionallyRemovePrecedingSlash(currentPath.get());

                final boolean isDirectory = currentNode.getAsset() == null;
                String resolvedPath = pathName;

                if (isDirectory) {
                    resolvedPath = PathUtil.optionallyAppendSlash(resolvedPath);
                    startAsset(resolvedPath);
                    endAsset();
                } else {
                    startAsset(resolvedPath);

                    try {
                        currentNodeStream = currentNode.getAsset().openStream();
                        doCopy();
                    } catch (final Throwable t) {
                        throw new ArchiveExportException("Failed to write asset to output: " + currentPath.get(), t);
                    }
                    bufferInputStream = new ByteArrayInputStream(bufferedOutputStream.toByteArray());
                    bufferedOutputStream.reset();
                }

            } else {
                // each node was processed
                if (!outputStreamClosed) {
                    outputStream.close();
                    outputStreamClosed = true;

                    // output closed, now process what was saved on close
                    bufferInputStream = new ByteArrayInputStream(bufferedOutputStream.toByteArray());
                    bufferedOutputStream.close();

                    currentNodeStream = null;
                    outputStream = null;
                    return this.read();
                }

                // everything was read, end
                return -1;
            }

            // chosen new node or new data in buffer - read again
            return this.read();
        }

        return value;
    }

    /**
     * Performs copy operation between currentNodeStream and outputStream using buffer length.
     *
     * @throws IOException
     */
    private void doCopy() throws IOException {
        int copied = IOUtil.copy(currentNodeStream, outputStream, BUFFER_LENGTH);
        if (copied < BUFFER_LENGTH || copied == -1) {
            currentNodeStream.close();
            currentNodeStream = null;
            endAsset();
        }
    }

    /**
     * Start entry in stream.
     *
     * @param path
     * @throws IOException
     */
    private void startAsset(final String path) throws IOException {
        putNextEntry(outputStream, path);
    }

    /**
     * Close entry in stream.
     *
     * @throws IOException
     */
    private void endAsset() throws IOException {
        closeEntry(outputStream);
    }

    /**
     * Creates the real {@link OutputStream} to which we'll write, wrapping the provided target.
     *
     * @param out
     * @return
     * @throws IOException
     *             If an error occurred in creating the stream
     */
    protected abstract T createOutputStream(final OutputStream outputStream) throws IOException;

    /**
     * Writes the next entry (demarcates a new file/folder is to be written).
     *
     * @param outputStream
     * @param context
     * @throws IOException
     *             If an error occurred writing the entry
     */
    protected abstract void putNextEntry(final T outputStream, final String context) throws IOException;

    /**
     * Closes the current entry context for the specified {@link OutputStream}.
     *
     * @param outputStream
     */
    protected abstract void closeEntry(final T outputStream) throws IOException;
}

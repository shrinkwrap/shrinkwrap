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
package org.jboss.shrinkwrap.impl.nio.file;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;

/**
 * ShrinkWrap implementation of a {@link DirectoryStream}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class ShrinkWrapDirectoryStream implements DirectoryStream<Path> {

    private final ShrinkWrapFileSystem fs;

    private final DirectoryStream.Filter<? super Path> filter;

    private final Path startingPath;

    private boolean closed = false;

    private boolean iteratorReturned = false;

    /**
     * Creates a new instance starting from startingPath with is required backing the specified
     * {@link ShrinkWrapFileSystem}, which is required. An optional {@link DirectoryStream.Filter} may be
     * specified as well.
     *
     * @param startingPath
     * @param fs
     * @param filter
     * @throws IllegalArgumentException
     *             If the fs is not specified
     */
    ShrinkWrapDirectoryStream(final Path startingPath, final ShrinkWrapFileSystem fs,
            final DirectoryStream.Filter<? super Path> filter)
        throws IllegalArgumentException {
        if (fs == null) {
            throw new IllegalArgumentException("File system must be specified");
        }
        if (startingPath == null) {
            throw new IllegalArgumentException("Starting path must be specified");
        }
        this.startingPath = startingPath.toAbsolutePath();
        this.fs = fs;
        this.filter = filter;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.Closeable#close()
     */
    @Override
    public void close() throws IOException {
        this.closed = true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.DirectoryStream#iterator()
     */
    @Override
    public Iterator<Path> iterator() {
        if (closed) {
            throw new IllegalStateException("Directory Stream is closed");
        } else if (iteratorReturned) {
            throw new IllegalStateException("Iterator was already returned");
        }

        boolean finishedSuccessfully = true;
        try {
            // Translate ShrinkWrap API to NIO.2 API Path
            final Map<ArchivePath, Node> content = this.fs.getArchive().getContent();
            final Collection<Path> newPaths = new ArrayList<>(content.size());
            final Collection<ArchivePath> archivePaths = content.keySet();
            for (final ArchivePath path : archivePaths) {
                final Path newPath = new ShrinkWrapPath(path, fs);

                if (!newPath.getParent().equals(startingPath)) {
                    continue;
                }

                // If we have a filter, and it rejects this path
                try {
                    if (filter != null && !(filter.accept(newPath))) {
                        // Move along
                        continue;
                    }
                } catch (IOException ioe) {
                    throw new RuntimeException("Error encountered during filtering", ioe);
                }

                // Add the new Path; the filter either wasn't specified or didn't reject this Path
                newPaths.add(newPath);
            }

            // Return
            return newPaths.iterator();
        } catch (Throwable t) {
            finishedSuccessfully = false;
            throw t;
        } finally {
            if (finishedSuccessfully) {
                iteratorReturned = true;
            }
        }
    }

}

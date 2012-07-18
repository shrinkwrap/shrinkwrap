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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileStore;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.util.Collection;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * {@link FileStore} implementation for ShrinkWrap {@link Archive}s; immutable and thread-safe.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ShrinkWrapFileStore extends FileStore {

    /**
     * {@link FileStore} type
     */
    private static final String TYPE = "shrinkwrap";

    /**
     * Underlying archive
     */
    private final Archive<?> archive;

    public ShrinkWrapFileStore(final Archive<?> archive) {
        if (archive == null) {
            throw new IllegalArgumentException("Underlying archive must be specified");
        }
        this.archive = archive;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#name()
     */
    @Override
    public String name() {
        return archive.getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#type()
     */
    @Override
    public String type() {
        return TYPE;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#getTotalSpace()
     */
    @Override
    public long getTotalSpace() throws IOException {
        return this.getUsableSpace() + this.getUsedSpace();
    }

    /**
     * Iterates through the underlying archive, counting the size of each {@link Asset}, returning the fully-tallied
     * count in bytes.
     *
     * @return
     */
    public long getUsedSpace() {
        long count = 0L;
        final int bufferSize = 1024 * 8;// Relatively big buffer (8MB); we're just reading

        final Map<ArchivePath, Node> contents = archive.getContent();
        final Collection<Node> nodes = contents.values();
        for (final Node node : nodes) {
            final Asset asset = node.getAsset();
            if (asset == null) {
                continue; // Directory
            }
            final InputStream in = new BufferedInputStream(asset.openStream(), bufferSize);
            final byte[] buffer = new byte[bufferSize];

            int read = 0;
            try {
                while ((read = in.read(buffer)) != -1) {
                    // Just count
                    count += read;
                }
            } catch (final IOException ioe) {
                throw new RuntimeException("Could not count size of archive " + this.archive.getName() + " at "
                    + asset.toString(), ioe);
            }

        }

        return count;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#getUsableSpace()
     */
    @Override
    public long getUsableSpace() throws IOException {
        return Runtime.getRuntime().freeMemory();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#getUnallocatedSpace()
     */
    @Override
    public long getUnallocatedSpace() throws IOException {
        return this.getUsableSpace();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#supportsFileAttributeView(java.lang.Class)
     */
    @Override
    public boolean supportsFileAttributeView(final Class<? extends FileAttributeView> type) {
        return BasicFileAttributeView.class.equals(type);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#supportsFileAttributeView(java.lang.String)
     */
    @Override
    public boolean supportsFileAttributeView(final String name) {
        // Only support "basic"
        return ShrinkWrapFileSystem.FILE_ATTR_VIEW_BASIC.equals(name);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#getFileStoreAttributeView(java.lang.Class)
     */
    @Override
    public <V extends FileStoreAttributeView> V getFileStoreAttributeView(final Class<V> type) {
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileStore#getAttribute(java.lang.String)
     */
    @Override
    public Object getAttribute(final String attribute) throws IOException {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + " does not support attributes.");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " for: " + archive.toString();
    }

}

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
import java.io.InputStream;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * ShrinkWrap implementation of {@link BasicFileAttributes}; not all operations are supported
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
final class ShrinkWrapFileAttributes implements BasicFileAttributes {

    private final ShrinkWrapPath path;
    private final Archive<?> archive;

    ShrinkWrapFileAttributes(final ShrinkWrapPath path, Archive<?> archive) {
        assert path != null : "Path must be specified";
        assert archive != null : "Archive must be specified";
        this.path = path;
        this.archive = archive;
    }

    /**
     * @throws UnsupportedOperationException
     * @see java.nio.file.attribute.BasicFileAttributes#lastModifiedTime()
     */
    @Override
    public FileTime lastModifiedTime() {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     * @see java.nio.file.attribute.BasicFileAttributes#lastAccessTime()
     */
    @Override
    public FileTime lastAccessTime() {
        throw new UnsupportedOperationException();
    }

    /**
     * @throws UnsupportedOperationException
     * @see java.nio.file.attribute.BasicFileAttributes#creationTime()
     */
    @Override
    public FileTime creationTime() {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.attribute.BasicFileAttributes#isRegularFile()
     */
    @Override
    public boolean isRegularFile() {
        return !this.isDirectory();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.attribute.BasicFileAttributes#isDirectory()
     */
    @Override
    public boolean isDirectory() {
        final ArchivePath archivePath = ArchivePaths.create(path.toString());
        Node node = archive.get(archivePath);
        return node.getAsset() == null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.attribute.BasicFileAttributes#isSymbolicLink()
     */
    @Override
    public boolean isSymbolicLink() {
        // No symlinks
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.attribute.BasicFileAttributes#isOther()
     */
    @Override
    public boolean isOther() {
        // Either a dir or regular file
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.attribute.BasicFileAttributes#size()
     */
    @Override
    public long size() {
        if (this.isDirectory()) {
            return -1L;
        }

        final Asset asset = this.getArchive().get(this.path.toString()).getAsset();
        final InputStream stream = asset.openStream();
        int totalRead = 0;
        final byte[] buffer = new byte[1024 * 4];
        int read = 0;
        try {
            while ((read = stream.read(buffer, 0, buffer.length)) != -1) {
                totalRead += read;
            }
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }

        return totalRead;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.attribute.BasicFileAttributes#fileKey()
     */
    @Override
    public Object fileKey() {
        return this.getArchive().getId() + "/" + this.path.toString();
    }

    private Archive<?> getArchive() {
        return ((ShrinkWrapFileSystem) path.getFileSystem()).getArchive();
    }

}

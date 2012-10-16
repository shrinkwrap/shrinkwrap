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
import java.nio.file.ClosedFileSystemException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;

/**
 * ShrinkWrap implementation adapting a {@link Archive} to a {@link FileSystem}; Thread-safe, though access to the
 * underlying {@link Archive} is *not*.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ShrinkWrapFileSystem extends FileSystem {

    /**
     * Contracted name of the {@link BasicFileAttributeView}
     */
    static final String FILE_ATTR_VIEW_BASIC = "basic";

    /**
     * Provider which created this {@link ShrinkWrapFileSystem}
     */
    private final ShrinkWrapFileSystemProvider provider;

    /**
     * Underlying {@link Archive}
     */
    private final Archive<?> archive;

    private final List<FileStore> fileStores;

    /**
     * Whether or not this FS is open; volatile as we don't need compound operations and thus don't need full sync
     */
    private volatile boolean open;

    public ShrinkWrapFileSystem(final ShrinkWrapFileSystemProvider provider, final Archive<?> archive) {
        this.provider = provider;
        this.archive = archive;
        this.open = true;
        // Each ShrinkWrapFileSystem has one file store
        final FileStore store = new ShrinkWrapFileStore(this.archive);
        final List<FileStore> stores = new ArrayList<>(1);
        stores.add(store);
        this.fileStores = Collections.unmodifiableList(stores);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#provider()
     */
    @Override
    public FileSystemProvider provider() {
        return provider;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#close()
     */
    @Override
    public void close() throws IOException {
        this.open = false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#isOpen()
     */
    @Override
    public boolean isOpen() {
        return this.open;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#isReadOnly()
     */
    @Override
    public boolean isReadOnly() {
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#getSeparator()
     */
    @Override
    public String getSeparator() {
        return ArchivePath.SEPARATOR_STRING;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#getRootDirectories()
     */
    @Override
    public Iterable<Path> getRootDirectories() {

        this.checkClosed();

        // Each ShrinkWrapFileSystem has one root directory
        final Path path = new ShrinkWrapPath(ArchivePaths.root(), this);
        final List<Path> paths = new ArrayList<>(1);
        paths.add(path);
        return Collections.unmodifiableList(paths);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#getFileStores()
     */
    @Override
    public Iterable<FileStore> getFileStores() {

        this.checkClosed();

        return this.fileStores;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#supportedFileAttributeViews()
     */
    @Override
    public Set<String> supportedFileAttributeViews() {

        this.checkClosed();

        final Set<String> names = new HashSet<>(1);
        // Required by spec
        names.add(FILE_ATTR_VIEW_BASIC);
        return Collections.unmodifiableSet(names);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#getPath(java.lang.String, java.lang.String[])
     */
    @Override
    public Path getPath(final String first, final String... more) {

        this.checkClosed();

        if (first == null) {
            throw new IllegalArgumentException("At least one path component must be specified");
        }

        final String merged = this.merge(first, more);
        final Path path = new ShrinkWrapPath(merged, this);

        return path;
    }

    /**
     * Merges the path context with a varargs String sub-contexts, returning the result
     *
     * @param first
     * @param more
     * @return
     */
    private String merge(final String first, final String[] more) {
        assert first != null : "first must be specified";
        assert more != null : "more must be specified";

        final StringBuilder merged = new StringBuilder();
        merged.append(first);
        for (int i = 0; i < more.length; i++) {
            merged.append(ArchivePath.SEPARATOR);
            merged.append(more[i]);
        }
        return merged.toString();

    }

    /*
     * (non-Javadoc)
     *
     * @see java.nio.file.FileSystem#getPathMatcher(java.lang.String)
     */
    @Override
    public PathMatcher getPathMatcher(final String syntaxAndPattern) {

        this.checkClosed();

        // TODO Is there some matcher we can reuse?
        throw new UnsupportedOperationException("ShrinkWrap archives do not support Path Matcher operations");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#getUserPrincipalLookupService()
     */
    @Override
    public UserPrincipalLookupService getUserPrincipalLookupService() {
        throw new UnsupportedOperationException("ShrinkWrap archives do not support file ownership.");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.FileSystem#newWatchService()
     */
    @Override
    public WatchService newWatchService() throws IOException {
        throw new UnsupportedOperationException("ShrinkWrap archives do not support a "
            + WatchService.class.getSimpleName() + ".");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " with mounted archive: " + archive.toString();
    }

    /**
     * Checks if the {@link ShrinkWrapFileSystem} is closed, and throws a {@link ClosedFileSystemException} if so
     *
     * @throws ClosedFileSystemException
     */
    private void checkClosed() throws ClosedFileSystemException {
        // Check closed?
        if (!this.isOpen()) {
            throw new ClosedFileSystemException();
        }
    }

    /**
     * Obtains the underlying archive
     */
    Archive<?> getArchive() {
        return this.archive;
    }

}

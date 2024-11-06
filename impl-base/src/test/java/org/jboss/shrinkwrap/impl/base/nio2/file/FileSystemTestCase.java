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
package org.jboss.shrinkwrap.impl.base.nio2.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.nio2.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link FileSystem} is working as contracted.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class FileSystemTestCase {

    private FileSystem fileSystem;

    private JavaArchive archive;

    @BeforeEach
    public void createFileSystem() throws IOException {

        // Setup and mount the archive
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        this.fileSystem = ShrinkWrapFileSystems.newFileSystem(archive);
        this.archive = archive;
    }

    @AfterEach
    public void closeFs() throws IOException {
        this.fileSystem.close();
    }

    @Test
    public void rootDirectories() {
        final Iterable<Path> paths = fileSystem.getRootDirectories();
        int count = 0;
        for (final Path path : paths) {
            count++;
            Assertions.assertEquals(ArchivePaths.root().get(), path.toString(), "Root was not in expected form");
        }
        Assertions.assertEquals(1, count, "Should only be one root path per FS");
    }

    @Test
    public void fileSeparator() {
        final String fileSeparator = fileSystem.getSeparator();
        Assertions.assertEquals(ArchivePath.SEPARATOR_STRING, fileSeparator, "File separator was not as expected");
    }

    @Test
    public void provider() {
        final FileSystemProvider provider = fileSystem.provider();
        Assertions.assertNotNull(provider, "Provider must be linked from file system");
        Assertions.assertInstanceOf(ShrinkWrapFileSystemProvider.class, provider, "Provider supplied is of wrong type");
    }

    @Test
    public void isReadOnly() {
        Assertions.assertFalse(fileSystem.isReadOnly(), "ShrinkWrap File Systems are not read-only");
    }

    @Test
    public void isOpen() {
        Assertions.assertTrue(fileSystem.isOpen(), "Should report as open");
    }

    @Test
    public void isOpenAfterClose() throws IOException {
        fileSystem.close();
        Assertions.assertFalse(fileSystem.isOpen(), "Should report as closed");
    }

    @Test
    public void getFileStores() {
        final Iterable<FileStore> fileStores = fileSystem.getFileStores();
        int count = 0;
        for (final FileStore fileStore : fileStores) {
            count++;
            Assertions.assertInstanceOf(ShrinkWrapFileStore.class, fileStore, "file store is not of correct type");
        }

        Assertions.assertEquals(1, count, "Should only be one file store per file system");
    }

    @Test
    public void supportedFileAttributeViews() {

        final Set<String> fileAttrViews = fileSystem.supportedFileAttributeViews();
        // By contract, we must support "basic", so we'll verify just that
        Assertions.assertEquals(1, fileAttrViews.size(), "Only support \"basic\" file att view");
        Assertions.assertTrue(fileAttrViews.contains("basic"), "By contract we must support the \"basic\" view");
    }

    @Test
    public void getPathRoot() {
        final Path path = fileSystem.getPath("/");
        Assertions.assertEquals(ArchivePaths.root().get(), path.toString(), "Root path not obtained correctly");
    }

    @Test
    public void getPathRootFromEmptyString() {
        final Path path = fileSystem.getPath("");
        Assertions.assertNull(path.getRoot(), "Root path of empty string should be null");
    }

    @Test
    public void getPathNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileSystem.getPath(null));
    }

    @Test
    public void getHierarchicalPath() {
        final Path path = fileSystem.getPath("toplevel", "parent", "child");
        Assertions.assertEquals("toplevel/parent/child", path.toString(),
                "Path not obtained correctly from hierarchical input");
    }

    @Test
    public void getHierarchicalPathFromAbsolute() {
        final Path path = fileSystem.getPath("/toplevel", "parent", "child");
        Assertions.assertEquals("/toplevel/parent/child", path.toString(),
                "Path not obtained correctly from hierarchical input");
    }

    @Test
    public void getHierarchicalPathFromMixedInput() {
        final Path path = fileSystem.getPath("toplevel/parent", "child");
        Assertions.assertEquals("toplevel/parent/child", path.toString(),
                "Path not obtained correctly from mixed hierarchical input");
    }

    @Test
    // We don't support security
    public void getUserPrincipalLookupService() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> fileSystem.getUserPrincipalLookupService());
    }

    @Test
    // We don't support a watch service
    public void newWatchService() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> fileSystem.newWatchService());
    }

    @Test
    public void fileSystemAlreadyExists() throws IllegalArgumentException {
        // exception should be thrown, second file system for the same archive
        Assertions.assertThrows(FileSystemAlreadyExistsException.class, () -> ShrinkWrapFileSystems.newFileSystem(archive));
    }

    @Test
    public void fileSystemClosedNewInstanceCreated() throws IllegalArgumentException, IOException {
        this.fileSystem.close();

        Assertions.assertNotNull(ShrinkWrapFileSystems.newFileSystem(archive));
    }

    @Test
    public void getFileSystem() {
        URI uri = ShrinkWrapFileSystems.getRootUri(archive);

        Assertions.assertEquals(this.fileSystem, FileSystems.getFileSystem(uri),
                "getFileSystem should return same existing file system");
    }

    // TODO Test case for getPathMatcher, but first implement it

}

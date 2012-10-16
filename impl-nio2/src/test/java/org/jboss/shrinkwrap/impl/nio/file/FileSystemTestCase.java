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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Set;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link FileSystem} is working as contracted.
 * 
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class FileSystemTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(FileSystemTestCase.class.getName());

    private FileSystem fileSystem;

    private JavaArchive archive;

    @Before
    public void createFileSystem() throws URISyntaxException, IOException {

        // Setup and mount the archive
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        final FileSystem fs = ShrinkWrapFileSystems.newFileSystem(archive);
        this.fileSystem = fs;
        this.archive = archive;
    }

    @After
    public void closeFs() throws IOException {
        this.fileSystem.close();
    }

    @Test
    public void rootDirectories() {
        final Iterable<Path> paths = fileSystem.getRootDirectories();
        int count = 0;
        for (final Path path : paths) {
            count++;
            Assert.assertEquals("Root was not in expected form", ArchivePaths.root().get(), path.toString());
        }
        Assert.assertEquals("Should only be one root path per FS", 1, count);
    }

    @Test
    public void fileSeparator() {
        final String fileSeparator = fileSystem.getSeparator();
        Assert.assertEquals("File separator was not as expected", ArchivePath.SEPARATOR_STRING, fileSeparator);
    }

    @Test
    public void provider() {
        final FileSystemProvider provider = fileSystem.provider();
        Assert.assertNotNull("Provider must be linked from file system", provider);
        Assert.assertTrue("Provider supplied is of wrong type", provider instanceof ShrinkWrapFileSystemProvider);
    }

    @Test
    public void isReadOnly() {
        Assert.assertFalse("ShrinkWrap File Systems are not read-only", fileSystem.isReadOnly());
    }

    @Test
    public void isOpen() {
        Assert.assertTrue("Should report as open", fileSystem.isOpen());
    }

    @Test
    public void isOpenAfterClose() throws IOException {
        fileSystem.close();
        Assert.assertFalse("Should report as closed", fileSystem.isOpen());
    }

    @Test
    public void getFileStores() {
        final Iterable<FileStore> fileStores = fileSystem.getFileStores();
        int count = 0;
        for (final FileStore fileStore : fileStores) {
            count++;
            Assert.assertTrue("file store is not of correct type", fileStore instanceof ShrinkWrapFileStore);
        }

        Assert.assertEquals("Should only be one file store per file system", 1, count);
    }

    @Test
    public void supportedFileAttributeViews() {

        final Set<String> fileAttrViews = fileSystem.supportedFileAttributeViews();
        // By contract we must support "basic", so we'll verify just that
        Assert.assertEquals("Only support \"basic\" file att view", 1, fileAttrViews.size());
        Assert.assertTrue("By contract we must support the \"basic\" view", fileAttrViews.contains("basic"));
    }

    @Test
    public void getPathRoot() {
        final Path path = fileSystem.getPath("/");
        Assert.assertEquals("Root path not obtained correctly", ArchivePaths.root().get(), path.toString());
    }

    @Test
    public void getPathRootFromEmptyString() {
        final Path path = fileSystem.getPath("");
        Assert.assertNull("Root path of empty string should be null", path.getRoot());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPathNull() {
        fileSystem.getPath(null);
    }

    @Test
    public void getHierarchicalPath() {
        final Path path = fileSystem.getPath("toplevel", "parent", "child");
        Assert.assertEquals("Path not obtained correctly from hierarchical input", "toplevel/parent/child",
            path.toString());
    }

    @Test
    public void getHierarchicalPathFromAbsolute() {
        final Path path = fileSystem.getPath("/toplevel", "parent", "child");
        Assert.assertEquals("Path not obtained correctly from hierarchical input", "/toplevel/parent/child",
            path.toString());
    }

    @Test
    public void getHierarchicalPathFromMixedInput() {
        final Path path = fileSystem.getPath("toplevel/parent", "child");
        Assert.assertEquals("Path not obtained correctly from mixed hierarchical input", "toplevel/parent/child",
            path.toString());
    }

    @Test(expected = UnsupportedOperationException.class)
    // We don't support security
    public void getUserPrincipalLookupService() {
        fileSystem.getUserPrincipalLookupService();
    }

    @Test(expected = UnsupportedOperationException.class)
    // We don't support a watch service
    public void newWatchService() throws IOException {
        fileSystem.newWatchService();
    }

    @Test(expected = FileSystemAlreadyExistsException.class)
    public void fileSystemAlreadyExists() throws IllegalArgumentException, IOException {
        // exception should be thrown, second file system for the same archive
        ShrinkWrapFileSystems.newFileSystem(archive);
    }

    @Test
    public void fileSystemClosedNewIsntanceCreated() throws IllegalArgumentException, IOException {
        this.fileSystem.close();

        Assert.assertNotNull("", ShrinkWrapFileSystems.newFileSystem(archive));
    }

    @Test
    public void getFileSystem() {
        URI uri = ShrinkWrapFileSystems.getRootUri(archive);

        Assert.assertEquals("getFileSystem should return same existing file system", this.fileSystem,
            FileSystems.getFileSystem(uri));
    }

    // TODO Test case for getPathMatcher, but first implement it

}

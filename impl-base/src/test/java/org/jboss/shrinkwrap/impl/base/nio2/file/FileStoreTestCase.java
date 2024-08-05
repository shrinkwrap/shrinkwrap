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
import java.io.InputStream;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.nio2.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link FileStore} is working as contracted.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class FileStoreTestCase {

    private JavaArchive archive;

    private FileStore fileStore;

    private FileSystem fileSystem;

    @BeforeEach
    public void createStore() throws IOException {
        // Setup
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        final FileSystem fs = ShrinkWrapFileSystems.newFileSystem(archive);
        final FileStore fileStore = fs.getFileStores().iterator().next();

        // Set
        this.fileStore = fileStore;
        this.archive = archive;
        this.fileSystem = fs;
    }

    @AfterEach
    public void closeFs() throws IOException {
        this.fileSystem.close();
    }

    @Test
    public void usedSpace() throws IOException {
        // Add a class to the archive
        final Class<?> classToAdd = Asset.class;
        archive.addClass(classToAdd);

        // Get size of the class
        final String pathToClass = classToAdd.getName().replace('.', ArchivePath.SEPARATOR) + ".class";
        final InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(pathToClass);
        long thisClassFileSize = 0L;
        final byte[] buffer = new byte[8 * 1024];
        int read = 0;

        while ((read = in.read(buffer)) != -1) {
            // Just count
            thisClassFileSize += read;
        }

        // Get size of the archive as reported by the FS
        final long sizeOfArchive = ((ShrinkWrapFileStore) this.fileStore).getUsedSpace();

        Assertions.assertEquals(thisClassFileSize, sizeOfArchive,
                "Size of archive as reported by file store is not equal to the size of the contained class");
    }

    @Test
    public void totalSpace() throws IOException {
        // We can't really test this value properly as the JVM can reallocate memory inbetween calls, so just ensure
        // we're returning something sane
        Assertions.assertTrue(this.fileStore.getTotalSpace() > 0, "Total space is not returning a positive integer");
    }

    @Test
    public void usableSpace() throws IOException {
        // We can't really test this value properly as the JVM can reallocate memory inbetween calls, so just ensure
        // we're returning something sane
        Assertions.assertTrue(this.fileStore.getUsableSpace() > 0, "Usable space is not returning a positive integer");
    }

    @Test
    public void unallocatedSpace() throws IOException {
        // We can't really test this value properly as the JVM can reallocate memory inbetween calls, so just ensure
        // we're returning something sane
        Assertions.assertTrue(this.fileStore.getUnallocatedSpace() > 0,
                "Unallocated space is not returning a positive integer");
    }

    @Test
    public void name() {
        Assertions.assertEquals(this.archive.getName(), this.fileStore.name(),
                "Name of the file store should be equal to the name of the underlying archive");
    }

    @Test
    public void type() {
        Assertions.assertEquals("shrinkwrap", this.fileStore.type(), "Type of the file store should be \"shrinkwrap\"");
    }

    @Test
    public void readOnly() {
        Assertions.assertFalse(this.fileStore.isReadOnly(), "ShrinkWrap file stores are not read-only");
    }

    @Test
    public void supportsBasicFileAttributeView() {
        Assertions.assertTrue(this.fileStore.supportsFileAttributeView("basic"),
                "ShrinkWrap file store must support basic file attribute view");
    }

    @Test
    public void supportsBasicFileAttributeViewType() {
        Assertions.assertTrue(this.fileStore.supportsFileAttributeView(BasicFileAttributeView.class),
                "ShrinkWrap file store must support basic file attribute view");
    }

    @Test
    public void supportsOtherFileAttributeView() {
        Assertions.assertFalse(this.fileStore.supportsFileAttributeView("somethingelse"),
                "ShrinkWrap file store should not support other file attribute views");
    }

    @Test
    public void supportsOtherFileAttributeViewType() {
        Assertions.assertFalse(this.fileStore.supportsFileAttributeView(((FileAttributeView) () -> "mock").getClass()),
                "ShrinkWrap file store should not support other file attribute views");
    }

    @Test
    public void getAttribute() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> this.fileStore.getAttribute("something"));
    }

    @Test
    public void getFileStoreAttributeView() {
        Assertions.assertNull(this.fileStore.getFileStoreAttributeView(FileStoreAttributeView.class));
    }

    @Test
    public void nullArgsProhibited() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ShrinkWrapFileStore(null));
    }

}

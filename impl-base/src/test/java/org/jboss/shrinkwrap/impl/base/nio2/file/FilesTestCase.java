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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.nio2.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link FileSystem} is working as expected via the
 * {@link Files} convenience API.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class FilesTestCase {

    /**
     * {@link FileSystem} under test
     */
    private FileSystem fs;

    @BeforeEach
    public void createFileSystem() throws IOException {
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        this.fs = ShrinkWrapFileSystems.newFileSystem(archive);
    }

    @AfterEach
    public void closeFileSystem() throws IOException {
        this.fs.close();
    }

    @Test
    public void delete() throws IOException {

        // Backdoor add, because we only test delete here (not adding via the Files API)
        final Archive<?> archive = this.getArchive();
        final String pathString = "fileToDelete";
        archive.add(EmptyAsset.INSTANCE, pathString);

        // Ensure added
        Assertions.assertTrue(archive.contains(pathString));

        // Delete
        final Path path = fs.getPath(pathString);
        Files.delete(path);

        // Ensure deleted
        Assertions.assertFalse(archive.contains(pathString));
    }

    @Test
    public void deleteNonexistent() {

        final Archive<?> archive = this.getArchive();
        final String pathString = "nonexistent";

        // Ensure file doesn't exist
        Assertions.assertFalse(archive.contains(pathString));

        // Attempt delete
        final Path path = fs.getPath(pathString);
        Assertions.assertThrows(NoSuchFileException.class, () -> Files.delete(path),
                "Request to remove nonexistent path should have thrown " + NoSuchFileException.class.getSimpleName());
    }

    @Test
    public void deleteIfExists() throws IOException {

        // Backdoor add, because we only test delete here (not adding via the Files API)
        final Archive<?> archive = this.getArchive();
        final String pathString = "fileToDelete";
        archive.add(EmptyAsset.INSTANCE, pathString);

        // Ensure added
        Assertions.assertTrue(archive.contains(pathString));

        // Delete
        final Path path = fs.getPath(pathString);
        final boolean deleted = Files.deleteIfExists(path);

        // Ensure deleted
        Assertions.assertTrue(deleted, "Did not report deleted");
    }

    @Test
    public void deleteIfExistsDoesntExist() throws IOException {
        final String pathString = "fileWhichDoesNotExist";
        final Path path = fs.getPath(pathString);
        final boolean deleted = Files.deleteIfExists(path);
        Assertions.assertFalse(deleted, "Should not report deleted");
    }

    @Test
    public void deleteDirectory() throws IOException {

        final String directoryName = "directory";
        final Archive<?> archive = this.getArchive().addAsDirectory(directoryName);

        // Preconditions
        Assertions.assertNull(archive.get(directoryName).getAsset(),
                "Test archive should contain the directory, not content");

        // Attempt delete
        final Path path = fs.getPath(directoryName);
        Files.delete(path);

        // Assertion
        Assertions.assertFalse(archive.contains(directoryName),"Archive should no longer contain directory ");
    }

    @Test
    public void deleteNonemptyDirectory() {

        final String directoryName = "/directory";
        final String subDirectoryName = directoryName + "/subdir";
        final Archive<?> archive = this.getArchive().addAsDirectory(subDirectoryName);

        // Preconditions
        Assertions.assertNull(archive.get(subDirectoryName).getAsset(),
                "Test archive should contain the directory, not content");

        // Attempt delete
        final Path path = fs.getPath(directoryName);
        Assertions.assertThrows(DirectoryNotEmptyException.class, () -> Files.delete(path),
                "Should not be able to delete non-empty directory");
    }

    @Test
    public void createDirectory() throws IOException {
        final String dirName = "/newDirectory";
        final Path dir = fs.getPath(dirName);

        // Ensure dir doesn't exist
        final Archive<?> archive = this.getArchive();
        Assertions.assertFalse(archive.contains(dirName));

        // Attempt create
        final Path createdDir = Files.createDirectory(dir, (FileAttribute<?>) null);
        Assertions.assertTrue(archive.contains(dirName), "Archive does not contain created directory");
        Assertions.assertNull(archive.get(dirName).getAsset(), "Created path is not a directory");
        Assertions.assertEquals(dirName, createdDir.toString(), "Created directory name was not as expected");
    }

    @Test
    public void createDirectoryRecursiveProhibited() {
        final String dirName = "/newDirectory/child";
        final Path dir = fs.getPath(dirName);

        // Ensure dir doesn't exist
        final Archive<?> archive = this.getArchive();
        Assertions.assertFalse(archive.contains(dirName));

        // Attempt create
        Assertions.assertThrows(IOException.class, () -> Files.createDirectory(dir, (FileAttribute<?>) null),
                "Should not be able to create directory unless parents are first present");
    }

    @Test
    public void createDirectoriesRecursive() throws IOException {
        final String dirName = "/newDirectory/child";
        final Path dir = fs.getPath(dirName);

        // Ensure dir doesn't exist
        final Archive<?> archive = this.getArchive();
        Assertions.assertFalse(archive.contains(dirName));

        // Attempt create
        final Path createdDir = Files.createDirectories(dir, (FileAttribute<?>) null);
        Assertions.assertTrue(archive.contains(dirName), "Archive does not contain created directory");
        Assertions.assertNull(archive.get(dirName).getAsset(), "Created path is not a directory");
        Assertions.assertEquals(dirName, createdDir.toString(), "Created directory name was not as expected");
    }

    @Test
    public void copyFromInputStreamToPath() throws IOException {
        final String contents = "Hello, testing content writing!";
        final byte[] bytes = contents.getBytes(StandardCharsets.UTF_8);
        final String pathName = "content";
        final Path path = fs.getPath(pathName);

        try (final InputStream in = new ByteArrayInputStream(bytes)) {
            final long bytesCopied = Files.copy(in, path);

            try (final InputStreamReader inputStreamReader = new InputStreamReader(this.getArchive().get(pathName).getAsset()
                    .openStream());
                 final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final String roundtrip = bufferedReader.readLine();
                Assertions.assertEquals(contents, roundtrip, "Contents after copy were not as expected");
                Assertions.assertEquals(bytes.length, bytesCopied);
            }
        }
    }

    @Test
    public void copyFromInputStreamToExistingPath() throws IOException {
        final String pathName = "content";
        final Path path = fs.getPath(pathName);
        // Add some dummy asset to the archive
        this.getArchive().add(EmptyAsset.INSTANCE, pathName);

        try (final InputStream in = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8))) {
            // Now try to copy to the same path as the dummy asset
            Assertions.assertThrows(FileAlreadyExistsException.class, () -> Files.copy(in, path),
                    "Overwrite of existing path should fail");
        }
    }

    @Test
    public void copyFromInputStreamToExistingDirectory() throws IOException {
        final String pathName = "directory";
        final Path path = fs.getPath(pathName);
        // Add some directory to the archive
        this.getArchive().addAsDirectories(pathName);

        try (final InputStream in = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8))) {
            // Now try to copy to the same path as the dir
            Assertions.assertThrows(FileAlreadyExistsException.class, () -> Files.copy(in, path),
                    "Overwrite of existing directory should fail");
        }
    }

    @Test
    public void copyFromInputStreamToExistingNonEmptyDirectoryWithReplaceExistingOption() throws IOException {
        final String dir = "/directory";
        final String subdir = dir + "/subdir";
        final Path dirPath = fs.getPath(dir);
        // Add some nested directory to the archive
        this.getArchive().addAsDirectories(subdir);

        try (final InputStream in = new ByteArrayInputStream("test".getBytes(StandardCharsets.UTF_8))) {
            // Now try to copy to a nonempty dir
            Assertions.assertThrows(DirectoryNotEmptyException.class, () -> Files.copy(in, dirPath, StandardCopyOption.REPLACE_EXISTING),
                    "Overwrite of existing non-empty dir should fail, even with replace option");
        }
    }

    @Test
    public void copyFromInputStreamToExistingPathWithOverwriteOption() throws IOException {
        final String contents = "Hello, testing content writing!";
        final byte[] bytes = contents.getBytes(StandardCharsets.UTF_8);
        final String pathName = "content";
        final Path path = fs.getPath(pathName);
        // Add some dummy asset to the archive
        this.getArchive().add(EmptyAsset.INSTANCE, pathName);

        try (final InputStream in = new ByteArrayInputStream(bytes)) {
            // Now try to copy to the same path as the dummy asset, using the option to overwrite
            final long bytesCopied = Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);

            try (final InputStreamReader inputStreamReader = new InputStreamReader(this.getArchive().get(pathName).getAsset()
                    .openStream());
                 final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final String roundtrip = bufferedReader.readLine();
                Assertions.assertEquals(contents, roundtrip, "Contents after copy were not as expected");
                Assertions.assertEquals(bytes.length, bytesCopied);
            }
        }
    }

    @Test
    public void copyFromPathToOutputStream() throws IOException {
        // Populate the archive w/ content
        final String contents = "Here we're gonna test reading from the archive and writing the contents to an OutStream";
        final String path = "contentPath";
        this.getArchive().add(new StringAsset(contents), path);

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            // Copy
            final long bytesCopied = Files.copy(fs.getPath(path), out);
            // Get out the content
            final String roundtrip = new String(out.toByteArray(), StandardCharsets.UTF_8);
            Assertions.assertEquals(contents, roundtrip, "Contents after copy were not as expected");
            Assertions.assertEquals(contents.length(), bytesCopied);
        }
    }

    @Test
    public void copyFromDirectoryPathToOutputStream() {
        // Populate the archive w/ content
        final String path = "dirPath";
        this.getArchive().addAsDirectories(path);
        // Attempt to copy the dir
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        Assertions.assertThrows(IllegalArgumentException.class, () -> Files.copy(fs.getPath(path), out),
                "Call to copy a directory contents to an output stream should not succeed");
    }

    @Test
    public void writeWithAppendOption() throws IOException {
        final String initialContent = "initial content";
        final String appendContent = " - appended contents";
        final String pathName = "content";
        final Path path = fs.getPath(pathName);
        // Add the first initial content to the archive
        final Archive<?> archive = this.getArchive();
        archive.add(new StringAsset(initialContent), pathName);
        // Write in append mode
        Files.write(path, appendContent.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);

        try (final InputStreamReader inputStreamReader = new InputStreamReader(archive.get(pathName).getAsset()
                .openStream());
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final String newContents = bufferedReader.readLine();
            Assertions.assertEquals(initialContent + appendContent, newContents,
                    "New contents was not appended as expected");
        }
    }

    @Test
    public void createFile() throws IOException {
        final String pathName = "fileToCreate";
        final Path path = fs.getPath(pathName);
        final Path newPath = Files.createFile(path, (FileAttribute<?>) null);
        Assertions.assertEquals(path.toString(), newPath.toString());
    }

    @Test
    public void createFileWhenFileAlreadyExists() {
        final String pathName = "fileToCreate";
        this.getArchive().add(EmptyAsset.INSTANCE, pathName);
        final Path path = fs.getPath(pathName);
        Assertions.assertThrows(FileAlreadyExistsException.class, () -> Files.createFile(path, (FileAttribute<?>) null),
                "Create new file should fail if path already exists");
    }

    @Test
    public void createLink() {
        final String linkToCreateString = "linkToCreate";
        final String existingPathString = "existingPath";
        this.getArchive().add(EmptyAsset.INSTANCE, existingPathString);
        final Path linkToCreatePath = fs.getPath(linkToCreateString);
        final Path existingPath = fs.getPath(existingPathString);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> Files.createLink(linkToCreatePath, existingPath),
                "We should not support creation of links");
    }

    @Test
    public void createSymbolicLink() {
        final String symbolicLinkToCreateString = "linkToCreate";
        final String existingPathString = "existingPath";
        this.getArchive().add(EmptyAsset.INSTANCE, existingPathString);
        final Path symbolicLinkToCreatePath = fs.getPath(symbolicLinkToCreateString);
        final Path existingPath = fs.getPath(existingPathString);
        Assertions.assertThrows(UnsupportedOperationException.class, () -> Files.createSymbolicLink(symbolicLinkToCreatePath, existingPath),
                "We should not support creation of symbolic links");
    }

    // This will fail until we establish relative Paths
    @Test
    public void createTempDirectory() throws IOException {
        final String tempDir = "/tmp";
        this.getArchive().addAsDirectories(tempDir);
        final String prefix = "prefix";
        final Path tempDirPath = fs.getPath(tempDir);
        final Path newPath = Files.createTempDirectory(tempDirPath, prefix);
        Assertions.assertTrue(newPath.toString().startsWith(tempDir + "/" + prefix),
                "temp dir name was not in expected form");
    }

    @Test
    public void existsFalse() {
        final Path path = fs.getPath("fileWhichDoesNotExist");
        Assertions.assertFalse(Files.exists(path), "Should report exists");
    }

    @Test
    public void existsTrue() {
        final Archive<?> archive = this.getArchive();
        final String pathString = "file";
        archive.add(EmptyAsset.INSTANCE, pathString);
        final Path path = fs.getPath(pathString);
        Assertions.assertTrue(Files.exists(path), "Should report exists");
    }

    @Test
    public void getAttribute() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> Files.getAttribute(fs.getPath("file"), "basic", (LinkOption) null));
    }

    @Test
    public void getFileAttributeView() {
        Assertions.assertInstanceOf(ShrinkWrapFileAttributeView.class, Files.getFileAttributeView(fs.getPath("file"),
                BasicFileAttributeView.class, (LinkOption) null));
    }

    @Test
    public void getFileStore() throws IOException {
        Assertions.assertSame(fs.getFileStores().iterator().next(), Files.getFileStore(fs.getPath("path")),
                "Returned file store was not as expected");
    }

    @Test
    public void getLastModifiedTime() {
        this.getArchive().add(EmptyAsset.INSTANCE, "path");
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> Files.getLastModifiedTime(fs.getPath("path"), (LinkOption) null));
    }

    @Test
    public void getOwner() {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> Files.getOwner(fs.getPath("path"), (LinkOption) null));
    }

    @Test
    public void getPosixFilePermissions() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> Files.getPosixFilePermissions(fs.getPath("path"), (LinkOption) null));
    }

    @Test
    public void isDirectoryTrue() {
        this.getArchive().addAsDirectories("path");
        Assertions.assertTrue(Files.isDirectory(fs.getPath("path"), (LinkOption) null));
    }

    @Test
    public void isDirectoryFalse() {
        Assertions.assertFalse(Files.isDirectory(fs.getPath("path"), (LinkOption) null));
    }

    @Test
    // All paths are executable
    public void isExecutable() {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assertions.assertTrue(Files.isExecutable(fs.getPath(path)));
    }

    @Test
    // No paths are hidden
    public void isHidden() throws IOException {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assertions.assertFalse(Files.isHidden(fs.getPath(path)));
    }

    @Test
    // All paths are readable
    public void isReadable() {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assertions.assertTrue(Files.isReadable(fs.getPath(path)));
    }

    @Test
    // No nonexistent paths are executable
    public void isExecutableNonexistent() {
        final String path = "path";
        Assertions.assertFalse(Files.isExecutable(fs.getPath(path)));
    }

    @Test
    // No nonexistent paths are readable
    public void isReadableNonexistent() {
        final String path = "path";
        Assertions.assertFalse(Files.isReadable(fs.getPath(path)));
    }

    @Test
    public void isRegularFile() {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assertions.assertTrue(Files.isRegularFile(fs.getPath(path)));
    }

    @Test
    public void isRegularFileFalse() {
        final String path = "path/";
        Assertions.assertFalse(Files.isRegularFile(fs.getPath(path)));
    }

    @Test
    public void isSameFile() throws IOException {
        final String path1 = "path/sub";
        final String path2 = "path/sub";
        Assertions.assertTrue(Files.isSameFile(fs.getPath(path1), fs.getPath(path2)));
    }

    @Test
    public void isSameFileFalse() throws IOException {
        final String path1 = "path/sub";
        final String path2 = "path/sub/notsame";
        Assertions.assertFalse(Files.isSameFile(fs.getPath(path1), fs.getPath(path2)));
    }

    @Test
    public void isSymbolicLink() {
        final String path = "path";
        // No symlinks
        Assertions.assertFalse(Files.isSymbolicLink(fs.getPath(path)));
    }

    @Test
    // All paths are writable
    public void isWritable() {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assertions.assertTrue(Files.isWritable(fs.getPath(path)));
    }

    @Test
    // No nonexistent paths are writable
    public void isWritableNonexistent() {
        final String path = "path";
        Assertions.assertFalse(Files.isWritable(fs.getPath(path)));
    }

    @Test
    public void move() throws IOException {
        final String source = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), source);
        final String dest = "newPath";
        final Path src = fs.getPath(source);
        final Path dst = fs.getPath(dest);
        final Path moved = Files.move(src, dst);
        Assertions.assertEquals(dest, moved.toString());

        try (final InputStreamReader inputStreamReader = new InputStreamReader(this.getArchive().get(dest).getAsset()
                .openStream());
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final String roundtrip = bufferedReader.readLine();
            Assertions.assertEquals(contents, roundtrip, "Contents not as expected after move");
        }
    }

    @Test
    public void moveDirectory() throws IOException {
        final String source = "dir";
        this.getArchive().addAsDirectory(source);
        final String dest = "newPath";
        final Path src = fs.getPath(source);
        final Path dst = fs.getPath(dest);
        final Path moved = Files.move(src, dst);
        Assertions.assertEquals(dest, moved.toString());
        Assertions.assertNull(this.getArchive().get(dest).getAsset(), "Directory expected after move");
    }

    @Test
    public void newBufferedReader() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);

        try (final BufferedReader reader = Files.newBufferedReader(fs.getPath(path), Charset.defaultCharset())) {
            final CharBuffer buffer = CharBuffer.allocate(contents.length());
            reader.read(buffer);
            buffer.position(0);
            Assertions.assertEquals(contents, buffer.toString(), "Contents not read as expected from the buffered reader");
        }
    }

    @Test
    public void newBufferedWriter() throws IOException {
        final String path = "path";
        final String contents = "contents";

        try (final BufferedWriter writer = Files.newBufferedWriter(fs.getPath(path), Charset.defaultCharset(),
                (OpenOption) null)) {
            writer.write(contents);
        }

        try (final InputStreamReader inputStreamReader = new InputStreamReader(this.getArchive().get(path).getAsset()
                .openStream());
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final String roundtrip = bufferedReader.readLine();
            Assertions.assertEquals(contents, roundtrip, "Contents not written as expected from the buffered writer");
        }
    }

    @Test
    public void newByteChannel() throws IOException {
        final String path = "path";
        final String contents = "ALR is putting some contents into here.";
        // Open for reading and writing
        try (final SeekableByteChannel channel = Files.newByteChannel(fs.getPath(path), StandardOpenOption.READ,
                StandardOpenOption.WRITE)) {
            final ByteBuffer writeBuffer = ByteBuffer.wrap(contents.getBytes());
            channel.write(writeBuffer);
            final ByteBuffer readBuffer = ByteBuffer.allocate(contents.length());
            channel.position(0);
            channel.read(readBuffer);
            final String roundtrip = new String(readBuffer.array());

            try (final InputStreamReader inputStreamReader = new InputStreamReader(this.getArchive().get(path)
                    .getAsset().openStream());
                 final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final String roundTripViaArchive = bufferedReader.readLine();
                Assertions.assertEquals(contents, roundtrip, "Contents not read as expected from the channel");
                Assertions.assertEquals(contents, roundTripViaArchive, "Contents not read as expected from the archive");
            }
        }
    }

    @Test
    public void newByteChannelForReadDoesntExist() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> Files.newByteChannel(fs.getPath("path"), (OpenOption) null));
    }

    @Test
    public void newDirectoryStream() throws IOException {
        final String dirs = "a/b/c/d/e";
        this.getArchive().addAsDirectories(dirs);
        try (final DirectoryStream<Path> stream = Files.newDirectoryStream(fs.getPath("/"))) {
            final Iterator<Path> paths = stream.iterator();
            int counter = 0;
            while (paths.hasNext()) {
                counter++;
                final Path path = paths.next();
                Assertions.assertTrue(this.getArchive().contains(path.toString()));
            }
            Assertions.assertEquals(1, counter);
        }
    }

    @Test
    public void newInputStream() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);
        final byte[] buffer = new byte[contents.length()];
        try (final InputStream in = Files.newInputStream(fs.getPath(path), StandardOpenOption.READ)) {
            in.read(buffer);
            final String roundtrip = new String(buffer);
            Assertions.assertEquals(contents, roundtrip, "Contents not read as expected from the instream");
        }
    }

    @Test
    public void newOutputStream() throws IOException {
        final String path = "path";
        final String contents = "contents";

        try (final OutputStream outStream = Files.newOutputStream(fs.getPath(path), StandardOpenOption.WRITE)) {
            outStream.write(contents.getBytes());
        }

        try (final InputStreamReader inputStreamReader = new InputStreamReader(this.getArchive().get(path).getAsset()
                .openStream());
             final BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            final String roundtrip = bufferedReader.readLine();
            Assertions.assertEquals(contents, roundtrip, "Contents not read as expected from the outstream");
        }
    }

    @Test
    public void notExistsTrue() {
        Assertions.assertTrue(Files.notExists(fs.getPath("fake"), LinkOption.NOFOLLOW_LINKS));
    }

    @Test
    public void notExistsFalse() {
        this.getArchive().add(EmptyAsset.INSTANCE, "path");
        Assertions.assertFalse(Files.notExists(fs.getPath("path"), LinkOption.NOFOLLOW_LINKS));
    }

    @Test
    public void probeContentType() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);
        // To be honest, I don't know WTF this is supposed to do, so we'll just check that it doesn't error out
        Assertions.assertNull(Files.probeContentType(fs.getPath(path)));
    }

    @Test
    public void readAllBytes() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);
        final byte[] bytes = Files.readAllBytes(fs.getPath(path));
        final String roundtrip = new String(bytes);
        Assertions.assertEquals(contents, roundtrip, "Contents not read as expected from the readAllBytes");
    }

    @Test
    public void createdDirectoryIsADirectory() throws Exception {
        Path dirPath = fs.getPath("dir");
        Files.createDirectory(dirPath);
        Assertions.assertTrue(Files.isDirectory(dirPath), "Created directory was not a directory");
    }

    @Test
    public void createdDirectoryCanBeStreamed() throws Exception {
        Path dirPath = fs.getPath("dir");
        Files.createDirectory(dirPath);
        Files.createFile(dirPath.resolve("file"));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            Iterator<Path> it = stream.iterator();
            Assertions.assertEquals("/dir/file", it.next().toString());
            Assertions.assertFalse(it.hasNext(), "No further elements expected in stream");
        }
    }

    @Test
    public void createdDirectoryStreamThrowsExceptionWhenIsClosed() throws Throwable {
        // given
        Path dirPath = fs.getPath("dir");
        Files.createDirectory(dirPath);
        Files.createFile(dirPath.resolve("file"));
        DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath);

        // when
        stream.close();

        // then
        Assertions.assertThrows(IllegalStateException.class, stream::iterator);
    }

    @Test
    public void createdDirectoryStreamThrowsExceptionWhenIteratorWasReturnedBefore() throws Exception {
        // given
        Path dirPath = fs.getPath("dir");
        Files.createDirectory(dirPath);
        Files.createFile(dirPath.resolve("file"));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath)) {
            // when
            Iterator<Path> it = stream.iterator();
            // then
            Assertions.assertThrows(IllegalStateException.class, stream::iterator);
        }
    }

    @Test
    public void directoryStreamDoesNotContainSubfolders() throws Exception {
        Files.createDirectories(fs.getPath("dir/subdir"));
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(fs.getPath("/"))) {
            Iterator<Path> it = stream.iterator();
            Assertions.assertEquals("/dir", it.next().toString());
            Assertions.assertFalse(it.hasNext(), "No further elements expected in stream");
        }
    }

    @Test
    public void createdDirectoryCanBeWalked() throws Exception {
        Path dirPath = fs.getPath("dir");
        Files.createDirectory(dirPath);
        Files.createFile(dirPath.resolve("file"));
        final int[] visitFileCalled = new int[1];
        Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                visitFileCalled[0]++;
                return super.visitFile(file, attrs);
            }
        });
        Assertions.assertEquals(1, visitFileCalled[0]);
    }

    /**
     * Gets the archive associated with the filesystem
     */
    private Archive<?> getArchive() {
        final ShrinkWrapFileSystem swfs = (ShrinkWrapFileSystem) this.fs;
        return swfs.getArchive();
    }

}

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.Charset;
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
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link FileSystem} is working as expected via the
 * {@link Files} convenience API.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class FilesTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(FilesTestCase.class.getName());

    /**
     * {@link FileSystem} under test
     */
    private FileSystem fs;

    @Before
    public void createFileSystem() throws IOException {
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        this.fs = ShrinkWrapFileSystems.newFileSystem(archive);
    }

    @After
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
        Assert.assertTrue(archive.contains(pathString));

        // Delete
        final Path path = fs.getPath(pathString);
        Files.delete(path);

        // Ensure deleted
        Assert.assertFalse(archive.contains(pathString));
    }

    @Test
    public void deleteNonexistant() throws IOException {

        final Archive<?> archive = this.getArchive();
        final String pathString = "nonexistant";

        // Ensure file doesn't exist
        Assert.assertFalse(archive.contains(pathString));

        // Attempt delete
        final Path path = fs.getPath(pathString);
        boolean gotException = false;
        try {
            Files.delete(path);
        } catch (final NoSuchFileException nsfe) {
            gotException = true;
        }
        Assert.assertTrue(
            "Request to remove nonexistant path should have thrown " + NoSuchFileException.class.getSimpleName(),
            gotException);
    }

    @Test
    public void deleteIfExists() throws IOException {

        // Backdoor add, because we only test delete here (not adding via the Files API)
        final Archive<?> archive = this.getArchive();
        final String pathString = "fileToDelete";
        archive.add(EmptyAsset.INSTANCE, pathString);

        // Ensure added
        Assert.assertTrue(archive.contains(pathString));

        // Delete
        final Path path = fs.getPath(pathString);
        final boolean deleted = Files.deleteIfExists(path);

        // Ensure deleted
        Assert.assertTrue("Did not report deleted", deleted);
    }

    @Test
    public void deleteIfExistsDoesntExist() throws IOException {
        final String pathString = "fileWhichDoesNotExist";
        final Path path = fs.getPath(pathString);
        final boolean deleted = Files.deleteIfExists(path);
        Assert.assertFalse("Should not report deleted", deleted);
    }

    @Test
    public void deleteDirectory() throws IOException {

        final String directoryName = "directory";
        final Archive<?> archive = this.getArchive().addAsDirectory(directoryName);

        // Preconditions
        Assert.assertNull("Test archive should contain the directory, not content", archive.get(directoryName)
            .getAsset());

        // Attempt delete
        final Path path = fs.getPath(directoryName);
        Files.delete(path);

        // Assertion
        Assert.assertFalse("Archive should no longer contain directory ", archive.contains(directoryName));
    }

    @Test
    public void deleteUnemptyDirectory() throws IOException {

        final String directoryName = "/directory";
        final String subDirectoryName = directoryName + "/subdir";
        final Archive<?> archive = this.getArchive().addAsDirectory(subDirectoryName);

        // Preconditions
        Assert.assertNull("Test archive should contain the directory, not content", archive.get(subDirectoryName)
            .getAsset());

        // Attempt delete
        final Path path = fs.getPath(directoryName);
        boolean gotException = false;
        try {
            Files.delete(path);
        } catch (final DirectoryNotEmptyException dnee) {
            gotException = true;
        }
        Assert.assertTrue("Should not be able to delete non-empty directory", gotException);
    }

    @Test
    public void createDirectory() throws IOException {
        final String dirName = "/newDirectory";
        final Path dir = fs.getPath(dirName);

        // Ensure dir doesn't exist
        final Archive<?> archive = this.getArchive();
        Assert.assertFalse(archive.contains(dirName));

        // Attempt create
        final Path createdDir = Files.createDirectory(dir, (FileAttribute<?>) null);
        Assert.assertTrue("Archive does not contain created directory", archive.contains(dirName));
        Assert.assertTrue("Created path is not a directory", archive.get(dirName).getAsset() == null);
        Assert.assertEquals("Created directory name was not as expected", dirName, createdDir.toString());
    }

    @Test
    public void createDirectoryRecursiveProhibited() throws IOException {
        final String dirName = "/newDirectory/child";
        final Path dir = fs.getPath(dirName);

        // Ensure dir doesn't exist
        final Archive<?> archive = this.getArchive();
        Assert.assertFalse(archive.contains(dirName));

        // Attempt create
        boolean gotException = false;
        try {
            Files.createDirectory(dir, (FileAttribute<?>) null);
        }
        // Just check for IOException, expected to be thrown via the NIO.2 API (wouldn't be *my* choice)
        catch (final IOException ioe) {
            gotException = true;
        }
        Assert.assertTrue("Should not be able to create directory unless parents are first present", gotException);
    }

    @Test
    public void createDirectoriesRecursive() throws IOException {
        final String dirName = "/newDirectory/child";
        final Path dir = fs.getPath(dirName);

        // Ensure dir doesn't exist
        final Archive<?> archive = this.getArchive();
        Assert.assertFalse(archive.contains(dirName));

        // Attempt create
        final Path createdDir = Files.createDirectories(dir, (FileAttribute<?>) null);
        Assert.assertTrue("Archive does not contain created directory", archive.contains(dirName));
        Assert.assertTrue("Created path is not a directory", archive.get(dirName).getAsset() == null);
        Assert.assertEquals("Created directory name was not as expected", dirName, createdDir.toString());
    }

    @Test
    public void copyFromInputStreamToPath() throws IOException {
        final String contents = "Hello, testing content writing!";
        final byte[] bytes = contents.getBytes("UTF-8");
        final InputStream in = new ByteArrayInputStream(bytes);
        final String pathName = "content";
        final Path path = fs.getPath(pathName);
        final long bytesCopied = Files.copy(in, path);
        final String roundtrip = new BufferedReader(new InputStreamReader(this.getArchive().get(pathName).getAsset()
            .openStream())).readLine();
        Assert.assertEquals("Contents after copy were not as expected", contents, roundtrip);
        Assert.assertEquals(bytes.length, bytesCopied);
    }

    @Test
    public void copyFromInputStreamToExistingPath() throws IOException {
        final InputStream in = new ByteArrayInputStream("test".getBytes("UTF-8"));
        final String pathName = "content";
        final Path path = fs.getPath(pathName);
        // Add some dummy asset to the archive
        this.getArchive().add(EmptyAsset.INSTANCE, pathName);
        // Now try to copy to the same path as the dummy asset
        boolean gotException = false;
        try {
            Files.copy(in, path);
        } catch (final FileAlreadyExistsException faee) {
            gotException = true;
        }
        Assert.assertTrue("Overwrite of existing path should fail", gotException);
    }

    @Test
    public void copyFromInputStreamToExistingDirectory() throws IOException {
        final InputStream in = new ByteArrayInputStream("test".getBytes("UTF-8"));
        final String pathName = "directory";
        final Path path = fs.getPath(pathName);
        // Add some directory to the archive
        this.getArchive().addAsDirectories(pathName);
        // Now try to copy to the same path as the dir
        boolean gotException = false;
        try {
            Files.copy(in, path);
        } catch (final FileAlreadyExistsException faee) {
            gotException = true;
        }
        Assert.assertTrue("Overwrite of existing directory should fail", gotException);
    }

    @Test
    public void copyFromInputStreamToExistingNonEmptyDirectoryWithReplaceExistingOption() throws IOException {
        final InputStream in = new ByteArrayInputStream("test".getBytes("UTF-8"));
        final String dir = "/directory";
        final String subdir = dir + "/subdir";
        final Path dirPath = fs.getPath(dir);
        // Add some nested directory to the archive
        this.getArchive().addAsDirectories(subdir);
        // Now try to copy to a nonempty dir
        boolean gotException = false;
        try {
            Files.copy(in, dirPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (final DirectoryNotEmptyException dnee) {
            gotException = true;
        }
        Assert.assertTrue("Overwrite of existing non-empty dir should fail, even with replace option", gotException);
    }

    @Test
    public void copyFromInputStreamToExistingPathWithOverwriteOption() throws IOException {
        final String contents = "Hello, testing content writing!";
        final byte[] bytes = contents.getBytes("UTF-8");
        final InputStream in = new ByteArrayInputStream(bytes);
        final String pathName = "content";
        final Path path = fs.getPath(pathName);
        // Add some dummy asset to the archive
        this.getArchive().add(EmptyAsset.INSTANCE, pathName);
        // Now try to copy to the same path as the dummy asset, using the option to overwrite
        final long bytesCopied = Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        final String roundtrip = new BufferedReader(new InputStreamReader(this.getArchive().get(pathName).getAsset()
            .openStream())).readLine();
        Assert.assertEquals("Contents after copy were not as expected", contents, roundtrip);
        Assert.assertEquals(bytes.length, bytesCopied);
    }

    @Test
    public void copyFromPathToOutputStream() throws IOException {
        // Populate the archive w/ content
        final String contents = "Here we're gonna test reading from the archive and writing the contents to an OutStream";
        final String path = "contentPath";
        this.getArchive().add(new StringAsset(contents), path);
        // Copy
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final long bytesCopied = Files.copy(fs.getPath(path), out);
        // Get out the content
        final String roundtrip = new String(out.toByteArray(), "UTF-8");
        Assert.assertEquals("Contents after copy were not as expected", contents, roundtrip);
        Assert.assertEquals(contents.length(), bytesCopied);
    }

    @Test
    public void copyFromDirectoryPathToOutputStream() throws IOException {
        // Populate the archive w/ content
        final String path = "dirPath";
        this.getArchive().addAsDirectories(path);
        // Attempt to copy the dir
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean gotException = false;
        try {
            Files.copy(fs.getPath(path), out);
        } catch (final IllegalArgumentException iae) {
            gotException = true;
        }
        Assert.assertTrue("Call to copy a directory contents to an outstream should not succeed", gotException);
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
        Files.write(path, appendContent.getBytes("UTF-8"), StandardOpenOption.APPEND);
        final String newContents = new BufferedReader(new InputStreamReader(archive.get(pathName).getAsset()
            .openStream())).readLine();
        Assert.assertEquals("New contents was not appended as expected", initialContent + appendContent, newContents);
    }

    @Test
    public void createFile() throws IOException {
        final String pathName = "fileToCreate";
        final Path path = fs.getPath(pathName);
        final Path newPath = Files.createFile(path, (FileAttribute<?>) null);
        Assert.assertEquals(path.toString(), newPath.toString());
    }

    @Test
    public void createFileWhenFileAlreadyExists() throws IOException {
        final String pathName = "fileToCreate";
        this.getArchive().add(EmptyAsset.INSTANCE, pathName);
        final Path path = fs.getPath(pathName);
        boolean gotException = false;
        try {
            Files.createFile(path, (FileAttribute<?>) null);
        } catch (final FileAlreadyExistsException faee) {
            gotException = true;
        }
        Assert.assertTrue("create new file should fail if path already exists", gotException);
    }

    @Test
    public void createLink() throws IOException {
        final String linkToCreateString = "linkToCreate";
        final String existingPathString = "existingPath";
        this.getArchive().add(EmptyAsset.INSTANCE, existingPathString);
        final Path linkToCreatePath = fs.getPath(linkToCreateString);
        final Path existingPath = fs.getPath(existingPathString);
        boolean gotException = false;
        try {
            Files.createLink(linkToCreatePath, existingPath);
        } catch (final UnsupportedOperationException ooe) {
            gotException = true;
        }
        Assert.assertTrue("We should not support creation of links", gotException);
    }

    @Test
    public void createSymbolicLink() throws IOException {
        final String symbolicLinkToCreateString = "linkToCreate";
        final String existingPathString = "existingPath";
        this.getArchive().add(EmptyAsset.INSTANCE, existingPathString);
        final Path symbolicLinkToCreatePath = fs.getPath(symbolicLinkToCreateString);
        final Path existingPath = fs.getPath(existingPathString);
        boolean gotException = false;
        try {
            Files.createSymbolicLink(symbolicLinkToCreatePath, existingPath);
        } catch (final UnsupportedOperationException ooe) {
            gotException = true;
        }
        Assert.assertTrue("We should not support creation of synbolic links", gotException);
    }

    // This will fail until we establish relative Paths
    @Test
    public void createTempDirectory() throws IOException {
        final String tempDir = "/tmp";
        this.getArchive().addAsDirectories(tempDir);
        final String prefix = "prefix";
        final Path tempDirPath = fs.getPath(tempDir);
        final Path newPath = Files.createTempDirectory(tempDirPath, prefix, new FileAttribute<?>[] {});
        Assert.assertTrue("temp dir name was not in expected form",
            newPath.toString().startsWith(tempDir + "/" + prefix));
    }

    @Test
    public void existsFalse() throws IOException {
        final String pathString = "fileWhichDoesNotExist";
        final Path path = fs.getPath(pathString);
        final boolean exists = Files.exists(path);
        Assert.assertFalse("Should report exists", exists);
    }

    @Test
    public void existsTrue() throws IOException {
        final Archive<?> archive = this.getArchive();
        final String pathString = "file";
        archive.add(EmptyAsset.INSTANCE, pathString);
        final Path path = fs.getPath(pathString);
        final boolean exists = Files.exists(path);
        Assert.assertTrue("Should report exists", exists);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAttribute() throws IOException {
        Files.getAttribute(fs.getPath("file"), "basic", (LinkOption) null);
    }

    @Test
    public void getFileAttributeView() throws IOException {
        Assert.assertTrue(Files.getFileAttributeView(fs.getPath("file"), BasicFileAttributeView.class,
            (LinkOption) null) instanceof ShrinkWrapFileAttributeView);
    }

    @Test
    public void getFileStore() throws IOException {
        Assert.assertTrue("Returned file store was not as expected",
            fs.getFileStores().iterator().next() == Files.getFileStore(fs.getPath("path")));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getLastModifiedTime() throws IOException {
        this.getArchive().add(EmptyAsset.INSTANCE, "path");
        Files.getLastModifiedTime(fs.getPath("path"), (LinkOption) null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getOwner() throws IOException {
        Files.getOwner(fs.getPath("path"), (LinkOption) null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getPosixFilePermissions() throws IOException {
        Files.getPosixFilePermissions(fs.getPath("path"), (LinkOption) null);
    }

    @Test
    public void isDirectoryTrue() throws IOException {
        this.getArchive().addAsDirectories("path");
        Assert.assertTrue(Files.isDirectory(fs.getPath("path"), (LinkOption) null));
    }

    @Test
    public void isDirectoryFalse() throws IOException {
        Assert.assertFalse(Files.isDirectory(fs.getPath("path"), (LinkOption) null));
    }

    @Test
    // All paths are executable
    public void isExecutable() throws IOException {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assert.assertTrue(Files.isExecutable(fs.getPath(path)));
    }

    @Test
    // No paths are hidden
    public void isHidden() throws IOException {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assert.assertFalse(Files.isHidden(fs.getPath(path)));
    }

    @Test
    // All paths are readable
    public void isReadable() throws IOException {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assert.assertTrue(Files.isReadable(fs.getPath(path)));
    }

    @Test
    // No nonexistant paths are executable
    public void isExecutableNonexistant() throws IOException {
        final String path = "path";
        Assert.assertFalse(Files.isExecutable(fs.getPath(path)));
    }

    @Test
    // No nonexistant paths are readable
    public void isReadableNonexistant() throws IOException {
        final String path = "path";
        Assert.assertFalse(Files.isReadable(fs.getPath(path)));
    }

    @Test
    public void isRegularFile() throws IOException {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assert.assertTrue(Files.isRegularFile(fs.getPath(path)));
    }

    @Test
    public void isRegularFileFalse() throws IOException {
        final String path = "path/";
        Assert.assertFalse(Files.isRegularFile(fs.getPath(path)));
    }

    @Test
    public void isSameFile() throws IOException {
        final String path1 = "path/sub";
        final String path2 = "path/sub";
        Assert.assertTrue(Files.isSameFile(fs.getPath(path1), fs.getPath(path2)));
    }

    @Test
    public void isSameFileFalse() throws IOException {
        final String path1 = "path/sub";
        final String path2 = "path/sub/notsame";
        Assert.assertFalse(Files.isSameFile(fs.getPath(path1), fs.getPath(path2)));
    }

    @Test
    public void isSymbolicLink() throws IOException {
        final String path = "path";
        // No symlinks
        Assert.assertFalse(Files.isSymbolicLink(fs.getPath(path)));
    }

    @Test
    // All paths are writable
    public void isWritable() throws IOException {
        final String path = "path";
        this.getArchive().add(EmptyAsset.INSTANCE, path);
        Assert.assertTrue(Files.isWritable(fs.getPath(path)));
    }

    @Test
    // No nonexistant paths are writable
    public void isWritableNonexistant() throws IOException {
        final String path = "path";
        Assert.assertFalse(Files.isWritable(fs.getPath(path)));
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
        Assert.assertEquals(dest, moved.toString());
        final String roundtrip = new BufferedReader(new InputStreamReader(this.getArchive().get(dest).getAsset()
            .openStream())).readLine();
        Assert.assertEquals("Contents not as expected after move", contents, roundtrip);
    }

    @Test
    public void moveDirectory() throws IOException {
        final String source = "dir";
        this.getArchive().addAsDirectory(source);
        final String dest = "newPath";
        final Path src = fs.getPath(source);
        final Path dst = fs.getPath(dest);
        final Path moved = Files.move(src, dst);
        Assert.assertEquals(dest, moved.toString());
        Assert.assertNull("Directory expected after move", this.getArchive().get(dest).getAsset());
    }

    @Test
    public void newBufferedReader() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);
        final BufferedReader reader = Files.newBufferedReader(fs.getPath(path), Charset.defaultCharset());
        final CharBuffer buffer = CharBuffer.allocate(contents.length());
        reader.read(buffer);
        reader.close();
        buffer.position(0);
        Assert.assertEquals("Contents not read as expected from the buffered reader", contents, buffer.toString());
    }

    @Test
    public void newBufferedWriter() throws IOException {
        final String path = "path";
        final String contents = "contents";
        final BufferedWriter writer = Files.newBufferedWriter(fs.getPath(path), Charset.defaultCharset(),
            (OpenOption) null);
        writer.write(contents);
        writer.close();
        final String roundtrip = new BufferedReader(new InputStreamReader(this.getArchive().get(path).getAsset()
            .openStream())).readLine();
        Assert.assertEquals("Contents not written as expected from the buffered writer", contents, roundtrip);
    }

    @Test
    public void newByteChannel() throws IOException {
        final String path = "path";
        final String contents = "ALR is putting some contents into here.";
        // Open for reading and writing
        final SeekableByteChannel channel = Files.newByteChannel(fs.getPath(path), StandardOpenOption.READ,
            StandardOpenOption.WRITE);
        final ByteBuffer writeBuffer = ByteBuffer.wrap(contents.getBytes());
        channel.write(writeBuffer);
        final ByteBuffer readBuffer = ByteBuffer.allocate(contents.length());
        channel.position(0);
        channel.read(readBuffer);
        final String roundtrip = new String(readBuffer.array());
        final String roundTripViaArchive = new BufferedReader(new InputStreamReader(this.getArchive().get(path)
            .getAsset().openStream())).readLine();
        Assert.assertEquals("Contents not read as expected from the channel", contents, roundtrip);
        Assert.assertEquals("Contents not read as expected from the archive", contents, roundTripViaArchive);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newByteChannelForReadDoesntExist() throws IOException {
        Files.newByteChannel(fs.getPath("path"), (OpenOption) null);
    }

    @Test
    public void newDirectoryStream() throws IOException {
        final String dirs = "a/b/c/d/e";
        this.getArchive().addAsDirectories(dirs);
        final DirectoryStream<Path> stream = Files.newDirectoryStream(fs.getPath("/"));
        final Iterator<Path> paths = stream.iterator();
        int counter = 0;
        while (paths.hasNext()) {
            counter++;
            final Path path = paths.next();
            Assert.assertTrue(this.getArchive().contains(path.toString()));
        }
        Assert.assertEquals(1, counter);
    }

    @Test
    public void newInputStream() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);
        final InputStream in = Files.newInputStream(fs.getPath(path), StandardOpenOption.READ);
        final byte[] buffer = new byte[contents.length()];
        in.read(buffer);
        in.close();
        final String roundtrip = new String(buffer);
        Assert.assertEquals("Contents not read as expected from the instream", contents, roundtrip);
    }

    @Test
    public void newOutputStream() throws IOException {
        final String path = "path";
        final String contents = "contents";
        final OutputStream outStream = Files.newOutputStream(fs.getPath(path), StandardOpenOption.WRITE);
        outStream.write(contents.getBytes());
        outStream.close();
        final String roundtrip = new BufferedReader(new InputStreamReader(this.getArchive().get(path).getAsset()
            .openStream())).readLine();
        Assert.assertEquals("Contents not read as expected from the outstream", contents, roundtrip);
    }

    @Test
    public void notExistsTrue() throws IOException {
        Assert.assertTrue(Files.notExists(fs.getPath("fake"), LinkOption.NOFOLLOW_LINKS));
    }

    @Test
    public void notExistsFalse() throws IOException {
        this.getArchive().add(EmptyAsset.INSTANCE, "path");
        Assert.assertFalse(Files.notExists(fs.getPath("path"), LinkOption.NOFOLLOW_LINKS));
    }

    @Test
    public void probeContentType() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);
        // To be honest, I don't know WTF this is supposed to do, so we'll just check that it doesn't error out
        Assert.assertNull(Files.probeContentType(fs.getPath(path)));
    }

    @Test
    public void readAllBytes() throws IOException {
        final String path = "path";
        final String contents = "contents";
        this.getArchive().add(new StringAsset(contents), path);
        final byte[] bytes = Files.readAllBytes(fs.getPath(path));
        final String roundtrip = new String(bytes);
        Assert.assertEquals("Contents not read as expected from the readAllBytes", contents, roundtrip);
    }

    @Test
    public void createdDirectoryIsADirectory() throws Exception {
        Path dirPath = fs.getPath("dir");
        Files.createDirectory(dirPath);
        Assert.assertTrue("Created directory was not a directory", Files.isDirectory(dirPath));
    }

    @Test
    public void createdDirectoryCanBeStreamed() throws Exception {
        Path dirPath = fs.getPath("dir");
        Files.createDirectory(dirPath);
        Files.createFile(dirPath.resolve("file"));
        DirectoryStream<Path> stream = Files.newDirectoryStream(dirPath);
        Iterator<Path> it = stream.iterator();
        Assert.assertEquals("/dir/file", it.next().toString());
        Assert.assertFalse("No further elements expected in stream", it.hasNext());
    }

    @Test
    public void directoryStreamDoesNotContainSubfolders() throws Exception {
        Files.createDirectories(fs.getPath("dir/subdir"));
        DirectoryStream<Path> stream = Files.newDirectoryStream(fs.getPath("/"));
        Iterator<Path> it = stream.iterator();
        Assert.assertEquals("/dir", it.next().toString());
        Assert.assertFalse("No further elements expected in stream", it.hasNext());
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
        Assert.assertEquals(1, visitFileCalled[0]);
    }

    /**
     * Gets the archive associated with the filesystem
     *
     * @return
     */
    private Archive<?> getArchive() {
        final ShrinkWrapFileSystem swfs = (ShrinkWrapFileSystem) this.fs;
        final Archive<?> archive = swfs.getArchive();
        return archive;
    }

}

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.Iterator;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link Path} is working as contracted.
 * 
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class PathTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PathTestCase.class.getName());

    private ShrinkWrapFileSystem fileSystem;

    @Before
    public void createFileSystem() throws URISyntaxException, IOException {

        // Setup and mount the archive
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        final ShrinkWrapFileSystem fs = (ShrinkWrapFileSystem) ShrinkWrapFileSystems.newFileSystem(archive);
        this.fileSystem = fs;
    }

    @After
    public void closeFs() throws IOException {
        this.fileSystem.close();
    }

    @Test
    public void rootIsAbsolute() {
        final Path path = fileSystem.getPath("/");
        Assert.assertTrue("Root path must be absolute", path.isAbsolute());
        Assert.assertEquals("Root path should be equal to root archive path value", path.toString(), ArchivePaths
            .root().get());
    }

    @Test
    public void getFileSystem() {
        final Path path = fileSystem.getPath("/");
        Assert.assertEquals("FileSystem not obtained correctly via Path", fileSystem, path.getFileSystem());
    }

    @Test
    public void getPathEmptyPath() {
        final String empty = "";
        final Path path = fileSystem.getPath(empty);
        Assert.assertEquals("Empty path should be resolved to empty path value", empty, path.toString());
    }

    @Test
    public void getFileNameEmptyPath() {
        final String empty = "";
        final Path path = fileSystem.getPath(empty);
        Assert.assertEquals("Empty path should return null file name", null, path.getFileName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPathNullPath() {
        fileSystem.getPath(null);
    }

    @Test
    public void getPathRelative() {
        final String relative = "relative";
        final Path path = fileSystem.getPath(relative);
        Assert.assertFalse("Relative paths must not be adjusted to absolute", path.isAbsolute());
        Assert.assertEquals("Relative input was not resolved to path as expected", relative, path.toString());
    }

    @Test
    public void getRoot() {
        final Path path = fileSystem.getPath("/someNode");
        final Path root = path.getRoot();
        Assert.assertEquals("Did not return correct root", root.toString(), "/");
    }

    @Test
    public void getRootFromRelative() {
        final Path path = fileSystem.getPath("someNode");
        final Path root = path.getRoot();
        Assert.assertNull("Relative path should have null root", root);
    }

    @Test
    public void getRootFromNested() {
        final Path path = fileSystem.getPath("/someNode/child");
        final Path root = path.getRoot();
        Assert.assertEquals("Did not return correct root", root.toString(), "/");
    }

    @Test
    public void getParent() {
        final Path path = fileSystem.getPath("parent/child");
        final Path parent = path.getParent();
        Assert.assertEquals("Did not return correct parent", parent.toString(), "parent");
    }

    @Test
    public void getParentRelative() {
        final Path path = fileSystem.getPath("relativeName");
        final Path parent = path.getParent();
        Assert.assertNull("Single node relative paths should have no parent", parent);
    }

    @Test
    public void getParentNested() {
        final Path path = fileSystem.getPath("parent/child/grandchild");
        final Path parent = path.getParent();
        Assert.assertEquals("Did not return correct parent", parent.toString(), "parent/child");
    }

    @Test
    public void getParentAbsolute() {
        final Path path = fileSystem.getPath("/parent/child/grandchild");
        final Path parent = path.getParent();
        Assert.assertEquals("Did not return correct parent", parent.toString(), "/parent/child");
    }

    @Test
    public void getParentRootIsNull() {
        final Path path = fileSystem.getPath("/");
        final Path parent = path.getParent();
        Assert.assertNull("Parent of root should be null", parent);
    }

    @Test
    public void getFileName() {
        final String location = "/dir/nestedDir/";
        final String fileNameString = "fileName";
        final Path path = fileSystem.getPath(location + fileNameString);
        final Path fileName = path.getFileName();
        Assert.assertEquals("File name was not as expected", fileNameString, fileName.toString());
    }

    @Test
    public void getRootFileName() {
        final Path path = fileSystem.getPath("/");
        final Path fileName = path.getFileName();
        Assert.assertNull("Root file name should be null", fileName);
    }

    @Test
    public void getRootNameCount() {
        final Path path = fileSystem.getPath("/");
        final int count = path.getNameCount();
        Assert.assertEquals("Root should have no name count", 0, count);
    }

    @Test
    public void getTopLevelNameCount() {
        final Path path = fileSystem.getPath("/toplevel");
        final int count = path.getNameCount();
        Assert.assertEquals("Top-level element should have name count 1", 1, count);
    }

    @Test
    public void getTopLevelAppendedSlashNameCount() {
        final Path path = fileSystem.getPath("/toplevel/");
        final int count = path.getNameCount();
        Assert.assertEquals("Top-level element should have name count 1", 1, count);
    }

    @Test
    public void getTopLevelNoPrecedingSlashNameCount() {
        final Path path = fileSystem.getPath("toplevel/");
        final int count = path.getNameCount();
        Assert.assertEquals("Top-level element should have name count 1", 1, count);
    }

    @Test
    public void nestedNameCount() {
        final Path path = fileSystem.getPath("toplevel/nested");
        final int count = path.getNameCount();
        Assert.assertEquals("nested-level element should have name count 2", 2, count);
    }

    @Test
    public void toAbsolutePath() {
        final Path path = fileSystem.getPath("toplevel");
        Assert.assertEquals("toAbsolute should return the absolute form of the Path", "/toplevel", path
            .toAbsolutePath().toString());
    }

    @Test
    public void toAbsolutePathAlreadyAbsolute() {
        final String absolutePath = "/absolute";
        final Path path = fileSystem.getPath(absolutePath);
        Assert.assertEquals("toAbsolute should return the absolute form of the Path", absolutePath, path
            .toAbsolutePath().toString());
    }

    @Test
    public void isAbsolute() {
        final Path path = fileSystem.getPath("/absolute");
        Assert.assertTrue(path.isAbsolute());
    }

    @Test
    public void isAbsoluteFalse() {
        final Path path = fileSystem.getPath("relative");
        Assert.assertFalse(path.isAbsolute());
    }

    @Test
    public void iterator() {
        final Path path = fileSystem.getPath("toplevel/second/third/fourth");
        final Iterator<Path> paths = path.iterator();
        Assert.assertEquals("/toplevel", paths.next().toString());
        Assert.assertEquals("/toplevel/second", paths.next().toString());
        Assert.assertEquals("/toplevel/second/third", paths.next().toString());
        Assert.assertEquals("/toplevel/second/third/fourth", paths.next().toString());
    }

    @Test
    public void iteratorRoot() {
        final Path path = fileSystem.getPath("/");
        final Iterator<Path> paths = path.iterator();
        Assert.assertFalse("Iterator should not return root element", paths.hasNext());
    }

    @Test
    public void toUri() {
        final Path path = fileSystem.getPath("/toplevel/second");
        final URI uri = path.toUri();
        final String expected = ShrinkWrapFileSystems.PROTOCOL + "://" + fileSystem.getArchive().getId()
            + path.toString();
        Assert.assertEquals("toUri did not return form as expected", expected, uri.toString());
    }

    @Test
    public void getName() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final Path second = path.getName(2);
        Assert.assertEquals("/toplevel/second/third", second.toString());
    }

    @Test
    public void getNameRoot() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final Path second = path.getName(0);
        Assert.assertEquals("/toplevel", second.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathNegativeBegin() {
        fileSystem.getPath("/toplevel/second/third").subpath(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathNegativeEnd() {
        fileSystem.getPath("/toplevel/second/third").subpath(0, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathEndBeforeBegin() {
        fileSystem.getPath("/toplevel/second/third").subpath(2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathBeginTooLarge() {
        fileSystem.getPath("/toplevel/second/third").subpath(4, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathEndTooLarge() {
        fileSystem.getPath("/toplevel/second/third").subpath(2, 4);
    }

    @Test
    public void subpath() {
        final Path subpath = fileSystem.getPath("/toplevel/second/third").subpath(1, 2);
        Assert.assertEquals("/toplevel/second", subpath.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void startsWithNullPathInput() {
        fileSystem.getPath("/toplevel/second/third").startsWith((Path) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void startsWithNullStringInput() {
        fileSystem.getPath("/toplevel/second/third").startsWith((String) null);
    }

    @Test
    public void startsWithOtherPathImpl() {
        final boolean startsWith = fileSystem.getPath("/toplevel/second/third").startsWith(new MockPath());
        Assert.assertFalse(startsWith);
    }

    /**
     * Path cannot start with a path from another FS
     */
    @Test
    public void startsWithOtherFs() throws IOException {
        final FileSystem otherFs = ShrinkWrapFileSystems.newFileSystem(ShrinkWrap.create(GenericArchive.class));
        final String pathName = "/toplevel/second";
        final Path otherPath = otherFs.getPath(pathName);
        final boolean startsWith = fileSystem.getPath(pathName).startsWith(otherPath);
        Assert.assertFalse(startsWith);
    }

    @Test
    public void startsWith() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/toplevel/second/"));
        Assert.assertTrue(startsWith);
    }

    @Test
    public void startsWithRelative() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("toplevel/second/"));
        Assert.assertTrue(startsWith);
    }

    @Test
    public void startsWithAbsolute() {
        final Path path = fileSystem.getPath("toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/toplevel/second/third"));
        Assert.assertFalse("Other absolute pah and this relative path cannot match startsWith", startsWith);
    }

    @Test
    public void startsWithString() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith("/toplevel/second/");
        Assert.assertTrue(startsWith);
    }

    @Test
    public void startsWithNegative() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/top"));
        Assert.assertFalse(startsWith);
    }

    @Test
    public void startsWithBiggerThan() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/toplevel/second/third/fourth"));
        Assert.assertFalse(startsWith);
    }

    // We don't interface w/ File API
    @Test(expected = UnsupportedOperationException.class)
    public void toFile() {
        fileSystem.getPath("/toplevel").toFile();
    }

    // We don't support events
    @Test(expected = UnsupportedOperationException.class)
    public void register() throws IOException {
        fileSystem.getPath("/toplevel").register(null, (Kind<?>) null);
    }

    // We don't support events
    @Test(expected = UnsupportedOperationException.class)
    public void registerLongform() throws IOException {
        fileSystem.getPath("/toplevel").register(null, (Kind<?>) null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void endsWithNullPathInput() {
        fileSystem.getPath("/toplevel/second/third").endsWith((Path) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void endsWithNullStringInput() {
        fileSystem.getPath("/toplevel/second/third").endsWith((String) null);
    }

    @Test
    public void endsWithOtherPathImpl() {
        final boolean endsWith = fileSystem.getPath("/toplevel/second/third").endsWith(new MockPath());
        Assert.assertFalse(endsWith);
    }

    /**
     * Path cannot start with a path from another FS
     */
    @Test
    public void endsWithOtherFs() throws IOException {
        final FileSystem otherFs = ShrinkWrapFileSystems.newFileSystem(ShrinkWrap.create(GenericArchive.class));
        final Path otherPath = otherFs.getPath("/otherpath");
        final boolean endsWith = fileSystem.getPath("/toplevel/second/third").endsWith(otherPath);
        Assert.assertFalse(endsWith);
    }

    @Test
    public void endsWith() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("third"));
        Assert.assertTrue("Should have identified path ends with last name component", endsWith);
    }

    @Test
    public void endsString() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("third").toString());
        Assert.assertTrue("Should have identified path ends with last name component", endsWith);
    }

    @Test
    public void endsWithNested() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("second/third"));
        Assert.assertTrue("Should have identified path ends with last and penultimate name component", endsWith);
    }

    @Test
    public void endsWithPartial() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("ird"));
        Assert.assertFalse(endsWith);
    }

    @Test
    public void endsWithIncorrectRoot() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("/third"));
        Assert.assertFalse(endsWith);
    }

    @Test
    public void endsWithNegative() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("/toplevel"));
        Assert.assertFalse(endsWith);
    }

    @Test
    public void endsWithBiggerThan() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("/toplevel/second/third/fourth"));
        Assert.assertFalse(endsWith);
    }

    @Test
    public void toRealPath() throws IOException {
        final String newPathName = "/toplevel/myAsset";
        this.fileSystem.getArchive().add(EmptyAsset.INSTANCE, newPathName);
        final Path path = fileSystem.getPath(newPathName);
        final Path realPath = path.toRealPath((LinkOption[]) null);
        Assert.assertEquals(path.toString(), realPath.toString());
    }

    @Test(expected = FileNotFoundException.class)
    public void toRealPathDoesntExist() throws IOException {
        final String newPathName = "/toplevel/myAsset";
        final Path path = fileSystem.getPath(newPathName);
        path.toRealPath((LinkOption[]) null);
    }

    @Test
    public void normalizeNoop() {
        final Path path = fileSystem.getPath("/a/b");
        final Path normalized = path.normalize();
        Assert.assertEquals(path.toString(), normalized.toString());
    }

    @Test
    public void normalizeRelative() {
        final Path path = fileSystem.getPath("a/b");
        final Path normalized = path.normalize();
        Assert.assertEquals(path.toString(), normalized.toString());
    }

    @Test
    public void normalizeCurrentDirRefs() {
        final Path path = fileSystem.getPath("/a/./b/./c/d/./e");
        final Path normalized = path.normalize();
        Assert.assertEquals("/a/b/c/d/e", normalized.toString());
    }

    @Test
    public void normalizeBackDirRefs() {
        final Path path = fileSystem.getPath("/a/../b/./c/d/../e");
        final Path normalized = path.normalize();
        Assert.assertEquals("/b/c/e", normalized.toString());
    }

    @Test
    public void relativizeSuperset() {
        final Path path = fileSystem.getPath("/a/b");
        final Path other = fileSystem.getPath("/a/b/c/d");
        final Path relativized = path.relativize(other);
        Assert.assertEquals("c/d", relativized.toString());
    }

    @Test
    public void relativizeSuperset2() {
        final Path path = fileSystem.getPath("/a/b/c");
        final Path other = fileSystem.getPath("/a/b/c/d");
        final Path relativized = path.relativize(other);
        Assert.assertEquals("d", relativized.toString());
    }

    @Test
    public void relativizeCommonNode() {
        final Path path = fileSystem.getPath("/a/b");
        final Path other = fileSystem.getPath("/a/c");
        final Path relativized = path.relativize(other);
        Assert.assertEquals("../c", relativized.toString());
    }

    @Test
    public void relativizeCommonNode2() {
        final Path path = fileSystem.getPath("/a/b");
        final Path other = fileSystem.getPath("/a/c/d");
        final Path relativized = path.relativize(other);
        Assert.assertEquals("../c/d", relativized.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void relativizeNull() {
        final Path path = fileSystem.getPath("/toplevel/myAsset");
        path.relativize(null);
    }

    @Test
    public void resolve() {
        final Path path = fileSystem.getPath("/topLevel/secondLevel");
        final Path other = fileSystem.getPath("thirdLevel");
        final Path resolved = path.resolve(other);
        Assert.assertEquals("Resolve should join other to this", "/topLevel/secondLevel/thirdLevel",
            resolved.toString());
    }

    @Test
    public void resolveTrailingSlashOnThis() {
        final Path path = fileSystem.getPath("/topLevel/secondLevel/");
        final Path other = fileSystem.getPath("thirdLevel");
        final Path resolved = path.resolve(other);
        Assert.assertEquals("Resolve should join other to this", "/topLevel/secondLevel/thirdLevel",
            resolved.toString());
    }

    @Test
    public void resolveEmpty() {
        final String thisLocation = "/topLevel/secondLevel";
        final Path path = fileSystem.getPath(thisLocation);
        final Path other = fileSystem.getPath("");
        final Path resolved = path.resolve(other);
        Assert.assertEquals("Resolve of empty path should return this path", thisLocation, resolved.toString());
    }

    @Test
    public void resolveAbsolute() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final Path other = fileSystem.getPath("/toplevel/myDir/myAsset");
        final Path resolved = path.resolve(other);
        // Since absolute, by spec return the value of the other path
        Assert.assertEquals(other.toString(), resolved.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveNull() {
        fileSystem.getPath("/toplevel/myDir/").resolve((Path) null);
    }

    @Test
    public void resolveString() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final String otherName = "/toplevel/myDir/myAsset";
        final Path other = fileSystem.getPath(otherName);
        final Path resolved = path.resolve(otherName);
        // Since absolute, by spec return the value of the other path
        Assert.assertEquals(other.toString(), resolved.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveNullString() {
        fileSystem.getPath("/toplevel/myDir/").resolve((String) null);
    }

    /**
     * This case outlined in {@link Path#relativize(Path)} APIDocs:
     * 
     * For any two normalized paths p and q, where q does not have a root component,
     * 
     * <code>  p.relativize(p.resolve(q)).equals(q) </code>
     */
    @Test
    public void roundtripResolveRelativizeOfNormalizedPaths() {
        final Path path = fileSystem.getPath("/a/b/f/../");
        final Path other = fileSystem.getPath("a/b/../c");
        final Path pathNormalized = path.normalize();
        final Path otherNormalized = other.normalize();
        Assert.assertTrue("Failed check that relativize undoes resolve, taking into account normalization",
            (pathNormalized.relativize(pathNormalized.resolve(otherNormalized)).equals(otherNormalized)));

    }

    @Test
    public void resolveSibling() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final Path other = fileSystem.getPath("/toplevel/myDir/myAsset");
        final Path resolved = path.resolveSibling(other);
        // Since absolute, by spec return the value of the other path
        Assert.assertEquals(other.toString(), resolved.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveSiblingNull() {
        fileSystem.getPath("/toplevel/myDir/").resolveSibling((Path) null);
    }

    @Test
    public void resolveSiblingString() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final String otherName = "/toplevel/myDir/myAsset";
        final Path other = fileSystem.getPath(otherName);
        final Path resolved = path.resolveSibling(otherName);
        // Since absolute, by spec return the value of the other path
        Assert.assertEquals(other.toString(), resolved.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveSublingNullString() {
        fileSystem.getPath("/toplevel/myDir/").resolveSibling((String) null);
    }

    @Test
    public void compareToGreaterValue() {
        final Path path = fileSystem.getPath("/toplevel/a");
        final String otherName = "/toplevel/b";
        final Path other = fileSystem.getPath(otherName);
        final int compare = path.compareTo(other);
        Assert.assertEquals(-1, compare);
    }

    @Test
    public void compareToLesserValue() {
        final Path path = fileSystem.getPath("/toplevel/b");
        final String otherName = "/toplevel/a";
        final Path other = fileSystem.getPath(otherName);
        final int compare = path.compareTo(other);
        Assert.assertEquals(1, compare);
    }

    @Test
    public void compareToEqualValue() {
        final Path path = fileSystem.getPath("/toplevel/a");
        final String otherName = "/toplevel/a";
        final Path other = fileSystem.getPath(otherName);
        final int compare = path.compareTo(other);
        Assert.assertEquals(0, compare);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareToNull() throws IOException {
        fileSystem.getPath("/toplevel/a").compareTo(null);
    }
}

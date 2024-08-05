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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.Iterator;

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.nio2.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link Path} is working as contracted.
 * 
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class PathTestCase {

    private ShrinkWrapFileSystem fileSystem;

    @BeforeEach
    public void createFileSystem() throws IOException {

        // Setup and mount the archive
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        final ShrinkWrapFileSystem fs = (ShrinkWrapFileSystem) ShrinkWrapFileSystems.newFileSystem(archive);
        this.fileSystem = fs;
    }

    @AfterEach
    public void closeFs() {
        this.fileSystem.close();
    }

    @Test
    public void rootIsAbsolute() {
        final Path path = fileSystem.getPath("/");
        Assertions.assertTrue(path.isAbsolute(), "Root path must be absolute");
        Assertions.assertEquals(path.toString(), ArchivePaths.root().get(),
                "Root path should be equal to root archive path value");
    }

    @Test
    public void getFileSystem() {
        final Path path = fileSystem.getPath("/");
        Assertions.assertEquals(fileSystem, path.getFileSystem(), "FileSystem not obtained correctly via Path");
    }

    @Test
    public void getPathEmptyPath() {
        final String empty = "";
        final Path path = fileSystem.getPath(empty);
        Assertions.assertEquals(empty, path.toString(), "Empty path should be resolved to empty path value");
    }

    @Test
    public void getFileNameEmptyPath() {
        final String empty = "";
        final Path path = fileSystem.getPath(empty);
        Assertions.assertNull(path.getFileName(), "Empty path should return null file name");
    }

    @Test
    public void getPathNullPath() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> fileSystem.getPath(null));
    }

    @Test
    public void getPathRelative() {
        final String relative = "relative";
        final Path path = fileSystem.getPath(relative);
        Assertions.assertFalse(path.isAbsolute(), "Relative paths must not be adjusted to absolute");
        Assertions.assertEquals(relative, path.toString(), "Relative input was not resolved to path as expected");
    }

    @Test
    public void getRoot() {
        final Path path = fileSystem.getPath("/someNode");
        final Path root = path.getRoot();
        Assertions.assertEquals(root.toString(), "/", "Did not return correct root");
    }

    @Test
    public void getRootFromRelative() {
        final Path path = fileSystem.getPath("someNode");
        final Path root = path.getRoot();
        Assertions.assertNull(root, "Relative path should have null root");
    }

    @Test
    public void getRootFromNested() {
        final Path path = fileSystem.getPath("/someNode/child");
        final Path root = path.getRoot();
        Assertions.assertEquals(root.toString(), "/", "Did not return correct root");
    }

    @Test
    public void getParent() {
        final Path path = fileSystem.getPath("parent/child");
        final Path parent = path.getParent();
        Assertions.assertEquals(parent.toString(), "parent", "Did not return correct parent");
    }

    @Test
    public void getParentRelative() {
        final Path path = fileSystem.getPath("relativeName");
        final Path parent = path.getParent();
        Assertions.assertNull(parent, "Single node relative paths should have no parent");
    }

    @Test
    public void getParentNested() {
        final Path path = fileSystem.getPath("parent/child/grandchild");
        final Path parent = path.getParent();
        Assertions.assertEquals(parent.toString(), "parent/child", "Did not return correct parent");
    }

    @Test
    public void getParentAbsolute() {
        final Path path = fileSystem.getPath("/parent/child/grandchild");
        final Path parent = path.getParent();
        Assertions.assertEquals(parent.toString(), "/parent/child", "Did not return correct parent");
    }

    @Test
    public void getParentRootIsNull() {
        final Path path = fileSystem.getPath("/");
        final Path parent = path.getParent();
        Assertions.assertNull(parent, "Parent of root should be null");
    }

    @Test
    public void getFileName() {
        final String location = "/dir/nestedDir/";
        final String fileNameString = "fileName";
        final Path path = fileSystem.getPath(location + fileNameString);
        final Path fileName = path.getFileName();
        Assertions.assertEquals(fileNameString, fileName.toString(), "File name was not as expected");
    }

    @Test
    public void getRootFileName() {
        final Path path = fileSystem.getPath("/");
        final Path fileName = path.getFileName();
        Assertions.assertNull(fileName, "Root file name should be null");
    }

    @Test
    public void getRootNameCount() {
        final Path path = fileSystem.getPath("/");
        final int count = path.getNameCount();
        Assertions.assertEquals(0, count, "Root should have no name count");
    }

    @Test
    public void getTopLevelNameCount() {
        final Path path = fileSystem.getPath("/toplevel");
        final int count = path.getNameCount();
        Assertions.assertEquals(1, count, "Top-level element should have name count 1");
    }

    @Test
    public void getTopLevelAppendedSlashNameCount() {
        final Path path = fileSystem.getPath("/toplevel/");
        final int count = path.getNameCount();
        Assertions.assertEquals(1, count, "Top-level element should have name count 1");
    }

    @Test
    public void getTopLevelNoPrecedingSlashNameCount() {
        final Path path = fileSystem.getPath("toplevel/");
        final int count = path.getNameCount();
        Assertions.assertEquals(1, count, "Top-level element should have name count 1");
    }

    @Test
    public void nestedNameCount() {
        final Path path = fileSystem.getPath("toplevel/nested");
        final int count = path.getNameCount();
        Assertions.assertEquals(2, count, "nested-level element should have name count 2");
    }

    @Test
    public void toAbsolutePath() {
        final Path path = fileSystem.getPath("toplevel");
        Assertions.assertEquals("/toplevel", path.toAbsolutePath().toString(),
                "toAbsolute should return the absolute form of the Path");
    }

    @Test
    public void toAbsolutePathAlreadyAbsolute() {
        final String absolutePath = "/absolute";
        final Path path = fileSystem.getPath(absolutePath);
        Assertions.assertEquals(absolutePath, path.toAbsolutePath().toString(),
                "toAbsolute should return the absolute form of the Path");
    }

    @Test
    public void isAbsolute() {
        final Path path = fileSystem.getPath("/absolute");
        Assertions.assertTrue(path.isAbsolute());
    }

    @Test
    public void isAbsoluteFalse() {
        final Path path = fileSystem.getPath("relative");
        Assertions.assertFalse(path.isAbsolute());
    }

    @Test
    public void iterator() {
        final Path path = fileSystem.getPath("toplevel/second/third/fourth");
        final Iterator<Path> paths = path.iterator();
        Assertions.assertEquals("/toplevel", paths.next().toString());
        Assertions.assertEquals("/toplevel/second", paths.next().toString());
        Assertions.assertEquals("/toplevel/second/third", paths.next().toString());
        Assertions.assertEquals("/toplevel/second/third/fourth", paths.next().toString());
    }

    @Test
    public void iteratorRoot() {
        final Path path = fileSystem.getPath("/");
        final Iterator<Path> paths = path.iterator();
        Assertions.assertFalse(paths.hasNext(), "Iterator should not return root element");
    }

    @Test
    public void toUri() {
        final Path path = fileSystem.getPath("/toplevel/second");
        final URI uri = path.toUri();
        final String expected = ShrinkWrapFileSystems.PROTOCOL + "://" + fileSystem.getArchive().getId() + path;
        Assertions.assertEquals(expected, uri.toString(), "toUri did not return form as expected");
    }

    @Test
    public void getName() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final Path second = path.getName(2);
        Assertions.assertEquals("/toplevel/second/third", second.toString());
    }

    @Test
    public void getNameRoot() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final Path second = path.getName(0);
        Assertions.assertEquals("/toplevel", second.toString());
    }

    @Test
    public void subpathNegativeBegin() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").subpath(-1, 1));
    }

    @Test
    public void subpathNegativeEnd() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").subpath(0, -1));
    }

    @Test
    public void subpathEndBeforeBegin() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").subpath(2, 1));
    }

    @Test
    public void subpathBeginTooLarge() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").subpath(4, 5));
    }

    @Test
    public void subpathEndTooLarge() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").subpath(2, 4));
    }

    @Test
    public void subpath() {
        final Path subpath = fileSystem.getPath("/toplevel/second/third").subpath(1, 2);
        Assertions.assertEquals("/toplevel/second", subpath.toString());
    }

    @Test
    public void startsWithNullPathInput() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").startsWith((Path) null));
    }

    @Test
    public void startsWithNullStringInput() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").startsWith((String) null));
    }

    @Test
    public void startsWithOtherPathImpl() {
        final boolean startsWith = fileSystem.getPath("/toplevel/second/third").startsWith(new MockPath());
        Assertions.assertFalse(startsWith);
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
        Assertions.assertFalse(startsWith);
    }

    @Test
    public void startsWith() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/toplevel/second/"));
        Assertions.assertTrue(startsWith);
    }

    @Test
    public void startsWithRelative() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("toplevel/second/"));
        Assertions.assertTrue(startsWith);
    }

    @Test
    public void startsWithAbsolute() {
        final Path path = fileSystem.getPath("toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/toplevel/second/third"));
        Assertions.assertFalse(startsWith, "Other absolute pah and this relative path cannot match startsWith");
    }

    @Test
    public void startsWithString() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith("/toplevel/second/");
        Assertions.assertTrue(startsWith);
    }

    @Test
    public void startsWithNegative() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/top"));
        Assertions.assertFalse(startsWith);
    }

    @Test
    public void startsWithBiggerThan() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean startsWith = path.startsWith(fileSystem.getPath("/toplevel/second/third/fourth"));
        Assertions.assertFalse(startsWith);
    }

    // We don't interface w/ File API
    @Test
    public void toFile() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> fileSystem.getPath("/toplevel").toFile());
    }

    // We don't support events
    @Test
    public void register(){
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> fileSystem.getPath("/toplevel").register(null, (Kind<?>) null));
    }

    // We don't support events
    @Test
    public void registerLongform() {
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> fileSystem.getPath("/toplevel").register(null, (Kind<?>) null, null));
    }

    @Test
    public void endsWithNullPathInput() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").endsWith((Path) null));
    }

    @Test
    public void endsWithNullStringInput() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/second/third").endsWith((String) null));
    }

    @Test
    public void endsWithOtherPathImpl() {
        final boolean endsWith = fileSystem.getPath("/toplevel/second/third").endsWith(new MockPath());
        Assertions.assertFalse(endsWith);
    }

    /**
     * Path cannot start with a path from another FS
     */
    @Test
    public void endsWithOtherFs() throws IOException {
        final FileSystem otherFs = ShrinkWrapFileSystems.newFileSystem(ShrinkWrap.create(GenericArchive.class));
        final Path otherPath = otherFs.getPath("/otherpath");
        final boolean endsWith = fileSystem.getPath("/toplevel/second/third").endsWith(otherPath);
        Assertions.assertFalse(endsWith);
    }

    @Test
    public void endsWith() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("third"));
        Assertions.assertTrue(endsWith, "Should have identified path ends with last name component");
    }

    @Test
    public void endsString() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("third").toString());
        Assertions.assertTrue(endsWith, "Should have identified path ends with last name component");
    }

    @Test
    public void endsWithNested() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("second/third"));
        Assertions.assertTrue(endsWith, "Should have identified path ends with last and penultimate name component");
    }

    @Test
    public void endsWithPartial() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("ird"));
        Assertions.assertFalse(endsWith);
    }

    @Test
    public void endsWithIncorrectRoot() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("/third"));
        Assertions.assertFalse(endsWith);
    }

    @Test
    public void endsWithNegative() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("/toplevel"));
        Assertions.assertFalse(endsWith);
    }

    @Test
    public void endsWithBiggerThan() {
        final Path path = fileSystem.getPath("/toplevel/second/third");
        final boolean endsWith = path.endsWith(fileSystem.getPath("/toplevel/second/third/fourth"));
        Assertions.assertFalse(endsWith);
    }

    @Test
    public void toRealPath() throws IOException {
        final String newPathName = "/toplevel/myAsset";
        this.fileSystem.getArchive().add(EmptyAsset.INSTANCE, newPathName);
        final Path path = fileSystem.getPath(newPathName);
        final Path realPath = path.toRealPath((LinkOption[]) null);
        Assertions.assertEquals(path.toString(), realPath.toString());
    }

    @Test
    public void toRealPathDoesntExist() throws IOException {
        final String newPathName = "/toplevel/myAsset";
        final Path path = fileSystem.getPath(newPathName);
        Assertions.assertThrows(FileNotFoundException.class, () -> path.toRealPath((LinkOption[]) null));
    }

    @Test
    public void normalizeNoop() {
        final Path path = fileSystem.getPath("/a/b");
        final Path normalized = path.normalize();
        Assertions.assertEquals(path.toString(), normalized.toString());
    }

    @Test
    public void normalizeRelative() {
        final Path path = fileSystem.getPath("a/b");
        final Path normalized = path.normalize();
        Assertions.assertEquals(path.toString(), normalized.toString());
    }

    @Test
    public void normalizeCurrentDirRefs() {
        final Path path = fileSystem.getPath("/a/./b/./c/d/./e");
        final Path normalized = path.normalize();
        Assertions.assertEquals("/a/b/c/d/e", normalized.toString());
    }

    @Test
    public void normalizeBackDirRefs() {
        final Path path = fileSystem.getPath("/a/../b/./c/d/../e");
        final Path normalized = path.normalize();
        Assertions.assertEquals("/b/c/e", normalized.toString());
    }

    @Test
    public void relativizeSuperset() {
        final Path path = fileSystem.getPath("/a/b");
        final Path other = fileSystem.getPath("/a/b/c/d");
        final Path relativized = path.relativize(other);
        Assertions.assertEquals("c/d", relativized.toString());
    }

    @Test
    public void relativizeSuperset2() {
        final Path path = fileSystem.getPath("/a/b/c");
        final Path other = fileSystem.getPath("/a/b/c/d");
        final Path relativized = path.relativize(other);
        Assertions.assertEquals("d", relativized.toString());
    }

    @Test
    public void relativizeCommonNode() {
        final Path path = fileSystem.getPath("/a/b");
        final Path other = fileSystem.getPath("/a/c");
        final Path relativized = path.relativize(other);
        Assertions.assertEquals("../c", relativized.toString());
    }

    @Test
    public void relativizeCommonNode2() {
        final Path path = fileSystem.getPath("/a/b");
        final Path other = fileSystem.getPath("/a/c/d");
        final Path relativized = path.relativize(other);
        Assertions.assertEquals("../c/d", relativized.toString());
    }

    @Test
    public void relativizeNull() {
        final Path path = fileSystem.getPath("/toplevel/myAsset");
        Assertions.assertThrows(IllegalArgumentException.class, () -> path.relativize(null));
    }

    @Test
    public void resolve() {
        final Path path = fileSystem.getPath("/topLevel/secondLevel");
        final Path other = fileSystem.getPath("thirdLevel");
        final Path resolved = path.resolve(other);
        Assertions.assertEquals("/topLevel/secondLevel/thirdLevel", resolved.toString(),
                "Resolve should join other to this");
    }

    @Test
    public void resolveTrailingSlashOnThis() {
        final Path path = fileSystem.getPath("/topLevel/secondLevel/");
        final Path other = fileSystem.getPath("thirdLevel");
        final Path resolved = path.resolve(other);
        Assertions.assertEquals("/topLevel/secondLevel/thirdLevel", resolved.toString(),
                "Resolve should join other to this");
    }

    @Test
    public void resolveEmpty() {
        final String thisLocation = "/topLevel/secondLevel";
        final Path path = fileSystem.getPath(thisLocation);
        final Path other = fileSystem.getPath("");
        final Path resolved = path.resolve(other);
        Assertions.assertEquals(thisLocation, resolved.toString(), "Resolve of empty path should return this path");
    }

    @Test
    public void resolveAbsolute() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final Path other = fileSystem.getPath("/toplevel/myDir/myAsset");
        final Path resolved = path.resolve(other);
        // Since absolute, by spec return the value of the other path
        Assertions.assertEquals(other.toString(), resolved.toString());
    }

    @Test
    public void resolveNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/myDir/").resolve((Path) null));
    }

    @Test
    public void resolveString() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final String otherName = "/toplevel/myDir/myAsset";
        final Path other = fileSystem.getPath(otherName);
        final Path resolved = path.resolve(otherName);
        // Since absolute, by spec return the value of the other path
        Assertions.assertEquals(other.toString(), resolved.toString());
    }

    @Test
    public void resolveNullString() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/myDir/").resolve((String) null));
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
        Assertions.assertEquals(pathNormalized.relativize(pathNormalized.resolve(otherNormalized)), otherNormalized,
                "Failed check that relativize undoes resolve, taking into account normalization");
    }

    @Test
    public void resolveSibling() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final Path other = fileSystem.getPath("/toplevel/myDir/myAsset");
        final Path resolved = path.resolveSibling(other);
        // Since absolute, by spec return the value of the other path
        Assertions.assertEquals(other.toString(), resolved.toString());
    }

    @Test
    public void resolveSiblingNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/myDir/").resolveSibling((Path) null));
    }

    @Test
    public void resolveSiblingString() {
        final Path path = fileSystem.getPath("/toplevel/myDir/");
        final String otherName = "/toplevel/myDir/myAsset";
        final Path other = fileSystem.getPath(otherName);
        final Path resolved = path.resolveSibling(otherName);
        // Since absolute, by spec return the value of the other path
        Assertions.assertEquals(other.toString(), resolved.toString());
    }

    @Test
    public void resolveSublingNullString() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/myDir/").resolveSibling((String) null));
    }

    @Test
    public void compareToGreaterValue() {
        final Path path = fileSystem.getPath("/toplevel/a");
        final String otherName = "/toplevel/b";
        final Path other = fileSystem.getPath(otherName);
        final int compare = path.compareTo(other);
        Assertions.assertEquals(-1, compare);
    }

    @Test
    public void compareToLesserValue() {
        final Path path = fileSystem.getPath("/toplevel/b");
        final String otherName = "/toplevel/a";
        final Path other = fileSystem.getPath(otherName);
        final int compare = path.compareTo(other);
        Assertions.assertEquals(1, compare);
    }

    @Test
    public void compareToEqualValue() {
        final Path path = fileSystem.getPath("/toplevel/a");
        final String otherName = "/toplevel/a";
        final Path other = fileSystem.getPath(otherName);
        final int compare = path.compareTo(other);
        Assertions.assertEquals(0, compare);
    }

    @Test
    public void compareToNull() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> fileSystem.getPath("/toplevel/a").compareTo(null));
    }
}

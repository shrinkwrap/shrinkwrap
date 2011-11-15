/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.base.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.jboss.shrinkwrap.impl.base.io.IOUtil.asByteArray;
import static org.junit.Assert.assertArrayEquals;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.IllegalArchivePathException;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.TestIOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.spi.ArchiveFormatAssociable;
import org.junit.After;
import org.junit.Test;

public abstract class ArchiveTestBase<T extends Archive<T>> {
    public static final String NAME_TEST_PROPERTIES = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";
    public static final String NAME_TEST_PROPERTIES_2 = "org/jboss/shrinkwrap/impl/base/asset/Test2.properties";
    private static final Asset ASSET = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

    protected abstract T getArchive();

    // Used to test Archive.add(Archive) type addings.
    protected abstract Archive<T> createNewArchive();

    protected abstract ArchiveFormat getExpectedArchiveFormat();

    @After
    public void ls() {
        ls(getArchive());
    }

    protected void ls(T archive) {
        System.out.println("test@jboss:/$ ls -l " + (archive != null ? archive.getName() : "<null>"));
        System.out.println(archive != null ? archive.toString(true) : "<null>");
    }

    @Test
    public void testDefaultArchiveFormatIsSet() {
        // sanity check
        ArchiveFormat defaultArchiveFormat = ((ArchiveFormatAssociable) getArchive()).getArchiveFormat();
        assertEquals(getExpectedArchiveFormat(), defaultArchiveFormat);
    }

    @Test
    public void ensureAddingAnAssetToThePathResultsInSuccesfulStorage() {
        ArchivePath location = new BasicPath("/", "test.properties");

        getArchive().add(ASSET, location);

        assertTrue(getArchive().contains(location));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathRequiresPath() {
        getArchive().add(ASSET, (ArchivePath) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathRequiresAnAsset() {
        getArchive().add((Asset) null, new BasicPath("/", "Test.properties"));
    }

    @Test
    public void ensureAddingAnAssetToASTringPathResultsInSuccesfulStorage() {
        ArchivePath location = new BasicPath("/", "test.properties");

        getArchive().add(ASSET, location.get());

        assertTrue(getArchive().contains(location));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToAStringPathRequiresPath() {
        getArchive().add(ASSET, (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathStringRequiresAnAsset() {
        getArchive().add((Asset) null, "/Test.properties");
    }

    @Test
    public void ensureAddingAnAssetWithANameUnderAnArchiveContextResultsInSuccessfulStorage() {
        String name = "test.properties";
        ArchivePath location = ArchivePaths.root();

        getArchive().add(ASSET, location, name);

        ArchivePath expectedPath = new BasicPath("/", "test.properties");

        assertTrue("Asset should be placed on " + expectedPath.get(), getArchive().contains(expectedPath));
    }

    @Test
    public void ensureAddingAnAssetWithANameUnderAStringContextResultsInSuccesfulStorage() {
        String name = "test.properties";

        getArchive().add(ASSET, "/", name);

        ArchivePath expectedPath = new BasicPath("/", "test.properties");

        assertTrue(getArchive().contains(expectedPath));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresThePathAttributeAsAnArchive() {
        getArchive().add(new ClassLoaderAsset(NAME_TEST_PROPERTIES), (ArchivePath) null, "test.properties");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresThePathAttributeAsAString() {
        getArchive().add(EmptyAsset.INSTANCE, (String) null, "childPath");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresTheNameAttribute() {
        getArchive().add(ASSET, new BasicPath("/", "Test.properties"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresTheAssetAttribute() {
        getArchive().add(null, new BasicPath("/", "Test.properties"), "test.properties");
    }

    @Test
    public void testAddNamedAsset() {
        getArchive().add(new NamedAsset() {
            @Override
            public String getName() {
                return "check.properties";
            }

            @Override
            public InputStream openStream() {
                return ASSET.openStream();
            }
        });

        assertTrue(getArchive().contains("check.properties"));
    }

    @Test
    public void ensureEmptyDirectoriesMayBeAddedToTheArchive() {
        ArchivePath path1 = ArchivePaths.create("path/to/dir");
        ArchivePath path2 = ArchivePaths.create("path/to/dir2");
        ArchivePath path3 = ArchivePaths.create("path/to");

        getArchive().addAsDirectories(path1, path2, path3);

        assertTrue(getArchive().contains(path1));
        assertTrue(getArchive().contains(path2));
        assertTrue(getArchive().contains(path3));
    }

    @Test
    public void ensureArchiveContainsWorksAsExpectedForStrings() {
        String path = "testpath";
        getArchive().add(EmptyAsset.INSTANCE, path);
        assertTrue(getArchive().contains(path));
    }

    @Test
    public void ensureArchiveContainsWorksAsExpectedForArchivePaths() {
        ArchivePath path = ArchivePaths.create("testpath");
        getArchive().add(EmptyAsset.INSTANCE, path);
        assertTrue(getArchive().contains(path));
    }

    @Test
    public void ensureDeletingAnAssetWithAnArchivePathSuccessfullyRemovesAssetFromStorage() {
        ArchivePath location = new BasicPath("/", "test.properties");
        getArchive().add(ASSET, location);
        assertTrue(getArchive().contains(location)); // Sanity check

        Node node = getArchive().delete(location);

        assertEquals(ASSET, node.getAsset());
        assertFalse(getArchive().contains(location));
    }

    @Test
    public void ensureDeletingAnAssetWithAStringPathSuccessfullyRemovesAssetFromStorage() {
        String location = "/test.properties";
        getArchive().add(ASSET, location);
        assertTrue(getArchive().contains(location)); // Sanity check

        Node node = getArchive().delete(location);

        assertEquals(ASSET, node.getAsset());
        assertFalse(getArchive().contains(location));
    }

    @Test
    public void ensureDeletingAMissingAssetWithAnArchivePatheturnsCorrectStatus() {
        ArchivePath location = new BasicPath("/", "test.properties");

        assertNull(getArchive().delete(location));
    }

    @Test
    public void ensureDeletingAMissingAssetWithAStringPathReturnsCorrectStatus() {
        String location = "/test.properties";

        assertNull(getArchive().delete(location));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureDeletingAnAssetRequiresAnArchivePath() {
        getArchive().delete((ArchivePath) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureDeletingAnAssetRequiresAStringPath() {
        getArchive().delete((String) null);
    }

    @Test
    public void ensureAnAssetCanBeRetrievedByItsPath() {
        ArchivePath location = new BasicPath("/", "test.properties");
        getArchive().add(ASSET, location);

        Node fetchedNode = getArchive().get(location);

        assertArrayEquals(bytes(ASSET), bytes(fetchedNode.getAsset()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureGetAssetRequiresAPath() {
        getArchive().get((ArchivePath) null);
    }

    @Test
    public void ensureAnAssetCanBeRetrievedByAStringPath() {
        ArchivePath location = new BasicPath("/", "test.properties");
        getArchive().add(ASSET, location);

        Node fetchedNode = getArchive().get(location.get());

        assertArrayEquals(bytes(ASSET), bytes(fetchedNode.getAsset()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureGetAssetByStringRequiresAPath() {
        getArchive().get((String) null);
    }

    @Test
    public void testImportArchiveAsTypeFromString() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        JavaArchive jar = archive.getAsType(JavaArchive.class, resourcePath, ArchiveFormat.ZIP).add(
            new StringAsset("test file content"), "test.txt");

        assertEquals(resourcePath, jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get(resourcePath).getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromStringUsingDefaultFormat() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        JavaArchive jar = archive.getAsType(JavaArchive.class, resourcePath).add(new StringAsset("test file content"),
            "test.txt");

        assertEquals("JAR imported with wrong name", resourcePath, jar.getName());
        assertNotNull("Class in JAR not imported", jar.get("test/classloader/DummyClass.class"));
        assertNotNull("Inner Class in JAR not imported",
            jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull("Should contain a new asset", ((ArchiveAsset) archive.get(resourcePath).getAsset())
            .getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromArchivePath() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        JavaArchive jar = archive.getAsType(JavaArchive.class, ArchivePaths.create(resourcePath), ArchiveFormat.ZIP)
            .add(new StringAsset("test file content"), "test.txt");

        assertEquals(resourcePath, jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get(resourcePath).getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromArchivePathUsingDefaultFormat() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        JavaArchive jar = archive.getAsType(JavaArchive.class, ArchivePaths.create(resourcePath)).add(
            new StringAsset("test file content"), "test.txt");

        assertEquals(resourcePath, jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get(resourcePath).getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromFilter() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        Collection<JavaArchive> jars = archive
            .getAsType(JavaArchive.class, Filters.include(".*jar"), ArchiveFormat.ZIP);

        assertEquals("Unexpected result found", 1, jars.size());

        JavaArchive jar = jars.iterator().next().add(new StringAsset("test file content"), "test.txt");

        assertEquals(resourcePath, jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get(resourcePath).getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromFilterUsingDefaultFormat() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        Collection<JavaArchive> jars = archive.getAsType(JavaArchive.class, Filters.include(".*jar"));

        assertEquals("Unexpected result found", 1, jars.size());

        JavaArchive jar = jars.iterator().next().add(new StringAsset("test file content"), "test.txt");

        assertEquals(resourcePath, jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get(resourcePath).getAsset()).getArchive().get("test.txt"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromStringThrowExceptionIfClassIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType((Class<GenericArchive>) null, "/path", ArchiveFormat.ZIP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromStringThrowExceptionIfPathIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType(JavaArchive.class, (String) null, ArchiveFormat.ZIP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromStringThrowExceptionIfFormatIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType(JavaArchive.class, "/path", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromArchivePathThrowExceptionIfClassIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType((Class<GenericArchive>) null, ArchivePaths.create("/path"),
            ArchiveFormat.ZIP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromArchivePathThrowExceptionIfPathIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType(JavaArchive.class, (ArchivePath) null, ArchiveFormat.ZIP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromArchivePathThrowExceptionIfFormatIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType(JavaArchive.class, ArchivePaths.create("/path"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromFilterThrowExceptionIfClassIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType((Class<JavaArchive>) null, Filters.includeAll(),
            ArchiveFormat.ZIP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromFilterThrowExceptionIfPathIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType(JavaArchive.class, (Filter<ArchivePath>) null,
            ArchiveFormat.ZIP);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testImportArchiveFromFilterThrowExceptionIfFormatIsNull() {
        ShrinkWrap.create(GenericArchive.class).getAsType(JavaArchive.class, Filters.includeAll(), null);
    }

    @Test
    public void ensureWeCanGetAnAddedArchiveAsAString() {
        GenericArchive child = ShrinkWrap.create(GenericArchive.class);
        getArchive().add(child, "/", ZipExporter.class);

        GenericArchive found = getArchive().getAsType(GenericArchive.class, child.getName());

        assertNotNull(found);
    }

    @Test
    public void ensureWeCanGetAnAddedArchiveAsAnArchivePath() {
        GenericArchive child = ShrinkWrap.create(GenericArchive.class);
        getArchive().add(child, "/", ZipExporter.class);

        GenericArchive found = getArchive().getAsType(GenericArchive.class, ArchivePaths.create(child.getName()));

        assertNotNull(found);
    }

    @Test
    public void ensureWeCanGetAnAddedArchiveAsASpecificType() {
        GenericArchive child1 = ShrinkWrap.create(GenericArchive.class);
        GenericArchive child2 = ShrinkWrap.create(GenericArchive.class);
        // Create one not to be found by filter.
        GenericArchive child3 = ShrinkWrap.create(GenericArchive.class, "SHOULD_NOT_BE_FOUND.xxx");

        Archive<?> archive = getArchive().add(child1, "/", ZipExporter.class).add(child2, "/", ZipExporter.class)
            .add(child3, "/", ZipExporter.class);

        Collection<GenericArchive> matches = archive.getAsType(GenericArchive.class, Filters.include(".*\\.jar"));

        assertNotNull(matches);
        assertEquals("Two archives should be found", 2, matches.size());

        for (GenericArchive match : matches) {
            if (!match.getName().equals(child1.getName()) && !match.getName().equals(child2.getName())) {
                fail("Wrong getArchive() found, " + match.getName() + ". Expected " + child1.getName() + " or "
                    + child2.getName());
            }
        }
    }

    @Test
    public void ensureGetContentReturnsTheCorrectMapOfContent() {
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        getArchive().add(ASSET, location).add(assetTwo, locationTwo);

        Map<ArchivePath, Node> content = getArchive().getContent();

        Node node1 = content.get(location);
        Node node2 = content.get(locationTwo);

        assertArrayEquals(bytes(ASSET), bytes(node1.getAsset()));
        assertArrayEquals(bytes(assetTwo), bytes(node2.getAsset()));
    }

    @Test
    public void ensureGetContentReturnsTheCorrectMapOfContentBasedOnTheGivenFilter() {
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        getArchive().add(ASSET, location).add(assetTwo, locationTwo);

        Map<ArchivePath, Node> content = getArchive().getContent(Filters.include(".*test2.*"));

        Node node1 = content.get(location);
        Node node2 = content.get(locationTwo);

        assertEquals(1, content.size());
        assertNull(node1);
        assertNotNull(node2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnArchiveToAPathRequiresAnArchivePath() {
        getArchive().add(ShrinkWrap.create(JavaArchive.class), (ArchivePath) null, ZipExporter.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnArchiveToAPathRequiresAStringPath() {
        getArchive().add(ShrinkWrap.create(JavaArchive.class), (String) null, ZipExporter.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnArchiveToAPathRequiresAnArchive() {
        getArchive().add((Archive<?>) null, ArchivePaths.root(), ZipExporter.class);
    }

    @Test(expected = IllegalArchivePathException.class)
    public void ensureThatTryingToAddAnAssetOnAnIllegalPathThrowsAnException() {
        ArchivePath location = new BasicPath("/", "test.properties");
        getArchive().add(ASSET, location);

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        ArchivePath locationTwo = ArchivePaths.create("/test.properties/somewhere");
        getArchive().add(assetTwo, locationTwo);
    }

    @Test(expected = IllegalArchivePathException.class)
    public void ensureThatTryingToAddADirectoryOnAnIllegalPathThrowsAnException() {
        ArchivePath location = new BasicPath("/somewhere/test.properties");
        getArchive().add(ASSET, location);

        getArchive().addAsDirectory("/somewhere/test.properties/test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureMergingContentRequiresASourceArchive() {
        getArchive().merge(null);
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveSuccessfullyStoresAllAssets() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(ASSET, location).add(assetTwo, locationTwo);

        getArchive().merge(sourceArchive);

        Node node1 = getArchive().get(location);
        Node node2 = getArchive().get(locationTwo);

        assertArrayEquals(bytes(ASSET), bytes(node1.getAsset()));
        assertArrayEquals(bytes(assetTwo), bytes(node2.getAsset()));
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveToAPathSuccessfullyStoresAllAssetsToSpecificPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(ASSET, location).add(assetTwo, locationTwo);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().merge(sourceArchive, baseLocation);

        ArchivePath expectedPath = new BasicPath(baseLocation, location);
        ArchivePath expectedPathTwo = new BasicPath(baseLocation, locationTwo);

        Node nodeOne = getArchive().get(expectedPath);
        Node nodeTwo = getArchive().get(expectedPathTwo);

        assertArrayEquals(bytes(ASSET), bytes(nodeOne.getAsset()));
        assertArrayEquals(bytes(assetTwo), bytes(nodeTwo.getAsset()));
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveToAStringPathSuccessfullyStoresAllAssetsToSpecificPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(ASSET, location).add(assetTwo, locationTwo);

        String baseLocation = "somewhere";

        getArchive().merge(sourceArchive, baseLocation);

        ArchivePath expectedPath = new BasicPath(baseLocation, location);
        ArchivePath expectedPathTwo = new BasicPath(baseLocation, locationTwo);

        Node nodeOne = getArchive().get(expectedPath);
        Node nodeTwo = getArchive().get(expectedPathTwo);

        assertArrayEquals(bytes(ASSET), bytes(nodeOne.getAsset()));
        assertArrayEquals(bytes(assetTwo), bytes(nodeTwo.getAsset()));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMergingOnAPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(ASSET, location).add(assetTwo, locationTwo);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().merge(sourceArchive, baseLocation, Filters.include(".*test2.*"));

        assertEquals(1, numberOfAssetsIn(getArchive()));

        ArchivePath expectedPath = new BasicPath(baseLocation, locationTwo);

        assertArrayEquals(bytes(ASSET), bytes(getArchive().get(expectedPath).getAsset()));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMergingOnAStringPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(ASSET, location).add(assetTwo, locationTwo);

        String baseLocation = "somewhere";

        getArchive().merge(sourceArchive, baseLocation, Filters.include(".*test2.*"));

        assertEquals(1, numberOfAssetsIn(getArchive()));

        ArchivePath expectedPath = new BasicPath(baseLocation, locationTwo);

        assertArrayEquals(bytes(ASSET), bytes(getArchive().get(expectedPath).getAsset()));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMerging() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(ASSET, location).add(assetTwo, locationTwo);

        getArchive().merge(sourceArchive, Filters.include(".*test2.*"));

        assertEquals(1, numberOfAssetsIn(getArchive()));

        assertArrayEquals(bytes(ASSET), bytes(getArchive().get(locationTwo).getAsset()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureMergingContentFromAnotherArchiveRequiresAPath() {
        getArchive().merge(createNewArchive(), (ArchivePath) null);
    }

    @Test
    public void ensureAddingAnArchiveToAPathSuccessfullyStoresAllAssetsToSpecificPathIncludingTheArchiveName() {
        Archive<T> sourceArchive = createNewArchive();

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().add(sourceArchive, baseLocation, ZipExporter.class);

        ArchivePath expectedPath = new BasicPath(baseLocation, sourceArchive.getName());

        Node node = getArchive().get(expectedPath);
        assertTrue(node.getAsset() instanceof ArchiveAsset);
        assertEquals(sourceArchive, ArchiveAsset.class.cast(node.getAsset()).getArchive());
    }

    @Test
    public void ensureAnArchiveContainsAssetsFromNestedArchives() {
        Archive<T> sourceArchive = createNewArchive();

        ArchivePath nestedAssetPath = new BasicPath("/", "test.properties");

        sourceArchive.add(ASSET, nestedAssetPath);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().add(sourceArchive, baseLocation, ZipExporter.class);

        ArchivePath archivePath = new BasicPath(baseLocation, sourceArchive.getName());

        ArchivePath expectedPath = new BasicPath(archivePath, "test.properties");

        assertTrue(getArchive().contains(expectedPath));
    }

    @Test
    public void ensureAssetsFromANestedArchiveAreAccessibleFromParentArchives() {
        Archive<T> nestedArchive = createNewArchive();

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().add(nestedArchive, baseLocation, ZipExporter.class);

        Archive<T> nestedNestedArchive = createNewArchive();

        nestedArchive.add(nestedNestedArchive, ArchivePaths.root(), ZipExporter.class);

        ArchivePath nestedAssetPath = new BasicPath("/", "test.properties");

        nestedNestedArchive.add(ASSET, nestedAssetPath);

        ArchivePath nestedArchivePath = new BasicPath(baseLocation, nestedArchive.getName());

        ArchivePath nestedNestedArchivePath = new BasicPath(nestedArchivePath, nestedNestedArchive.getName());

        ArchivePath expectedPath = new BasicPath(nestedNestedArchivePath, "test.properties");

        Node nestedNode = getArchive().get(expectedPath);

        assertNotNull(nestedNode.getAsset());
    }

    @Test
    public void ensureShallowCopyPreservesPointers() {
        getArchive().add(ASSET, "location");

        Archive<T> copyArchive = getArchive().shallowCopy();

        assertTrue(copyArchive.contains("location"));
        assertSame(copyArchive.get("location").getAsset(), getArchive().get("location").getAsset());
    }

    @Test
    public void ensureShallowCopyHasASeparateCollectionOfTheSamePointers() {
        getArchive().add(ASSET, "location");

        Archive<T> copyArchive = getArchive().shallowCopy();
        getArchive().delete("location");

        assertTrue(copyArchive.contains("location"));
    }

    private static byte[] bytes(Asset one) {
        return asByteArray(one.openStream());
    }

    protected static int numberOfAssetsIn(Archive<?> archive) {
        int assets = 0;
        for (Map.Entry<ArchivePath, Node> entry : archive.getContent().entrySet()) {
            if (entry.getValue().getAsset() != null) {
                assets++;
            }
        }
        return assets;
    }

}

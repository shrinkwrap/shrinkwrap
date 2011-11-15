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

import java.io.InputStream;
import java.util.Arrays;
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
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.spi.ArchiveFormatAssociable;
import org.junit.After;
import org.junit.Test;

public abstract class ArchiveTestBase<T extends Archive<T>> {
    public static final String NAME_TEST_PROPERTIES = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";
    public static final String NAME_TEST_PROPERTIES_2 = "org/jboss/shrinkwrap/impl/base/asset/Test2.properties";

    protected abstract T getArchive();

    // Used to test Archive.add(Archive) type addings.
    protected abstract Archive<T> createNewArchive();

    protected abstract ArchiveFormat getExpectedArchiveFormat();

    @Test
    public void testDefaultArchiveFormatIsSet() {
        assertEquals("Unexpected default getArchive() format", getExpectedArchiveFormat(), getDefaultArchiveFormat());
    }

    private ArchiveFormat getDefaultArchiveFormat() {
        return ((ArchiveFormatAssociable) getArchive()).getArchiveFormat();
    }

    @After
    public void simplePrintoutOfTheTestedArchive() {
        System.out.println("test@jboss:/$ ls -l " + getArchive().getName());
        System.out.println(getArchive().toString(true));
    }

    @Test
    public void ensureAddingAnAssetToThePathResultsInSuccesfulStorage() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        ArchivePath location = new BasicPath("/", "test.properties");

        getArchive().add(asset, location);

        assertTrue("Asset should be placed on " + location.get(), getArchive().contains(location));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathRequiresPath() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

        getArchive().add(asset, (ArchivePath) null);

        fail("Should have thrown an IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathRequiresAnAsset() {
        getArchive().add((Asset) null, new BasicPath("/", "Test.properties"));

        fail("Should have throw an IllegalArgumentException");
    }

    @Test
    public void ensureAddingAnAssetToASTringPathResultsInSuccesfulStorage() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        ArchivePath location = new BasicPath("/", "test.properties");

        getArchive().add(asset, location.get());

        assertTrue("Asset should be placed on " + new BasicPath("/", "test.properties"),
            getArchive().contains(location));
    }

    @Test
    public void ensureAddingAnAssetToAStringPathRequiresPath() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

        try {
            getArchive().add(asset, (String) null);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
            // success
        }
    }

    @Test
    public void ensureAddingAnAssetToThePathStringRequiresAnAsset() {
        try {
            getArchive().add((Asset) null, "/Test.properties");
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
            // success
        }
    }

    @Test
    public void ensureAddingAnAssetWithANameUnderAnArchiveContextResultsInSuccessfulStorage() {
        final String name = "test.properties";
        final Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        ArchivePath location = ArchivePaths.root();

        getArchive().add(asset, location, name);

        ArchivePath expectedPath = new BasicPath("/", "test.properties");

        assertTrue("Asset should be placed on " + expectedPath.get(), getArchive().contains(expectedPath));
    }

    @Test
    public void ensureAddingAnAssetWithANameUnderAStringContextResultsInSuccesfulStorage() {
        final String name = "test.properties";
        final Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

        getArchive().add(asset, "/", name);

        ArchivePath expectedPath = new BasicPath("/", "test.properties");

        assertTrue("Asset should be placed on " + expectedPath.get(), getArchive().contains(expectedPath));
    }

    @Test
    public void ensureAddingAnAssetWithNameRequiresThePathAttributeAsAnArchive() {
        final String name = "test.properties";
        final Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        try {
            getArchive().add(asset, (ArchivePath) null, name);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresThePathAttributeAsAString() {
        getArchive().add(EmptyAsset.INSTANCE, (String) null, "childPath");
    }

    @Test
    public void ensureAddingAnAssetWithNameRequiresTheNameAttribute() {
        final ArchivePath path = new BasicPath("/", "Test.properties");
        final String resource = NAME_TEST_PROPERTIES;
        try {
            getArchive().add(new ClassLoaderAsset(resource), path, null);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void ensureAddingAnAssetWithNameRequiresTheAssetAttribute() {
        final String name = "test.properties";
        final ArchivePath path = new BasicPath("/", "Test.properties");
        try {
            getArchive().add(null, path, name);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void testAddNamedAsset() {
        final String testName = "check.properties";
        final Asset testAsset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

        final NamedAsset namedAsset = new NamedAsset() {

            @Override
            public String getName() {
                return testName;
            }

            @Override
            public InputStream openStream() {
                return testAsset.openStream();
            }

        };

        getArchive().add(namedAsset);

        assertTrue("Asset should be placed on " + testName, getArchive().contains(testName));
    }

    @Test
    public void ensureEmptyDirectoriesMayBeAddedToTheArchive() {
        final ArchivePath path1 = ArchivePaths.create("path/to/dir");
        final ArchivePath path2 = ArchivePaths.create("path/to/dir2");
        final ArchivePath path3 = ArchivePaths.create("path/to");

        getArchive().addAsDirectories(path1, path2, path3);

        String message = "Should be able to add directory: ";
        TestCase.assertTrue(message + path1, getArchive().contains(path1));
        TestCase.assertTrue(message + path2, getArchive().contains(path2));
        TestCase.assertTrue(message + path3, getArchive().contains(path3));
    }

    @Test
    public void ensureArchiveContainsWorksAsExpectedForStrings() {
        final String path = "testpath";
        getArchive().add(EmptyAsset.INSTANCE, path);
        assertTrue("Archive should contain the path added", getArchive().contains(path));
    }

    @Test
    public void ensureArchiveContainsWorksAsExpectedForArchivePaths() {
        final ArchivePath path = ArchivePaths.create("testpath");
        getArchive().add(EmptyAsset.INSTANCE, path);
        assertTrue("Archive should contain the path added", getArchive().contains(path));
    }

    @Test
    public void ensureDeletingAnAssetWithAnArchivePathSuccessfullyRemovesAssetFromStorage() {
        String resource = NAME_TEST_PROPERTIES;
        ArchivePath location = new BasicPath("/", "test.properties");
        final Asset asset = new ClassLoaderAsset(resource);
        getArchive().add(asset, location);
        assertTrue(getArchive().contains(location)); // Sanity check

        assertEquals("Successfully deleting an Asset should return the removed Node", asset,
            getArchive().delete(location).getAsset());

        assertFalse("There should no longer be an asset at: " + location.get() + " after deleted",
            getArchive().contains(location));
    }

    @Test
    public void ensureDeletingAnAssetWithAStringPathSuccessfullyRemovesAssetFromStorage() {
        String resource = NAME_TEST_PROPERTIES;
        String location = "/test.properties";
        final Asset asset = new ClassLoaderAsset(resource);
        getArchive().add(asset, location);
        assertTrue(getArchive().contains(location)); // Sanity check

        assertEquals("Successfully deleting an Asset should return the removed Node", asset,
            getArchive().delete(location).getAsset());

        assertFalse("There should no longer be an asset at: " + location + " after deleted",
            getArchive().contains(location));
    }

    @Test
    public void ensureDeletingAMissingAssetWithAnArchivePatheturnsCorrectStatus() {
        ArchivePath location = new BasicPath("/", "test.properties");

        assertNull("Deleting a non-existent Asset should return null", getArchive().delete(location));
    }

    @Test
    public void ensureDeletingAMissingAssetWithAStringPathReturnsCorrectStatus() {
        String location = "/test.properties";

        assertNull("Deleting a non-existent Asset should return null", getArchive().delete(location));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureDeletingAnAssetRequiresAnArchivePath() {
        getArchive().delete((ArchivePath) null);
        fail("Should have throw an IllegalArgumentException");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureDeletingAnAssetRequiresAStringPath() {
        getArchive().delete((String) null);
        fail("Should have throw an IllegalArgumentException");
    }

    @Test
    public void ensureAnAssetCanBeRetrievedByItsPath() {
        ArchivePath location = new BasicPath("/", "test.properties");
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        getArchive().add(asset, location);

        Node fetchedNode = getArchive().get(location);

        assertTrue("Asset should be returned from path: " + location.get(),
            compareAssetsContent(asset, fetchedNode.getAsset()));
    }

    @Test
    public void ensureGetAssetRequiresAPath() {
        try {
            getArchive().get((ArchivePath) null);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
            // success
        }
    }

    @Test
    public void ensureAnAssetCanBeRetrievedByAStringPath() {
        ArchivePath location = new BasicPath("/", "test.properties");
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        getArchive().add(asset, location);

        Node fetchedNode = getArchive().get(location.get());

        assertTrue("Asset should be returned from path: " + location.get(),
            compareAssetsContent(asset, fetchedNode.getAsset()));
    }

    @Test
    public void ensureGetAssetByStringRequiresAPath() {
        try {
            getArchive().get((String) null);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void testImportArchiveAsTypeFromString() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        JavaArchive jar = archive.getAsType(JavaArchive.class, resourcePath, ArchiveFormat.ZIP).add(
            new StringAsset("test file content"), "test.txt");

        assertEquals("JAR imported with wrong name", resourcePath, jar.getName());
        assertNotNull("Class in JAR not imported", jar.get("test/classloader/DummyClass.class"));
        assertNotNull("Inner Class in JAR not imported",
            jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull("Should contain a new asset", ((ArchiveAsset) archive.get(resourcePath).getAsset())
            .getArchive().get("test.txt"));
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

        assertEquals("JAR imported with wrong name", resourcePath, jar.getName());
        assertNotNull("Class in JAR not imported", jar.get("test/classloader/DummyClass.class"));
        assertNotNull("Inner Class in JAR not imported",
            jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull("Should contain an getArchive() asset", ((ArchiveAsset) archive.get(resourcePath).getAsset())
            .getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromArchivePathUsingDefaultFormat() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        JavaArchive jar = archive.getAsType(JavaArchive.class, ArchivePaths.create(resourcePath)).add(
            new StringAsset("test file content"), "test.txt");

        assertEquals("JAR imported with wrong name", resourcePath, jar.getName());
        assertNotNull("Class in JAR not imported", jar.get("test/classloader/DummyClass.class"));
        assertNotNull("Inner Class in JAR not imported",
            jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull("Should contain an getArchive() asset", ((ArchiveAsset) archive.get(resourcePath).getAsset())
            .getArchive().get("test.txt"));
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

        assertEquals("JAR imported with wrong name", resourcePath, jar.getName());
        assertNotNull("Class in JAR not imported", jar.get("test/classloader/DummyClass.class"));
        assertNotNull("Inner Class in JAR not imported",
            jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull("Should contain a new asset", ((ArchiveAsset) archive.get(resourcePath).getAsset())
            .getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromFilterUsingDefaultFormat() throws Exception {
        String resourcePath = "/test/cl-test.jar";
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), resourcePath);

        Collection<JavaArchive> jars = archive.getAsType(JavaArchive.class, Filters.include(".*jar"));

        assertEquals("Unexpected result found", 1, jars.size());

        JavaArchive jar = jars.iterator().next().add(new StringAsset("test file content"), "test.txt");

        assertEquals("JAR imported with wrong name", resourcePath, jar.getName());
        assertNotNull("Class in JAR not imported", jar.get("test/classloader/DummyClass.class"));
        assertNotNull("Inner Class in JAR not imported",
            jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull("Should contain a new asset", ((ArchiveAsset) archive.get(resourcePath).getAsset())
            .getArchive().get("test.txt"));
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

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        getArchive().add(asset, location).add(assetTwo, locationTwo);

        Map<ArchivePath, Node> content = getArchive().getContent();

        final Node node1 = content.get(location);
        final Node node2 = content.get(locationTwo);

        assertTrue("Asset should existing in content with key: " + location.get(),
            this.compareAssetsContent(asset, node1.getAsset()));

        assertTrue("Asset should existing in content with key: " + locationTwo.get(),
            this.compareAssetsContent(assetTwo, node2.getAsset()));
    }

    @Test
    public void ensureGetContentReturnsTheCorrectMapOfContentBasedOnTheGivenFilter() {
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        getArchive().add(asset, location).add(assetTwo, locationTwo);

        Map<ArchivePath, Node> content = getArchive().getContent(Filters.include(".*test2.*"));

        final Node node1 = content.get(location);
        final Node node2 = content.get(locationTwo);

        assertEquals("Only 1 Asset should have been included", 1, content.size());
        assertNull("Should not be included in content", node1);

        assertNotNull("Should be included in content", node2);
    }

    @Test
    public void ensureAddingAnArchiveToAPathRequiresAnArchivePath() {
        try {
            getArchive().add(ShrinkWrap.create(JavaArchive.class), (ArchivePath) null, ZipExporter.class);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
            // success
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnArchiveToAPathRequiresAStringPath() {
        getArchive().add(ShrinkWrap.create(JavaArchive.class), (String) null, ZipExporter.class);
    }

    @Test
    public void ensureAddingAnArchiveToAPathRequiresAnArchive() {
        try {
            getArchive().add((Archive<?>) null, ArchivePaths.root(), ZipExporter.class);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
            // success
        }
    }

    @Test(expected = IllegalArchivePathException.class)
    public void ensureThatTryingToAddAnAssetOnAnIllegalPathThrowsAnException() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        ArchivePath location = new BasicPath("/", "test.properties");
        getArchive().add(asset, location);

        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        ArchivePath locationTwo = ArchivePaths.create("/test.properties/somewhere");
        getArchive().add(assetTwo, locationTwo);

    }

    @Test(expected = IllegalArchivePathException.class)
    public void ensureThatTryingToAddADirectoryOnAnIllegalPathThrowsAnException() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        ArchivePath location = new BasicPath("/somewhere/test.properties");
        getArchive().add(asset, location);

        getArchive().addAsDirectory("/somewhere/test.properties/test");
    }

    @Test
    public void ensureMergingContentRequiresASourceArchive() {
        try {
            getArchive().merge(null);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
            // success
        }
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveSuccessfullyStoresAllAssets() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(asset, location).add(assetTwo, locationTwo);

        getArchive().merge(sourceArchive);

        Node node1 = getArchive().get(location);
        Node node2 = getArchive().get(locationTwo);

        assertTrue("Asset should have been added to path: " + location.get(),
            this.compareAssetsContent(node1.getAsset(), asset));

        assertTrue("Asset should have been added to path: " + location.get(),
            this.compareAssetsContent(node2.getAsset(), assetTwo));
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveToAPathSuccessfullyStoresAllAssetsToSpecificPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(asset, location).add(assetTwo, locationTwo);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().merge(sourceArchive, baseLocation);

        ArchivePath expectedPath = new BasicPath(baseLocation, location);
        ArchivePath expectedPathTwo = new BasicPath(baseLocation, locationTwo);

        Node nodeOne = getArchive().get(expectedPath);
        Node nodeTwo = getArchive().get(expectedPathTwo);

        assertTrue("Asset should have been added to path: " + expectedPath.get(),
            this.compareAssetsContent(nodeOne.getAsset(), asset));

        assertTrue("Asset should have been added to path: " + expectedPathTwo.getClass(),
            this.compareAssetsContent(nodeTwo.getAsset(), assetTwo));
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveToAStringPathSuccessfullyStoresAllAssetsToSpecificPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(asset, location).add(assetTwo, locationTwo);

        String baseLocation = "somewhere";

        getArchive().merge(sourceArchive, baseLocation);

        ArchivePath expectedPath = new BasicPath(baseLocation, location);
        ArchivePath expectedPathTwo = new BasicPath(baseLocation, locationTwo);

        Node nodeOne = getArchive().get(expectedPath);
        Node nodeTwo = getArchive().get(expectedPathTwo);

        assertTrue("Asset should have been added to path: " + expectedPath.get(),
            this.compareAssetsContent(nodeOne.getAsset(), asset));

        assertTrue("Asset should have been added to path: " + expectedPathTwo.getClass(),
            this.compareAssetsContent(nodeTwo.getAsset(), assetTwo));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMergingOnAPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(asset, location).add(assetTwo, locationTwo);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().merge(sourceArchive, baseLocation, Filters.include(".*test2.*"));

        assertEquals("Should only have merged 1", 1, numberOfAssetsIn(getArchive()));

        ArchivePath expectedPath = new BasicPath(baseLocation, locationTwo);

        assertTrue("Asset should have been added to path: " + expectedPath.get(),
            this.compareAssetsContent(getArchive().get(expectedPath).getAsset(), asset));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMergingOnAStringPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(asset, location).add(assetTwo, locationTwo);

        String baseLocation = "somewhere";

        getArchive().merge(sourceArchive, baseLocation, Filters.include(".*test2.*"));

        assertEquals("Should only have merged 1", 1, numberOfAssetsIn(getArchive()));

        ArchivePath expectedPath = new BasicPath(baseLocation, locationTwo);

        assertTrue("Asset should have been added to path: " + expectedPath.get(),
            this.compareAssetsContent(getArchive().get(expectedPath).getAsset(), asset));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMerging() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
        sourceArchive.add(asset, location).add(assetTwo, locationTwo);

        getArchive().merge(sourceArchive, Filters.include(".*test2.*"));

        assertEquals("Should only have merged 1", 1, numberOfAssetsIn(getArchive()));

        assertTrue("Asset should have been added to path: " + locationTwo.get(),
            this.compareAssetsContent(getArchive().get(locationTwo).getAsset(), asset));
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveRequiresAPath() {
        try {
            getArchive().merge(createNewArchive(), (ArchivePath) null);
            fail("Should have throw an IllegalArgumentException");
        } catch (IllegalArgumentException expectedException) {
        }
    }

    @Test
    public void ensureAddingAnArchiveToAPathSuccessfullyStoresAllAssetsToSpecificPathIncludingTheArchiveName() {
        Archive<T> sourceArchive = createNewArchive();

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().add(sourceArchive, baseLocation, ZipExporter.class);

        ArchivePath expectedPath = new BasicPath(baseLocation, sourceArchive.getName());

        Node node = getArchive().get(expectedPath);
        assertNotNull("Asset should have been added to path: " + expectedPath.get(), node);
        assertTrue("An instance of ArchiveAsset should have been added to path: " + expectedPath.get(),
            node.getAsset() instanceof ArchiveAsset);
        ArchiveAsset archiveAsset = ArchiveAsset.class.cast(node.getAsset());

        Archive<?> nestedArchive = archiveAsset.getArchive();
        assertEquals("Nested Archive should be same getArchive() that was added", sourceArchive, nestedArchive);

    }

    @Test
    public void ensureAnArchiveContainsAssetsFromNestedArchives() {
        Archive<T> sourceArchive = createNewArchive();

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

        ArchivePath nestedAssetPath = new BasicPath("/", "test.properties");

        sourceArchive.add(asset, nestedAssetPath);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().add(sourceArchive, baseLocation, ZipExporter.class);

        ArchivePath archivePath = new BasicPath(baseLocation, sourceArchive.getName());

        ArchivePath expectedPath = new BasicPath(archivePath, "test.properties");

        assertTrue("Nested getArchive() assets should be verified through a fully qualified path",
            getArchive().contains(expectedPath));
    }

    @Test
    public void ensureAssetsFromANestedArchiveAreAccessibleFromParentArchives() {
        Archive<T> nestedArchive = createNewArchive();

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().add(nestedArchive, baseLocation, ZipExporter.class);

        Archive<T> nestedNestedArchive = createNewArchive();

        nestedArchive.add(nestedNestedArchive, ArchivePaths.root(), ZipExporter.class);

        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

        ArchivePath nestedAssetPath = new BasicPath("/", "test.properties");

        nestedNestedArchive.add(asset, nestedAssetPath);

        ArchivePath nestedArchivePath = new BasicPath(baseLocation, nestedArchive.getName());

        ArchivePath nestedNestedArchivePath = new BasicPath(nestedArchivePath, nestedNestedArchive.getName());

        ArchivePath expectedPath = new BasicPath(nestedNestedArchivePath, "test.properties");

        Node nestedNode = getArchive().get(expectedPath);

        assertNotNull(
            "Nested getArchive() asset should be available through partent getArchive() at " + expectedPath.get(),
            nestedNode.getAsset());
    }

    @Test
    public void ensureShallowCopyPreservesPointers() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        getArchive().add(asset, "location");

        Archive<T> copyArchive = getArchive().shallowCopy();

        assertTrue(copyArchive.contains("location"));
        assertSame(copyArchive.get("location").getAsset(), getArchive().get("location").getAsset());
    }

    @Test
    public void ensureShallowCopyHasASeparateCollectionOfTheSamePointers() {
        Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
        getArchive().add(asset, "location");

        Archive<T> copyArchive = getArchive().shallowCopy();
        getArchive().delete("location");

        assertTrue(copyArchive.contains("location"));
    }

    private boolean compareAssetsContent(final Asset one, final Asset two) throws IllegalArgumentException {
        Validate.notNull(one, "Asset one must be specified");
        Validate.notNull(two, "Asset two must be specified");

        byte[] oneData = IOUtil.asByteArray(one.openStream());
        byte[] twoData = IOUtil.asByteArray(two.openStream());

        return Arrays.equals(oneData, twoData);
    }

    protected int numberOfAssetsIn(final Archive<?> archive) {
        Validate.notNull(archive, "Archive must be specified");

        int assets = 0;
        for (Map.Entry<ArchivePath, Node> entry : getArchive().getContent().entrySet()) {
            if (entry.getValue().getAsset() != null) {
                assets++;
            }
        }

        return assets;
    }

}

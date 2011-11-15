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
    public static final Asset ASSET_1 = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
    public static final Asset ASSET_2 = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
    private T archive;

    protected abstract T createNewArchive();
    protected abstract ArchiveFormat getExpectedArchiveFormat();

    @After
    public void ls() {
        System.out.println("test@jboss:/$ ls -l " + (getArchive() != null ? getArchive().getName() : "<null>"));
        System.out.println(getArchive() != null ? getArchive().toString(true) : "<null>");
    }

    protected final T getArchive() {
        if (archive == null) {
            archive = createNewArchive();
        }
        return archive;
    }

    @Test
    // sanity check
    public void testDefaultArchiveFormatIsSet() {
        ArchiveFormat defaultArchiveFormat = ((ArchiveFormatAssociable) getArchive()).getArchiveFormat();
        assertEquals(getExpectedArchiveFormat(), defaultArchiveFormat);
    }

    @Test
    public void ensureAddingAnAssetToThePathResultsInSuccesfulStorage() {
        ArchivePath location = new BasicPath("/", "test.properties");

        getArchive().add(ASSET_1, location);

        assertTrue(getArchive().contains(location));
    }

    @Test
    public void ensureAddingAnAssetToAStringPathResultsInSuccesfulStorage() {
        ArchivePath location = new BasicPath("/", "test.properties");

        getArchive().add(ASSET_1, location.get());

        assertTrue(getArchive().contains(location));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathRequiresPath() {
        getArchive().add(ASSET_1, (ArchivePath) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathRequiresAnAsset() {
        getArchive().add((Asset) null, new BasicPath("/", "Test.properties"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToAStringPathRequiresPath() {
        getArchive().add(ASSET_1, (String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetToThePathStringRequiresAnAsset() {
        getArchive().add((Asset) null, "/Test.properties");
    }

    @Test
    public void ensureAddingAnAssetWithANameUnderAnArchiveContextResultsInSuccessfulStorage() {
        getArchive().add(ASSET_1, ArchivePaths.root(), "test.properties");

        assertTrue(getArchive().contains(new BasicPath("/", "test.properties")));
    }

    @Test
    public void ensureAddingAnAssetWithANameUnderAStringContextResultsInSuccesfulStorage() {
        getArchive().add(ASSET_1, "/", "test.properties");

        assertTrue(getArchive().contains(new BasicPath("/", "test.properties")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresThePathAttributeAsAnArchive() {
        getArchive().add(ASSET_1, (ArchivePath) null, "test.properties");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresThePathAttributeAsAString() {
        getArchive().add(ASSET_1, (String) null, "childPath");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresTheNameAttribute() {
        getArchive().add(ASSET_1, new BasicPath("/", "Test.properties"), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureAddingAnAssetWithNameRequiresTheAssetAttribute() {
        getArchive().add(null, new BasicPath("/", "Test.properties"), "test.properties");
    }

    @Test
    public void ensureAddingANamedAssetResultsInSuccesfulStorage() {
        getArchive().add(new NamedAsset() {
            @Override
            public String getName() {
                return "check.properties";
            }

            @Override
            public InputStream openStream() {
                return ASSET_1.openStream();
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
        getArchive().add(ASSET_1, "testpath");

        assertTrue(getArchive().contains("testpath"));
    }

    @Test
    public void ensureArchiveContainsWorksAsExpectedForArchivePaths() {
        ArchivePath path = ArchivePaths.create("testpath");

        getArchive().add(ASSET_1, path);

        assertTrue(getArchive().contains(path));
    }

    @Test
    public void ensureDeletingAnAssetWithAnArchivePathSuccessfullyRemovesAssetFromStorage() {
        ArchivePath location = new BasicPath("/", "test.properties");
        getArchive().add(ASSET_1, location);
        assertTrue(getArchive().contains(location)); // Sanity check

        Node node = getArchive().delete(location);

        assertEquals(ASSET_1, node.getAsset());
        assertFalse(getArchive().contains(location));
    }

    @Test
    public void ensureDeletingAnAssetWithAStringPathSuccessfullyRemovesAssetFromStorage() {
        getArchive().add(ASSET_1, "/test.properties");
        assertTrue(getArchive().contains("/test.properties")); // Sanity check

        Node node = getArchive().delete("/test.properties");

        assertEquals(ASSET_1, node.getAsset());
        assertFalse(getArchive().contains("/test.properties"));
    }

    @Test
    public void ensureDeletingAMissingAssetWithAnArchivePatheturnsCorrectStatus() {
        assertNull(getArchive().delete(new BasicPath("/", "test.properties")));
    }

    @Test
    public void ensureDeletingAMissingAssetWithAStringPathReturnsCorrectStatus() {
        assertNull(getArchive().delete("/test.properties"));
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
        getArchive().add(ASSET_1, location);

        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(location).getAsset()));
    }

    @Test
    public void ensureAnAssetCanBeRetrievedByItsStringPath() {
        ArchivePath location = new BasicPath("/", "test.properties");
        getArchive().add(ASSET_1, location);

        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(location.get()).getAsset()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureGetAssetRequiresAPath() {
        getArchive().get((ArchivePath) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureGetAssetByStringRequiresAPath() {
        getArchive().get((String) null);
    }

    @Test
    public void testImportArchiveAsTypeFromString() throws Exception {
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), "/test/cl-test.jar");

        JavaArchive jar = archive.getAsType(JavaArchive.class, "/test/cl-test.jar", ArchiveFormat.ZIP).add(
            new StringAsset("test file content"), "test.txt");

        assertEquals("/test/cl-test.jar", jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get("/test/cl-test.jar").getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromStringUsingDefaultFormat() throws Exception {
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
                new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), "/test/cl-test.jar");

        JavaArchive jar = archive.getAsType(JavaArchive.class, "/test/cl-test.jar").add(new StringAsset("test file content"),
                "test.txt");

        assertEquals("/test/cl-test.jar", jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get("/test/cl-test.jar").getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromArchivePath() throws Exception {
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), "/test/cl-test.jar");

        JavaArchive jar = archive.getAsType(JavaArchive.class, ArchivePaths.create("/test/cl-test.jar"), ArchiveFormat.ZIP)
            .add(new StringAsset("test file content"), "test.txt");

        assertEquals("/test/cl-test.jar", jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get("/test/cl-test.jar").getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromArchivePathUsingDefaultFormat() throws Exception {
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), "/test/cl-test.jar");

        JavaArchive jar = archive.getAsType(JavaArchive.class, ArchivePaths.create("/test/cl-test.jar")).add(
                new StringAsset("test file content"), "test.txt");

        assertEquals("/test/cl-test.jar", jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get("/test/cl-test.jar").getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromFilter() throws Exception {
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), "/test/cl-test.jar");

        Collection<JavaArchive> jars = archive.getAsType(JavaArchive.class, Filters.include(".*jar"), ArchiveFormat.ZIP);

        assertEquals("Unexpected result found", 1, jars.size());

        JavaArchive jar = jars.iterator().next().add(new StringAsset("test file content"), "test.txt");

        assertEquals("/test/cl-test.jar", jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get("/test/cl-test.jar").getAsset()).getArchive().get("test.txt"));
    }

    @Test
    public void testImportArchiveAsTypeFromFilterUsingDefaultFormat() throws Exception {
        GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(
            new FileAsset(TestIOUtil.createFileFromResourceName("cl-test.jar")), "/test/cl-test.jar");

        Collection<JavaArchive> jars = archive.getAsType(JavaArchive.class, Filters.include(".*jar"));

        assertEquals("Unexpected result found", 1, jars.size());

        JavaArchive jar = jars.iterator().next().add(new StringAsset("test file content"), "test.txt");

        assertEquals("/test/cl-test.jar", jar.getName());
        assertNotNull(jar.get("test/classloader/DummyClass.class"));
        assertNotNull(jar.get("test/classloader/DummyClass$DummyInnerClass.class"));
        assertNotNull(((ArchiveAsset) archive.get("/test/cl-test.jar").getAsset()).getArchive().get("test.txt"));
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
    public void ensureWeCanGetAnAddedArchiveWithItsStringName() {
        GenericArchive child = ShrinkWrap.create(GenericArchive.class);
        getArchive().add(child, "/", ZipExporter.class);

        assertNotNull(getArchive().getAsType(GenericArchive.class, child.getName()));
    }

    @Test
    public void ensureWeCanGetAnAddedArchiveWithItsArchivePath() {
        GenericArchive child = ShrinkWrap.create(GenericArchive.class);
        getArchive().add(child, "/", ZipExporter.class);

        assertNotNull(getArchive().getAsType(GenericArchive.class, ArchivePaths.create(child.getName())));
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

        assertEquals(2, matches.size());

        for (GenericArchive match : matches) {
            if (!match.getName().equals(child1.getName()) && !match.getName().equals(child2.getName())) {
                fail("Wrong getArchive() found, " + match.getName() + ". Expected " + child1.getName() + " or "
                    + child2.getName());
            }
        }
    }

    @Test
    public void ensureGetContentReturnsTheCorrectMapOfContent() {
        ArchivePath location1 = new BasicPath("/", "test.properties");
        ArchivePath location2 = new BasicPath("/", "test2.properties");

        getArchive().add(ASSET_1, location1).add(ASSET_2, location2);

        Map<ArchivePath, Node> content = getArchive().getContent();

        assertArrayEquals(bytes(ASSET_1), bytes(content.get(location1).getAsset()));
        assertArrayEquals(bytes(ASSET_2), bytes(content.get(location2).getAsset()));
    }

    @Test
    public void ensureGetContentReturnsTheCorrectMapOfContentBasedOnTheGivenFilter() {
        ArchivePath location1 = new BasicPath("/", "test.properties");
        ArchivePath location2 = new BasicPath("/", "test2.properties");

        getArchive().add(ASSET_1, location1).add(ASSET_2, location2);

        Map<ArchivePath, Node> content = getArchive().getContent(Filters.include(".*test2.*"));

        assertEquals(1, content.size());
        assertNull(content.get(location1));
        assertNotNull(content.get(location2));
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
        getArchive().add(ASSET_1, new BasicPath("/", "test.properties"));
        getArchive().add(ASSET_2, ArchivePaths.create("/test.properties/somewhere"));
    }

    @Test(expected = IllegalArchivePathException.class)
    public void ensureThatTryingToAddADirectoryOnAnIllegalPathThrowsAnException() {
        getArchive().add(ASSET_1, new BasicPath("/somewhere/test.properties"));
        getArchive().addAsDirectory("/somewhere/test.properties/test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void ensureMergingContentRequiresASourceArchive() {
        getArchive().merge(null);
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveSuccessfullyStoresAllAssets() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location1 = new BasicPath("/", "test.properties");
        ArchivePath location2 = new BasicPath("/", "test2.properties");

        sourceArchive.add(ASSET_1, location1).add(ASSET_2, location2);

        getArchive().merge(sourceArchive);

        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(location1).getAsset()));
        assertArrayEquals(bytes(ASSET_2), bytes(getArchive().get(location2).getAsset()));
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveToAPathSuccessfullyStoresAllAssetsToSpecificPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location1 = new BasicPath("/", "test.properties");
        ArchivePath location2 = new BasicPath("/", "test2.properties");

        sourceArchive.add(ASSET_1, location1).add(ASSET_2, location2);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().merge(sourceArchive, baseLocation);

        ArchivePath expectedPath1 = new BasicPath(baseLocation, location1);
        ArchivePath expectedPath2 = new BasicPath(baseLocation, location2);

        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(expectedPath1).getAsset()));
        assertArrayEquals(bytes(ASSET_2), bytes(getArchive().get(expectedPath2).getAsset()));
    }

    @Test
    public void ensureMergingContentFromAnotherArchiveToAStringPathSuccessfullyStoresAllAssetsToSpecificPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location1 = new BasicPath("/", "test.properties");
        ArchivePath location2 = new BasicPath("/", "test2.properties");

        sourceArchive.add(ASSET_1, location1).add(ASSET_2, location2);

        getArchive().merge(sourceArchive, "somewhere");

        ArchivePath expectedPath = new BasicPath("somewhere", location1);
        ArchivePath expectedPathTwo = new BasicPath("somewhere", location2);

        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(expectedPath).getAsset()));
        assertArrayEquals(bytes(ASSET_2), bytes(getArchive().get(expectedPathTwo).getAsset()));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMergingOnAPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location1 = new BasicPath("/", "test.properties");
        ArchivePath location2 = new BasicPath("/", "test2.properties");

        sourceArchive.add(ASSET_1, location1).add(ASSET_2, location2);

        ArchivePath baseLocation = new BasicPath("somewhere");

        getArchive().merge(sourceArchive, baseLocation, Filters.include(".*test2.*"));

        assertEquals(1, numberOfAssetsIn(getArchive()));
        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(new BasicPath(baseLocation, location2)).getAsset()));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMergingOnAStringPath() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location1 = new BasicPath("/", "test.properties");
        ArchivePath location2 = new BasicPath("/", "test2.properties");

        sourceArchive.add(ASSET_1, location1).add(ASSET_2, location2);

        getArchive().merge(sourceArchive, "somewhere", Filters.include(".*test2.*"));

        assertEquals(1, numberOfAssetsIn(getArchive()));
        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(new BasicPath("somewhere", location2)).getAsset()));
    }

    @Test
    public void ensureThatTheFilterIsUsedWhenMerging() {
        Archive<T> sourceArchive = createNewArchive();
        ArchivePath location = new BasicPath("/", "test.properties");
        ArchivePath locationTwo = new BasicPath("/", "test2.properties");

        sourceArchive.add(ASSET_1, location).add(ASSET_2, locationTwo);

        getArchive().merge(sourceArchive, Filters.include(".*test2.*"));

        assertEquals(1, numberOfAssetsIn(getArchive()));
        assertArrayEquals(bytes(ASSET_1), bytes(getArchive().get(locationTwo).getAsset()));
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

        Node node = getArchive().get(new BasicPath(baseLocation, sourceArchive.getName()));
        assertTrue(node.getAsset() instanceof ArchiveAsset);
        assertEquals(sourceArchive, ArchiveAsset.class.cast(node.getAsset()).getArchive());
    }

    @Test
    public void ensureAnArchiveContainsAssetsFromNestedArchives() {
        Archive<T> sourceArchive = createNewArchive();
        sourceArchive.add(ASSET_1, new BasicPath("/", "test.properties"));

        ArchivePath baseLocation = new BasicPath("somewhere");
        getArchive().add(sourceArchive, baseLocation, ZipExporter.class);

        ArchivePath archivePath = new BasicPath(baseLocation, sourceArchive.getName());
        assertTrue(getArchive().contains(new BasicPath(archivePath, "test.properties")));
    }

    @Test
    public void ensureAssetsFromANestedArchiveAreAccessibleFromParentArchives() {
        Archive<T> nestedArchive = createNewArchive();
        Archive<T> nestedNestedArchive = createNewArchive();
        ArchivePath baseLocation = new BasicPath("somewhere");
        ArchivePath nestedArchivePath = new BasicPath(baseLocation, nestedArchive.getName());
        ArchivePath nestedNestedArchivePath = new BasicPath(nestedArchivePath, nestedNestedArchive.getName());

        getArchive().add(nestedArchive, baseLocation, ZipExporter.class);
        nestedArchive.add(nestedNestedArchive, ArchivePaths.root(), ZipExporter.class);
        nestedNestedArchive.add(ASSET_1, new BasicPath("/", "test.properties"));

        assertNotNull(getArchive().get(new BasicPath(nestedNestedArchivePath, "test.properties")).getAsset());
    }

    @Test
    public void ensureShallowCopyPreservesPointers() {
        getArchive().add(ASSET_1, "location");

        Archive<T> copyArchive = getArchive().shallowCopy();

        assertSame(copyArchive.get("location").getAsset(), getArchive().get("location").getAsset());
    }

    @Test
    public void ensureShallowCopyHasASeparateCollectionOfTheSamePointers() {
        getArchive().add(ASSET_1, "location");

        Archive<T> copyArchive = getArchive().shallowCopy();
        getArchive().delete("location");

        assertFalse(getArchive().contains("location"));
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

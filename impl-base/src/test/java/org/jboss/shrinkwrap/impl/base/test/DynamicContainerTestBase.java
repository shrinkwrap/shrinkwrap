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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.junit.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.IllegalOverwriteException;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.TestIOUtil;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassA;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassParent;
import org.jboss.shrinkwrap.impl.base.test.dummy.DummyClassForTest;
import org.jboss.shrinkwrap.impl.base.test.dummy.DummyInterfaceForTest;
import org.jboss.shrinkwrap.impl.base.test.dummy.nested1.EmptyClassForFiltersTest1;
import org.jboss.shrinkwrap.impl.base.test.dummy.nested2.EmptyClassForFiltersTest2;
import org.jboss.shrinkwrap.impl.base.test.dummy.nested3.EmptyClassForFiltersTest3;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * DynamicContainerTestBase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
@RunWith(ContainerTestRunner.class)
public abstract class DynamicContainerTestBase<T extends Archive<T>> extends ArchiveTestBase<T> {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    public static String MANIFEST_FILE = "MANIFEST.MF";

    // -------------------------------------------------------------------------------------||
    // Contracts ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    protected abstract ArchivePath getResourcePath();

    protected abstract ResourceContainer<T> getResourceContainer();

    protected abstract ArchivePath getClassPath();

    protected abstract ClassContainer<T> getClassContainer();

    protected abstract ArchivePath getManifestPath();

    protected abstract ManifestContainer<T> getManifestContainer();

    protected abstract ServiceProviderContainer<T> getServiceProviderContainer();

    protected abstract ArchivePath getLibraryPath();

    protected abstract LibraryContainer<T> getLibraryContainer();

    protected URL getURLForClassResource(String name) {
        return SecurityActions.getThreadContextClassLoader().getResource(name);
    }

    protected File getFileForClassResource(String name) throws Exception {
        return new File(getURLForClassResource(name).toURI());
    }

    protected Asset getAssetForClassResource(String name) {
        return new ClassLoaderAsset(name);
    }

    @Before
    public void createEmptyDirectory() {
        File emptyDir = createDirectory("org/jboss/shrinkwrap/impl/base/recursion/empty");
        Assert.assertTrue("Empty directory not found at " + emptyDir.getAbsolutePath(), emptyDir.exists());
        Assert.assertEquals("Directory not empty", emptyDir.list().length, 0);
    }

    // -------------------------------------------------------------------------------------||
    // Test Implementations - ManifestContainer -------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-142
     *
     */
    @Test
    @ArchiveType(ManifestContainer.class)
    public void testSetManifestResource() {
        getManifestContainer().setManifest(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getManifestPath(), MANIFEST_FILE);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testSetManifestResourceInPackage() {
        getManifestContainer().setManifest(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getManifestPath(), MANIFEST_FILE);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-142
     *
     * @throws Exception
     */
    @Test
    @ArchiveType(ManifestContainer.class)
    public void testSetManifestFile() throws Exception {
        getManifestContainer().setManifest(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getManifestPath(), MANIFEST_FILE);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-142
     *
     */
    @Test
    @ArchiveType(ManifestContainer.class)
    public void testSetManifestURL() {
        getManifestContainer().setManifest(getURLForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getManifestPath(), MANIFEST_FILE);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-142
     *
     */
    @Test
    @ArchiveType(ManifestContainer.class)
    public void testSetManifestAsset() {
        getManifestContainer().setManifest(getAssetForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getManifestPath(), MANIFEST_FILE);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestResource() {
        getManifestContainer().addAsManifestResource(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getManifestPath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ManifestContainer.class)
    public void testAddNonExistentManifestResource() {
        final String nonExistentResourceName = "ejb/security/ejb-jar.xml";

        // Since the resource doesn't exist the ManifestContainer implementation throws the expected exception
        getManifestContainer().addAsManifestResource(nonExistentResourceName);
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestResourceRecursively() throws Exception {
        String baseFolder = "org/jboss/shrinkwrap/impl/base/recursion";
        getManifestContainer().addAsManifestResource("org/jboss/shrinkwrap/impl/base/recursion");

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolder), getManifestPath(), baseFolder);
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestFile() throws Exception {
        getManifestContainer().addAsManifestResource(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestResourceRecursivelyWithTarget() throws Exception {
        String baseFolder = "org/jboss/shrinkwrap/impl/base/recursion";
        getManifestContainer().addAsManifestResource(baseFolder, baseFolder);

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolder), getManifestPath(), baseFolder);
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testArchiveContainsEmptyManifestResourceDirectory() throws Exception {
        String baseFolder = "org/jboss/shrinkwrap/impl/base/recursion";
        getManifestContainer().addAsManifestResource(baseFolder);

        String emptyFolderPath = baseFolder + "/empty";
        assertArchiveContainsFolderRecursively(getFileForClassResource(emptyFolderPath), getManifestPath(),
            emptyFolderPath);
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestFileRecursively() throws Exception {
        File baseFolder = getFileForClassResource("org/jboss/shrinkwrap/impl/base/recursion");
        getManifestContainer().addAsManifestResource(baseFolder);

        assertArchiveContainsFolderRecursively(baseFolder, getManifestPath(), "/recursion");
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestFileRecursivelyWithTarget() throws Exception {
        File baseFolder = getFileForClassResource("org/jboss/shrinkwrap/impl/base/recursion");
        getManifestContainer().addAsManifestResource(baseFolder, "/new-name");

        assertArchiveContainsFolderRecursively(baseFolder, getManifestPath(), "/new-name");
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestFileRecursivelyWithArchivePath() throws Exception {
        File baseFolder = getFileForClassResource("org/jboss/shrinkwrap/impl/base/recursion");
        getManifestContainer().addAsManifestResource(baseFolder, new BasicPath("/new-name"));

        assertArchiveContainsFolderRecursively(baseFolder, getManifestPath(), "/new-name");
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestResourceRecursivelyWithTargetArchivePath() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        getManifestContainer().addAsManifestResource(baseFolderPath, new BasicPath("/new-name"));

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getManifestPath(), "/new-name");
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestURL() {
        ArchivePath targetPath = new BasicPath("Test.properties");
        getManifestContainer().addAsManifestResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
        ArchivePath testPath = new BasicPath(getManifestPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestURLRecursively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        URL baseFolderURL = getURLForClassResource(baseFolderPath);
        getManifestContainer().addAsManifestResource(baseFolderURL, new BasicPath("/new-name"));

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getManifestPath(), "/new-name");
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestStringTargetResource() {
        getManifestContainer().addAsManifestResource(NAME_TEST_PROPERTIES, "Test.txt");

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestStringTargetFile() throws Exception {
        getManifestContainer().addAsManifestResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestStringTargetURL() {
        getManifestContainer().addAsManifestResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestStringTargetAsset() {
        getManifestContainer().addAsManifestResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPathTargetResource() {
        getManifestContainer().addAsManifestResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPathTargetFile() throws Exception {
        getManifestContainer().addAsManifestResource(getFileForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPathTargetURL() {
        getManifestContainer().addAsManifestResource(getURLForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPathTargetAsset() {
        getManifestContainer().addAsManifestResource(getAssetForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddServiceProvider() {
        getManifestContainer().addAsServiceProvider(DummyInterfaceForTest.class, DummyClassForTest.class);

        ArchivePath testPath = new BasicPath(getManifestPath(), "services/" + DummyInterfaceForTest.class.getName());
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddServiceProviderString() throws Exception {
        String[] impls = { "do.not.exist.impl.Dummy1", "do.not.exist.impl.Dummy2", "do.not.exist.impl.Dummy3" };
        String serviceInterface = "do.not.exist.api.Dummy";
        getManifestContainer().addAsServiceProvider(serviceInterface, impls);

        ArchivePath testPath = new BasicPath(getManifestPath(), "services/" + serviceInterface);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));

        assertServiceProviderContent(getArchive().get(testPath), impls);
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ManifestContainer.class)
    public void testAddServiceProviderStringInterfaceValidation() {
        String[] impls = { "do.not.exist.impl.Dummy1", "do.not.exist.impl.Dummy2", "do.not.exist.impl.Dummy3" };
        getManifestContainer().addAsServiceProvider(null, impls);
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ManifestContainer.class)
    public void testAddServiceProviderStringImplementationsValidation() {
        getManifestContainer().addAsServiceProvider("do.not.exist.impl.Dummy1", (String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ManifestContainer.class)
    public void testAddServiceProviderStringImplementationsValueValidation() {
        String[] impls = { "do.not.exist.impl.Dummy1", null };
        getManifestContainer().addAsServiceProvider("do.not.exist.impl.Dummy", impls);
    }

    protected void assertServiceProviderContent(Node node, String[] impls) throws IOException {
        try (BufferedReader reader = createReader(node.getAsset())) {
            for (String impl : impls) {
                Assert.assertEquals("Wrong entry in service provider: " + impl, impl, reader.readLine());
            }
        }
    }

    private BufferedReader createReader(Asset asset) {
        InputStream openStream = asset.openStream();
        return new BufferedReader(new InputStreamReader(openStream));
    }

    @Test
    @ArchiveType(ServiceProviderContainer.class)
    public void testAddServiceProviderWithClasses() {
        getServiceProviderContainer().addAsServiceProviderAndClasses(DummyInterfaceForTest.class,
            DummyClassForTest.class);

        ArchivePath testPath = new BasicPath(getManifestPath(), "services/" + DummyInterfaceForTest.class.getName());
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));

        Class<?>[] expectedResources = { DummyInterfaceForTest.class, DummyClassForTest.class };
        for (Class<?> expectedResource : expectedResources) {
            ArchivePath expectedClassPath = new BasicPath(getClassPath(),
                AssetUtil.getFullPathForClassResource(expectedResource));
            assertContainsClass(expectedClassPath);
        }
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPackage() {
        getManifestContainer().addAsManifestResource(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getManifestPath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPackages() {
        getManifestContainer().addAsManifestResources(AssetUtil.class.getPackage(), "Test.properties",
            "Test2.properties");
        ArchivePath testPath = new BasicPath(getManifestPath(), NAME_TEST_PROPERTIES);
        ArchivePath testPath2 = new BasicPath(getManifestPath(), NAME_TEST_PROPERTIES_2);

        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPackageStringTarget() {
        getManifestContainer().addAsManifestResource(AssetUtil.class.getPackage(), "Test.properties", "Test.txt");

        ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestPackagePathTarget() {
        ArchivePath targetPath = ArchivePaths.create("Test.txt");

        getManifestContainer().addAsManifestResource(AssetUtil.class.getPackage(), "Test.properties", targetPath);

        ArchivePath testPath = new BasicPath(getManifestPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    // -------------------------------------------------------------------------------------||
    // Test Implementations - ResourceContainer -------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceResource() {
        getResourceContainer().addAsResource(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getResourcePath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceFile() throws Exception {
        getResourceContainer().addAsResource(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceFileRecusively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        File baseFolder = getFileForClassResource(baseFolderPath);
        getResourceContainer().addAsResource(baseFolder);

        assertArchiveContainsFolderRecursively(baseFolder, getResourcePath(), "/recursion");
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceFileRecusivelyWithTarget() throws Exception {
        File baseFolder = getFileForClassResource("org/jboss/shrinkwrap/impl/base/recursion");
        getResourceContainer().addAsResource(baseFolder, "/new-name");

        assertArchiveContainsFolderRecursively(baseFolder, getResourcePath(), "/new-name");
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceRecusively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        getResourceContainer().addAsResource(baseFolderPath);

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getResourcePath(),
            baseFolderPath);
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceRecusivelyWithTarget() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        getResourceContainer().addAsResource(baseFolderPath, "/new-name");

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getResourcePath(), "/new-name");
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceRecusivelyWithTargetPath() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        getResourceContainer().addAsResource(baseFolderPath, new BasicPath("/new-name"));

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getResourcePath(), "/new-name");
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceURL() {
        ArchivePath targetPath = new BasicPath("Test.properties");
        getResourceContainer().addAsResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
        ArchivePath testPath = new BasicPath(getResourcePath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * SHRINKWRAP-275
     */
    @Test
    @ArchiveType(ManifestContainer.class)
    public void testAddManifestStringTargetResourceFromJar() {
        // Causing NPE
        getManifestContainer().addAsManifestResource("java/lang/String.class", "String.class");

        ArchivePath testPath = new BasicPath(getManifestPath(), "String.class");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * SHRINKWRAP-275
     */
    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceStringTargetResourceFromJar() {
        // Causing NPE
        getResourceContainer().addAsResource("java/lang/String.class", "String.class");

        ArchivePath testPath = new BasicPath(getResourcePath(), "String.class");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Logger.getAnonymousLogger().info(getArchive().toString(true));
    }

    /*
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-145 - Should be Resource, Target
     */
    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceStringTargetResource() {
        getResourceContainer().addAsResource(NAME_TEST_PROPERTIES, "Test.txt");

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /*
     * https://issues.jboss.org/browse/SHRINKWRAP-187 - Do not override existing paths.
     */
    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceStringTargetResourceOverride() {
        ArchivePath targetPath = new BasicPath("META-INF/Test.txt");
        ArchivePath targetPath2 = new BasicPath("META-INF");

        getResourceContainer().addAsResource(NAME_TEST_PROPERTIES, targetPath);

        boolean gotExpectedException = false;
        try {
            getResourceContainer().addAsResource(NAME_TEST_PROPERTIES, targetPath2);
        } catch (final IllegalOverwriteException ioe) {
            gotExpectedException = true;
        }

        ArchivePath testPath = new BasicPath(getResourcePath(), "META-INF/Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue(gotExpectedException);
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceStringTargetFile() throws Exception {
        getResourceContainer().addAsResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceStringTargetURL() {

        getResourceContainer().addAsResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceUrlWithTargetStringRecursively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        URL baseFolderUrl = getURLForClassResource(baseFolderPath);
        getResourceContainer().addAsResource(baseFolderUrl, "/new-name");

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getResourcePath(), "/new-name");
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceUrlWithTargetArchviePathRecursively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        URL baseFolderUrl = getURLForClassResource(baseFolderPath);
        getResourceContainer().addAsResource(baseFolderUrl, new BasicPath("/new-name"));

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getResourcePath(), "/new-name");
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testArchiveContainsEmptyResourceDirectory() throws Exception {
        String baseFolder = "org/jboss/shrinkwrap/impl/base/recursion";
        getResourceContainer().addAsResource(baseFolder);

        String emptyFolderPath = baseFolder + "/empty";
        assertArchiveContainsFolderRecursively(getFileForClassResource(emptyFolderPath), getResourcePath(),
            emptyFolderPath);
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourceStringTargetAsset() {
        getResourceContainer().addAsResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePathTargetResource() {
        getResourceContainer().addAsResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePathTargetFile() throws Exception {
        getResourceContainer().addAsResource(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePathTargetURL() {
        getResourceContainer().addAsResource(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePathTargetAsset() {
        getResourceContainer().addAsResource(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePackage() {
        getResourceContainer().addAsResource(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getResourcePath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePackages() {
        getResourceContainer().addAsResources(AssetUtil.class.getPackage(), "Test.properties", "Test2.properties");

        ArchivePath testPath = new BasicPath(getResourcePath(), NAME_TEST_PROPERTIES);
        ArchivePath testPath2 = new BasicPath(getResourcePath(), NAME_TEST_PROPERTIES_2);

        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePackageStringTarget() {

        getResourceContainer().addAsResource(AssetUtil.class.getPackage(), "Test.properties", "Test.txt");

        ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceContainer.class)
    public void testAddResourcePackagePathTarget() {

        ArchivePath targetPath = ArchivePaths.create("Test.txt");

        getResourceContainer().addAsResource(AssetUtil.class.getPackage(), "Test.properties", targetPath);

        ArchivePath testPath = new BasicPath(getResourcePath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    // -------------------------------------------------------------------------------------||
    // Test Implementations - ClassContainer ----------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensure a class can be added to a container
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClass() {
        getClassContainer().addClass(DummyClassA.class);

        this.ensureClassesAdded();
    }

    /**
     * Ensure classes can be added to containers
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClasses() {
        getClassContainer().addClasses(DummyClassA.class);

        this.ensureClassesAdded();
    }

    /**
     * Ensures that the "addClass*" tests result in all expected classes added
     */
    private void ensureClassesAdded() {
        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassA.class));

        assertContainsClass(expectedPath);

        // SHRINKWRAP-106
        // Ensure inner classes are added
        final ArchivePath expectedPathInnerClass = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassA.InnerClass.class));
        final ArchivePath expectedPathInnerClassParent = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassParent.ParentInnerClass.class));

        Assert.assertTrue("Adding a class should also add its inner classes",
            getArchive().contains(expectedPathInnerClass));
        Assert.assertFalse("Adding a class should not add the public inner classes of its parent", getArchive()
            .contains(expectedPathInnerClassParent));

        // Ensure anonymous/private inner classes are added
        final ArchivePath expectedPathPrivateInnerClass = new BasicPath(getClassPath(), AssetUtil
            .getFullPathForClassResource(DummyClassA.InnerClass.class).get().replaceAll("InnerClass", "Test"));

        final ArchivePath expectedPathAnonymousInnerClass = new BasicPath(getClassPath(), AssetUtil
            .getFullPathForClassResource(DummyClassA.InnerClass.class).get().replaceAll("InnerClass", "1"));

        Assert.assertTrue("Adding a class should also add its private inner classes",
            getArchive().contains(expectedPathPrivateInnerClass));
        Assert.assertTrue("Adding a class should also add the anonymous inner classes",
            getArchive().contains(expectedPathAnonymousInnerClass));

    }

    /**
     * Ensure classes can be added to containers by name
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClassesByFqn() {
        final Class<?> classToAdd = DummyClassA.class;

        getClassContainer().addClass(classToAdd.getName());

        ArchivePath expectedPath = new BasicPath(getClassPath(), AssetUtil.getFullPathForClassResource(classToAdd));
        assertContainsClass(expectedPath);
    }

    /**
     * Ensure classes can be added to containers by name using a classloader
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClassesByFqnAndTccl() {
        final Class<?> classToAdd = DummyClassA.class;

        getClassContainer().addClass(classToAdd.getName(), classToAdd.getClassLoader());

        ArchivePath expectedPath = new BasicPath(getClassPath(), AssetUtil.getFullPathForClassResource(classToAdd));
        assertContainsClass(expectedPath);
    }

    /**
     * Ensure classes can be added to containers by name using a classloader
     *
     * @throws Exception
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClassByFqnAndClassLoader() throws Exception {
        ClassLoader emptyClassLoader = new ClassLoader(null) {
        };
        ClassLoader originalClassLoader = SecurityActions.getThreadContextClassLoader();
        ClassLoaderTester classCl = new ClassLoaderTester("cl-test.jar");

        try {
            Thread.currentThread().setContextClassLoader(emptyClassLoader);
            getClassContainer().addClass("test.classloader.DummyClass", classCl);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }

        Assert.assertTrue("Classloader not used to load inner class", classCl.isUsedForInnerClasses());
        ArchivePath expectedClassPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource("/test/classloader/DummyClass"));
        assertContainsClass(expectedClassPath);
    }

    /**
     * Ensure classes from the Bootstrap CL can be added to {@link ClassContainer}s.
     *
     * SHRINKWRAP-335
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClassFromBootstrapCl() {
        final ClassContainer<T> archive = this.getClassContainer();
        archive.addClass(String.class);
        final ArchivePath classRoot = this.getClassPath();
        Assert.assertTrue("Archive does not contain class added from bootstrap CL",
            ((Archive<?>) archive).contains(ArchivePaths.create(classRoot, "/java/lang/String.class")));
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClassFromCustomClassloader() throws Exception {
        ClassLoader emptyClassLoader = new ClassLoader(null) {
        };
        ClassLoader originalClassLoader = SecurityActions.getThreadContextClassLoader();
        ClassLoaderTester myClassLoader = new ClassLoaderTester("cl-test.jar");

        try {
            Thread.currentThread().setContextClassLoader(emptyClassLoader);
            Class<?> dummyClass = myClassLoader.loadClass("test.classloader.DummyClass");
            getClassContainer().addClass(dummyClass);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }

        Assert.assertTrue("Classloader not used to load inner class", myClassLoader.isUsedForInnerClasses());
        ArchivePath expectedClassPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource("/test/classloader/DummyClass"));
        assertContainsClass(expectedClassPath);
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddClassesFromCustomClassloader() throws Exception {
        ClassLoader emptyClassLoader = new ClassLoader(null) {
        };
        ClassLoader originalClassLoader = SecurityActions.getThreadContextClassLoader();
        ClassLoaderTester myClassLoader = new ClassLoaderTester("cl-test.jar");

        try {
            Thread.currentThread().setContextClassLoader(emptyClassLoader);
            Class<?> dummyClass = myClassLoader.loadClass("test.classloader.DummyClass");
            Class<?> dummyInnerClass = myClassLoader.loadClass("test.classloader.DummyClass$DummyInnerClass");
            getClassContainer().addClasses(dummyClass, dummyInnerClass);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }

        Assert.assertTrue("Classloader not used to load inner class", myClassLoader.isUsedForInnerClasses());
        String[] expetedResources = { "/test/classloader/DummyClass", "/test/classloader/DummyClass$DummyInnerClass" };
        for (String expectedResource : expetedResources) {
            ArchivePath expectedClassPath = new BasicPath(getClassPath(),
                AssetUtil.getFullPathForClassResource(expectedResource));
            assertContainsClass(expectedClassPath);
        }
    }

    /**
     * Ensure a package can be added to a container
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddPackage() {
        getClassContainer().addPackage(DummyClassA.class.getPackage());

        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassA.class));

        assertContainsClass(expectedPath);
    }

    /**
     * Ensure packages can be added to containers
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddPackageNonRecursive() {
        getClassContainer().addPackages(false, DummyClassA.class.getPackage());

        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassA.class));

        assertContainsClass(expectedPath);
    }

    /**
     * Ensure packages can be added with filters
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddPackageRecursiveFiltered() {
        getClassContainer().addPackages(true, Filters.include(DynamicContainerTestBase.class),
            DynamicContainerTestBase.class.getPackage());

        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DynamicContainerTestBase.class));

        Assert.assertEquals("Should only be one class added", 1, numAssets(getArchive()));

        assertContainsClass(expectedPath);
    }

    /**
     * Ensure a package as a String can be added to a container
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddPackageAsString() {
        getClassContainer().addPackage(DummyClassA.class.getPackage().getName());

        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassA.class));

        assertContainsClass(expectedPath);
    }

    /**
     * Ensure a package as a String can be added to a container
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddPackageAsStringNonRecursive() {
        getClassContainer().addPackages(false, DummyClassA.class.getPackage().getName());

        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassA.class));

        assertContainsClass(expectedPath);
    }

    /**
     * Ensure a package as a String can be added to a container with filter
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testAddPackageAsStringRecursiveFiltered() {
        getClassContainer().addPackages(true, Filters.include(DynamicContainerTestBase.class),
            DynamicContainerTestBase.class.getPackage().getName());

        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DynamicContainerTestBase.class));

        Assert.assertEquals("Should only be one class added", 1, numAssets(getArchive()));

        assertContainsClass(expectedPath);
    }

    /**
     * SHRINKWRAP-233: Tests adding a non existent package doesn't add any asset to the archive.
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddNonExistentPackage() {
        final String packageName = "non.existent.package";
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);

        // Here the exception should be thrown
        archive.addPackages(true, Package.getPackage(packageName));
    }

    /**
     * Ensure a package as a String can be added to a container with filter
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testShouldIcludeOnlySelectedPackages() {
        Package parent = DummyClassForTest.class.getPackage();
        Package nested1 = EmptyClassForFiltersTest1.class.getPackage();
        Package nested2 = EmptyClassForFiltersTest2.class.getPackage();

        getClassContainer().addPackages(true, Filters.include(nested1, nested2), parent.getName());

        ArchivePath expectedPath1 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(EmptyClassForFiltersTest1.class));

        ArchivePath expectedPath2 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(EmptyClassForFiltersTest2.class));

        ArchivePath notExpectedPath1 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(EmptyClassForFiltersTest3.class));

        ArchivePath notExpectedPath2 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassForTest.class));

        Assert.assertEquals("Should only include selected packages", 2, numAssets(getArchive()));

        assertContainsClass(expectedPath1);
        assertContainsClass(expectedPath2);
        assertNotContainsClass(notExpectedPath1);
        assertNotContainsClass(notExpectedPath2);
    }

    /**
     * Ensure a package as a String can be added to a container with filter
     *
     */
    @Test
    @ArchiveType(ClassContainer.class)
    public void testShouldExcludeOnlySelectedPackages() {
        Package parent = DummyClassForTest.class.getPackage();
        Package nested1 = EmptyClassForFiltersTest1.class.getPackage();
        Package nested2 = EmptyClassForFiltersTest2.class.getPackage();

        getClassContainer().addPackages(true, Filters.exclude(nested1, nested2), parent.getName());

        ArchivePath expectedPath1 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(EmptyClassForFiltersTest3.class));

        ArchivePath expectedPath2 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DummyClassForTest.class));

        ArchivePath notExpectedPath1 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(EmptyClassForFiltersTest1.class));

        ArchivePath notExpectedPath2 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(EmptyClassForFiltersTest2.class));

        assertContainsClass(expectedPath1);
        assertContainsClass(expectedPath2);
        assertNotContainsClass(notExpectedPath1);
        assertNotContainsClass(notExpectedPath2);
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void shouldIncludeOnlySelectedClasses() {

        getClassContainer().addPackages(true, Filters.include(DynamicContainerTestBase.class, ArchiveType.class),
            DynamicContainerTestBase.class.getPackage().getName());

        ArchivePath expectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DynamicContainerTestBase.class));

        ArchivePath expectedPath2 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(ArchiveType.class));

        Assert.assertEquals("Should only include selected classes", 2, numAssets(getArchive()));

        assertContainsClass(expectedPath);

        assertContainsClass(expectedPath2);
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void shouldExcludeOnlySelectedClasses() {

        getClassContainer().addPackages(true, Filters.exclude(DynamicContainerTestBase.class, ArchiveType.class),
            DynamicContainerTestBase.class.getPackage().getName());

        ArchivePath notExpectedPath = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(DynamicContainerTestBase.class));

        ArchivePath notExpectedPath2 = new BasicPath(getClassPath(),
            AssetUtil.getFullPathForClassResource(ArchiveType.class));

        Assert.assertFalse("Archive should not contain " + notExpectedPath.get(), getArchive()
            .contains(notExpectedPath));

        Assert.assertFalse("Archive should not contain " + notExpectedPath2.get(),
            getArchive().contains(notExpectedPath2));
    }

    /*
     * Adds some exemplary classes for the delete* methods to operate on.
     */
    private void addExemplaryClasses() {
        getClassContainer().addPackages(true, DummyClassA.class.getPackage(), DummyClassForTest.class.getPackage());
        getClassContainer().addDefaultPackage();

        ArchivePath classPath1 = getArchivePathFromClass(DummyClassA.class);
        ArchivePath classPath2 = getArchivePathFromClass(DummyClassForTest.class);
        ArchivePath classPath3 = getArchivePathFromClass(EmptyClassForFiltersTest1.class);

        assertContainsClass(classPath1);
        assertContainsClass(classPath2);
        assertContainsClass(classPath3);
    }

    /**
     * Returns archive patch to the private inner class. Useful when checking if all inner classes were deleted from
     * archive.
     *
     * @param clazz
     *            that contains inner class
     * @param className
     *            name of the inner class within <code>clazz</code>
     *
     * @return archive path
     */
    String getPrivInnerClassPath(final Class<?> clazz, final String className) {
        final String innerClassPath = "/" + clazz.getName().replace(".", "/");
        final String privInnerClassPath = innerClassPath + "$" + className;

        return privInnerClassPath + ".class";
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeleteClass() {
        addExemplaryClasses();

        getClassContainer().deleteClass(DummyClassA.class);

        assertNotContainsClass(getArchivePathFromClass(DummyClassA.class));
        assertNotContainsClass(getArchivePathFromClass(DummyClassA.InnerClass.class));
        assertNotContainsClass(getPrivInnerClassPath(DummyClassA.class, "Test"));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeleteClassNullParam() {
        addExemplaryClasses();

        getClassContainer().deleteClass((Class<?>) null);

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeleteClassByFqn() {
        addExemplaryClasses();

        getClassContainer().deleteClass(DummyClassA.class.getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassA.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeleteClassByFqnNullParam() {
        addExemplaryClasses();

        getClassContainer().deleteClass((String) null);

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeleteClasses() {
        addExemplaryClasses();

        getClassContainer().deleteClasses(DummyClassA.class, DummyClassForTest.class);

        assertNotContainsClass(getArchivePathFromClass(DummyClassA.class));
        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeleteClassesNullParam() {
        addExemplaryClasses();

        getClassContainer().deleteClasses((Class<?>[]) null);

        Assert.fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeleteClassesOneClassNull() {
        addExemplaryClasses();

        getClassContainer().deleteClasses(DummyClassA.class, null, DummyClassForTest.class);

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeleteDefaultPackage() {
        addExemplaryClasses();

        getClassContainer().deleteDefaultPackage();

        // Replace with type-safe test class within default package?
        boolean actual = getArchive().contains("ClassInDefaultPackage.class");

        Assert.assertFalse("Class from default package should not be in the archive", actual);
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackage() {
        addExemplaryClasses();

        getClassContainer().deletePackage(DummyClassForTest.class.getPackage());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));
        assertNotContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));

        // Sub package class must not be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackageNullParam() {
        addExemplaryClasses();

        getClassContainer().deletePackage((Package) null);

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackageAsString() {
        addExemplaryClasses();

        getClassContainer().deletePackage(DummyClassForTest.class.getPackage().getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));
        assertNotContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));

        // Sub package class must not be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackageAsStringNullParam() {
        addExemplaryClasses();

        getClassContainer().deletePackage((String) null);

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesWithoutSubpackages() {
        addExemplaryClasses();

        getClassContainer().deletePackages(false, DummyClassForTest.class.getPackage(),
            EmptyClassForFiltersTest2.class.getPackage());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));
        assertNotContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));
        assertNotContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest2.class));

        // Sub packages, if not listed, must NOT be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesWithSubpackages() {
        addExemplaryClasses();

        getClassContainer().deletePackages(true, DummyClassForTest.class.getPackage(),
            EmptyClassForFiltersTest2.class.getPackage());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));
        assertNotContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));

        // Sub package class must also be removed
        assertNotContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesNullParam() {
        addExemplaryClasses();

        // Sub package parameter doesn't matter
        getClassContainer().deletePackages(false, (Package[]) null);

        Assert.fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesOnePackageNull() {
        addExemplaryClasses();

        // Sub package parameter doesn't matter
        getClassContainer().deletePackages(false, DummyClassForTest.class.getPackage(), null,
            EmptyClassForFiltersTest1.class.getPackage());

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringWithoutSubpackages() {
        addExemplaryClasses();

        getClassContainer().deletePackages(false, DummyClassForTest.class.getPackage().getName(),
            EmptyClassForFiltersTest2.class.getPackage().getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));
        assertNotContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));
        assertNotContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest2.class));

        // Sub packages, if not listed, must NOT be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringWithSubpackages() {
        addExemplaryClasses();

        getClassContainer().deletePackages(true, DummyClassForTest.class.getPackage().getName(),
            EmptyClassForFiltersTest2.class.getPackage().getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));
        assertNotContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));

        // Sub package class must also be removed
        assertNotContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringNullParam() {
        addExemplaryClasses();

        // Sub package parameter doesn't matter
        getClassContainer().deletePackages(false, (String[]) null);

        Assert.fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringOnePackageNull() {
        addExemplaryClasses();

        // Sub package parameter doesn't matter
        getClassContainer().deletePackages(false, DummyClassForTest.class.getPackage().getName(), null,
            EmptyClassForFiltersTest1.class.getPackage().getName());

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesFilteredWithoutSubpackages() {
        addExemplaryClasses();

        Filter<ArchivePath> filter = Filters.include(DummyClassForTest.class);

        getClassContainer().deletePackages(false, filter, DummyClassForTest.class.getPackage().getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));

        // Sub packages, if not listed, must NOT be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));

        // Classes from the same package not included in the filter must NOT be removed
        assertContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesFilteredWithSubpackages() {
        addExemplaryClasses();

        Filter<ArchivePath> filter = Filters.include(DummyClassForTest.class);

        getClassContainer().deletePackages(true, filter, DummyClassForTest.class.getPackage().getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));

        // Classes from the same (or sub-) package not included in the filter must NOT be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
        assertContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesFilteredNullFilter() {
        addExemplaryClasses();

        // Sub package and package parameters don't matter
        getClassContainer().deletePackages(false, (Filter<ArchivePath>) null, DummyClassForTest.class.getPackage());

        Assert.fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesFilteredNullPackage() {
        addExemplaryClasses();

        // Sub package and filter parameters don't matter
        getClassContainer().deletePackages(false, Filters.includeAll(), (Package) null);

        Assert.fail("Exception expected");
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringsFilteredWithoutSubpackages() {
        addExemplaryClasses();

        Filter<ArchivePath> filter = Filters.include(DummyClassForTest.class);

        getClassContainer().deletePackages(false, filter, DummyClassForTest.class.getPackage().getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));

        // Sub packages, if not listed, must NOT be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));

        // Classes from the same package not included in the filter must NOT be removed
        assertContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));
    }

    @Test
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringsFilteredWithSubpackages() {
        addExemplaryClasses();

        Filter<ArchivePath> filter = Filters.include(DummyClassForTest.class);

        getClassContainer().deletePackages(true, filter, DummyClassForTest.class.getPackage().getName());

        assertNotContainsClass(getArchivePathFromClass(DummyClassForTest.class));

        // Classes from the same (or sub-) package not included in the filter must NOT be removed
        assertContainsClass(getArchivePathFromClass(EmptyClassForFiltersTest1.class));
        assertContainsClass(getArchivePathFromClass(DummyInterfaceForTest.class));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringsFilteredNullFilter() {
        addExemplaryClasses();

        // Sub package and package parameters don't matter
        getClassContainer().deletePackages(false, (Filter<ArchivePath>) null,
            DummyClassForTest.class.getPackage().getName());

        Assert.fail("Exception expected");
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(ClassContainer.class)
    public void testDeletePackagesAsStringsFilteredNullPackage() {
        addExemplaryClasses();

        // Sub package and filter parameters don't matter
        getClassContainer().deletePackages(false, Filters.includeAll(), (String[]) null);

        Assert.fail("Exception expected");
    }

    // -------------------------------------------------------------------------------------||
    // Test Implementations - LibraryContainer ----------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryResource() {
        getLibraryContainer().addAsLibrary(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getLibraryPath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryFile() throws Exception {
        getLibraryContainer().addAsLibrary(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryResourceRecursively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        getLibraryContainer().addAsLibrary(baseFolderPath);

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getLibraryPath(),
            baseFolderPath);
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testArchiveContainsEmptyLibraryDirectory() throws Exception {
        String baseFolder = "org/jboss/shrinkwrap/impl/base/recursion";
        getLibraryContainer().addAsLibrary(baseFolder);

        String emptyFolderPath = baseFolder + "/empty";
        assertArchiveContainsFolderRecursively(getFileForClassResource(emptyFolderPath), getLibraryPath(),
            emptyFolderPath);
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryResourceWithTargetRecursively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        getLibraryContainer().addAsLibrary(baseFolderPath, "/new-name");

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getLibraryPath(), "/new-name");
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryResourceWithTargetPathRecursively() throws Exception {
        String baseFolderPath = "org/jboss/shrinkwrap/impl/base/recursion";
        getLibraryContainer().addAsLibrary(baseFolderPath, new BasicPath("/new-name"));

        assertArchiveContainsFolderRecursively(getFileForClassResource(baseFolderPath), getLibraryPath(), "/new-name");
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryFileRecursively() throws Exception {
        File baseFolder = getFileForClassResource("org/jboss/shrinkwrap/impl/base/recursion");
        getLibraryContainer().addAsLibrary(baseFolder);

        assertArchiveContainsFolderRecursively(baseFolder, getLibraryPath(), "/recursion");
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryURLRecursively() {
        URL baseFolder = getURLForClassResource("org/jboss/shrinkwrap/impl/base/recursion");
        getLibraryContainer().addAsLibrary(baseFolder, "/recursion");

        assertArchiveContainsFolderRecursively(new File(baseFolder.getFile()), getLibraryPath(), "/recursion");
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryURLWithArchivePathRecursively() {
        URL baseFolder = getURLForClassResource("org/jboss/shrinkwrap/impl/base/recursion");
        getLibraryContainer().addAsLibrary(baseFolder, new BasicPath("/recursion"));

        assertArchiveContainsFolderRecursively(new File(baseFolder.getFile()), getLibraryPath(), "/recursion");
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryURL() {
        final ArchivePath targetPath = new BasicPath("Test.properties");
        getLibraryContainer().addAsLibrary(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
        ArchivePath testPath = new BasicPath(getLibraryPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryStringTargetResource() {
        getLibraryContainer().addAsLibrary(NAME_TEST_PROPERTIES, "Test.txt");

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryStringTargetFile() throws Exception {
        getLibraryContainer().addAsLibrary(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryStringTargetURL() {
        getLibraryContainer().addAsLibrary(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryStringTargetAsset() {
        getLibraryContainer().addAsLibrary(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryPathTargetResource() {
        getLibraryContainer().addAsLibrary(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryPathTargetFile() throws Exception {
        getLibraryContainer().addAsLibrary(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryPathTargetURL() {
        getLibraryContainer().addAsLibrary(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryPathTargetAsset() {
        getLibraryContainer().addAsLibrary(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryArchive() {
        Archive<?> archive = createNewArchive();
        getLibraryContainer().addAsLibrary(archive);

        ArchivePath testPath = new BasicPath(getLibraryPath(), archive.getName());
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibrariesResource() {
        getLibraryContainer().addAsLibraries(NAME_TEST_PROPERTIES, NAME_TEST_PROPERTIES_2);

        ArchivePath testPath = new BasicPath(getLibraryPath(), NAME_TEST_PROPERTIES);
        ArchivePath testPath2 = new BasicPath(getLibraryPath(), NAME_TEST_PROPERTIES_2);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibrariesFile() throws Exception {
        getLibraryContainer().addAsLibraries(getFileForClassResource(NAME_TEST_PROPERTIES),
            getFileForClassResource(NAME_TEST_PROPERTIES_2));

        ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.properties");
        ArchivePath testPath2 = new BasicPath(getLibraryPath(), "Test2.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibrariesArchive() {
        Archive<?> archive = createNewArchive();
        Archive<?> archive2 = createNewArchive();

        getLibraryContainer().addAsLibraries(archive, archive2);

        ArchivePath testPath = new BasicPath(getLibraryPath(), archive.getName());
        ArchivePath testPath2 = new BasicPath(getLibraryPath(), archive.getName());
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibrariesArchiveCollection() {
        Archive<?> archive = createNewArchive();
        Archive<?> archive2 = createNewArchive();
        final Collection<Archive<?>> archives = new ArrayList<>();
        archives.add(archive);
        archives.add(archive2);

        getLibraryContainer().addAsLibraries(archives);

        ArchivePath testPath = new BasicPath(getLibraryPath(), archive.getName());
        ArchivePath testPath2 = new BasicPath(getLibraryPath(), archive.getName());
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(LibraryContainer.class)
    public void testAddLibrariesArchiveArrays() {
        Archive<?> archive1 = createNewArchive();
        Archive<?> archive2 = createNewArchive();
        Archive<?> archive3 = createNewArchive();
        Archive<?> archive4 = createNewArchive();
        Archive<?>[] archiveArray1 = { archive1, archive2 };
        Archive<?>[] archiveArray2 = { archive3, archive4 };

        getLibraryContainer().addAsLibraries(archiveArray1, archiveArray2);

        ArchivePath testPath1 = new BasicPath(getLibraryPath(), archive1.getName());
        ArchivePath testPath2 = new BasicPath(getLibraryPath(), archive2.getName());
        ArchivePath testPath3 = new BasicPath(getLibraryPath(), archive3.getName());
        ArchivePath testPath4 = new BasicPath(getLibraryPath(), archive4.getName());
        Assert.assertTrue("Archive should contain " + testPath1, getArchive().contains(testPath1));
        Assert.assertTrue(

        "Archive should contain " + testPath2, getArchive().contains(testPath2));
        Assert.assertTrue("Archive should contain " + testPath3, getArchive().contains(testPath3));
        Assert.assertTrue("Archive should contain " + testPath4, getArchive().contains(testPath4));
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(LibraryContainer.class)
    public void testAddLibrariesArchiveArraysWithNullArguments() {
        Archive<?> archive1 = createNewArchive();
        Archive<?> archive2 = createNewArchive();
        Archive<?> archive3 = createNewArchive();
        Archive<?> archive4 = createNewArchive();
        Archive<?>[] archives = { archive1, archive2, archive3, archive4 };

        getLibraryContainer().addAsLibraries(archives, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(LibraryContainer.class)
    public void testAddLibrariesArchiveArraysWithNullValues() {
        Archive<?> archive1 = createNewArchive();
        Archive<?> archive2 = createNewArchive();
        Archive<?> archive3 = createNewArchive();
        Archive<?> archive4 = createNewArchive();
        Archive<?>[] archives = { archive1, archive2, archive3, null, archive4, null };

        getLibraryContainer().addAsLibraries(archives);
    }

    @Test(expected = IllegalArgumentException.class)
    @ArchiveType(LibraryContainer.class)
    public void testAddLibraryNotExistingResource() {
        getLibraryContainer().addAsLibrary("notExistingPath/notExistingJar.jar", "notExistingJar.jar");
    }

    /**
     * Tests that a default MANIFEST.MF is generated through the addManifest method call. SHRINKWRAP-191
     *
     */
    @Test
    public void testAddManifest() {
        String expectedManifestPath = "META-INF/MANIFEST.MF";

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
        Assert.assertFalse("Archive should not contain manifest file", archive.contains(expectedManifestPath));

        archive.addManifest();
        Assert.assertTrue("Archive should contain manifest file: " + expectedManifestPath,
            archive.contains(expectedManifestPath));
    }

    /**
     * Reproduces the bug in SHRINKWRAP-300
     */
    @Test
    public void testAddFileWithWhitespaceInFilename() {
        String manifest = "Whitespace manifest.MF";

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
        org.junit.Assert.assertFalse("Archive should not contain file", archive.contains(manifest));
        archive.addAsResource(manifest);

        Assert.assertTrue("Archive should contain file: " + manifest, archive.contains(manifest));
    }

    /**
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-320 Empty Directory Causes FileNotFoundException
     */
    @Test
    public void testAddingEmptyResourceDirectory() throws Exception {
        final File directory = File.createTempFile("resources", null);
        directory.delete();
        directory.deleteOnExit();
        final File svn = new File(directory, ".svn");
        svn.deleteOnExit();
        svn.mkdirs();

        final ByteArrayOutputStream out = new ByteArrayOutputStream();

        ShrinkWrap.create(JavaArchive.class).addAsResource(directory, "/").as(ZipExporter.class).exportTo(out);

        final ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(out.toByteArray()));
        final ZipEntry entry = zis.getNextEntry();
        Assert.assertNotNull("Missing '.svn/' Entry from Exported Archive", entry);
        Assert.assertEquals("Zip Entry Missing Expected Name '.svn/'", ".svn/", entry.getName());
        Assert.assertEquals("Zip Entry '.svn/' Not A Directory", true, entry.isDirectory());
        zis.closeEntry();
        zis.close();
    }

    /**
     * SHRINKWRAP-329 SHRINKWRAP-389
     */
    @Test
    public void addDuplicateResourceMakesOverwrite() throws IOException {
        // Create the new archive
        final Archive<?> archive = createNewArchive();

        // Put in an asset
        final ArchivePath path = ArchivePaths.create("testPath");
        archive.add(EmptyAsset.INSTANCE, path);

        // Now try again with a new asset, and this should replace the old content
        final String content = "newContent";
        archive.add(new StringAsset(content), path);

        final String contentFound = new BufferedReader(new InputStreamReader(archive.get(path).getAsset().openStream()))
            .readLine();
        Assert.assertEquals(content, contentFound);
    }

    /**
     * SHRINKWRAP-416
     */
    @Test
    public void addResourceToExistingDirThrowsIllegalOverwriteException() {
        // Create the new archive
        final Archive<?> archive = createNewArchive();

        // Put in an asset
        final ArchivePath path = ArchivePaths.create("testPath");
        archive.addAsDirectories(path);

        // Now try again with a new asset, and this should fail
        try {
            archive.add(new StringAsset("failContent"), path);
        } catch (final IllegalOverwriteException ioe) {
            // Good
            return;
        }

        // Fail us
        Assert.fail("Expected " + IllegalOverwriteException.class.getName() + " not received");
    }

    /**
     * ` Reproduces a bug within Archive.contains, discovered in SHRINKWRAP-348
     */
    @Test
    @ArchiveType(LibraryContainer.class)
    public void containsShouldReturnFalseWhenParentNodeHasBeenDeleted() {
        Archive<?> archive = createNewArchive();
        final String archivePath = "WEB-INF/classes/org/drools/guvnor/gwtutil/";
        final String file = archivePath + "file";

        archive.add(EmptyAsset.INSTANCE, ArchivePaths.create(file));
        Assert.assertTrue(archive.contains(file));

        archive.delete(ArchivePaths.create("WEB-INF/classes/org/drools/guvnor/gwtutil"));
        Assert.assertFalse(archive.contains(ArchivePaths.create(archivePath)));
        Assert.assertFalse(archive.contains(ArchivePaths.create(file)));
    }

    /**
     * Reproduces the behaviour described in SHRINKWRAP-348
     */
    @Test
    @ArchiveType(LibraryContainer.class)
    public void shouldDeleteArchivePathWithTrailingSlash() {
        Archive<?> archive = createNewArchive();
        final String archivePath = "WEB-INF/classes/org/drools/guvnor/gwtutil/";
        final String file = archivePath + "file";

        archive.add(EmptyAsset.INSTANCE, ArchivePaths.create(file));
        Assert.assertTrue(archive.contains(file));

        archive.delete(ArchivePaths.create(archivePath));
        Assert.assertFalse(archive.contains(ArchivePaths.create(archivePath)));
        Assert.assertFalse(archive.contains(ArchivePaths.create(file)));
    }

    private void assertNotContainsClass(ArchivePath notExpectedPath) {
        Assert.assertFalse("Located unexpected class at " + notExpectedPath.get(),
            getArchive().contains(notExpectedPath));
    }

    private void assertNotContainsClass(String notExpectedPath) {
        Assert.assertFalse("Located unexpected class at " + notExpectedPath, getArchive().contains(notExpectedPath));
    }

    private void assertContainsClass(ArchivePath expectedPath) {
        Assert.assertTrue("A class should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||
    /**
     * Asserts that the archive recursively contains the specified file in the target starting from the base position.
     */
    private void assertArchiveContainsFolderRecursively(File file, ArchivePath base, String target) {
        ArchivePath testPath = new BasicPath(base, target);
        Assert.assertTrue("Archive should contain " + testPath, this.getArchive().contains(testPath));

        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                assertArchiveContainsFolderRecursively(child, base, target + "/" + child.getName());
            }
            int folderInArchiveSize = this.getArchive().get(testPath).getChildren().size();
            Assert.assertEquals("Wrong number of files in the archive folder: " + testPath.get(),
                file.listFiles().length, folderInArchiveSize);
        }
    }

    protected File getTarget() {
        try {
            return new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (final URISyntaxException urise) {
            throw new RuntimeException("Could not obtain the target URI", urise);
        }
    }

    private File createDirectory(String resourcePath) {
        File directory = this.getTarget();
        String[] split = resourcePath.split("/");
        for (String folder : split) {
            directory = new File(directory, folder);
            if (directory.exists()) {
                continue;
            }

            boolean created = directory.mkdir();
            if (!created) {
                throw new RuntimeException("Impossible to create directory at path:" + directory.getAbsolutePath());
            }
        }
        return directory;
    }

    /*
     * Used the check if the classloader is called when loading inner classes.
     */
    private class ClassLoaderTester extends URLClassLoader {

        private boolean usedForInnerClasses = false;

        public ClassLoaderTester(String resource) throws MalformedURLException, URISyntaxException {
            this(TestIOUtil.createFileFromResourceName(resource));
        }

        private ClassLoaderTester(File jar) throws MalformedURLException {
            super(new URL[] { jar.toURI().toURL() }, null);
        }

        /*
         * Called by URLPackageScanner class when looking for a resource in a package.
         *
         * @see java.lang.ClassLoader#getResources(java.lang.String)
         *
         * @see org.jboss.shrinkwrap.impl.base.URLPackageScanner#scanPackage()
         *
         * @see org.jboss.shrinkwrap.impl.base.container#addPackage(final boolean recursive, final Filter<ArchivePath>
         * filter, final ClassLoader classLoader, String packageName)
         */
        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            usedForInnerClasses = true;
            return super.getResources(name);
        }

        public boolean isUsedForInnerClasses() {
            return usedForInnerClasses;
        }

    }

    private ArchivePath getArchivePathFromClass(Class<?> clazz) {
        return new BasicPath(getClassPath(), AssetUtil.getFullPathForClassResource(clazz));
    }

}

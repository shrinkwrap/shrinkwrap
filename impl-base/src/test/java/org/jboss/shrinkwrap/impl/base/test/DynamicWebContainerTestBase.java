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

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.container.WebContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.WebArchiveImpl;
import org.jboss.shrinkwrap.impl.base.test.dummy.DummyClassForTest;
import org.jboss.shrinkwrap.impl.base.test.dummy.DummyInterfaceForTest;
import org.junit.Test;

/**
 * DynamicWebContainerTestBase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class DynamicWebContainerTestBase<T extends Archive<T>> extends DynamicContainerTestBase<T> {
    public abstract ArchivePath getWebPath();

    public abstract ArchivePath getWebInfPath();

    public abstract WebContainer<T> getWebContainer();

    // -------------------------------------------------------------------------------------||
    // Test Implementations - WebContainer ------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Test
    @ArchiveType(WebContainer.class)
    public void testSetWebXMLResource() {
        getWebContainer().setWebXML(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getWebInfPath(), "web.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testSetWebXMLResourceInPackage() {
        getWebContainer().setWebXML(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getWebInfPath(), "web.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testSetWebXMLFile() throws Exception {
        getWebContainer().setWebXML(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "web.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testSetWebXMLURL() {
        getWebContainer().setWebXML(getURLForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "web.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testSetWebXMLAsset() {
        getWebContainer().setWebXML(getAssetForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "web.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourceResource() {
        getWebContainer().addAsWebResource(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourceFile() throws Exception {
        getWebContainer().addAsWebResource(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourceURL() {
        ArchivePath targetPath = new BasicPath("Test.properties");
        getWebContainer().addAsWebResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
        ArchivePath testPath = new BasicPath(getWebPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourceStringTargetResource() {
        getWebContainer().addAsWebResource(NAME_TEST_PROPERTIES, "Test.txt");

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourceStringTargetFile() throws Exception {
        getWebContainer().addAsWebResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourceStringTargetURL() {
        getWebContainer().addAsWebResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourceStringTargetAsset() {
        getWebContainer().addAsWebResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePathTargetResource() {
        getWebContainer().addAsWebResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePathTargetFile() throws Exception {
        getWebContainer().addAsWebResource(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePathTargetURL() {
        getWebContainer().addAsWebResource(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePathTargetAsset() {
        getWebContainer().addAsWebResource(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePackage() {
        getWebContainer().addAsWebResource(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getWebPath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePackages() {
        getWebContainer().addAsWebResources(AssetUtil.class.getPackage(), "Test.properties", "Test2.properties");

        ArchivePath testPath = new BasicPath(getWebPath(), NAME_TEST_PROPERTIES);
        ArchivePath testPath2 = new BasicPath(getWebPath(), NAME_TEST_PROPERTIES_2);

        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePackageStringTarget() {

        getWebContainer().addAsWebResource(AssetUtil.class.getPackage(), "Test.properties", "Test.txt");

        ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebResourcePackagePathTarget() {

        ArchivePath targetPath = ArchivePaths.create("Test.txt");

        getWebContainer().addAsWebResource(AssetUtil.class.getPackage(), "Test.properties", targetPath);

        ArchivePath testPath = new BasicPath(getWebPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourceResource() {
        getWebContainer().addAsWebInfResource(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourceFile() throws Exception {
        getWebContainer().addAsWebInfResource(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourceURL() {
        ArchivePath targetPath = new BasicPath("Test.properties");
        getWebContainer().addAsWebInfResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
        ArchivePath testPath = new BasicPath(getWebInfPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourceStringTargetResource() {
        getWebContainer().addAsWebInfResource(NAME_TEST_PROPERTIES, "Test.txt");

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourceStringTargetFile() throws Exception {
        getWebContainer().addAsWebInfResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourceStringTargetURL() {
        getWebContainer().addAsWebInfResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourceStringTargetAsset() {
        getWebContainer().addAsWebInfResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePathTargetResource() {
        getWebContainer().addAsWebInfResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePathTargetFile() throws Exception {
        getWebContainer().addAsWebInfResource(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePathTargetURL() {
        getWebContainer().addAsWebInfResource(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePathTargetAsset() {
        getWebContainer()
            .addAsWebInfResource(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePackage() {
        getWebContainer().addAsWebInfResource(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getWebInfPath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePackages() {
        getWebContainer().addAsWebInfResources(AssetUtil.class.getPackage(), "Test.properties", "Test2.properties");

        ArchivePath testPath = new BasicPath(getWebInfPath(), NAME_TEST_PROPERTIES);
        ArchivePath testPath2 = new BasicPath(getWebInfPath(), NAME_TEST_PROPERTIES_2);

        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePackageStringTarget() {

        getWebContainer().addAsWebInfResource(AssetUtil.class.getPackage(), "Test.properties", "Test.txt");

        ArchivePath testPath = new BasicPath(getWebInfPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfResourcePackagePathTarget() {

        ArchivePath targetPath = ArchivePaths.create("Test.txt");

        getWebContainer().addAsWebInfResource(AssetUtil.class.getPackage(), "Test.properties", targetPath);

        ArchivePath testPath = new BasicPath(getWebInfPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * SHRINKWRAP-275
     */
    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebStringTargetResourceFromJar() {
        // Causing NPE
        getWebContainer().addAsWebResource("java/lang/String.class", "String.class");

        ArchivePath testPath = new BasicPath(getWebPath(), "String.class");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * SHRINKWRAP-275
     */
    @Test
    @ArchiveType(WebContainer.class)
    public void testAddWebInfStringTargetResourceFromJar() {
        // Causing NPE
        getWebContainer().addAsWebInfResource("java/lang/String.class", "String.class");

        ArchivePath testPath = new BasicPath(getWebInfPath(), "String.class");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    /**
     * SHRINKWRAP-476
     */
    @Test
    @ArchiveType(WebContainer.class)
    public void canDeleteEmbeddedArchiveAddedAsLibrary(){
        String name = "test.jar";
        WebArchive war = ShrinkWrap.create(WebArchive.class)
                .addAsLibraries(
                        ShrinkWrap.create(JavaArchive.class, name)
                                .add(EmptyAsset.INSTANCE, "some"));

        ArchivePath path = ArchivePaths.create("WEB-INF/lib", name);
        Assert.assertTrue(war.contains(path));
        war.delete(path);
        Assert.assertFalse("Path should have been deleted", war.contains(path));
    }

    /**
     * Override to handle web archive special case (service providers in WEB-INF/classes/META-INF/services)
     *
     */
    @Test
    @Override
    public void testAddServiceProvider() {
        ServiceProviderPathExposingWebArchive webArchive = new ServiceProviderPathExposingWebArchive(
            ShrinkWrap.create(WebArchive.class));
        webArchive.addAsServiceProvider(DummyInterfaceForTest.class, DummyClassForTest.class);

        ArchivePath testPath = webArchive.getServiceProvidersPath();
        Assert.assertTrue("Archive should contain " + testPath, webArchive.contains(testPath));

        testPath = new BasicPath(webArchive.getServiceProvidersPath(), DummyInterfaceForTest.class.getName());
        Assert.assertTrue("Archive should contain " + testPath, webArchive.contains(testPath));
    }

    /**
     * Override to handle web archive special case (service providers in WEB-INF/classes/META-INF/services)
     *
     * @throws Exception
     *             if an exception occurs
     */
    @Test
    @Override
    public void testAddServiceProviderString() throws Exception {
        String serviceInterface = "do.not.exist.api.Dummy";
        String[] impls = {"do.not.exist.impl.Dummy1", "do.not.exist.impl.Dummy2", "do.not.exist.impl.Dummy3"};

        ServiceProviderPathExposingWebArchive webArchive = new ServiceProviderPathExposingWebArchive(
             ShrinkWrap.create(WebArchive.class));
        webArchive.addAsServiceProvider(serviceInterface, impls);

        ArchivePath testPath = new BasicPath(webArchive.getServiceProvidersPath(), "/do.not.exist.api.Dummy");
        Assert.assertTrue("Archive should contain " + testPath, webArchive.contains(testPath));

        assertServiceProviderContent(webArchive.get(testPath), impls);
    }

    @Test(expected = IllegalArgumentException.class)
    @Override
    public void testAddServiceProviderStringInterfaceValidation() {
        String[] impls = {"do.not.exist.impl.Dummy1", "do.not.exist.impl.Dummy2", "do.not.exist.impl.Dummy3"};
        ShrinkWrap.create(WebArchive.class).addAsServiceProvider(null, impls);
    }

    @Test(expected = IllegalArgumentException.class)
    @Override
    public void testAddServiceProviderStringImplementationsValidation() {
       ShrinkWrap.create(WebArchive.class).addAsServiceProvider("do.not.exist.impl.Dummy", (String[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    @Override
    public void testAddServiceProviderStringImplementationsValueValidation() {
        String[] impls = {"do.not.exist.impl.Dummy1", null};
        ShrinkWrap.create(WebArchive.class).addAsServiceProvider("do.not.exist.impl.Dummy", impls);
    }

    /**
     * Override to handle web archive special case (service providers in WEB-INF/classes/META-INF/services)
     *
     */
    @Test
    @Override
    @ArchiveType(ServiceProviderContainer.class)
    public void testAddServiceProviderWithClasses() {
        ServiceProviderPathExposingWebArchive webArchive = new ServiceProviderPathExposingWebArchive(
            ShrinkWrap.create(WebArchive.class));
        webArchive.addAsServiceProviderAndClasses(DummyInterfaceForTest.class, DummyClassForTest.class);

        ArchivePath testPath = webArchive.getServiceProvidersPath();
        Assert.assertTrue("Archive should contain " + testPath, webArchive.contains(testPath));

        testPath = new BasicPath(webArchive.getServiceProvidersPath(), DummyInterfaceForTest.class.getName());
        Assert.assertTrue("Archive should contain " + testPath, webArchive.contains(testPath));

        Class<?>[] expectedResources = { DummyInterfaceForTest.class, DummyClassForTest.class };
        for (Class<?> expectedResource : expectedResources) {
            ArchivePath expectedClassPath = new BasicPath(getClassPath(),
                AssetUtil.getFullPathForClassResource(expectedResource));
            Assert.assertTrue("Archive should contain " + testPath, webArchive.contains(expectedClassPath));
        }
    }

    private class ServiceProviderPathExposingWebArchive extends WebArchiveImpl {
        private ServiceProviderPathExposingWebArchive(final Archive<?> delegate) {
            super(delegate);
        }

        @Override
        public ArchivePath getServiceProvidersPath() {
            return super.getServiceProvidersPath();
        }
    }
}

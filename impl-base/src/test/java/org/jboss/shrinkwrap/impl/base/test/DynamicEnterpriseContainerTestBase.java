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

import org.junit.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.container.EnterpriseContainer;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Test;

/**
 * DynamicEnterpriseContainerTestBase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class DynamicEnterpriseContainerTestBase<T extends Archive<T>> extends DynamicContainerTestBase<T> {

    protected abstract ArchivePath getModulePath();

    protected abstract ArchivePath getApplicationPath();

    protected abstract EnterpriseContainer<T> getEnterpriseContainer();

    // -------------------------------------------------------------------------------------||
    // Test Implementations - WebContainer ------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testSetApplicationXMLResource() throws Exception {
        getEnterpriseContainer().setApplicationXML(NAME_TEST_PROPERTIES);

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "application.xml");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testSetApplicationXMLResourceInPackage() throws Exception {
        getEnterpriseContainer().setApplicationXML(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "application.xml");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testSetApplicationXMLFile() throws Exception {
        getEnterpriseContainer().setApplicationXML(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "application.xml");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testSetApplicationXMLURL() throws Exception {
        getEnterpriseContainer().setApplicationXML(getURLForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "application.xml");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testSetApplicationXMLAsset() throws Exception {
        getEnterpriseContainer().setApplicationXML(getAssetForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "application.xml");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationResource() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(NAME_TEST_PROPERTIES);

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationFile() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationURL() throws Exception {
        final ArchivePath targetPath = new BasicPath("Test.properties");
        ;
        getEnterpriseContainer().addAsApplicationResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
        ArchivePath expectedPath = new BasicPath(getApplicationPath(), targetPath);
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationStringTargetResource() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(NAME_TEST_PROPERTIES, "Test.txt");

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationStringTargetFile() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationStringTargetURL() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationStringTargetAsset() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationPathTargetResource() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationPathTargetFile() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(getFileForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.txt"));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationPathTargetURL() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(getURLForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.txt"));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationPathTargetAsset() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(getAssetForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.txt"));

        ArchivePath expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddAplicationResourcePackage() throws Exception {
        getEnterpriseContainer().addAsApplicationResource(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getApplicationPath(), NAME_TEST_PROPERTIES);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationResourcePackages() throws Exception {
        getEnterpriseContainer().addAsApplicationResources(AssetUtil.class.getPackage(), "Test.properties",
            "Test2.properties");

        ArchivePath testPath = new BasicPath(getApplicationPath(), NAME_TEST_PROPERTIES);
        ArchivePath testPath2 = new BasicPath(getApplicationPath(), NAME_TEST_PROPERTIES_2);

        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
        Assert.assertTrue("Archive should contain " + testPath2, getArchive().contains(testPath2));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationResourcePackageStringTarget() throws Exception {

        getEnterpriseContainer().addAsApplicationResource(AssetUtil.class.getPackage(), "Test.properties", "Test.txt");

        ArchivePath testPath = new BasicPath(getApplicationPath(), "Test.txt");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddApplicationResourcePackagePathTarget() throws Exception {

        ArchivePath targetPath = ArchivePaths.create("Test.txt");

        getEnterpriseContainer().addAsApplicationResource(AssetUtil.class.getPackage(), "Test.properties", targetPath);

        ArchivePath testPath = new BasicPath(getApplicationPath(), targetPath);
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleResource() throws Exception {
        getEnterpriseContainer().addAsModule(NAME_TEST_PROPERTIES);

        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModulesResources() throws Exception {
        getEnterpriseContainer().addAsModules(NAME_TEST_PROPERTIES, NAME_TEST_PROPERTIES_2);

        final ArchivePath expectedPath1 = new BasicPath(getModulePath(), "Test.properties");
        final ArchivePath expectedPath2 = new BasicPath(getModulePath(), "Test2.properties");
        Assert.assertTrue("Archive should contain " + expectedPath1, getArchive().contains(expectedPath1));
        Assert.assertTrue("Archive should contain " + expectedPath2, getArchive().contains(expectedPath2));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleFile() throws Exception {
        getEnterpriseContainer().addAsModule(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModulesFiles() throws Exception {
        getEnterpriseContainer().addAsModules(getFileForClassResource(NAME_TEST_PROPERTIES),
            getFileForClassResource(NAME_TEST_PROPERTIES_2));

        final ArchivePath expectedPath1 = new BasicPath(getModulePath(), "Test.properties");
        final ArchivePath expectedPath2 = new BasicPath(getModulePath(), "Test2.properties");
        Assert.assertTrue("Archive should contain " + expectedPath1, getArchive().contains(expectedPath1));
        Assert.assertTrue("Archive should contain " + expectedPath2, getArchive().contains(expectedPath2));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleURL() throws Exception {
        final ArchivePath targetPath = new BasicPath("Test.properties");
        getEnterpriseContainer().addAsModule(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
        ArchivePath expectedPath = new BasicPath(getModulePath(), targetPath);
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleStringTargetResource() throws Exception {
        getEnterpriseContainer().addAsModule(NAME_TEST_PROPERTIES, "Test.properties");
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleStringTargetFile() throws Exception {
        getEnterpriseContainer().addAsModule(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.properties");
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleStringTargetURL() throws Exception {
        getEnterpriseContainer().addAsModule(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.properties");
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleStringTargetAsset() throws Exception {
        getEnterpriseContainer().addAsModule(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.properties");
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModulePathTargetResource() throws Exception {
        getEnterpriseContainer().addAsModule(NAME_TEST_PROPERTIES, new BasicPath("Test.properties"));
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModulePathTargetFile() throws Exception {
        getEnterpriseContainer().addAsModule(getFileForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.properties"));
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModulePathTargetURL() throws Exception {
        getEnterpriseContainer().addAsModule(getURLForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.properties"));
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModulePathTargetAsset() throws Exception {
        getEnterpriseContainer().addAsModule(getAssetForClassResource(NAME_TEST_PROPERTIES),
            new BasicPath("Test.properties"));
        ArchivePath expectedPath = new BasicPath(getModulePath(), "Test.properties");
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModuleArchive() throws Exception {
        Archive<?> archive = createNewArchive();
        getEnterpriseContainer().addAsModule(archive);

        ArchivePath expectedPath = new BasicPath(getModulePath(), archive.getName());
        Assert.assertTrue("Archive should contain " + expectedPath, getArchive().contains(expectedPath));
    }

    @Test
    @ArchiveType(EnterpriseContainer.class)
    public void testAddModulesArchives() throws Exception {
        final Archive<?> archive1 = createNewArchive();
        final Archive<?> archive2 = createNewArchive();
        getEnterpriseContainer().addAsModules(archive1, archive2);

        ArchivePath expectedPath1 = new BasicPath(getModulePath(), archive1.getName());
        ArchivePath expectedPath2 = new BasicPath(getModulePath(), archive2.getName());
        Assert.assertTrue("Archive should contain " + expectedPath1, getArchive().contains(expectedPath1));
        Assert.assertTrue("Archive should contain " + expectedPath2, getArchive().contains(expectedPath2));
    }
}
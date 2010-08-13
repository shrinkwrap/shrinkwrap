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
import org.jboss.shrinkwrap.api.container.ConvergedSipWebContainer;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Test;

/**
 * DynamicWebContainerTestBase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class DynamicConvergedSipWebContainerTestBase<T extends Archive<T>> extends DynamicContainerTestBase<T>
{
   public abstract ArchivePath getWebPath();
   public abstract ConvergedSipWebContainer<T> getConvergedSipWebContainer();
   
   //-------------------------------------------------------------------------------------||
   // Test Implementations - ConvergedSipWebContainer-------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetWebXMLResource() throws Exception {
      getConvergedSipWebContainer().setWebXML(NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getWebPath(), "web.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetWebXMLFile() throws Exception {
      getConvergedSipWebContainer().setWebXML(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "web.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetWebXMLURL() throws Exception {
      getConvergedSipWebContainer().setWebXML(getURLForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "web.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetWebXMLAsset() throws Exception {
      getConvergedSipWebContainer().setWebXML(getAssetForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "web.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetSipXMLResource() throws Exception {
      getConvergedSipWebContainer().setSipXML(NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getWebPath(), "sip.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetSipXMLFile() throws Exception {
      getConvergedSipWebContainer().setSipXML(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "sip.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetSipXMLURL() throws Exception {
      getConvergedSipWebContainer().setSipXML(getURLForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "sip.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testSetSipXMLAsset() throws Exception {
      getConvergedSipWebContainer().setSipXML(getAssetForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "sip.xml");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourceResource() throws Exception {
      getConvergedSipWebContainer().addWebResource(NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourceFile() throws Exception {
      getConvergedSipWebContainer().addWebResource(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourceURL() throws Exception {
      ArchivePath targetPath = new BasicPath("Test.properties");
      getConvergedSipWebContainer().addWebResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
      ArchivePath testPath = new BasicPath(getWebPath(), targetPath);
      Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourceStringTargetResource() throws Exception {
      getConvergedSipWebContainer().addWebResource(NAME_TEST_PROPERTIES, "Test.txt");
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourceStringTargetFile() throws Exception {
      getConvergedSipWebContainer().addWebResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourceStringTargetURL() throws Exception {
      getConvergedSipWebContainer().addWebResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourceStringTargetAsset() throws Exception {
      getConvergedSipWebContainer().addWebResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePathTargetResource() throws Exception {
      getConvergedSipWebContainer().addWebResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePathTargetFile() throws Exception {
      getConvergedSipWebContainer().addWebResource(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePathTargetURL() throws Exception {
      getConvergedSipWebContainer().addWebResource(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePathTargetAsset() throws Exception {
      getConvergedSipWebContainer().addWebResource(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePackage() throws Exception {
      getConvergedSipWebContainer().addWebResource(AssetUtil.class.getPackage(), "Test.properties");
      
      ArchivePath testPath = new BasicPath(getWebPath(), NAME_TEST_PROPERTIES);
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePackages() throws Exception {
      getConvergedSipWebContainer().addWebResources(AssetUtil.class.getPackage(), "Test.properties", "Test2.properties");
      
      ArchivePath testPath = new BasicPath(getWebPath(), NAME_TEST_PROPERTIES);
      ArchivePath testPath2 = new BasicPath(getWebPath(), NAME_TEST_PROPERTIES_2);
      
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
      Assert.assertTrue(
            "Archive should contain " + testPath2,
            getArchive().contains(testPath2));
   }

   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePackageStringTarget() throws Exception {
      
      getConvergedSipWebContainer().addWebResource(AssetUtil.class.getPackage(), "Test.properties", "Test.txt");
      
      ArchivePath testPath = new BasicPath(getWebPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ConvergedSipWebContainer.class)
   public void testAddWebResourcePackagePathTarget() throws Exception {
      
      ArchivePath targetPath = ArchivePaths.create("Test.txt");
      
      getConvergedSipWebContainer().addWebResource(AssetUtil.class.getPackage(), "Test.properties", targetPath);
      
      ArchivePath testPath = new BasicPath(getWebPath(), targetPath);
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
}

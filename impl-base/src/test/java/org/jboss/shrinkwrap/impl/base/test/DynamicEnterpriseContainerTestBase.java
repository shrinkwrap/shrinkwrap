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
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.container.EnterpriseContainer;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Test;

/**
 * DynamicEnterpriseContainerTestBase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class DynamicEnterpriseContainerTestBase<T extends Archive<T>> extends DynamicContainerTestBase<T>
{

   protected abstract Path getModulePath();
   protected abstract Path getApplicationPath();
   protected abstract EnterpriseContainer<T> getEnterpriseContainer();
   
   //-------------------------------------------------------------------------------------||
   // Test Implementations - WebContainer ------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testSetApplicationXMLResource() throws Exception {
      getEnterpriseContainer().setApplicationXML(NAME_TEST_PROPERTIES);
      
      Path expectedPath = new BasicPath(getApplicationPath(), "application.xml");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testSetApplicationXMLFile() throws Exception {
      getEnterpriseContainer().setApplicationXML(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "application.xml");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testSetApplicationXMLURL() throws Exception {
      getEnterpriseContainer().setApplicationXML(getURLForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "application.xml");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testSetApplicationXMLAsset() throws Exception {
      getEnterpriseContainer().setApplicationXML(getAssetForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "application.xml");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }
   
   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationResource() throws Exception {
      getEnterpriseContainer().addApplicationResource(NAME_TEST_PROPERTIES);
      
      Path expectedPath = new BasicPath(getApplicationPath(), NAME_TEST_PROPERTIES);
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }
   
   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationFile() throws Exception {
      getEnterpriseContainer().addApplicationResource(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationURL() throws Exception {
      getEnterpriseContainer().addApplicationResource(getURLForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationStringTargetResource() throws Exception {
      getEnterpriseContainer().addApplicationResource("Test.txt", NAME_TEST_PROPERTIES);
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationStringTargetFile() throws Exception {
      getEnterpriseContainer().addApplicationResource("Test.txt", getFileForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationStringTargetURL() throws Exception {
      getEnterpriseContainer().addApplicationResource("Test.txt", getURLForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationStringTargetAsset() throws Exception {
      getEnterpriseContainer().addApplicationResource("Test.txt", getAssetForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationPathTargetResource() throws Exception {
      getEnterpriseContainer().addApplicationResource(new BasicPath("Test.txt"), NAME_TEST_PROPERTIES);
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationPathTargetFile() throws Exception {
      getEnterpriseContainer().addApplicationResource(new BasicPath("Test.txt"), getFileForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationPathTargetURL() throws Exception {
      getEnterpriseContainer().addApplicationResource(new BasicPath("Test.txt"), getURLForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }
   
   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddApplicationPathTargetAsset() throws Exception {
      getEnterpriseContainer().addApplicationResource(new BasicPath("Test.txt"), getAssetForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getApplicationPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddModuleResource() throws Exception {
      getEnterpriseContainer().addModule(NAME_TEST_PROPERTIES);
      
      Path expectedPath = new BasicPath(getModulePath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddModuleFile() throws Exception {
      getEnterpriseContainer().addModule(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getModulePath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }

   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddModuleURL() throws Exception {
      getEnterpriseContainer().addModule(getURLForClassResource(NAME_TEST_PROPERTIES));
      
      Path expectedPath = new BasicPath(getModulePath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }
   
   @Test
   @ArchiveType(EnterpriseContainer.class)
   public void testAddModuleArchive() throws Exception {
      Archive<?> archive = createNewArchive();
      getEnterpriseContainer().addModule(archive);
      
      Path expectedPath = new BasicPath(getModulePath(), archive.getName());
      Assert.assertTrue(
            "Archive should contain " + expectedPath,
            getArchive().contains(expectedPath));
   }   
}
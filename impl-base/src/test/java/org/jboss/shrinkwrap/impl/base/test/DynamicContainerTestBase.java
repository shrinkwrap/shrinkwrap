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

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.DirectoryContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassA;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassParent;
import org.jboss.shrinkwrap.impl.base.test.dummy.DummyInterfaceForTest;
import org.jboss.shrinkwrap.impl.base.test.dummy.DummyClassForTest;
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
public abstract class DynamicContainerTestBase<T extends Archive<T>> extends ArchiveTestBase<T>
{
   
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(DynamicContainerTestBase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Contracts ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   protected abstract ArchivePath getResourcePath();
   protected abstract ResourceContainer<T> getResourceContainer();
   protected abstract ArchivePath getClassPath();
   protected abstract ClassContainer<T> getClassContainer();
   protected abstract ArchivePath getManifestPath();
   protected abstract ManifestContainer<T> getManifestContainer();
   protected abstract ArchivePath getLibraryPath();
   protected abstract LibraryContainer<T> getLibraryContainer();
   protected abstract DirectoryContainer<T> getDirectoryContainer();
   
   protected URL getURLForClassResource(String name) {
      return SecurityActions.getThreadContextClassLoader().getResource(name);
   }
   
   protected File getFileForClassResource(String name) throws Exception {
      return new File(getURLForClassResource(name).toURI());
   }
   
   protected Asset getAssetForClassResource(String name) {
      return new ClassLoaderAsset(name);
   }
   
   //-------------------------------------------------------------------------------------||
   // Test Implementations - ManifestContainer -------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testSetManifestResource() throws Exception {
      getManifestContainer().setManifest(NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "MANIFEST.FM");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ManifestContainer.class)
   public void testSetManifestFile() throws Exception {
      getManifestContainer().setManifest(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "MANIFEST.FM");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testSetManifestURL() throws Exception {
      getManifestContainer().setManifest(getURLForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "MANIFEST.FM");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testSetManifestAsset() throws Exception {
      getManifestContainer().setManifest(getAssetForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "MANIFEST.FM");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestResource() throws Exception {
      getManifestContainer().addManifestResource(NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getManifestPath(), NAME_TEST_PROPERTIES);
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestFile() throws Exception {
      getManifestContainer().addManifestResource(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestURL() throws Exception {
      ArchivePath targetPath = new BasicPath("Test.properties");
      getManifestContainer().addManifestResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
      ArchivePath testPath = new BasicPath(getManifestPath(), targetPath);
      Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestStringTargetResource() throws Exception {
      getManifestContainer().addManifestResource(NAME_TEST_PROPERTIES, "Test.txt");
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestStringTargetFile() throws Exception {
      getManifestContainer().addManifestResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestStringTargetURL() throws Exception {
      getManifestContainer().addManifestResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestStringTargetAsset() throws Exception {
      getManifestContainer().addManifestResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestPathTargetResource() throws Exception {
      getManifestContainer().addManifestResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestPathTargetFile() throws Exception {
      getManifestContainer().addManifestResource(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestPathTargetURL() throws Exception {
      getManifestContainer().addManifestResource(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddManifestPathTargetAsset() throws Exception {
      getManifestContainer().addManifestResource(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ManifestContainer.class)
   public void testAddServiceProvider() throws Exception {
      getManifestContainer().addServiceProvider(DummyInterfaceForTest.class, DummyClassForTest.class);
      
      ArchivePath testPath = new BasicPath(getManifestPath(), "services/" + DummyInterfaceForTest.class.getName());
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   //-------------------------------------------------------------------------------------||
   // Test Implementations - ResourceContainer -------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourceResource() throws Exception {
      getResourceContainer().addResource(NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getResourcePath(), NAME_TEST_PROPERTIES);
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourceFile() throws Exception {
      getResourceContainer().addResource(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourceURL() throws Exception {
      ArchivePath targetPath = new BasicPath("Test.properties");
      getResourceContainer().addResource(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
      ArchivePath testPath = new BasicPath(getResourcePath(), targetPath);
      Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourceStringTargetResource() throws Exception {
      getResourceContainer().addResource("Test.txt", NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourceStringTargetFile() throws Exception {
      getResourceContainer().addResource(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourceStringTargetURL() throws Exception {
      
      getResourceContainer().addResource(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourceStringTargetAsset() throws Exception {
      getResourceContainer().addResource(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourcePathTargetResource() throws Exception {
      getResourceContainer().addResource(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourcePathTargetFile() throws Exception {
      getResourceContainer().addResource(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourcePathTargetURL() throws Exception {
      getResourceContainer().addResource(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(ResourceContainer.class)
   public void testAddResourcePathTargetAsset() throws Exception {
      getResourceContainer().addResource(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getResourcePath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   //-------------------------------------------------------------------------------------||
   // Test Implementations - ClassContainer ----------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensure a class can be added to a container
    * 
    * @throws Exception
    */
   @Test
   @ArchiveType(ClassContainer.class)
   public void testAddClass() throws Exception
   {
      getClassContainer().addClass(DummyClassA.class);

      this.ensureClassesAdded();
   }

   /**
    * Ensure classes can be added to containers
    * 
    * @throws Exception
    */
   @Test
   @ArchiveType(ClassContainer.class)
   public void testAddClasses() throws Exception
   {
      getClassContainer().addClasses(DummyClassA.class);

      this.ensureClassesAdded();
   }
   
   /**
    * Ensures that the "addClass*" tests result in all expected classes added 
    */
   private void ensureClassesAdded()
   {
      ArchivePath expectedPath = new BasicPath(getClassPath(), AssetUtil
            .getFullPathForClassResource(DummyClassA.class));

      Assert.assertTrue(
            "A class should be located at " + expectedPath.get(), 
            getArchive().contains(expectedPath));
      
      // SHRINKWRAP-106
      // Ensure inner classes are added
      final ArchivePath expectedPathInnerClass = new BasicPath(
            getClassPath(), AssetUtil.getFullPathForClassResource(DummyClassA.InnerClass.class));
      final ArchivePath expectedPathInnerClassParent = new BasicPath(
            getClassPath(), AssetUtil.getFullPathForClassResource(DummyClassParent.ParentInnerClass.class));
      
      Assert.assertTrue(
            "Adding a class should also add its inner classes", 
            getArchive().contains(expectedPathInnerClass));
      Assert.assertFalse(
            "Adding a class should not add the public inner classes of its parent", 
            getArchive().contains(expectedPathInnerClassParent));
      
      // Ensure anonymous/private inner classes are added
      final ArchivePath expectedPathPrivateInnerClass = new BasicPath(
            getClassPath(), 
            AssetUtil.getFullPathForClassResource(
                  DummyClassA.InnerClass.class)
                     .get().replaceAll("InnerClass", "Test"));

      final ArchivePath expectedPathAnonymousInnerClass = new BasicPath(
            getClassPath(), 
            AssetUtil.getFullPathForClassResource(
                  DummyClassA.InnerClass.class)
                     .get().replaceAll("InnerClass", "1"));

      Assert.assertTrue(
            "Adding a class should also add its private inner classes", 
            getArchive().contains(expectedPathPrivateInnerClass));
      Assert.assertTrue(
            "Adding a class should also add the anonymous inner classes", 
            getArchive().contains(expectedPathAnonymousInnerClass));

   }

   /**
    * Ensure classes can be added to containers by name
    * 
    * @throws Exception
    */
   @Test
   @ArchiveType(ClassContainer.class)
   public void testAddClassesByFqn() throws Exception
   {
      final Class<?> classToAdd = DummyClassA.class;

      getClassContainer().addClass(classToAdd.getName());

      ArchivePath expectedPath = new BasicPath(getClassPath(), AssetUtil.getFullPathForClassResource(classToAdd));
      Assert.assertTrue("A class should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure classes can be added to containers by name using a classloader
    * 
    * @throws Exception
    */
   @Test
   @ArchiveType(ClassContainer.class)
   public void testAddClassesByFqnAndTccl() throws Exception
   {
      final Class<?> classToAdd = DummyClassA.class;

      getClassContainer().addClass(classToAdd.getName(), classToAdd.getClassLoader());

      ArchivePath expectedPath = new BasicPath(getClassPath(), AssetUtil.getFullPathForClassResource(classToAdd));
      Assert.assertTrue("A class should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure a package can be added to a container
    * 
    * @throws Exception
    */
   @Test
   @ArchiveType(ClassContainer.class)
   public void testAddPackage() throws Exception
   {
      getClassContainer().addPackage(DummyClassA.class.getPackage());

      ArchivePath expectedPath = new BasicPath(getClassPath(), AssetUtil
            .getFullPathForClassResource(DummyClassA.class));

      Assert.assertTrue(
            "A class should be located at " + expectedPath.get(), 
            getArchive().contains(expectedPath));
   }

   /**
    * Ensure packages can be added to containers
    * 
    * @throws Exception
    */
   @Test
   @ArchiveType(ClassContainer.class)
   public void testAddPackageNonRecursive() throws Exception
   {
      getClassContainer().addPackages(false, DummyClassA.class.getPackage());

      ArchivePath expectedPath = new BasicPath(getClassPath(), AssetUtil
            .getFullPathForClassResource(DummyClassA.class));

      Assert.assertTrue(
            "A class should be located at " + expectedPath.get(), 
            getArchive().contains(expectedPath));
   }
   
   /**
    * Ensure packages can be added with filters
    * 
    * @throws Exception
    */
   @Test
   @ArchiveType(ClassContainer.class)
   public void testAddPakcageRecursiveFiltered() throws Exception 
   {
      getClassContainer().addPackages(true, new Filter<Class<?>>()
      {
         @Override
         public boolean include(Class<?> clazz)
         {
            return clazz == DynamicContainerTestBase.class;
         }
      }, DynamicContainerTestBase.class.getPackage());
      
      ArchivePath expectedPath = new BasicPath(
            getClassPath(), AssetUtil.getFullPathForClassResource(DynamicContainerTestBase.class));

      Assert.assertEquals(
            "Should only be one class added",
            1,
            getArchive().getContent().size());

      Assert.assertTrue(
            "A class should be located at " + expectedPath.get(), 
            getArchive().contains(expectedPath));
   }

   //-------------------------------------------------------------------------------------||
   // Test Implementations - LibraryContainer ----------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryResource() throws Exception {
      getLibraryContainer().addLibrary(NAME_TEST_PROPERTIES);
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), NAME_TEST_PROPERTIES);
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryFile() throws Exception {
      getLibraryContainer().addLibrary(getFileForClassResource(NAME_TEST_PROPERTIES));
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.properties");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryURL() throws Exception {
      final ArchivePath targetPath = new BasicPath("Test.properties");
      getLibraryContainer().addLibrary(getURLForClassResource(NAME_TEST_PROPERTIES), targetPath);
      ArchivePath testPath = new BasicPath(getLibraryPath(), targetPath);
      Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryStringTargetResource() throws Exception {
      getLibraryContainer().addLibrary(NAME_TEST_PROPERTIES, "Test.txt");
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryStringTargetFile() throws Exception {
      getLibraryContainer().addLibrary(getFileForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryStringTargetURL() throws Exception {
      getLibraryContainer().addLibrary(getURLForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryStringTargetAsset() throws Exception {
      getLibraryContainer().addLibrary(getAssetForClassResource(NAME_TEST_PROPERTIES), "Test.txt");
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryPathTargetResource() throws Exception {
      getLibraryContainer().addLibrary(NAME_TEST_PROPERTIES, new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryPathTargetFile() throws Exception {
      getLibraryContainer().addLibrary(getFileForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryPathTargetURL() throws Exception {
      getLibraryContainer().addLibrary(getURLForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryPathTargetAsset() throws Exception {
      getLibraryContainer().addLibrary(getAssetForClassResource(NAME_TEST_PROPERTIES), new BasicPath("Test.txt"));
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.txt");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }
   
   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibraryArchive() throws Exception {
      Archive<?> archive = createNewArchive();
      getLibraryContainer().addLibrary(archive);
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), archive.getName());
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibriesResource() throws Exception {
      getLibraryContainer().addLibraries(NAME_TEST_PROPERTIES, NAME_TEST_PROPERTIES_2);
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), NAME_TEST_PROPERTIES);
      ArchivePath testPath2 = new BasicPath(getLibraryPath(), NAME_TEST_PROPERTIES_2);
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
      Assert.assertTrue(
            "Archive should contain " + testPath2,
            getArchive().contains(testPath2));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibriesFile() throws Exception {
      getLibraryContainer().addLibraries(
            getFileForClassResource(NAME_TEST_PROPERTIES), 
            getFileForClassResource(NAME_TEST_PROPERTIES_2));
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), "Test.properties");
      ArchivePath testPath2 = new BasicPath(getLibraryPath(), "Test2.properties");
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
      Assert.assertTrue(
            "Archive should contain " + testPath2,
            getArchive().contains(testPath2));
   }

   @Test
   @ArchiveType(LibraryContainer.class)
   public void testAddLibrariesArchive() throws Exception {
      Archive<?> archive = createNewArchive();
      Archive<?> archive2 = createNewArchive();

      getLibraryContainer().addLibraries(archive, archive2);
      
      ArchivePath testPath = new BasicPath(getLibraryPath(), archive.getName());
      ArchivePath testPath2 = new BasicPath(getLibraryPath(), archive.getName());
      Assert.assertTrue(
            "Archive should contain " + testPath,
            getArchive().contains(testPath));
      Assert.assertTrue(
            "Archive should contain " + testPath2,
            getArchive().contains(testPath2));
   }

   /**
    * Tests that empty directories may be added to the archive
    * @throws Exception
    */
   @Test
   @ArchiveType(DirectoryContainer.class)
   public void testAddEmptyDirectories() throws Exception
   {

      // Get the container
      final DirectoryContainer<T> container = getDirectoryContainer();

      // Get Paths to add
      final ArchivePath path1 = ArchivePaths.create("path/to/dir");
      final ArchivePath path2 = ArchivePaths.create("path/to/dir2");
      final ArchivePath path3 = ArchivePaths.create("path/to");

      // Add
      container.addDirectories(path1, path2, path3);

      // Obtain as archive view
      final Archive<T> archive = this.getArchive();

      // Test
      final String message = "Should be able to add directory: ";
      TestCase.assertTrue(message + path1, archive.contains(path1));
      TestCase.assertTrue(message + path2, archive.contains(path2));
      TestCase.assertTrue(message + path3, archive.contains(path3));

      // Log out
      log.info("testAddEmptyDirectories:\n" + archive.toString(true));
   }

}

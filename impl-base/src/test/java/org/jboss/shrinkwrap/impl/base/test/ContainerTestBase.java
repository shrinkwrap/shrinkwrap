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

import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassUsedForClassResourceTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * ContainerTestBase
 * 
 * Base test for all Container providers to help ensure consistency between implementations.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public abstract class ContainerTestBase<T extends Archive<T>> extends ArchiveTestBase<T>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected static final String TEST_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

   protected static final Path NESTED_PATH = new BasicPath("nested");

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Return the archive as a ResourceContainer
    */
   protected abstract ResourceContainer<T> getResourceContainer();

   /**
    * Get the resource path for the container
    */
   protected abstract Path getResourcePath();
   
   /**
    * Return the archive as a ManifestContainer 
    */
   protected abstract ManifestContainer<T> getManifestContainer();

   /**
    * Get the manifest path for the container 
    */
   protected abstract Path getManifestPath();

   /**
    * Get the archive as a ClassContainer
    */
   protected abstract ClassContainer<T> getClassContainer();

   /**
    * Get the classes path for the container 
    */
   protected abstract Path getClassesPath();

   /**
    *  Get the archive as a LibraryContainer
    */
   protected abstract LibraryContainer<T> getLibraryContainer();

   /**
    * Get the library path for the container
    */
   protected abstract Path getLibraryPath();

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /** 
    * Ensure resources can be added to containers
    */
   @Test
   public void testAddResource() throws Exception
   {
      ResourceContainer<T> container = getResourceContainer();
      container.addResource(TEST_RESOURCE);

      Path expectedPath = new BasicPath(getResourcePath(), TEST_RESOURCE);

      Assert
            .assertTrue("A resource should be located under " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure resources can be added to containers witha specified path
    * 
    * @throws Exception
    */
   @Test
   public void testAddResourceToPath() throws Exception
   {
      ResourceContainer<T> container = getResourceContainer();

      Path path = new BasicPath(NESTED_PATH, TEST_RESOURCE);

      container.addResource(path, TEST_RESOURCE);

      Path expectedPath = new BasicPath(new BasicPath(getResourcePath(), NESTED_PATH), TEST_RESOURCE);

      Assert
            .assertTrue("A resource should be located under " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure resources can be added to containers with names
    * @throws Exception
    */
   @Test
   public void testAddResourceWithNewName() throws Exception
   {
      ResourceContainer<T> container = getResourceContainer();

      String newName = "test.txt";
      container.addResource(TEST_RESOURCE, newName);

      Path expectedPath = new BasicPath(getResourcePath(), "/org/jboss/shrinkwrap/impl/base/asset/" + newName);

      Assert
            .assertTrue("A resource should be located under " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure resources can be added to containers as a URL in a specific path
    * 
    * @throws Exception
    */
   @Test
   public void testAddResourceToPathFromUrl() throws Exception
   {
      ResourceContainer<T> container = getResourceContainer();

      URL url = getClass().getResource("/" + TEST_RESOURCE);

      Path path = new BasicPath(TEST_RESOURCE);

      container.addResource(path, url);

      Path expectedPath = new BasicPath(getResourcePath(), TEST_RESOURCE);

      Assert
            .assertTrue("A resource should be located under " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure resources can be added to a container from a classloader
    * 
    * @throws Exception
    */
   @Test
   public void testAddResourceFromClassloader() throws Exception
   {
      ResourceContainer<T> container = getResourceContainer();
      
      Path path = new BasicPath(TEST_RESOURCE);
      
      container.addResource(path, TEST_RESOURCE, this.getClass().getClassLoader());

      Path expectedPath = new BasicPath(getResourcePath(), TEST_RESOURCE);

      Assert
            .assertTrue("A resource should be located under " + expectedPath.get(), getArchive().contains(expectedPath));

   }

   /**
    * Ensure manifest resources can be added to containers
    * 
    * @throws Exception
    */
   @Test
   public void testAddManifestResource() throws Exception
   {
      ManifestContainer<T> container = getManifestContainer();

      container.addManifestResource(TEST_RESOURCE);

      Path expectedPath = new BasicPath(getManifestPath(), TEST_RESOURCE);

      Assert.assertTrue("A manifest resource should be located at " + expectedPath.get(), getArchive().contains(
            expectedPath));
   }

   /**
    * Ensure manifest resources can be added to containers with names
    * @throws Exception
    */
   @Test
   public void testAddManifestResourceWithNewName() throws Exception
   {
      ManifestContainer<T> container = getManifestContainer();

      String newName = "test.txt";
      container.addManifestResource(new BasicPath(newName), TEST_RESOURCE);

      Path expectedPath = new BasicPath(getManifestPath(), newName);

      Assert.assertTrue("A manifest resoruce should be located at " + expectedPath.get(), getArchive().contains(
            expectedPath));
   }

   /**
    * Ensure a class can be added to a container
    * 
    * @throws Exception
    */
   @Test
   public void testAddClass() throws Exception
   {
      ClassContainer<T> container = getClassContainer();

      container.addClass(DummyClassUsedForClassResourceTest.class);

      Path expectedPath = new BasicPath(getClassesPath(), AssetUtil
            .getFullPathForClassResource(DummyClassUsedForClassResourceTest.class));

      Assert.assertTrue("A class should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure classes can be added to containers
    * 
    * @throws Exception
    */
   @Test
   public void testAddClasses() throws Exception
   {
      ClassContainer<T> container = getClassContainer();

      container.addClasses(DummyClassUsedForClassResourceTest.class);

      Path expectedPath = new BasicPath(getClassesPath(), AssetUtil
            .getFullPathForClassResource(DummyClassUsedForClassResourceTest.class));

      Assert.assertTrue("A class should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure a package can be added to a container
    * 
    * @throws Exception
    */
   @Test
   public void testAddPackage() throws Exception
   {
      ClassContainer<T> container = getClassContainer();

      container.addPackage(DummyClassUsedForClassResourceTest.class.getPackage());

      Path expectedPath = new BasicPath(getClassesPath(), AssetUtil
            .getFullPathForClassResource(DummyClassUsedForClassResourceTest.class));

      Assert.assertTrue("A class should be located  at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure packages can be added to containers
    * 
    * @throws Exception
    */
   @Test
   public void testAddPackageNonRecursive() throws Exception
   {
      ClassContainer<T> container = getClassContainer();

      container.addPackages(false, DummyClassUsedForClassResourceTest.class.getPackage());

      Path expectedPath = new BasicPath(getClassesPath(), AssetUtil
            .getFullPathForClassResource(DummyClassUsedForClassResourceTest.class));

      Assert.assertTrue("A class should be located  at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure libraries can be added to containers
    * 
    * @throws Exception
    */
   @Test
   public void testAddLibrary() throws Exception
   {
      LibraryContainer<T> container = getLibraryContainer();

      container.addLibrary(TEST_RESOURCE);

      Path expectedPath = new BasicPath(getLibraryPath(), TEST_RESOURCE);

      Assert.assertTrue("A library should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure libraries can be added to containers in a specific path
    * 
    * @throws Exception
    */
   @Test
   public void testAddLibraryToPath() throws Exception
   {
      LibraryContainer<T> container = getLibraryContainer();

      Path path = new BasicPath(TEST_RESOURCE);

      container.addLibrary(path, TEST_RESOURCE);

      Path expectedPath = new BasicPath(getLibraryPath(), path);

      Assert.assertTrue("A library should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

   /**
    * Ensure archives can be added to containers as libraries
    * @throws Exception
    */
   @Test
   public void testAddArchiveAsLibrary() throws Exception
   {
      Archive<?> library = createNewArchive();

      LibraryContainer<T> container = getLibraryContainer();

      container.addLibrary(library);

      Path expectedPath = new BasicPath(getLibraryPath(), library.getName());

      Assert.assertTrue("A library should be located at " + expectedPath.get(), getArchive().contains(expectedPath));
   }

}

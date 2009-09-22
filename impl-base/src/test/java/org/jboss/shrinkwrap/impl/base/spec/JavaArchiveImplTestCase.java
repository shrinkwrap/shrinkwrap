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
package org.jboss.shrinkwrap.impl.base.spec;

import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.test.ContainerTestBase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * JavaArchiveImplTestCase
 * 
 * Test case to ensure that the JavaArchive follows the Jar spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public class JavaArchiveImplTestCase extends ContainerTestBase<JavaArchive>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final Logger log = Logger.getLogger(JavaArchiveImplTestCase.class.getName());

   private static final String TEST_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

   private static final Path PATH_MANIFEST = new BasicPath("META-INF");

   private static final Path PATH_CLASS = new BasicPath("/");

   private static final Path PATH_RESOURCE = new BasicPath();

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private JavaArchive archive;

   //-------------------------------------------------------------------------------------||
   // Lifecycle Methods ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Before
   public void createArchive()
   {
      archive = createNewArchive();
   }

   @After
   public void ls()
   {
      System.out.println("test@jboss:/$ ls -l " + archive.getName());
      System.out.println(archive.toString(true));
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - ArchiveTestBase ---------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Return the archive to super class
    */
   @Override
   protected JavaArchive getArchive()
   {
      return archive;
   }

   /** 
    * Create a new JavaArchive instance
    */
   @Override
   protected JavaArchive createNewArchive()
   {
      return new JavaArchiveImpl(new MemoryMapArchiveImpl());
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - ContainerTestBase ---------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ManifestContainer<JavaArchive> getManifestContainer()
   {
      return getArchive();
   }

   @Override
   protected ResourceContainer<JavaArchive> getResourceContainer()
   {
      return getArchive();
   }

   @Override
   protected ClassContainer<JavaArchive> getClassContainer()
   {
      return archive;
   }

   @Override
   protected LibraryContainer<JavaArchive> getLibraryContainer()
   {
      throw new UnsupportedOperationException("JavaArchive does not support libraries");
   }

   @Override
   protected Path getManifestPath()
   {
      return PATH_MANIFEST;
   }

   @Override
   protected Path getResourcePath()
   {
      return PATH_RESOURCE;
   }

   @Override
   protected Path getClassesPath()
   {
      return PATH_CLASS;
   }

   @Override
   protected Path getLibraryPath()
   {
      throw new UnsupportedOperationException("JavaArchive does not support libraries");
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   public void shouldBeAbleToSetManifestFile() throws Exception
   {
      archive.setManifest(TEST_RESOURCE);

      Path expectedPath = new BasicPath(PATH_MANIFEST, "MANIFEST.MF");

      Assert.assertTrue("The MANIFEST.MF file should be located under /META-INF/MANIFEST.MF", archive
            .contains(expectedPath));
   }

   @Ignore
   @Override
   public void testAddLibrary() throws Exception
   {
   }

   @Ignore
   @Override
   public void testAddLibraryToPath() throws Exception
   {
   }

   @Ignore
   @Override
   public void testAddArchiveAsLibrary() throws Exception
   {
   }

}

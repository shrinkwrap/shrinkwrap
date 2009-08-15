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
package org.jboss.declarchive.impl.base.jar;

import java.util.logging.Logger;

import junit.framework.Assert;

import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.jar.JavaArchive;
import org.jboss.declarchive.impl.base.jar.donotchange.DummyClassUsedForClassResourceTest;
import org.jboss.declarchive.impl.base.path.BasePath;
import org.jboss.declarchive.impl.base.path.RelativePath;
import org.jboss.declarchive.impl.base.path.ResourcePath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * ArchiveBaseTest
 * 
 * Test to ensure that the ArchiveBase provides functions required of the 
 * various container APIs
 * 
 * @see JavaArchive
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ArchiveBaseTest
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ArchiveBaseTest.class.getName());

   /**
    * Name of a test resource to be added to the archive, visible to the TCCL
    */
   private static final String TEST_RESOURCE = "org/jboss/declarchive/impl/base/resource/Test.properties";

   /**
    * Name of the test archive
    */
   private static final String NAME_ARCHIVE = "archive.jar";

   private static final Path PATH_MANIFEST = new BasePath("META-INF");

   private static final Path PATH_RESOURCE = new BasePath("/");

   private static final Path PATH_CLASS = new BasePath("/");

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Archive used in testing, created as part of lifecycle
    */
   private TestArchive archive;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates the archive used in tests
    */
   @Before
   public void createArchive()
   {
      archive = new MockArchive(NAME_ARCHIVE);
   }

   /**
    * Prints out the contents of the test archive
    */
   @After
   public void printArchive()
   {
      log.info("test@jboss:/$ ls -l " + archive.getName());
      log.info(archive.toString(true));
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   public void shouldBeAbleToSetManifestFile() throws Exception
   {
      archive.setManifest(TEST_RESOURCE);
      Assert.assertTrue("The MANIFEST.MF file should be located under /META-INF/MANIFEST.MF", archive
            .contains(new ResourcePath(PATH_MANIFEST, "MANIFEST.MF")));
   }

   @Test
   public void shouldBeAbleToAddManifestResource() throws Exception
   {
      archive.addManifestResource(TEST_RESOURCE);
      Assert.assertTrue("A manifest resource should be located under /META-INF/", archive.contains(new ResourcePath(
            new RelativePath(PATH_MANIFEST, "/org/jboss/declarchive/impl/base/resource/"), "Test.properties")));
   }

   @Test
   public void shouldBeAbleToAddManifestResourceWithNewName() throws Exception
   {
      String newName = "test.txt";
      archive.addManifestResource(TEST_RESOURCE, newName);
      Assert.assertTrue("A manifest resoruce should be located under /META-INF/", archive.contains(new ResourcePath(
            new RelativePath(PATH_MANIFEST, "/org/jboss/declarchive/impl/base/resource/"), newName)));
   }

   @Test
   public void shouldBeAbleToAddResource() throws Exception
   {
      archive.addResource(TEST_RESOURCE);
      Assert.assertTrue("A resoruce should be located under /", archive.contains(new ResourcePath(new RelativePath(
            PATH_RESOURCE, "/org/jboss/declarchive/impl/base/resource/"), "Test.properties")));
   }

   @Test
   public void shouldBeAbleToAddResourceWithNewName() throws Exception
   {
      String newName = "test.txt";
      archive.addResource(TEST_RESOURCE, newName);
      Assert.assertTrue("A resoruce should be located under /", archive.contains(new ResourcePath(new RelativePath(
            PATH_RESOURCE, "/org/jboss/declarchive/impl/base/resource/"), newName)));

   }

   @Test
   public void shouldBeAbleToAddClass() throws Exception
   {
      archive.add(DummyClassUsedForClassResourceTest.class);

      Assert.assertTrue("A classes should be located under /", archive
            .contains(new ResourcePath(new RelativePath(PATH_CLASS, "org/jboss/declarchive/impl/base/jar/donotchange"),
                  "DummyClassUsedForClassResourceTest.class")));
   }

   @Test
   public void shouldBeAbleToAddPackage() throws Exception
   {
      archive.add(DummyClassUsedForClassResourceTest.class.getPackage());

      Assert.assertTrue("A classes should be located under /", archive
            .contains(new ResourcePath(new RelativePath(PATH_CLASS, "org/jboss/declarchive/impl/base/jar/donotchange"),
                  "DummyClassUsedForClassResourceTest.class")));
   }
}

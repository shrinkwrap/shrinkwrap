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
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.JavaArchiveImpl;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassUsedForClassResourceTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JavaArchiveImplTestCase
 * 
 * Test case to ensure that the JavaArchive follows the Jar spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class JavaArchiveImplTestCase
{
   private static final Logger log = Logger.getLogger(JavaArchiveImplTestCase.class.getName());
   
   private static final String TEST_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

   private static final Path PATH_MANIFEST = new BasicPath("META-INF");

   private static final Path PATH_RESOURCE = new BasicPath("/");

   private static final Path PATH_CLASS = new BasicPath("/");

   private JavaArchive archive;

   @Before
   public void createArchive()
   {
      archive = new JavaArchiveImpl(new MemoryMapArchiveImpl());
   }

   @After
   public void ls()
   {
      System.out.println("test@jboss:/$ ls -l " + archive.getName());
      System.out.println(archive.toString(true));
   }

   @Test
   public void shouldBeAbleToSetManifestFile() throws Exception
   {
      archive.setManifest(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_MANIFEST, "MANIFEST.MF");
      
      Assert.assertTrue(
            "The MANIFEST.MF file should be located under /META-INF/MANIFEST.MF", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddManifestResource() throws Exception
   {
      archive.addManifestResource(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_MANIFEST, TEST_RESOURCE);
      
      Assert.assertTrue(
            "A manifest resource should be located under /META-INF/", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddManifestResourceWithNewName() throws Exception
   {
      String newName = "test.txt";
      archive.addManifestResource(new BasicPath(newName), TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_MANIFEST, newName);
      
      Assert.assertTrue(
            "A manifest resoruce should be located under /META-INF/", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddResource() throws Exception
   {
      archive.addResource(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_RESOURCE, TEST_RESOURCE);
      
      Assert.assertTrue(
            "A resoruce should be located under /", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddResourceWithNewName() throws Exception
   {
      String newName = "test.txt";
      archive.addResource(TEST_RESOURCE, newName);
      
      Path expectedPath = new BasicPath(
            PATH_RESOURCE, 
            "/org/jboss/shrinkwrap/impl/base/asset/" + newName);
      
      Assert.assertTrue(
            "A resoruce should be located under /", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddClass() throws Exception
   {
      archive.addClasses(DummyClassUsedForClassResourceTest.class);

      Path expectedPath = new BasicPath(
            PATH_CLASS, 
            AssetUtil.getFullPathForClassResource(DummyClassUsedForClassResourceTest.class));
      
      Assert.assertTrue(
            "A class should be located under /", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddPackage() throws Exception
   {
      archive.addPackages(false, DummyClassUsedForClassResourceTest.class.getPackage());

      Path expectedPath = new BasicPath(
            PATH_CLASS, 
            AssetUtil.getFullPathForClassResource(DummyClassUsedForClassResourceTest.class));
      
      Assert.assertTrue(
            "A class should be located under /", 
            archive.contains(expectedPath));
   }

}

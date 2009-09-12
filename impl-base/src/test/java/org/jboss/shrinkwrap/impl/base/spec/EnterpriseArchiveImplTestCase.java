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

import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.EnterpriseArchiveImpl;
import org.jboss.shrinkwrap.impl.base.spec.JavaArchiveImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * EnterpriseArchiveImplTest
 *
 * Test to ensure that EnterpriseArchiveImpl follow to java ear spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class EnterpriseArchiveImplTestCase
{
   private static final String TEST_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";
   
   private static final Path PATH_APPLICATION = new BasicPath("META-INF");
   
   private static final Path PATH_LIBRARY = new BasicPath("lib");

   private static final Path PATH_MODULE = new BasicPath("/");
   
   private EnterpriseArchive archive;

   @Before
   public void createWebArchive() throws Exception
   {
      archive = new EnterpriseArchiveImpl(new MemoryMapArchiveImpl());
   }
      
   @After
   public void ls()
   {
      System.out.println("test@jboss:/$ ls -l " + archive.getName());
      System.out.println(archive.toString(true));
   }
   
   @Test
   public void shouldBeAbleToAddApplicationXML() throws Exception 
   {
      archive.setApplicationXML(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_APPLICATION, "application.xml");
      
      Assert.assertTrue(
            "applicaton.xml should be located in /META-INF/application.xml", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddApplicationResource() throws Exception 
   {
      archive.addApplicationResource(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_APPLICATION, TEST_RESOURCE);
      
      Assert.assertTrue(
            "A application resource should be located in /META-INF/", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddApplicationResourceWithNewName() throws Exception 
   {
      String newName = "test.txt";
      archive.addApplicationResource(new BasicPath(newName), TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_APPLICATION, newName);
      
      Assert.assertTrue(
            "A application resource should be located in /META-INF/", 
            archive.contains(expectedPath));
   }
   
   @Test
   public void shouldBeAbleToAddLibrary() throws Exception 
   {
      archive.addLibrary(TEST_RESOURCE);

      Path expectedPath = new BasicPath(PATH_LIBRARY, TEST_RESOURCE);
      
      Assert.assertTrue(
            "A library should be located in /lib/", 
            archive.contains(expectedPath));
   }
   
   @Test
   public void shouldBeAbleToAddModule() throws Exception 
   {
      archive.addModule(TEST_RESOURCE);

      Path expectedPath = new BasicPath(
            PATH_MODULE, 
            AssetUtil.getNameForClassloaderResource(TEST_RESOURCE));
      
      Assert.assertTrue(
            "A application module should be located in /", 
            archive.contains(expectedPath));
   }
   
   //@Test
   // TODO: should we be able to add a ArchiveAsset, to add aarchive as a single jar not a exploded jar?
   public void shouldBeAbleToAddArchiveModule() throws Exception 
   {
      JavaArchive moduleArchive = new JavaArchiveImpl(new MemoryMapArchiveImpl("test.jar"));
      moduleArchive.addResource(TEST_RESOURCE);
      moduleArchive.addResource(new BasicPath("test.txt"), TEST_RESOURCE);
      
      archive.addModule(moduleArchive);

      Path expectedPath = new BasicPath(
            PATH_MODULE, 
            moduleArchive.getName());
      
      Assert.assertTrue(
            "A application module should be located in /", 
            archive.contains(expectedPath));
   }
   
}

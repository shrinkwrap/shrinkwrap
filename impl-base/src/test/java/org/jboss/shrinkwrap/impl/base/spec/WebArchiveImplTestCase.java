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
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.spec.WebArchiveImpl;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassUsedForClassResourceTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * WebArchiveImplTestCase
 * 
 * Test case to ensure that the WebArchive follows the War spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class WebArchiveImplTestCase
{
   private static final String TEST_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";
   
   private static final Path PATH_WEBINF = new BasicPath("WEB-INF");

   private static final Path PATH_LIBRARY = new BasicPath(PATH_WEBINF, "lib");

   private static final Path PATH_CLASSES = new BasicPath(PATH_WEBINF, "classes");

   private static final Path PATH_RESOURCE = new BasicPath();

   private WebArchive archive;

   @Before
   public void createWebArchive() throws Exception
   {
      archive = new WebArchiveImpl(new MemoryMapArchiveImpl());
   }
      
   @After
   public void ls()
   {
      System.out.println("test@jboss:/$ ls -l " + archive.getName());
      System.out.println(archive.toString(true));
   }

   @Test
   public void shouldBeAbleToSetWebXML() throws Exception
   {
      archive.setWebXML(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_WEBINF, "web.xml");
      
      Assert.assertTrue(
            "web.xml should be located in /WEB-INF/web.xml", 
            archive.contains(expectedPath));
   }
   
   @Test
   public void shouldBeAbleToAddWebResource() throws Exception {

      archive.addWebResource(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_WEBINF, TEST_RESOURCE);
      
      Assert.assertTrue(
            "A resource should be located in /WEB-INF/", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddWebResourceWithNewName() throws Exception {

      String newName = "test.txt";
      archive.addWebResource(new BasicPath(newName), TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_WEBINF, newName);
      
      Assert.assertTrue(
            "A resource should be located in /WEB-INF/", 
            archive.contains(expectedPath));
   }
   
   @Test
   public void shouldBeAbleToAddLibrary() throws Exception
   {
      archive.addLibrary(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_LIBRARY, TEST_RESOURCE);
      
      Assert.assertTrue(
            "A library should be located in /WEB-INF/lib/", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddClasses() throws Exception
   {
      archive.addClasses(DummyClassUsedForClassResourceTest.class);
      
      Path expectedPath = new BasicPath(
            PATH_CLASSES, 
            AssetUtil.getFullPathForClassResource(
                  DummyClassUsedForClassResourceTest.class));
            
      Assert.assertTrue(
            "A class should be located in /WEB-INF/classes/", 
            archive.contains(expectedPath));
   }

   @Test
   public void shouldBeAbleToAddPackages() throws Exception
   {
      archive.addPackages(true, DummyClassUsedForClassResourceTest.class.getPackage());
      
      Path expectedPath = new BasicPath(
            PATH_CLASSES, 
            AssetUtil.getFullPathForClassResource(
                  DummyClassUsedForClassResourceTest.class));
            
      Assert.assertTrue(
            "A class should be located in /WEB-INF/classes/", 
            archive.contains(expectedPath));
   }
   
   @Test
   public void shouldBeAbleToAddResource() throws Exception 
   {
      archive.addResource(TEST_RESOURCE);
      
      Path expectedPath = new BasicPath(PATH_RESOURCE, TEST_RESOURCE);
      
      Assert.assertTrue(
            "A resource should be located in /",
            archive.contains(expectedPath));
   }
}

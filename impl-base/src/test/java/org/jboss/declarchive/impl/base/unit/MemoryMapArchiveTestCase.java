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
package org.jboss.declarchive.impl.base.unit;

import junit.framework.Assert;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.MemoryMapArchiveImpl;
import org.jboss.declarchive.impl.base.asset.ClassLoaderAsset;
import org.jboss.declarchive.impl.base.path.BasicPath;
import org.jboss.declarchive.impl.base.test.ArchiveTestBase;
import org.jboss.declarchive.spi.MemoryMapArchive;
import org.junit.Before;
import org.junit.Test;

/**
 * MemoryMapArchiveTestCase
 * 
 * TestCase to ensure that the MemoryMapArchive works as expected.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 *  * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class MemoryMapArchiveTestCase extends ArchiveTestBase<MemoryMapArchive>
{
   private MemoryMapArchive archive;

   @Before
   public void createArchive() throws Exception  
   {
      archive = new MemoryMapArchiveImpl();
   }
   
   @Override
   protected Archive<MemoryMapArchive> getArchive()
   {
      return archive;
   }

   /**
    * Test to ensure MemoryMap archives can be created with a name
    * @throws Exception
    */
   @Test
   public void testConstructorWithName() throws Exception
   {
      String name = "test.jar";
      MemoryMapArchive tmp = new MemoryMapArchiveImpl(name);
      Assert.assertEquals("Should return the same name as construtor arg", name, tmp.getName());
   }

   /**
    * Test to ensure the MemoryMapArchive requires a name
    * @throws Exception
    */
   @Test
   public void testConstructorRequiresName() throws Exception
   {
      try
      {
         new MemoryMapArchiveImpl(null);
         Assert.fail("Should throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }


   /**
    * Ensure adding content from another archive successfully stores all assets
    * @throws Exception
    */
   @Test
   public void testAddContents() throws Exception
   {
      MemoryMapArchive sourceArchive = new MemoryMapArchiveImpl();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      archive.addContents(sourceArchive);
      Assert.assertEquals("Asset should have been added to path: " + location.get(), archive.get(location), asset);
      Assert.assertEquals("Asset should have been added to path: " + location.get(), archive.get(locationTwo), assetTwo);
   }

   /**
    * Ensure adding content requires a source archive
    * @throws Exception
    */
   @Test
   public void testAddContentsRequiresSource() throws Exception
   {
      try
      {
         archive.addContents(null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding content from another archive to a path successfully stores all assets to specific path
    * @throws Exception
    */
   @Test
   public void testAddContentsToPath() throws Exception
   {
      MemoryMapArchive sourceArchive = new MemoryMapArchiveImpl();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      Path baseLocation = new BasicPath("somewhere");

      archive.addContents(baseLocation, sourceArchive);

      Path expectedPath = new BasicPath(baseLocation, location);
      Path expectedPathTwo = new BasicPath(baseLocation, locationTwo);

      Assert.assertEquals("Asset should have been added to path: " + expectedPath.get(), archive.get(expectedPath), asset);
      Assert.assertEquals("Asset should have been added to path: " + expectedPathTwo.getClass(), archive
            .get(expectedPathTwo), assetTwo);
   }

   /**
    * Ensure adding content from another archive requires a path
    * @throws Exception
    */
   @Test
   public void testAddContentsToPathRequiresPath() throws Exception
   {
      try
      {
         archive.addContents(null, new MemoryMapArchiveImpl());
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding an archive to a path successfully stores all assets to specific path including the archive name
    * @throws Exception
    */
   @Test
   public void testAddArchiveToPath() throws Exception
   {
      MemoryMapArchive sourceArchive = new MemoryMapArchiveImpl("test.jar");
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");
      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      Path baseLocation = new BasicPath("somewhere");

      archive.add(baseLocation, sourceArchive);

      Path expectedPath = new BasicPath(new BasicPath(baseLocation, "test.jar"), location);
      Path expectedPathTwo = new BasicPath(new BasicPath(baseLocation, "test.jar"), locationTwo);

      Assert.assertEquals("Asset should have been added to path: " + expectedPath.get(), archive.get(expectedPath), asset);
      Assert.assertEquals("Asset should have been added to path: " + expectedPathTwo.get(), archive.get(expectedPathTwo),
            assetTwo);
   }

}

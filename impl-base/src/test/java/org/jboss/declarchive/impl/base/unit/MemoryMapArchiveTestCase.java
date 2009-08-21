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

import java.util.Map;

import junit.framework.Assert;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.MemoryMapArchiveImpl;
import org.jboss.declarchive.impl.base.asset.ClassLoaderAsset;
import org.jboss.declarchive.impl.base.path.BasicPath;
import org.jboss.declarchive.spi.MemoryMapArchive;
import org.junit.After;
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
public class MemoryMapArchiveTestCase
{
   private MemoryMapArchive archive;

   @Before
   public void createMemoryArchive() throws Exception
   {
      archive = new MemoryMapArchiveImpl();
   }

   @After
   public void ls()
   {
      System.out.println("test@jboss:/$ ls -l " + archive.getName());
      System.out.println(archive.toString(true));
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
    * Ensure adding an asset to the path results in successful storage.
    * @throws Exception
    */
   @Test
   public void testAddAssetToPath() throws Exception
   {
      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Path location = new BasicPath("/", "test.properties");

      archive.add(location, asset);

      Assert.assertTrue("Asset should be placed on " + location.get(), archive.contains(location));
   }

   /**
    * Ensure adding an asset to the path requires path.
    * @throws Exception
    */
   @Test
   public void testAddRequiresPath() throws Exception
   {
      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");

      try
      {
         archive.add((Path) null, asset);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding an asset to the path requires an asset.
    * @throws Exception
    */
   @Test
   public void testAddRequiresAssets() throws Exception
   {
      try
      {
         archive.add(new BasicPath("/", "Test.properties"), (Asset) null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /** 
    * Ensure adding an asset with a name results in successful storage
    * @throws Exception
    */
   @Test
   public void testAddAssetWithName() throws Exception
   {
      final String name = "test.properties";
      final Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Path location = new BasicPath("/");

      archive.add(location, name, asset);

      Path expectedPath = new BasicPath("/", "test.properties");

      Assert.assertTrue("Asset should be placed on " + expectedPath.get(), archive.contains(expectedPath));
   }

   /**
    * Ensure adding an asset with name requires the path attribute
    * @throws Exception
    */
   @Test
   public void testAddAssetWithNameRequiresPath() throws Exception
   {
      final String name = "test.properties";
      final Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      try
      {
         archive.add(null, name, asset);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding an asset with name requires the name attribute
    * @throws Exception
    */
   @Test
   public void testAddAssetWithNameRequiresName() throws Exception
   {
      final Path path = new BasicPath("/", "Test.properties");
      final String resource = "org/jboss/declarchive/impl/base/asset/Test.properties";
      try
      {
         archive.add(path, null, new ClassLoaderAsset(resource));
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding an asset with name requires the asset attribute
    * @throws Exception
    */
   @Test
   public void testAddAssetWithNameRequiresAsset() throws Exception
   {
      final String name = "test.properties";
      final Path path = new BasicPath("/", "Test.properties");
      try
      {
         archive.add(path, name, null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure deleting an asset successfully removes asset from storage
    * @throws Exception
    */
   @Test
   public void testDeleteAsset() throws Exception
   {
      String resource = "org/jboss/declarchive/impl/base/asset/Test.properties";
      Path location = new BasicPath("/", "test.properties");
      archive.add(location, new ClassLoaderAsset(resource));
      Assert.assertTrue(archive.contains(location)); // Sanity check

      Assert.assertTrue("Successfully deleting an Asset should ruturn true", archive.delete(location));

      Assert.assertFalse("There should no longer be an asset at: " + location.get() + " after deleted", archive
            .contains(location));
   }

   /**
    * Ensure deleting a missing asset returns correct status
    * @throws Exception
    */
   @Test
   public void testDeleteMissingAsset() throws Exception
   {
      Path location = new BasicPath("/", "test.properties");

      Assert.assertFalse("Deleting a non-existent Asset should ruturn false", archive.delete(location));
   }

   /**
    * Ensure deleting an asset requires a path
    * @throws Exception
    */
   @Test
   public void testDeleteAssetRequiresPath() throws Exception
   {
      try
      {
         archive.delete(null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure an asset can be retrieved by its path
    * @throws Exception
    */
   @Test
   public void testGetAsset() throws Exception
   {
      Path location = new BasicPath("/", "test.properties");
      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      archive.add(location, asset);

      Assert.assertEquals("Asset should be returned from path: " + location.get(), asset, archive.get(location));
   }

   /**
    * Ensure get asset requires a path
    * @throws Exception
    */
   @Test
   public void testGetAssetRequiresPath() throws Exception
   {
      try
      {
         archive.get((Path) null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure get content returns the correct map of content
    * @throws Exception
    */
   @Test
   public void testToGetContent() throws Exception
   {
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      archive.add(location, asset).add(locationTwo, assetTwo);

      Map<Path, Asset> content = archive.getContent();
      Assert.assertEquals("Asset should existing in content with key: " + location.get(), content.get(location), asset);
      Assert.assertEquals("Asset should existing in content with key: " + locationTwo.get(), content.get(locationTwo),
            assetTwo);
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

   /**
    * Ensure adding an archive to a path requires a path
    * @throws Exception
    */
   @Test
   public void testAddArchiveToPathRequirePath() throws Exception
   {
      try
      {
         archive.add(null, new MemoryMapArchiveImpl());
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding an archive to a path requires an archive
    * @throws Exception
    */
   @Test
   public void testAddArchiveToPathRequireArchive() throws Exception
   {
      try
      {
         archive.add(new BasicPath("/"), (Archive<?>) null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

}

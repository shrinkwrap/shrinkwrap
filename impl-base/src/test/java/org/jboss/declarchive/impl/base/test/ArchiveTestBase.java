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
package org.jboss.declarchive.impl.base.test;

import java.util.Arrays;
import java.util.Map;

import junit.framework.Assert;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.MemoryMapArchiveImpl;
import org.jboss.declarchive.impl.base.asset.ClassLoaderAsset;
import org.jboss.declarchive.impl.base.io.IOUtil;
import org.jboss.declarchive.impl.base.path.BasicPath;
import org.junit.After;
import org.junit.Test;

/**
 * ArchiveTestBase
 * 
 * Base test for all Archive service providers to help ensure consistency between implementations.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public abstract class ArchiveTestBase<T extends Archive<T>>
{
   /**
    * Get the {@link Archive} to test.
    * 
    * @return A Archive<T> instance.
    */
   protected abstract Archive<T> getArchive();

   /**
    * Create a new {@link Archive} instance.
    * <br/> 
    * Used to test Archive.add(Archive) type addings.
    * 
    * @return A new Archive<T> instance.
    */
   protected abstract Archive<T> createNewArchive();
   
   /**
    * Simple printout of the tested archive. 
    */
   @After
   public void ls()
   {
      Archive<T> archive = getArchive();
      System.out.println("test@jboss:/$ ls -l " + archive.getName());
      System.out.println(archive.toString(true));
   }

   /**
    * Ensure adding an asset to the path results in successful storage.
    * @throws Exception
    */
   @Test
   public void testAddAssetToPath() throws Exception
   {
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
      Path location = new BasicPath("/", "test.properties");
      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      archive.add(location, asset);

      byte[] addedData = IOUtil.asByteArray(asset.getStream());
      byte[] fetchedData = IOUtil.asByteArray(archive.get(location).getStream());
      
      Assert.assertTrue(
            "Asset should be returned from path: " + location.get(), 
            Arrays.equals(addedData, fetchedData));
   }

   /**
    * Ensure get asset requires a path
    * @throws Exception
    */
   @Test
   public void testGetAssetRequiresPath() throws Exception
   {
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      archive.add(location, asset).add(locationTwo, assetTwo);

      Map<Path, Asset> content = archive.getContent();
      
      Assert.assertTrue(
            "Asset should existing in content with key: " + location.get(), 
            ArchiveTestUtil.compareAssets(asset, content.get(location)));
      
      Assert.assertTrue(
            "Asset should existing in content with key: " + locationTwo.get(), 
            ArchiveTestUtil.compareAssets(assetTwo, content.get(locationTwo)));
   }
   
   /**
    * Ensure adding an archive to a path requires a path
    * @throws Exception
    */
   @Test
   public void testAddArchiveToPathRequirePath() throws Exception
   {
      Archive<T> archive = getArchive();
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
      Archive<T> archive = getArchive();
      try
      {
         archive.add(new BasicPath("/"), (Archive<?>) null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding content requires a source archive
    * @throws Exception
    */
   @Test
   public void testAddContentsRequiresSource() throws Exception
   {
      Archive<T> archive = getArchive();
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
    * Ensure adding content from another archive successfully stores all assets
    * @throws Exception
    */
   @Test
   public void testAddContents() throws Exception
   {
      Archive<T> archive = getArchive();
      Archive<T> sourceArchive = createNewArchive();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      archive.addContents(sourceArchive);
      Assert.assertTrue(
            "Asset should have been added to path: " + location.get(), 
            ArchiveTestUtil.compareAssets(archive.get(location), asset));
      
      Assert.assertTrue(
            "Asset should have been added to path: " + location.get(), 
            ArchiveTestUtil.compareAssets(archive.get(locationTwo), assetTwo));
   }

   /**
    * Ensure adding content from another archive to a path successfully stores all assets to specific path
    * @throws Exception
    */
   @Test
   public void testAddContentsToPath() throws Exception
   {
      Archive<T> archive = getArchive();
      Archive<T> sourceArchive = createNewArchive();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      Path baseLocation = new BasicPath("somewhere");

      archive.addContents(baseLocation, sourceArchive);

      Path expectedPath = new BasicPath(baseLocation, location);
      Path expectedPathTwo = new BasicPath(baseLocation, locationTwo);

      Assert.assertTrue(
            "Asset should have been added to path: " + expectedPath.get(), 
            ArchiveTestUtil.compareAssets(archive.get(expectedPath), asset));
      
      Assert.assertTrue(
            "Asset should have been added to path: " + expectedPathTwo.getClass(), 
            ArchiveTestUtil.compareAssets(archive.get(expectedPathTwo), assetTwo));
   }

   /**
    * Ensure adding content from another archive requires a path
    * @throws Exception
    */
   @Test
   public void testAddContentsToPathRequiresPath() throws Exception
   {
      Archive<T> archive = getArchive();
      try
      {
         archive.addContents(null, createNewArchive());
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
      Archive<T> archive = getArchive();
      Archive<T> sourceArchive = createNewArchive();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");
      Asset asset = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      Path baseLocation = new BasicPath("somewhere");

      archive.add(baseLocation, sourceArchive);

      Path expectedPath = new BasicPath(new BasicPath(baseLocation, sourceArchive.getName()), location);
      Path expectedPathTwo = new BasicPath(new BasicPath(baseLocation, sourceArchive.getName()), locationTwo);

      Assert.assertTrue(
            "Asset should have been added to path: " + expectedPath.get(), 
            ArchiveTestUtil.compareAssets(archive.get(expectedPath), asset));
      
      Assert.assertTrue(
            "Asset should have been added to path: " + expectedPathTwo.get(), 
            ArchiveTestUtil.compareAssets(archive.get(expectedPathTwo), assetTwo));
   }

}

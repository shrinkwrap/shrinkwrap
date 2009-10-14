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

import java.util.Arrays;
import java.util.Map;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.ArchiveAsset;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
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
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Name of a properties file upon the test CP
    */
   public static final String NAME_TEST_PROPERTIES = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

   /**
    * Name of another properties file upon the test CP
    */
   public static final String NAME_TEST_PROPERTIES_2 = "org/jboss/shrinkwrap/impl/base/asset/Test2.properties";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Get the {@link Archive} to test.
    * 
    * @return A Archive<T> instance.
    */
   protected abstract T getArchive();

   /**
    * Create a new {@link Archive} instance.
    * <br/> 
    * Used to test Archive.add(Archive) type addings.
    * 
    * @return A new Archive<T> instance.
    */
   protected abstract Archive<T> createNewArchive();

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

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
      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
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
      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

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
    * Ensure adding an asset to a string path results in successful storage.
    * @throws Exception
    */
   @Test
   public void testAddWithStringPath() throws Exception
   {
      Archive<T> archive = getArchive();
      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
      Path location = new BasicPath("/", "test.properties");

      archive.add(location.get(), asset);

      Assert.assertTrue("Asset should be placed on " + new BasicPath("/", "test.properties"), archive
            .contains(location));
   }

   /**
    * Ensure adding an asset to a string path requires path.
    * @throws Exception
    */
   @Test
   public void testAddWithStringPathRequiresPath() throws Exception
   {
      Archive<T> archive = getArchive();
      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

      try
      {
         archive.add((String) null, asset);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure adding an asset to the path string requires an asset.
    * @throws Exception
    */
   @Test
   public void testAddWithStringPathRequiresAssets() throws Exception
   {
      Archive<T> archive = getArchive();
      try
      {
         archive.add("/Test.properties", (Asset) null);
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
      final Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
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
      final Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
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
      final String resource = NAME_TEST_PROPERTIES;
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
      String resource = NAME_TEST_PROPERTIES;
      Path location = new BasicPath("/", "test.properties");
      archive.add(location, new ClassLoaderAsset(resource));
      Assert.assertTrue(archive.contains(location)); // Sanity check

      Assert.assertTrue("Successfully deleting an Asset should return true", archive.delete(location));

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

      Assert.assertFalse("Deleting a non-existent Asset should return false", archive.delete(location));
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
      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
      archive.add(location, asset);

      Asset fetchedAsset = archive.get(location);

      Assert.assertTrue("Asset should be returned from path: " + location.get(), compareAssets(asset, fetchedAsset));
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
    * Ensure an asset can be retrieved by a string path
    * @throws Exception
    */
   @Test
   public void testGetAssetWithString() throws Exception
   {
      Archive<T> archive = getArchive();
      Path location = new BasicPath("/", "test.properties");
      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
      archive.add(location, asset);

      Asset fetchedAsset = archive.get(location.get());

      Assert.assertTrue("Asset should be returned from path: " + location.get(), compareAssets(asset, fetchedAsset));
   }

   /**
    * Ensure get asset by string requires a path
    * @throws Exception
    */
   @Test
   public void testGetAssetWithStringRequiresPath() throws Exception
   {
      Archive<T> archive = getArchive();
      try
      {
         archive.get((String) null);
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

      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
      Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
      archive.add(location, asset).add(locationTwo, assetTwo);

      Map<Path, Asset> content = archive.getContent();

      final Asset asset1 = content.get(location);
      final Asset asset2 = content.get(locationTwo);

      Assert.assertTrue("Asset should existing in content with key: " + location.get(), this.compareAssets(asset,
            asset1));

      Assert.assertTrue("Asset should existing in content with key: " + locationTwo.get(), this.compareAssets(assetTwo,
            asset2));
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
    * Ensure merging content requires a source archive
    * @throws Exception
    */
   @Test
   public void testMergeRequiresSource() throws Exception
   {
      Archive<T> archive = getArchive();
      try
      {
         archive.merge(null);
         Assert.fail("Should have throw an IllegalArgumentException");
      }
      catch (IllegalArgumentException expectedException)
      {
      }
   }

   /**
    * Ensure merging content from another archive successfully stores all assets
    * @throws Exception
    */
   @Test
   public void testMerge() throws Exception
   {
      Archive<T> archive = getArchive();
      Archive<T> sourceArchive = createNewArchive();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
      Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      archive.merge(sourceArchive);
      Assert.assertTrue("Asset should have been added to path: " + location.get(), this.compareAssets(archive
            .get(location), asset));

      Assert.assertTrue("Asset should have been added to path: " + location.get(), this.compareAssets(archive
            .get(locationTwo), assetTwo));
   }

   /**
    * Ensure merging content from another archive to a path successfully stores all assets to specific path
    * @throws Exception
    */
   @Test
   public void testMergeToPath() throws Exception
   {
      Archive<T> archive = getArchive();
      Archive<T> sourceArchive = createNewArchive();
      Path location = new BasicPath("/", "test.properties");
      Path locationTwo = new BasicPath("/", "test2.properties");

      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);
      Asset assetTwo = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);
      sourceArchive.add(location, asset).add(locationTwo, assetTwo);

      Path baseLocation = new BasicPath("somewhere");

      archive.merge(baseLocation, sourceArchive);

      Path expectedPath = new BasicPath(baseLocation, location);
      Path expectedPathTwo = new BasicPath(baseLocation, locationTwo);

      Assert.assertTrue("Asset should have been added to path: " + expectedPath.get(), this.compareAssets(archive
            .get(expectedPath), asset));

      Assert.assertTrue("Asset should have been added to path: " + expectedPathTwo.getClass(), this.compareAssets(
            archive.get(expectedPathTwo), assetTwo));
   }

   /**
    * Ensure merging content from another archive requires a path
    * @throws Exception
    */
   @Test
   public void testMergeToPathRequiresPath() throws Exception
   {
      Archive<T> archive = getArchive();
      try
      {
         archive.merge(null, createNewArchive());
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

      Path baseLocation = new BasicPath("somewhere");

      archive.add(baseLocation, sourceArchive);

      Path expectedPath = new BasicPath(baseLocation, sourceArchive.getName());

      Asset asset = archive.get(expectedPath);
      Assert.assertNotNull("Asset should have been added to path: " + expectedPath.get(), asset);
      Assert.assertTrue("An instance of ArchiveAsset should have been added to path: " + expectedPath.get(),
            asset instanceof ArchiveAsset);
      ArchiveAsset archiveAsset = ArchiveAsset.class.cast(asset);

      Archive<?> nestedArchive = archiveAsset.getArchive();
      Assert.assertEquals("Nested Archive should be same archive that was added", sourceArchive, nestedArchive);

   }

   /**
    * Ensure an archive contains assets from nested archives.
    * @throws Exception
    */
   @Test
   public void testNestedArchiveContains() throws Exception
   {
      Archive<T> archive = getArchive();

      Archive<T> sourceArchive = createNewArchive();

      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

      Path nestedAssetPath = new BasicPath("/", "test.properties");

      sourceArchive.add(nestedAssetPath, asset);

      Path baseLocation = new BasicPath("somewhere");

      archive.add(baseLocation, sourceArchive);

      Path archivePath = new BasicPath(baseLocation, sourceArchive.getName());

      Path expectedPath = new BasicPath(archivePath, "test.properties");

      Assert.assertTrue("Nested archive assets should be verified through a fully qualified path", archive
            .contains(expectedPath));
   }

   /**
    * Ensure assets from a nested archive are accessible from parent archives.
    * @throws Exception
    */
   @Test
   public void testNestedArchiveGet() throws Exception
   {
      Archive<T> archive = getArchive();

      Archive<T> nestedArchive = createNewArchive();

      Path baseLocation = new BasicPath("somewhere");

      archive.add(baseLocation, nestedArchive);

      Archive<T> nestedNestedArchive = createNewArchive();

      nestedArchive.add(new BasicPath("/"), nestedNestedArchive);
      
      Asset asset = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

      Path nestedAssetPath = new BasicPath("/", "test.properties");

      nestedNestedArchive.add(nestedAssetPath, asset);
      
      Path nestedArchivePath = new BasicPath(baseLocation, nestedArchive.getName());
      
      Path nestedNestedArchivePath = new BasicPath(nestedArchivePath, nestedNestedArchive.getName());

      Path expectedPath = new BasicPath(nestedNestedArchivePath, "test.properties");

      Asset nestedAsset = archive.get(expectedPath);

      Assert.assertNotNull("Nested archive asset should be available through partent archive at " + expectedPath.get(),
            nestedAsset);
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Compare two Asset with each other.
    * <br/>
    * Does not check instances but content.
    * 
    * @param one Asset to compare
    * @param two Asset to compare
    * @return true if they are equal
    * @throws IllegalArgumentException If either asset is not specified
    */
   private boolean compareAssets(final Asset one, final Asset two) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(one, "Asset one must be specified");
      Validate.notNull(two, "Asset two must be specified");

      byte[] oneData = IOUtil.asByteArray(one.openStream());
      byte[] twoData = IOUtil.asByteArray(two.openStream());

      return Arrays.equals(oneData, twoData);
   }

}

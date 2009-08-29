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
package org.jboss.declarchive.impl.base.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.export.ExplodedExporter;
import org.jboss.declarchive.impl.base.MemoryMapArchiveImpl;
import org.jboss.declarchive.impl.base.asset.ClassLoaderAsset;
import org.jboss.declarchive.impl.base.io.IOUtil;
import org.jboss.declarchive.impl.base.path.BasicPath;
import org.jboss.declarchive.spi.MemoryMapArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * ExplodedExporterTestCase
 * 
 * TestCase to ensure that the {@link ExplodedExporter} correctly exports archive.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public class ExplodedExporterTestCase extends ExportTestBase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ExplodedExporterTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensure an archive can be exported to an exploded directory.
    * 
    * @throws Exception
    */
   @Test
   public void testExportExploded() throws Exception
   {
      log.info("testExportExploded");

      // Get a temp directory
      File tempDirectory = createTempDirectory("testExportExploded");

      // Get an archive instance
      MemoryMapArchive archive = new MemoryMapArchiveImpl("testArchive.jar");

      // Add some content
      Asset assetOne = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test.properties");
      Path pathOne = new BasicPath("test.properties");
      archive.add(pathOne, assetOne);
      Asset assetTwo = new ClassLoaderAsset("org/jboss/declarchive/impl/base/asset/Test2.properties");
      Path pathTwo = new BasicPath("nested", "test2.properties");
      archive.add(pathTwo, assetTwo);

      // Add a nested archive for good measure
      MemoryMapArchive nestedArchive = new MemoryMapArchiveImpl("nestedArchive.jar");
      nestedArchive.add(pathOne, assetOne);
      nestedArchive.add(pathTwo, assetTwo);
      archive.add(new BasicPath(), nestedArchive);

      // Export as Exploded directory
      File explodedDirectory = ExplodedExporter.exportExploded(archive, tempDirectory);

      // Validate the exploded directory was created 
      Assert.assertNotNull(explodedDirectory);

      // Assert the directory has the correct name
      File expectedDirectory = new File(tempDirectory, archive.getName());
      Assert.assertEquals(expectedDirectory, explodedDirectory);

      // Validate entries were written out
      assertAssetInExploded(explodedDirectory, pathOne, assetOne);
      assertAssetInExploded(explodedDirectory, pathTwo, assetTwo);

      // Validate nested archive entries were written out
      Path nestedArchivePath = new BasicPath(nestedArchive.getName());

      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, pathOne), assetOne);
      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, pathTwo), assetTwo);

   }

   /**
    * Ensure an archive is required to export.
    * 
    * @throws Exception
    */
   @Test
   public void testExportExplodedRequiresArchive() throws Exception
   {
      log.info("testExportExplodedRequiresArchive");

      try
      {
         ExplodedExporter.exportExploded(null, getNonexistantDirectory());
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   /**
    * Ensure an baseDirectory is required to export.
    * 
    * @throws Exception
    */
   @Test
   public void testExportExplodedRequiresBaseDirectroy() throws Exception
   {
      log.info("testExportExplodedRequiresBaseDirectroy");

      try
      {
         ExplodedExporter.exportExploded(new MemoryMapArchiveImpl(), null);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   /**
    * Ensure an baseDirectory must exist is required to export.
    * 
    * @throws Exception
    */
   @Test
   public void testExportExplodedRequiresExisitingDirectroy() throws Exception
   {
      log.info("testExportExplodedRequiresExisitingDirectroy");

      try
      {
         final File directory = this.getNonexistantDirectory();
         ExplodedExporter.exportExploded(new MemoryMapArchiveImpl(), directory);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   /**
    * Ensure an baseDirectory must be a directory.
    * 
    * @throws Exception
    */
   @Test
   public void testExportExplodedRequiresValidDirectory() throws Exception
   {
      log.info("testExportExplodedRequiresValidDirectory");
      try
      {
         final File directory = this.getNonexistantDirectory();
         ExplodedExporter.exportExploded(new MemoryMapArchiveImpl(), directory);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains a reference to a directory that does not exist
    */
   private File getNonexistantDirectory()
   {
      final File directory = new File(this.getTarget(), "someNonExistentDirectory");
      if (directory.exists())
      {
         IOUtil.deleteDirectory(directory);
      }
      Assert.assertTrue("Precondition Failure: Directory should not exist: " + directory, !directory.exists());
      return directory;
   }

   /**
    * Assert an asset is actually in the exploded directory
    * 
    * @throws FileNotFoundException 
    * @throws IOException 
    * @throws IllegalArgumentException 
    */
   private void assertAssetInExploded(File explodedDirectory, Path path, Asset asset) throws FileNotFoundException
   {
      File assetFile = new File(explodedDirectory, path.get());
      Assert.assertNotNull(assetFile);
      Assert.assertTrue(assetFile.exists());
      byte[] expectedContents = IOUtil.asByteArray(asset.getStream());

      InputStream inputStream = new FileInputStream(assetFile);

      byte[] actualContents = IOUtil.asByteArray(inputStream);
      Assert.assertArrayEquals(expectedContents, actualContents);
   }

}

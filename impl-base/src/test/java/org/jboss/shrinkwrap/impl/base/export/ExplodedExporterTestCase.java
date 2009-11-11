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
package org.jboss.shrinkwrap.impl.base.export;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.export.ArchiveExportException;
import org.jboss.shrinkwrap.api.export.ExplodedExporter;
import org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
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
      Archive<?> archive = createArchiveWithAssets();

      // Export as Exploded directory
      File explodedDirectory = ExplodedExporter.exportExploded(archive, tempDirectory);

      // Validate the exploded directory was created 
      Assert.assertNotNull(explodedDirectory);

      // Assert the directory has the correct name
      File expectedDirectory = new File(tempDirectory, archive.getName());
      Assert.assertEquals(expectedDirectory, explodedDirectory);

      // Validate entries were written out
      assertAssetInExploded(explodedDirectory, PATH_ONE, ASSET_ONE);
      assertAssetInExploded(explodedDirectory, PATH_TWO, ASSET_TWO);
   }

   /**
    * Ensure an archive exported to an exploded directory properly explodes nested archives.
    * 
    * @throws Exception
    */
   @Test
   public void testExportNestedExploded() throws Exception
   {
      log.info("testExportNestedExploded");

      // Get a temp directory
      File tempDirectory = createTempDirectory("testExportNestedExploded");

      // Get an archive instance
      Archive<?> archive = createArchiveWithNestedArchives();

      // Export as Exploded directory
      File explodedDirectory = ExplodedExporter.exportExploded(archive, tempDirectory);

      // Validate the exploded directory was created 
      Assert.assertNotNull(explodedDirectory);

      // Assert the directory has the correct name
      File expectedDirectory = new File(tempDirectory, archive.getName());
      Assert.assertEquals(expectedDirectory, explodedDirectory);

      // Validate nested archive entries were written out
      Path nestedArchivePath = new BasicPath(NAME_NESTED_ARCHIVE);

      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, PATH_ONE), ASSET_ONE);
      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, PATH_TWO), ASSET_TWO);

      Path nestedArchivePathTwo = new BasicPath(NESTED_PATH, NAME_NESTED_ARCHIVE_2);

      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePathTwo, PATH_ONE), ASSET_ONE);
      assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePathTwo, PATH_TWO), ASSET_TWO);
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
   public void testExportExplodedRequiresExistingDirectory() throws Exception
   {
      log.info("testExportExplodedRequiresExistingDirectory");
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
    * Ensure ExpolodedExporter requires a directory
    */
   @Test
   public void testExportExplodedRequiresValidDirectory() throws Exception
   {
      log.info("testExportExplodedRequiresValidDirectory");
      try
      {
         final File nonDirectory = new File(this.getTarget(), "tempFile.txt");
         nonDirectory.createNewFile();
         ExplodedExporter.exportExploded(new MemoryMapArchiveImpl(), nonDirectory);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }
   }

   /**
    * Ensure an ArchiveExportException is thrown when output directory can not be created
    */
   @Test
   public void testExportExplodedOutpuDirCreationFails() throws Exception
   {
      log.info("testExportExplodedOutpuDirCreationFails");
      try
      {
         final File directory = createTempDirectory("testExportExplodedOutpuDirCreationFails");
         // Will cause the creation of Archive directory to fail
         final File existingFile = new File(directory, NAME_ARCHIVE);
         existingFile.createNewFile();
         ExplodedExporter.exportExploded(new MemoryMapArchiveImpl(NAME_ARCHIVE), directory);
         Assert.fail("Should have thrown ArchiveExportException");
      }
      catch (ArchiveExportException expected)
      {
      }
   }

   /**
    * Ensure ArchiveException is thrown if Asset can not be written
    */
   @Test
   public void testExportExplodedThrowsExceptionOnAssetWrite() throws Exception
   {
      log.info("testExportExplodedThrowsExceptionOnAssetWrite");
      try
      {
         Archive<?> archive = createArchiveWithAssets();
         archive.add(new BasicPath("badAsset"), new Asset()
         {

            @Override
            public InputStream openStream()
            {
               throw new RuntimeException("Mock Esception getting Stream");
            }

         });
         final File directory = createTempDirectory("testExportExplodedThrowsExceptionOnAssetWrite");

         ExplodedExporter.exportExploded(archive, directory);
         Assert.fail("Should have thrown ArchiveExportException");
      }
      catch (ArchiveExportException expected)
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
      byte[] expectedContents = IOUtil.asByteArray(asset.openStream());

      InputStream inputStream = new FileInputStream(assetFile);

      byte[] actualContents = IOUtil.asByteArray(inputStream);
      Assert.assertArrayEquals(expectedContents, actualContents);
   }

}

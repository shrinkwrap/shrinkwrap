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
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;
import org.jboss.shrinkwrap.api.exporter.ZipExportHandle;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestCase to ensure that the {@link ZipExporter} correctly exports archives to Zip format.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ZipExporterTestCase extends ExportTestBase
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipExporterTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Test to make sue an archive can be exported to Zip and all contents are correctly located in the Zip.
    * @throws Exception
    */
   @Test
   public void testExportZip() throws Exception
   {
      log.info("testExportZip");

      // Get a temp directory for the test
      File tempDirectory = createTempDirectory("testExportZip");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as Zip InputStream
      final ZipExportHandle handle = archive.as(ZipExporter.class).exportZip();
      final InputStream zipStream = handle.getContent();

      // Write zip content to temporary file 
      ZipFile expectedZip = getExportedZipFile(NAME_ARCHIVE, zipStream, tempDirectory);

      // Ensure all's OK
      handle.checkComplete();

      // Validate
      ensureZipFileInExpectedForm(expectedZip);
   }

   /**
    * Test to ensure that the {@link JdkZipExporterDelegate} does not accept 
    * an empty archive as input
    * 
    * SHRINKWRAP-93
    * 
    * @throws Exception
    */
   @Test(expected = IllegalArgumentException.class)
   public void exportEmptyArchiveAsZip() throws Exception
   {
      // Attempt to export an empty archive, should fail
      Archives.create(NAME_ARCHIVE, JavaArchive.class).as(ZipExporter.class).exportZip();
   }

   /**
    * Test to make sue an archive can be exported to Zip and all contents are correctly located in the Zip.
    * @throws Exception
    */
   @Test(expected = IllegalStateException.class)
   public void checkCompleteBeforeReadingContents() throws Exception
   {
      log.info("checkCompleteBeforeReadingContents");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as Zip InputStream
      final ZipExportHandle handle = archive.as(ZipExporter.class).exportZip();
      // We cannot check complete until we fully read the instream from the handle
      handle.checkComplete();

   }

   /**
    * Ensures that we can export archives of large sizes without
    * leading to {@link OutOfMemoryError}
    */
   @Test
   public void exportHugeArchive() throws IOException
   {
      // Log
      log.info("exportHugeArchive");
      log.info("This test may take awhile as it's intended to fill memory");

      // Get an archive instance
      JavaArchive archive = Archives.create("hugeArchive.jar", JavaArchive.class);

      // Approximate the free memory to start
      final Runtime runtime = Runtime.getRuntime();
      final long startFreeMemBytes = totalFreeMemory(runtime);
      long currentFreeMemBytes = startFreeMemBytes;
      int counter = 0;
      // Loop through and add a MB Asset
      final String pathPrefix = "path";

      // Fill up the archive until we'e got only 30% of memory left
      while (currentFreeMemBytes > (startFreeMemBytes * .3))
      {
         archive.add(MegaByteAsset.newInstance(), pathPrefix + counter++);
         System.gc(); // Signal to the VM to try to clean up a bit, not the most reliable, but makes this OK on my machine
         currentFreeMemBytes = totalFreeMemory(runtime);
         log.info("Current Free Memory (MB): " + currentFreeMemBytes / 1024 / 1024);
      }
      log.info("Wrote: " + archive.toString());
      log.info("Started w/ free memory: " + startFreeMemBytes / 1024 / 1024 + "MB");
      log.info("Current free memory: " + currentFreeMemBytes / 1024 / 1024 + "MB");

      // Export; at this point we have less than 50% available memory so 
      // we can't carry the whole archive in RAM twice; this
      // should ensure the ZIP impl uses an internal buffer
      archive.as(ZipExporter.class).exportZip();
   }

   /**
    * Test to make sure an archive can be exported to Zip (file) and all 
    * contents are correctly located in the Zip.
    * @throws Exception
    */
   @Test
   public void testExportZipToFile() throws IOException
   {
      log.info("testExportZipToFile");

      // Get a temp directory for the test
      File tempDirectory = createTempDirectory("testExportZipToFile");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as File
      final File exported = new File(tempDirectory, archive.getName());
      archive.as(ZipExporter.class).exportZip(exported, true);

      // Get as ZipFile
      final ZipFile expectedZip = new ZipFile(exported);

      // Validate
      ensureZipFileInExpectedForm(expectedZip);
   }

   /**
    * Test to make sure an archive can be exported to Zip (file) and all 
    * contents are correctly located in the Zip.
    * @throws Exception
    */
   @Test(expected = FileExistsException.class)
   public void testExportZipToExistingFileFails() throws IOException
   {
      log.info("testExportZipToExistingFileFails");

      // Get a temp directory for the test
      File tempDirectory = createTempDirectory("testExportZipToExistingFileFails");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as File
      final File alreadyExists = new File(tempDirectory, archive.getName());
      final OutputStream alreadyExistsOutputStream = new FileOutputStream(alreadyExists);
      alreadyExistsOutputStream.write(new byte[]
      {});
      alreadyExistsOutputStream.close();
      TestCase.assertTrue("The test setup is incorrect; an empty file should exist before writing the archive",
            alreadyExists.exists());

      // Try to write to a file that already exists (should fail)
      archive.as(ZipExporter.class).exportZip(alreadyExists);
   }

   /**
    * Test to make sue an archive can be exported to Zip and nested archives are also in exported as nested Zip.
    * @throws Exception
    */
   @Test
   public void testExportNestedZip() throws Exception
   {
      log.info("testExportNestedZip");

      // Get a temp directory for the test
      File tempDirectory = createTempDirectory("testExportNestedZip");

      // Get an archive instance
      Archive<?> archive = createArchiveWithNestedArchives();

      // Export as Zip InputStream
      InputStream zipStream = archive.as(ZipExporter.class).exportZip().getContent();

      // Write out and retrieve Zip 
      ZipFile expectedZip = getExportedZipFile(NAME_ARCHIVE, zipStream, tempDirectory);

      // Validate entries were written out
      assertAssetInZip(expectedZip, PATH_ONE, ASSET_ONE);
      assertAssetInZip(expectedZip, PATH_TWO, ASSET_TWO);

      // Validate nested archive entries were written out
      ArchivePath nestedArchivePath = new BasicPath(NAME_NESTED_ARCHIVE);

      // Get Zip entry path
      String nestedArchiveZipEntryPath = PathUtil.optionallyRemovePrecedingSlash(nestedArchivePath.get());

      // Get nested archive entry from exported zip
      ZipEntry nestedArchiveEntry = expectedZip.getEntry(nestedArchiveZipEntryPath);

      // Get inputstream for entry 
      InputStream nesterArchiveStream = expectedZip.getInputStream(nestedArchiveEntry);

      // Write out and retrieve nested Zip
      ZipFile nestedZip = getExportedZipFile(NAME_NESTED_ARCHIVE, nesterArchiveStream, tempDirectory);

      assertAssetInZip(nestedZip, PATH_ONE, ASSET_ONE);
      assertAssetInZip(nestedZip, PATH_TWO, ASSET_TWO);

      // Validate nested archive entries were written out
      ArchivePath nestedArchiveTwoPath = new BasicPath(NESTED_PATH, NAME_NESTED_ARCHIVE_2);

      // Get Zip entry path
      String nestedArchiveTwoZipEntryPath = PathUtil.optionallyRemovePrecedingSlash(nestedArchiveTwoPath.get());

      // Get second nested archive entry from exported zip
      ZipEntry nestedArchiveTwoEntry = expectedZip.getEntry(nestedArchiveTwoZipEntryPath);

      // Get inputstream for entry 
      InputStream nesterArchiveTwoStream = expectedZip.getInputStream(nestedArchiveTwoEntry);

      // Write out and retrieve second nested Zip
      ZipFile nestedZipTwo = getExportedZipFile(NAME_NESTED_ARCHIVE_2, nesterArchiveTwoStream, tempDirectory);

      assertAssetInZip(nestedZipTwo, PATH_ONE, ASSET_ONE);
      assertAssetInZip(nestedZipTwo, PATH_TWO, ASSET_TWO);

   }

   @Test(expected = ArchiveExportException.class)
   public void testExportThrowsArchiveExceptionOnAssetWriteFailure() throws IOException
   {
      log.info("testExportThrowsArchiveExcepitonOnAssetWriteFailure");
      Archive<?> archive = createArchiveWithAssets();

      archive.add(new Asset()
      {
         @Override
         public InputStream openStream()
         {
            throw new RuntimeException("Mock Exception from an Asset write");
         }

      }, PATH_ONE);

      // Export
      final ZipExportHandle handle = archive.as(ZipExporter.class).exportZip();

      // Read in the full content (to in turn empty the underlying buffer and ensure we complete)
      final InputStream in = handle.getContent();
      final OutputStream sink = new OutputStream()
      {

         @Override
         public void write(int b) throws IOException
         {
         }
      };
      IOUtil.copyWithClose(in, sink);
      // Get access to the underlying exception
      handle.checkComplete();

   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private ZipFile getExportedZipFile(String archiveName, InputStream zipStream, File tempDirectory) throws Exception
   {

      // Validate the InputStream was created 
      Assert.assertNotNull(zipStream);

      // Create a temp file
      File outFile = new File(tempDirectory, archiveName);

      // Write Zip contents to file
      writeOutFile(outFile, zipStream);

      // Use standard ZipFile library to read in written Zip file
      ZipFile expectedZip = new ZipFile(outFile);

      return expectedZip;
   }

   /**
    * Assert an asset is actually in the Zip file
    * @throws IOException 
    * @throws IllegalArgumentException 
    */
   private void assertAssetInZip(ZipFile expectedZip, ArchivePath path, Asset asset) throws IllegalArgumentException,
         IOException
   {
      final ZipEntry entry = this.getEntryFromZip(expectedZip, path);
      byte[] expectedContents = IOUtil.asByteArray(asset.openStream());
      byte[] actualContents = IOUtil.asByteArray(expectedZip.getInputStream(entry));
      Assert.assertArrayEquals(expectedContents, actualContents);
   }

   /**
    * Obtains the entry from the specified ZIP file at the specified Path, ensuring
    * it exists along the way
    * @param expectedZip
    * @param path
    * @return
    * @throws IllegalArgumentException
    * @throws IOException
    */
   private ZipEntry getEntryFromZip(final ZipFile expectedZip, final ArchivePath path) throws IllegalArgumentException,
         IOException
   {
      String entryPath = PathUtil.optionallyRemovePrecedingSlash(path.get());
      ZipEntry entry = expectedZip.getEntry(entryPath);
      Assert.assertNotNull("Expected path not found in ZIP: " + path, entry);
      return entry;
   }

   /**
    * Write a InputStream out to file.
    * @param outFile
    * @param zipInputStream
    * @throws Exception
    */
   private void writeOutFile(File outFile, InputStream inputStream) throws Exception
   {
      OutputStream fileOutputStream = new FileOutputStream(outFile);
      IOUtil.copyWithClose(inputStream, fileOutputStream);
   }

   /**
    * Ensures that the specified {@link ZipFile} contains entries
    * in the expected form
    * @param expectedZip
    * @throws IOException
    */
   private void ensureZipFileInExpectedForm(final ZipFile expectedZip) throws IOException
   {
      // Validate entries were written out
      assertAssetInZip(expectedZip, PATH_ONE, ASSET_ONE);
      assertAssetInZip(expectedZip, PATH_TWO, ASSET_TWO);

      // Validate all paths were written
      // SHRINKWRAP-94
      getEntryFromZip(expectedZip, NESTED_PATH);

      // Ensure we don't write the root PAth
      // SHRINKWRAP-96
      ZipEntry rootEntry = expectedZip.getEntry("/");
      Assert.assertNull("ZIP should not have explicit root path written (SHRINKWRAP-96)", rootEntry);
   }

   /**
    * Obtains an estimate of the total amount of free memory available to the JVM
    * @param runtime
    * @return
    */
   private static long totalFreeMemory(final Runtime runtime)
   {
      return runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
   }

   /**
    * An {@link Asset} which contains a megabyte of dummy data
    *
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    */
   private static class MegaByteAsset extends ByteArrayAsset implements Asset
   {
      /**
       * Dummy megabyte
       */
      private static int MEGA = 1024 * 1024;

      private static final Random random = new Random();

      private MegaByteAsset(final byte[] content)
      {
         super(content);
      }

      static MegaByteAsset newInstance()
      {
         /**s
          * Bytes must be random/distributed so that compressing these in ZIP
          * isn't too efficient
          */
         final byte[] content = new byte[MEGA];
         random.nextBytes(content);
         return new MegaByteAsset(content);
      }
   }

}

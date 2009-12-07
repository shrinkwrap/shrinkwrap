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
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * ZipExporterTestCase
 * 
 * TestCase to ensure that the {@link ZipExporter} correctly exports archives to Zip format.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
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
      InputStream zipStream = archive.as(ZipExporter.class).exportZip();

      // Write zip content to temporary file 
      ZipFile expectedZip = getExportedZipFile(NAME_ARCHIVE, zipStream, tempDirectory);

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
      InputStream zipStream = archive.as(ZipExporter.class).exportZip();

      // Write out and retrieve Zip 
      ZipFile expectedZip = getExportedZipFile(NAME_ARCHIVE, zipStream, tempDirectory);

      // Validate entries were written out
      assertAssetInZip(expectedZip, PATH_ONE, ASSET_ONE);
      assertAssetInZip(expectedZip, PATH_TWO, ASSET_TWO);

      // Validate nested archive entries were written out
      Path nestedArchivePath = new BasicPath(NAME_NESTED_ARCHIVE);

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
      Path nestedArchiveTwoPath = new BasicPath(NESTED_PATH, NAME_NESTED_ARCHIVE_2);

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
   public void testExportThrowsArchiveExceptionOnAssetWriteFailure()
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

      archive.as(ZipExporter.class).exportZip();
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
   private void assertAssetInZip(ZipFile expectedZip, Path path, Asset asset) throws IllegalArgumentException,
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
   private ZipEntry getEntryFromZip(final ZipFile expectedZip, final Path path) throws IllegalArgumentException,
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

}

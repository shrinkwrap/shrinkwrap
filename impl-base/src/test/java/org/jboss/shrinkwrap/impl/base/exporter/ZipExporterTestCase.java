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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
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
      final InputStream zipStream = archive.as(ZipExporter.class).exportZip();

      // Write zip content to temporary file 
      ZipFile expectedZip = getExportedZipFile(NAME_ARCHIVE, zipStream, tempDirectory);

      // Validate
      ensureZipFileInExpectedForm(expectedZip);
   }

   /**
    * Ensures that the ZIP export write task uses the {@link ExecutorService}
    * that we've configured, and leaves it running (does not shut it down)
    * @throws Exception
    */
   @Test
   public void exportUsesOurExecutorService() throws Exception
   {
      // Make a custom ES
      final CountingExecutorService service = new CountingExecutorService();

      // Create a custom configuration
      final Domain domain = ShrinkWrap.createDomain(new ConfigurationBuilder().executorService(service).build());

      // Make an archive using the new configuration
      final Archive<?> archive = domain.getArchiveFactory().create(JavaArchive.class, "test.jar").addClass(
            ZipExporterTestCase.class);

      // Fully export by reading all content (export is on-demand)
      final InputStream zip = archive.as(ZipExporter.class).exportZip();
      while (zip.read() != -1)
      {

      }

      // Ensure the ES was used (one job was submitted to it)
      TestCase.assertEquals("Custom " + ExecutorService.class.getSimpleName() + " was not used by ZIP export", 1,
            service.counter);

      // Ensure the ES was not shut down by the export process
      TestCase.assertFalse("ZIP Export should not shut down a user-supplied " + ExecutorService.class.getName(),
            service.isShutdown());

      // Shut down the ES (clean up)
      service.shutdown();
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
      ShrinkWrap.create(JavaArchive.class, NAME_ARCHIVE).as(ZipExporter.class).exportZip();
   }

   /**
    * Test to ensure that the {@link JdkZipExporterDelegate} accepts an archive
    * with only directories, no assets.
    * 
    * @throws Exception
    */
   @Test
   public void testExportArchiveWithOnlyDirectories()
   {
      // Attempt to export an archive with some directories, should pass
      ShrinkWrap.create(JavaArchive.class, NAME_ARCHIVE).addDirectories("/test/game").as(ZipExporter.class).exportZip();
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
    * Test to make sure an archive can be exported to Zip (OutStream) and all 
    * contents are correctly located in the Zip.
    * @throws Exception
    */
   @Test
   public void testExportZipToOutStream() throws IOException
   {
      log.info("testExportZipToOutStream");

      // Get a temp directory for the test
      final File tempDirectory = createTempDirectory("testExportZipToOutStream");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as OutStream and flush to a file manually
      final File serializedArchive = new File(tempDirectory, archive.getName());
      final OutputStream out = new FileOutputStream(serializedArchive);
      archive.as(ZipExporter.class).exportZip(out);

      // Get as ZipFile
      final ZipFile expectedZip = new ZipFile(serializedArchive);

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
      InputStream zipStream = archive.as(ZipExporter.class).exportZip();

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
      log.info("testExportThrowsArchiveExceptionOnAssetWriteFailure");
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
      final InputStream in = archive.as(ZipExporter.class).exportZip();

      // Read in the full content (to in turn empty the underlying buffer and ensure we complete)
      final OutputStream sink = new OutputStream()
      {
         @Override
         public void write(int b) throws IOException
         {
         }
      };
      IOUtil.copyWithClose(in, sink);

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
    * Test implementation of an {@link ExecutorService} which 
    * counts all jobs submitted.
    * 
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    * @version $Revision: $
    */
   private static class CountingExecutorService implements ExecutorService
   {

      private final ExecutorService delegate;

      int counter = 0;

      public CountingExecutorService()
      {
         delegate = Executors.newSingleThreadExecutor();
      }

      @Override
      public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException
      {
         return delegate.awaitTermination(timeout, unit);
      }

      @Override
      public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException
      {
         return delegate.invokeAll(tasks);
      }

      @Override
      public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException
      {
         return delegate.invokeAll(tasks, timeout, unit);
      }

      @Override
      public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException,
            ExecutionException
      {
         return delegate.invokeAny(tasks);
      }

      @Override
      public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException
      {
         return delegate.invokeAny(tasks, timeout, unit);
      }

      @Override
      public boolean isShutdown()
      {
         return delegate.isShutdown();
      }

      @Override
      public boolean isTerminated()
      {
         return delegate.isTerminated();
      }

      @Override
      public void shutdown()
      {
         delegate.shutdown();
      }

      @Override
      public List<Runnable> shutdownNow()
      {
         return delegate.shutdownNow();
      }

      @Override
      public <T> Future<T> submit(final Callable<T> task)
      {
         counter++;
         return delegate.submit(task);
      }

      @Override
      public Future<?> submit(final Runnable task)
      {
         counter++;
         return delegate.submit(task);
      }

      @Override
      public <T> Future<T> submit(final Runnable task, final T result)
      {
         counter++;
         return delegate.submit(task, result);
      }

      @Override
      public void execute(final Runnable command)
      {
         counter++;
         delegate.execute(command);
      }

   }
}

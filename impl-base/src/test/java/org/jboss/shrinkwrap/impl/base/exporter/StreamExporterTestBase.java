/*
 * JBoss, Home of Professional Open Source  
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
import java.io.FileInputStream;
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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Assert;
import org.junit.Test;

/**
 * Base support for testing stream-based exporters
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public abstract class StreamExporterTestBase extends ExportTestBase
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(StreamExporterTestBase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Exports the specified archive as an {@link InputStream}
    * @param archive
    * @return
    */
   protected abstract InputStream exportAsInputStream(Archive<?> archive);

   /**
    * Exports the specified archive to an {@link OutputStream}
    * @param archive
    * @return
    */
   protected abstract void exportToOutputStream(Archive<?> archive, OutputStream out);

   /**
    * Exports the specified archive as a {@link File}
    * @param archive
    * @param file
    * @param overwrite
    */
   protected abstract void exportAsFile(Archive<?> archive, File file, boolean overwrite);

   /**
    * Ensures the contents of the specified {@link InputStream} are
    * as expected
    * @param instream
    * @throws IOException If an I/O error occurred
    */
   protected abstract void ensureInExpectedForm(InputStream instream) throws IOException;

   /**
    * Ensures the contents of the specified {@link File} are
    * as expected
    * @param file
    * @throws IOException If an I/O error occurred
    */
   protected abstract void ensureInExpectedForm(File file) throws IOException;

   /**
    * Obtains the {@link Asset} located at the specified {@link ArchivePath} in the
    * specified {@link File}, or null if nothing is found at the specified path
    * @param file
    * @param path
    * @return
    */
   protected abstract InputStream getContentsFromExportedFile(File file, ArchivePath path) throws IOException;

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Test to make sue an archive can be exported and all contents are correctly located.
    * @throws Exception
    */
   @Test
   public void testExport() throws Exception
   {
      log.info("testExport");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as InputStream
      final InputStream exportStream = this.exportAsInputStream(archive);

      // Validate
      ensureInExpectedForm(exportStream);
   }

   /**
    * Ensures that the export write task uses the {@link ExecutorService}
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
            StreamExporterTestBase.class);

      // Fully export by reading all content (export is on-demand)
      final InputStream content = this.exportAsInputStream(archive);
      final OutputStream sink = new OutputStream()
      {

         @Override
         public void write(int b) throws IOException
         {
            //NOOP
         }
      };
      IOUtil.copyWithClose(content, sink);

      // Ensure the ES was used (one job was submitted to it)
      Assert.assertEquals("Custom " + ExecutorService.class.getSimpleName() + " was not used by export process", 1,
            service.counter);

      // Ensure the ES was not shut down by the export process
      Assert.assertFalse("Export should not shut down a user-supplied " + ExecutorService.class.getName(), service
            .isShutdown());

      // Shut down the ES (clean up)
      service.shutdown();
   }

   /**
    * Test to ensure that the export process accepts an archive
    * with only directories, no assets.
    * 
    * @throws Exception
    */
   @Test
   public void testExportArchiveWithOnlyDirectories() throws IOException
   {
      // Create an archive with directories
      final Archive<?> archive = ShrinkWrap.create(JavaArchive.class, NAME_ARCHIVE).addDirectories("/test/game");

      // Fully export by reading all content (export is on-demand)
      final InputStream content = this.exportAsInputStream(archive);
      final OutputStream sink = new OutputStream()
      {

         @Override
         public void write(int b) throws IOException
         {
            //NOOP
         }
      };
      IOUtil.copyWithClose(content, sink);

      //TODO Why was this test originally written with no assertions? SHRINKWRAP-195
   }

   /**
    * Test to make sure an archive can be exported to file and all 
    * contents are correctly located.
    * @throws Exception
    */
   @Test
   public void testExportToFile() throws IOException
   {
      log.info("testExportToFile");

      // Get a temp directory for the test
      File tempDirectory = createTempDirectory("testExportToFile");

      // Get an archive instance
      Archive<?> archive = createArchiveWithAssets();

      // Export as File
      final File exported = new File(tempDirectory, archive.getName());
      this.exportAsFile(archive, exported, true);

      // Roundtrip assertion
      final InputStream instream = new FileInputStream(exported);
      this.ensureInExpectedForm(instream);
   }

   /**
    * Test to make sure an archive can be exported to a {@link OutputStream} and all 
    * contents are correctly located.
    * @throws Exception
    */
   @Test
   public void testExportToOutStream() throws IOException
   {
      log.info("testExportToOutStream");

      // Get a temp directory for the test
      final File tempDirectory = createTempDirectory("testExportToOutStream");

      // Get an archive instance
      final Archive<?> archive = createArchiveWithAssets();

      // Export as OutStream and flush to a file manually
      final File serializedArchive = new File(tempDirectory, archive.getName());
      final OutputStream out = new FileOutputStream(serializedArchive);
      this.exportToOutputStream(archive, out);

      // Validate
      this.ensureInExpectedForm(serializedArchive);
   }

   /**
    * Test to make sure an archive can be exported to file and all 
    * contents are correctly located.
    * @throws Exception
    */
   @Test
   public void testExportToExistingFileFails() throws IOException
   {
      log.info("testExportToExistingFileFails");

      // Get a temp directory for the test
      final File tempDirectory = createTempDirectory("testExportToExistingFileFails");

      // Get an archive instance
      final Archive<?> archive = createArchiveWithAssets();

      // Export as File
      final File alreadyExists = new File(tempDirectory, archive.getName());
      final OutputStream alreadyExistsOutputStream = new FileOutputStream(alreadyExists);
      alreadyExistsOutputStream.write(new byte[]
      {});
      alreadyExistsOutputStream.close();
      Assert.assertTrue("The test setup is incorrect; an empty file should exist before writing the archive",
            alreadyExists.exists());

      // Should fail, as we're not overwriting
      boolean gotExpectedException = false;
      try
      {
         this.exportAsFile(archive, alreadyExists, false);
      }
      catch (final FileExistsException fee)
      {
         gotExpectedException = true;
      }
      Assert.assertTrue("Should get " + FileExistsException.class.getSimpleName()
            + " when exporting to an existing file when overwrite is false", gotExpectedException);
   }

   /**
    * Test to make sue an archive can be exported and nested archives are also in exported.
    * @throws Exception
    */
   @Test
   public void testExportNested() throws Exception
   {
      log.info("testExportNested");

      // Get a temp directory for the test
      final File tempDirectory = createTempDirectory("testExportNested");

      // Get an archive instance
      final Archive<?> archive = createArchiveWithNestedArchives();

      // Export as InputStream
      final InputStream exportStream = this.exportAsInputStream(archive);

      // Write out and retrieve as exported file
      final File exported = new File(tempDirectory, NAME_ARCHIVE);
      final OutputStream exportedOut = new FileOutputStream(exported);
      IOUtil.copyWithClose(exportStream, exportedOut);

      // Validate entries were written out
      this.ensureAssetInExportedFile(exported, PATH_ONE, ASSET_ONE);
      this.ensureAssetInExportedFile(exported, PATH_TWO, ASSET_TWO);

      // Validate nested archive entries were written out
      final ArchivePath nestedArchivePath = ArchivePaths.create(NAME_NESTED_ARCHIVE);

      // Get inputstream for entry 
      final InputStream nestedArchiveStream = this.getContentsFromExportedFile(exported, nestedArchivePath);

      // Write out and retrieve nested contents
      final File nestedFile = new File(tempDirectory, NAME_NESTED_ARCHIVE);
      final OutputStream nestedOut = new FileOutputStream(nestedFile);
      IOUtil.copyWithClose(nestedArchiveStream, nestedOut);

      // Ensure contents are in the nested
      this.ensureAssetInExportedFile(nestedFile, PATH_ONE, ASSET_ONE);
      this.ensureAssetInExportedFile(nestedFile, PATH_TWO, ASSET_TWO);

      // Validate nested archive entries were written out
      final ArchivePath nestedArchiveTwoPath = new BasicPath(NESTED_PATH, NAME_NESTED_ARCHIVE_2);
      this.getContentsFromExportedFile(exported, nestedArchiveTwoPath);
      final InputStream nestedArchiveTwoStream = this.getContentsFromExportedFile(exported, nestedArchiveTwoPath);

      // Write out and retrieve secondnested contents
      final File nestedTwoFile = new File(tempDirectory, NAME_NESTED_ARCHIVE_2);
      final OutputStream nestedTwoOut = new FileOutputStream(nestedTwoFile);
      IOUtil.copyWithClose(nestedArchiveTwoStream, nestedTwoOut);

      // Ensure contents are in the second nested
      this.ensureAssetInExportedFile(nestedTwoFile, PATH_ONE, ASSET_ONE);
      this.ensureAssetInExportedFile(nestedTwoFile, PATH_TWO, ASSET_TWO);
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
      final InputStream in = this.exportAsInputStream(archive);

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
   // Helper Methods ---------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Write a InputStream out to file.
    * @param outFile
    * @param inputStream
    * @throws Exception
    */
   protected void writeOutFile(File outFile, InputStream inputStream) throws IOException
   {
      OutputStream fileOutputStream = new FileOutputStream(outFile);
      IOUtil.copyWithClose(inputStream, fileOutputStream);
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

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#ensureAssetInExportedFile(java.io.File, org.jboss.shrinkwrap.api.ArchivePath, org.jboss.shrinkwrap.api.asset.Asset)
    */
   protected final void ensureAssetInExportedFile(final File file, final ArchivePath path, final Asset asset)
         throws IOException
   {
      // Precondition checks
      assert file != null : "file must be specified";
      assert path != null : "path must be specified";
      assert asset != null : "asset must be specified";

      // Get as Exported File
      final InputStream actualStream = this.getContentsFromExportedFile(file, path);
      assert actualStream != null : "No contents found at path " + path + " in " + file.getAbsolutePath();
      byte[] actualContents = IOUtil.asByteArray(actualStream);
      byte[] expectedContents = IOUtil.asByteArray(asset.openStream());
      Assert.assertArrayEquals(expectedContents, actualContents);
   }
}

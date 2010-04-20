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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedOutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.io.StreamErrorHandler;
import org.jboss.shrinkwrap.impl.base.io.StreamTask;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.jboss.shrinkwrap.spi.Configurable;

/**
 * JDK-based implementation of a ZIP exporter.  Cannot handle archives
 * with no content (as there'd be no {@link ZipEntry}s to write to the
 * {@link ZipOutputStream}
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class JdkZipExporterDelegate extends AbstractExporterDelegate<InputStream>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(JdkZipExporterDelegate.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * ZipOutputStream used to write the zip entries
    */
   private ZipOutputStream zipOutputStream;

   /**
    * {@link InputStream} to be returned to the caller
    */
   private InputStream inputStream;

   /**
    * Used to see if we have exported at least one node
    */
   private Set<ArchivePath> pathsExported = new HashSet<ArchivePath>();

   /**
    * Synchronization point where the encoding process will wait until all streams have been set up
    */
   private final CountDownLatch latch = new CountDownLatch(1);

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new exporter delegate for exporting archives as Zip
    * 
    * @throws IllegalArgumentException If the archive has no {@link Asset}s; JDK ZIP
    * handling cannot support writing out to a {@link ZipOutputStream} with no
    * {@link ZipEntry}s.
    */
   public JdkZipExporterDelegate(final Archive<?> archive) throws IllegalArgumentException
   {
      super(archive);

      // Precondition check
      if (archive.getContent().isEmpty())
      {
         throw new IllegalArgumentException(
               "[SHRINKWRAP-93] Cannot use this JDK-based implementation to export as ZIP an archive with no content: "
                     + archive.toString());
      }
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#export()
    */
   protected void export()
   {

      // Define the task to operate in another Thread so we can pipe the output to an InStream
      final Callable<Void> exportTask = new Callable<Void>()
      {

         @Override
         public Void call() throws Exception
         {
            try
            {
               JdkZipExporterDelegate.super.export();
            }
            catch (final Exception e)
            {

               // Log this and rethrow; otherwise if we go into deadlock we won't ever 
               // be able to get the underlying cause from the Future 
               log.log(Level.WARNING, "Exception encountered during export of archive", e);

               // SHRINKWRAP-133 - if the Zip is empty, it won't close and a deadlock is triggered
               //TODO Find a better solution :)
               if (pathsExported.isEmpty())
               {
                  // Ensure the streams are set up before we do any work on them;
                  // it's possible that we encountered an exception before 
                  // everything has been initialized by the main Thread
                  // SHRINKWRAP-137
                  latch.await();

                  zipOutputStream.putNextEntry(new ZipEntry("dummy.txt"));
               }

               throw e;
            }
            finally
            {

               try
               {
                  zipOutputStream.close();
               }
               catch (final IOException ioe)
               {
                  // Ignore, but warn of danger
                  log.log(Level.WARNING,
                        "[SHRINKWRAP-120] Possible deadlock scenario: Got exception on closing the ZIP out stream: "
                              + ioe.getMessage(), ioe);
               }
            }

            return null;
         }
      };

      // Get an ExecutorService to which we may submit jobs.  This is either supplied by the user
      // in a custom domain, or if one has not been specified, we'll make one and shut it down right
      // here.  ExecutorServices supplied by the user are under the user's lifecycle, therefore it's
      // user responsibility to shut it down appropriately.
      boolean executorServiceIsOurs = false;
      ExecutorService service = this.getArchive().as(Configurable.class).getConfiguration().getExecutorService();
      if (service == null)
      {
         service = Executors.newSingleThreadExecutor();
         executorServiceIsOurs = true;
      }

      // Get a handle and return it to the caller
      final Future<Void> job = service.submit(exportTask);

      // If we've created the ES
      if (executorServiceIsOurs)
      {
         // Tell the service to shut down after the job has completed, and accept no new jobs
         service.shutdown();
      }

      /*
       * At this point the job will start, but hit the latch until we set up the streams
       * and tell it to proceed.
       */

      // Stream to return to the caller
      final FutureCompletionInputStream input = new FutureCompletionInputStream(job);
      inputStream = input;

      /**
       * OutputStream which will be associated with the returned InStream, and the 
       * chained IO point for the Zip OutStrea,
       */
      final OutputStream output;
      try
      {
         output = new PipedOutputStream(input);
      }
      catch (final IOException e)
      {
         throw new RuntimeException("Error in setting up output stream", e);
      }

      // Set up the stream to which we'll write entries, backed by the piped stream
      zipOutputStream = new ZipOutputStream(output);

      /*
       * The job is now waiting on us to signal that we've set up the streams; 
       * let it continue
       */
      latch.countDown();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#processNode(ArchivePath, Node)
    */
   @Override
   protected void processNode(final ArchivePath path, final Node node)
   {
      // Precondition checks
      if (path == null)
      {
         throw new IllegalArgumentException("Path must be specified");
      }
      if (node == null)
      {
         throw new IllegalArgumentException("asset must be specified");
      }

      // Mark if we're writing a directory
      final boolean isDirectory = node.getAsset() == null;

      InputStream stream = null;
      if (!isDirectory)
      {
         stream = node.getAsset().openStream();
      }

      final String pathName = PathUtil.optionallyRemovePrecedingSlash(path.get());

      // Make a task for this stream and close when done
      IOUtil.closeOnComplete(stream, new StreamTask<InputStream>()
      {

         @Override
         public void execute(InputStream stream) throws Exception
         {
            String resolvedPath = pathName;
            if (isDirectory)
            {
               resolvedPath = PathUtil.optionallyAppendSlash(resolvedPath);
            }

            // Make a ZipEntry
            final ZipEntry entry = new ZipEntry(resolvedPath);

            /*
             * Wait until all streams have been set up for encoding, or
             * do nothing if everything's set up already
             */
            latch.await();

            // Write the Asset under the same Path name in the Zip
            try
            {
               zipOutputStream.putNextEntry(entry);
            }
            catch (final ZipException ze)
            {
               log.log(Level.SEVERE, pathsExported.toString());
               throw new RuntimeException(ze);
            }

            // Mark that we've written this Path 
            pathsExported.add(path);

            // Read the contents of the asset and write to the JAR, 
            // if we're not just a directory
            if (!isDirectory)
            {
               IOUtil.copy(stream, zipOutputStream);
            }

            // Close up the instream and the entry
            zipOutputStream.closeEntry();
         }

      }, new StreamErrorHandler()
      {

         @Override
         public void handle(Throwable t)
         {
            throw new ArchiveExportException("Failed to write asset to Zip: " + path.get(), t);
         }

      });
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#getResult()
    */
   @Override
   protected InputStream getResult()
   {
      return inputStream;
   }

}

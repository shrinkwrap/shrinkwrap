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
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.ZipExportHandle;
import org.jboss.shrinkwrap.impl.base.asset.DirectoryAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.io.StreamErrorHandler;
import org.jboss.shrinkwrap.impl.base.io.StreamTask;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;

/**
 * JDK-based implementation of a ZIP exporter.  Cannot handle archives
 * with no content (as there'd be no {@link ZipEntry}s to write to the
 * {@link ZipOutputStream}
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class JdkZipExporterDelegate extends AbstractExporterDelegate<ZipExportHandle>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(JdkZipExporterDelegate.class.getName());

   /**
    * Services used to submit new jobs (encoding occurs in a separate Thread)
    */
   private static final ExecutorService service;
   static
   {
      service = Executors.newCachedThreadPool();
   }

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * ZipOutputStream used to write the zip entries
    */
   private ZipOutputStream zipOutputStream;

   /**
    * Handle to be returned to the caller
    */
   private ZipExportHandle handle;

   /**
    * A Set of Paths we've exported so far (so that we don't write
    * any entries twice)
    */
   private Set<ArchivePath> pathsExported = new HashSet<ArchivePath>();

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

      // Stream to return to the caller
      final IsReadReportingInputStream input = new IsReadReportingInputStream();

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

      // Get a handle and return it to the caller
      final Future<Void> job = service.submit(exportTask);
      final ZipExportHandle handle = new ZipExportHandleImpl(input, job);
      this.handle = handle;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#processAsset(ArchivePath, Asset)
    */
   @Override
   protected void processAsset(final ArchivePath path, final Asset asset)
   {
      // Precondition checks
      if (path == null)
      {
         throw new IllegalArgumentException("Path must be specified");
      }
      if (asset == null)
      {
         throw new IllegalArgumentException("asset must be specified");
      }

      if (isParentOfAnyPathsExported(path))
      {
         return;
      }

      /*
       * SHRINKWRAP-94
       * Add entries for all parents of this Path
       * by recursing first and adding parents that
       * haven't already been written.
       */
      final ArchivePath parent = PathUtil.getParent(path);
      if (parent != null && !this.pathsExported.contains(parent))
      {
         // If this is not the root
         // SHRINKWRAP-96
         final ArchivePath grandParent = PathUtil.getParent(parent);
         final boolean isRoot = grandParent == null;
         if (!isRoot)
         {
            // Process the parent as directory
            this.processAsset(parent, DirectoryAsset.INSTANCE);
         }
      }

      // Get Asset InputStream if the asset is specified (else it's a directory so use null)
      final InputStream assetStream = asset.openStream();

      // Mark if we're writing a directory
      final boolean isDirectory = assetStream == null;

      // If we haven't already written this path
      final String pathName = PathUtil.optionallyRemovePrecedingSlash(path.get());
      if (!this.pathsExported.contains(path))
      {
         // Make a task for this stream and close when done
         IOUtil.closeOnComplete(assetStream, new StreamTask<InputStream>()
         {

            @Override
            public void execute(InputStream stream) throws Exception
            {
               // If we're writing a directory, ensure we trail a slash for the ZipEntry
               String resolvedPath = pathName;
               if (isDirectory)
               {
                  resolvedPath = PathUtil.optionallyAppendSlash(resolvedPath);
               }

               // Make a ZipEntry
               final ZipEntry entry = new ZipEntry(resolvedPath);

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
               throw new ArchiveExportException("Failed to write asset to Zip: " + pathName, t);
            }

         });
      }
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#getResult()
    */
   @Override
   protected ZipExportHandle getResult()
   {
      return handle;
   }

   /**
    * Returns whether or not this Path is a parent of any Paths exported 
    * @param path
    * @return
    */
   //TODO The performance here will degrade geometrically with size of the archive
   private boolean isParentOfAnyPathsExported(final ArchivePath path)
   {
      // For all Paths already exported
      for (final ArchivePath exportedPath : this.pathsExported)
      {
         if (this.isParentOfSpecifiedHierarchy(path, exportedPath))
         {
            return true;
         }
      }

      return false;
   }

   /**
    * 
    * @param path
    * @param compare
    * @return
    */
   private boolean isParentOfSpecifiedHierarchy(final ArchivePath path, final ArchivePath compare)
   {
      // If we've reached the root, we're not a parent of any paths already exported
      final ArchivePath parent = PathUtil.getParent(compare);
      if (parent == null)
      {
         return false;
      }
      // If equal to me, yes
      if (path.equals(compare))
      {
         return true;
      }

      // Check my parent
      return this.isParentOfSpecifiedHierarchy(path, parent);
   }
}

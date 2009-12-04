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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.io.StreamErrorHandler;
import org.jboss.shrinkwrap.impl.base.io.StreamTask;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;

public class ZipExportDelegate extends AbstractExporterDelegate<InputStream>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipExportDelegate.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * OutputStream to hold the output contents
    */
   private final ByteArrayOutputStream output = new ByteArrayOutputStream();

   /**
    * ZipOutputStream used to write the zip entries
    */
   private ZipOutputStream zipOutputStream;

   /**
    * A Set of Paths we've exported so far (so that we don't write
    * any entries twice)
    */
   private Set<Path> pathsExported = new HashSet<Path>();

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new exporter delegate for exporting archives as Zip
    */
   public ZipExportDelegate(Archive<?> archive)
   {
      super(archive);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#export()
    */
   @Override
   protected void export()
   {
      zipOutputStream = new ZipOutputStream(output);

      // Enclose every IO Operation so we can close up cleanly
      IOUtil.closeOnComplete(zipOutputStream, new StreamTask<ZipOutputStream>()
      {

         @Override
         public void execute(ZipOutputStream stream) throws Exception
         {
            ZipExportDelegate.super.export();
         }

      }, new StreamErrorHandler()
      {

         @Override
         public void handle(Throwable t)
         {
            throw new ArchiveExportException("Failed to export Zip: " + getArchive().getName(), t);
         }

      });
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#processAsset(Path, Asset)
    */
   @Override
   protected void processAsset(final Path path, final Asset asset)
   {
      // Precondition checks
      if (path == null)
      {
         throw new IllegalArgumentException("Path must be specified");
      }

      /*
       * SHRINKWRAP-94
       * Add entries for all parents of this Path
       * by recursing first and adding parents that
       * haven't already been written.
       */
      final Path parent = getParent(path);
      if (parent != null && !this.pathsExported.contains(parent))
      {
         // Process the parent without any asset (it's a directory)
         this.processAsset(parent, null);
      }
      // Mark if we're writing a directory
      final boolean isDirectory = asset == null;

      // Get Asset InputStream if the asset is specified (else it's a directory so use null)
      final InputStream assetStream = !isDirectory ? asset.openStream() : null;
      final String pathName = PathUtil.optionallyRemovePrecedingSlash(path.get());

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
            zipOutputStream.putNextEntry(entry);

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

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#getResult()
    */
   @Override
   protected InputStream getResult()
   {
      // Flush the output to a byte array
      final byte[] zipContent = output.toByteArray();
      if (log.isLoggable(Level.FINE))
      {
         log.fine("Created Zip of size: " + zipContent.length + " bytes");
      }

      // Make an instream
      final InputStream inputStream = new ByteArrayInputStream(zipContent);

      // Return
      return inputStream;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the parent of this Path, if exists, else null.
    * For instance if the Path is "/my/path", the parent 
    * will be "/my".  Each call will result in a new object reference,
    * though subsequent calls upon the same Path will be equal by value.
    * @return
    * 
    * @param path The path whose parent context we should return
    */
   static Path getParent(final Path path)
   {
      // Precondition checks
      assert path != null : "Path must be specified";

      // Get the last index of "/"
      final String resolvedContext = PathUtil.optionallyRemoveFollowingSlash(path.get());
      final int lastIndex = resolvedContext.lastIndexOf(PathUtil.SLASH);
      // If it either doesn't occur or is the root
      if (lastIndex == -1 || (lastIndex == 0 && resolvedContext.length() == 1))
      {
         // No parent present, return null
         return null;
      }
      // Get the parent context
      final String sub = resolvedContext.substring(0, lastIndex);
      // Return
      return new BasicPath(sub);
   }

}

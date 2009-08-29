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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.export.ArchiveExportException;
import org.jboss.declarchive.impl.base.io.IOUtil;

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
    * @see org.jboss.declarchive.impl.base.export.AbstractExporterDelegate#export()
    */
   @Override
   protected InputStream export()
   {

      // Enclose every IO Operation so we can close up cleanly
      try
      {
         // Initialize the output streams
         zipOutputStream = new ZipOutputStream(output);

         return super.export();
      }
      catch (Exception ex)
      {
         // Problem occurred make sure the output is closed 
         closeOutputStream();
         throw new ArchiveExportException("Failed to export archive " + getArchive().getName(), ex);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.impl.base.export.AbstractExporterDelegate#processAsset(Path, Asset)
    */
   @Override
   protected void processAsset(Path path, Asset asset)
   {
      final String pathName = path.get();
      final ZipEntry entry = new ZipEntry(pathName);

      final InputStream in = asset.getStream();
      // Write the Asset under the same Path name in the Zip
      try
      {
         // Make a Zip Entry
         zipOutputStream.putNextEntry(entry);

         // Read the contents of the asset and write to the JAR
         IOUtil.copy(in, zipOutputStream);

         // Close up the instream and the entry
         zipOutputStream.closeEntry();
      }
      // Some error in writing this entry/asset
      catch (final IOException ioe)
      {
         // Throw
         throw new ArchiveExportException("Could not start new entry for " + pathName, ioe);
      }
      finally
      {
         // Try to close the instream.  Out stream is closed in finally block below.
         try
         {
            in.close();
         }
         catch (IOException ignored)
         {
         }
      }
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.impl.base.export.AbstractExporterDelegate#getResult()
    */
   @Override
   protected InputStream getResult()
   {
      // Make sure the output is closed
      closeOutputStream();

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
    * Close the ZipOutputStream
    */
   private void closeOutputStream()
   {
      try
      {
         if (zipOutputStream != null)
         {
            zipOutputStream.close();
            zipOutputStream = null;
         }
      }
      catch (final IOException ignored)
      {
      }
   }

}

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
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.export.ArchiveExportException;
import org.jboss.declarchive.api.export.ZipExporter;
import org.jboss.declarchive.impl.base.Validate;
import org.jboss.declarchive.impl.base.io.IOUtil;

/**
 * ZipExporterImpl
 * 
 * Implementation of ZipExporter used to export an Archive as a Zip format. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ZipExporterImpl extends ZipExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipExporterImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Required Implementations - ZipExporter ---------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /*
    * (non-Javadoc)
    * @see org.jboss.declarchive.api.export.ZipExporter#doExportZip(org.jboss.declarchive.api.Archive)
    */
   @Override
   protected InputStream doExportZip(Archive<?> archive)
   {
      Validate.notNull(archive, "No archive provided");

      // Obtain all contents
      final Map<Path, Asset> content = archive.getContent();

      // Create OutputStreams
      final ByteArrayOutputStream output = new ByteArrayOutputStream();
      ZipOutputStream zipOutputStream = null;

      // Enclose every IO Operation so we can close up cleanly
      try
      {
         // Make a ZipOutputStream
         zipOutputStream = new ZipOutputStream(output);

         // For every Path in the Archive
         for (final Entry<Path, Asset> contentEntry : content.entrySet())
         {
            // Get Asset information
            final Path path = contentEntry.getKey();
            final Asset asset = contentEntry.getValue();
            // Write asset content to Zip
            writeAsset(zipOutputStream, path, asset);
         }

      }
      // We're done, close
      finally
      {
         try
         {
            if (zipOutputStream != null)
            {
               zipOutputStream.close();
            }
         }
         catch (final IOException ignored)
         {
         }
      }

      return getContentStream(output);
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Write the content of a single asset to the Zip.
    * 
    * @param zipOutputStream
    * @param path
    * @param asset
    */
   private void writeAsset(ZipOutputStream zipOutputStream, Path path, Asset asset)
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
    * Get an InputStream representing the bytes from the Zip.
    * 
    * @param outputStream
    * @return
    */
   private InputStream getContentStream(ByteArrayOutputStream outputStream)
   {
      // Flush the output to a byte array
      final byte[] zipContent = outputStream.toByteArray();
      if (log.isLoggable(Level.FINE))
      {
         log.fine("Created Zip of size: " + zipContent.length + " bytes");
      }

      // Make an instream
      final InputStream inputStream = new ByteArrayInputStream(zipContent);

      // Return
      return inputStream;
   }
}

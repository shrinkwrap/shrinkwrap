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
      final String pathName = ZipExporterUtil.toZipEntryPath(path);

      final ZipEntry entry = new ZipEntry(pathName);

      // Get Asset InputStream
      final InputStream assetStream = asset.openStream();

      IOUtil.closeOnComplete(assetStream, new StreamTask<InputStream>()
      {

         @Override
         public void execute(InputStream stream) throws Exception
         {
            // Write the Asset under the same Path name in the Zip
            // Make a Zip Entry
            zipOutputStream.putNextEntry(entry);

            // Read the contents of the asset and write to the JAR
            IOUtil.copy(stream, zipOutputStream);

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

}

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
package org.jboss.shrinkwrap.impl.base.importer.zip;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.ZipFileEntryAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * Used to import existing Zip files/streams into the given {@link Archive}  
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ZipImporterImpl extends AssignableBase<Archive<?>> implements ZipImporter
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(ZipImporterImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   public ZipImporterImpl(final Archive<?> archive)
   {
      super(archive);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.ZipImporter#importZip(java.util.zip.ZipInputStream)
    */
   @Override
   @Deprecated
   public ZipImporter importZip(final ZipInputStream stream)
   {
      // Delegate
      return this.importFrom(stream);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.ZipImporter#importZip(java.util.zip.ZipFile)
    */
   @Deprecated
   @Override
   public ZipImporter importZip(ZipFile file)
   {
      // Delegate
      return this.importFrom(file);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.InputStream)
    */
   @Override
   public ZipImporter importFrom(final InputStream stream) throws ArchiveImportException
   {
      Validate.notNull(stream, "Stream must be specified");

      try
      {
         // Wrap in ZipInputStream if we haven't been given one
         final ZipInputStream zipStream = new ZipInputStream(stream);

         ZipEntry entry;
         while ((entry = zipStream.getNextEntry()) != null)
         {
            // Get the name
            final String entryName = entry.getName();

            // Get the archive
            final Archive<?> archive = this.getArchive();
            
            // Handle directories separately
            if (entry.isDirectory())
            {
               archive.addAsDirectory(entryName);
               continue;
            }

            final ByteArrayOutputStream output = new ByteArrayOutputStream(8192);
            IOUtil.copy(zipStream, output);
            archive.add(new ByteArrayAsset(output.toByteArray()), entryName);
            zipStream.closeEntry();
         }
      }
      catch (IOException e)
      {
         throw new ArchiveImportException("Could not import stream", e);
      }
      return this;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.File)
    */
   public ZipImporter importFrom(final File file) throws ArchiveImportException
   {
      Validate.notNull(file, "File must be specified");
      if (file.isDirectory())
      {
         throw new IllegalArgumentException("File to import as ZIP must not be a directory: " + file.getAbsolutePath());
      }

      final ZipFile zipFile;
      try
      {
         zipFile = new ZipFile(file);
      }
      catch (final IOException ioe)
      {
         throw new ArchiveImportException("Could not obtain ZIP File from File", ioe);
      }

      // Delegate
      return this.importFrom(zipFile);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.File)
    */
   @Override
   public ZipImporter importFrom(final ZipFile file) throws ArchiveImportException
   {
      Validate.notNull(file, "File must be specified");

      try
      {
         Enumeration<? extends ZipEntry> entries = file.entries();
         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();

            // Get the entry (path) name
            final String entryName = entry.getName();
            
            // Get the archive
            final Archive<?> archive = this.getArchive();

            // Handle directories separately
            if (entry.isDirectory())
            {
               archive.addAsDirectory(entryName);
               continue;
            }

            archive.add(new ZipFileEntryAsset(file, entry), new BasicPath(entryName));
         }
      }
      catch (Exception e)
      {
         throw new ArchiveImportException("Could not import file", e);
      }
      return this;
   }
}

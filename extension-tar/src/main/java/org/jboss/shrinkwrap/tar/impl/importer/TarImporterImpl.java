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
package org.jboss.shrinkwrap.tar.impl.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.tar.api.importer.TarImporter;
import org.jboss.tarbarian.api.TarEntry;
import org.jboss.tarbarian.api.TarInputStream;

/**
 * Used to import existing TAR files/streams into the given {@link Archive}  
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class TarImporterImpl extends AssignableBase implements TarImporter
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(TarImporterImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Archive to import into. 
    */
   private Archive<?> archive;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   public TarImporterImpl(Archive<?> archive)
   {
      Validate.notNull(archive, "Archive must be specified");
      this.archive = archive;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.AssignableBase#getArchive()
    */
   @Override
   protected Archive<?> getArchive()
   {
      return archive;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.InputStream)
    */
   @Override
   public TarImporter importFrom(final TarInputStream stream) throws ArchiveImportException
   {
      Validate.notNull(stream, "Stream must be specified");
      try
      {
         TarEntry entry;
         while ((entry = stream.getNextEntry()) != null)
         {
            // Get the name
            String entryName = entry.getName();

            // Handle directories separately
            if (entry.isDirectory())
            {
               archive.addDirectory(entryName);
               continue;
            }

            ByteArrayOutputStream output = new ByteArrayOutputStream(8192);
            byte[] content = new byte[4096];
            int readBytes;
            while ((readBytes = stream.read(content, 0, content.length)) != -1)
            {
               output.write(content, 0, readBytes);
            }
            archive.add(new ByteArrayAsset(output.toByteArray()), entryName);
         }
      }
      catch(final RuntimeException re)
      {
         throw new ArchiveImportException("Could not import stream", re);
      }
      catch (IOException e)
      {
         throw new ArchiveImportException("Could not import stream", e);
      }
      return this;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.lang.Object)
    */
   @Override
   public TarImporter importFrom(final File file) throws ArchiveImportException
   {
      Validate.notNull(file, "File must be specified");
      if (!file.exists())
      {
         throw new IllegalArgumentException("Specified file for import does not exist: " + file);
      }
      if (file.isDirectory())
      {
         throw new IllegalArgumentException("Specified file for import is a directory: " + file);
      }

      final TarInputStream archive;
      try
      {
         archive = new TarInputStream(new FileInputStream(file));
      }
      catch (IOException e)
      {
         throw new ArchiveImportException("Could not read archive file " + file, e);
      }

      return this.importFrom(archive);

   }
}

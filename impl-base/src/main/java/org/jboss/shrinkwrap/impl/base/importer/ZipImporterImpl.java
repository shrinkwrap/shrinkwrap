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
package org.jboss.shrinkwrap.impl.base.importer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.impl.base.asset.ZipFileEntryAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * Used to import existing Zip files/streams into the given {@link Archive}  
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ZipImporterImpl extends AssignableBase implements ZipImporter  
{
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

   public ZipImporterImpl(Archive<?> archive) 
   {
      Validate.notNull(archive, "Archive must be specified");
      this.archive = archive;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.impl.base.SpecializedBase#getArchive()
    */
   @Override
   protected Archive<?> getArchive()
   {
      return archive;
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.importer.ZipImporter#importZip(java.util.zip.ZipInputStream)
    */
   // TODO: create a ZipEntryAsset that can stream directly from the stream somehow?
   @Override
   public ZipImporter importZip(ZipInputStream stream)
   {
      Validate.notNull(stream, "Stream must be specified");
      try 
      {
         ZipEntry entry;
         while( (entry = stream.getNextEntry()) != null) 
         {
            /*
             *  TODO: we skip directories since the whole path is auto created and we have to directory concept. 
             *  we lose the empty directories, ok?
             */
            if(entry.isDirectory()) 
            {
               continue; 
            }
            String entryName = entry.getName();

            ByteArrayOutputStream output = new ByteArrayOutputStream(8192);
            byte[] content = new byte[4096];
            int readBytes;
            while( (readBytes = stream.read(content, 0, content.length)) != -1)
            {
               output.write(content, 0, readBytes);
            }
            archive.add(new ByteArrayAsset(output.toByteArray()), entryName);
            stream.closeEntry();
         }
      }
      catch (IOException e) 
      {
         throw new ArchiveImportException("Could not import stream", e);
      }
      finally 
      {
         try 
         {
            stream.close();
         } 
         catch (Exception ingore) 
         {
         }
      }
      return this;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.importer.ZipImporter#importZip(java.util.zip.ZipFile)
    */
   @Override
   public ZipImporter importZip(ZipFile file)
   {
      Validate.notNull(file, "File must be specified");
      
      Enumeration<? extends ZipEntry> entries = file.entries();
      while(entries.hasMoreElements())
      {
         ZipEntry entry = entries.nextElement();
         /*
          *  TODO: we skip directories since the whole path is auto created and we have to directory concept. 
          *  we lose the empty directories, ok?
          */
         if(entry.isDirectory()) {
            continue;
         }
         String entryName = entry.getName();
         
         archive.add(new ZipFileEntryAsset(file, entry), new BasicPath(entryName));
      }
      return this;
   }
}

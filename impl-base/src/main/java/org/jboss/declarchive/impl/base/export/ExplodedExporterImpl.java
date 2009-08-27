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

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.export.ArchiveExportException;
import org.jboss.declarchive.api.export.ExplodedExporter;
import org.jboss.declarchive.impl.base.Validate;
import org.jboss.declarchive.impl.base.io.IOUtil;

/**
 * ExplodedExporterImpl
 * 
 * Implementation of ExplodedExporter used to export an Archive as an exploded directory structure. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public class ExplodedExporterImpl extends ExplodedExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ExplodedExporterImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Required Implementations - ExplodedExporter ----------------------------------------||
   //-------------------------------------------------------------------------------------||

   /*
    * (non-Javadoc)
    * @see org.jboss.declarchive.api.export.ExplodedExporter#doExportExploded(org.jboss.declarchive.api.Archive, java.io.File)
    */
   @Override
   protected File doExportExploded(Archive<?> archive, File baseDirectory)
   {
      Validate.notNull(archive, "No archive provided");
      Validate.notNull(baseDirectory, "No baseDirectory provided");

      // Directory must exist
      if (!baseDirectory.exists())
      {
         throw new IllegalArgumentException("Parent directory does not exist");
      }
      // Must be a directory
      if (!baseDirectory.isDirectory())
      {
         throw new IllegalArgumentException("Provided parent directory is not a valid directory");
      }

      // Obtain all contents
      final Map<Path, Asset> content = archive.getContent();

      // Create output directory
      final File outputDirectory = new File(baseDirectory, archive.getName());
      if (!outputDirectory.mkdir())
      {
         throw new ArchiveExportException("Unable to create archive output directory - " + outputDirectory);
      }

      // For every Path in the Archive
      for (final Entry<Path, Asset> contentEntry : content.entrySet())
      {
         // Get Asset information
         final Path path = contentEntry.getKey();
         final Asset asset = contentEntry.getValue();
         // Write asset content to file
         writeAsset(outputDirectory, path, asset);
      }

      if (log.isLoggable(Level.FINE))
      {
         log.fine("Created Exploded Atchive: " + outputDirectory.getAbsolutePath());
      }
      // Return the output dir
      return outputDirectory;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Write the asset file to the output directory
    * 
    * @param outputDirectory
    * @param path
    * @param asset
    */
   private void writeAsset(File outputDirectory, Path path, Asset asset)
   {
      // Get path to file
      final String assetFilePath = path.get();

      // Create a file for the asset
      final File assetFile = new File(outputDirectory, assetFilePath);

      // Get the assets parent parent directory and make sure it exists
      final File assetParent = assetFile.getParentFile();
      if (!assetParent.exists())
      {
         if (!assetParent.mkdirs())
         {
            throw new ArchiveExportException("Failed to write asset.  Unable to create parent directory.");
         }
      }

      try
      {
         // Get the asset streams
         final InputStream assetInputStream = asset.getStream();
         final FileOutputStream assetFileOutputStream = new FileOutputStream(assetFile);

         // Write contents
         IOUtil.copyWithClose(assetInputStream, assetFileOutputStream);
      }
      catch (Throwable t)
      {
         throw new ArchiveExportException("Failed to write asset " + path + " to " + assetFile);
      }
   }

}

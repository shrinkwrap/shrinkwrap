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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.export.ExplodedExporter;
import org.jboss.declarchive.impl.base.Validate;

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

   
   /**
    * {@inheritDoc} 
    * @see ExplodedExporter#doExportExploded(Archive, File)
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

      // Get the export delegate
      ExplodedExporterDelegate exporterDelegate = new ExplodedExporterDelegate(archive, baseDirectory);

      // Run the export
      File explodedDirectory = exporterDelegate.export();

      if (log.isLoggable(Level.FINE))
      {
         log.fine("Created Exploded Atchive: " + explodedDirectory.getAbsolutePath());
      }
      // Return the exploded dir
      return explodedDirectory;
   }

}

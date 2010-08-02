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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * ExplodedImporterImpl
 * 
 * Importer used to import Exploded directory structures into a {@link Archive}
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExplodedImporterImpl extends AssignableBase<Archive<?>> implements ExplodedImporter
{
   // -------------------------------------------------------------------------------------||
   // Class Members -----------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ExplodedImporterImpl.class.getName());

   // -------------------------------------------------------------------------------------||
   // Constructor -------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   public ExplodedImporterImpl(final Archive<?> archive)
   {
      super(archive);
   }

   // -------------------------------------------------------------------------------------||
   // Required Implementations ------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||
   void a()
   {

   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.ExplodedImporter#importDirectory(java.lang.String)
    */
   @Override
   public ExplodedImporter importDirectory(String fileName)
   {
      Validate.notNull(fileName, "FileName must be specified");
      return importDirectory(new File(fileName));
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.importer.ExplodedImporter#importDirectory(java.io.File)
    */
   @Override
   public ExplodedImporter importDirectory(File file)
   {
      Validate.notNull(file, "FileName must be specified");
      if (!file.isDirectory())
      {
         throw new IllegalArgumentException("Given file is not a directory " + file.getAbsolutePath());
      }

      doImport(file, file.listFiles());
      return this;
   }

   private void doImport(File root, File[] files)
   {
      for (File file : files)
      {
         if (log.isLoggable(Level.FINER))
         {
            log.finer("Importing: " + file.getAbsolutePath());
         }
         final Archive<?> archive = this.getArchive();
         final ArchivePath path = calculatePath(root, file);
         if (file.isDirectory())
         {
            archive.addDirectory(path);
            doImport(root, file.listFiles());
         }
         else
         {
            archive.add(new FileAsset(file), path);
         }
      }
   }

   /**
    * Calculate the relative child path.
    * 
    * @param root
    *           The Archive root folder
    * @param child
    *           The Child file
    * @return a Path fort he child relative to root
    */
   private ArchivePath calculatePath(File root, File child)
   {
      String rootPath = unifyPath(root.getPath());
      String childPath = unifyPath(child.getPath());
      String archiveChildPath = childPath.replaceFirst(rootPath, "");
      return new BasicPath(archiveChildPath);
   }

   /**
    * Windows vs Linux will return different path separators, unify the paths.
    * 
    * @return
    */
   private String unifyPath(String path)
   {
      return path.replaceAll("\\\\", "/");
   }
}

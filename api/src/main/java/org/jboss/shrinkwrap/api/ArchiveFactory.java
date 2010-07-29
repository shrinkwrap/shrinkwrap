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
package org.jboss.shrinkwrap.api;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Responsible for creating {@link Archive}s, which may be
 * presented to the caller in a designated {@link Assignable} view.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public final class ArchiveFactory
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Implementation class name backing {@link Archive}s to be created
    */
   private static final String ARCHIVE_IMPL = "org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Configuration for all archives created from this factory
    */
   private final Configuration configuration;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link ArchiveFactory} which will use the supplied 
    * {@link Configuration} for each new {@link Archive} it creates.
    * 
    * @param configuration the {@link Configuration} to use
    * @throws IllegalArgumentException if configuration is not supplied
    */
   ArchiveFactory(final Configuration configuration) throws IllegalArgumentException
   {
      // Precondition checks
      assert configuration != null : "configuration must be supplied";

      // Set
      this.configuration = configuration;
   }

   //-------------------------------------------------------------------------------------||
   // Functional Methods ----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new archive of the specified type.  The archive
    * will be be backed by the {@link Configuration}
    * specific to this {@link ArchiveFactory}.
    * Generates a random name for the archive and adds proper extension based
    * on the service descriptor properties file if extension property is present
    * (e.g. shrinkwrap/impl-base/src/main/resources/META-INF/services/org.jboss.shrinkwrap.api.spec.JavaArchive)
    *
    *
    * @param type The type of the archive e.g. {@link WebArchive}
    * @return An {@link Assignable} archive base
    * @throws IllegalArgumentException if type is not specified
    * @throws UnknownExtensionTypeException If no extension mapping is found for the specified type
    */
   public <T extends Assignable> T create(final Class<T> type) throws IllegalArgumentException,
         UnknownExtensionTypeException
   {
      // Precondition checks
      if (type == null)
      {
         throw new IllegalArgumentException("Type must be specified");
      }

      // Get the extensionType type
      final String extension = configuration.getExtensionLoader().getExtensionFromExtensionMapping(type);

      // Generate a random name
      String archiveName = UUID.randomUUID().toString();

      // Delegate
      return create(type, archiveName += extension);
   }

   /**
    * Creates a new archive of the specified type.  The archive
    * will be be backed by the {@link Configuration}
    * specific to this {@link ArchiveFactory}.
    *
    * @param type The type of the archive e.g. {@link org.jboss.shrinkwrap.api.spec.WebArchive}
    * @param archiveName the archiveName to use
    * @return An {@link Assignable} view
    * @throws IllegalArgumentException either argument is not supplied
    */
   public <T extends Assignable> T create(final Class<T> type, final String archiveName)
         throws IllegalArgumentException
   {
      // Precondition checks
      if (type == null)
      {
         throw new IllegalArgumentException("Type must be specified");
      }
      if (archiveName == null)
      {
         throw new IllegalArgumentException("ArchiveName must be specified");
      }

      final Archive<?> archive = SecurityActions.newInstance(ARCHIVE_IMPL, new Class<?>[]
      {String.class, Configuration.class}, new Object[]
      {archiveName, configuration}, Archive.class);
      return archive.as(type);
   }

   /**
    * Creates a new archive of the specified type as imported 
    * from the specified {@link File}.  The file is expected to be encoded as
    * ZIP (ie. JAR/WAR/EAR).   The name of the archive will be set to {@link File#getName()}. 
    * The archive will be be backed by the {@link Configuration}
    * specific to this {@link ArchiveFactory}.
    *
    * @param type The type of the archive e.g. {@link org.jboss.shrinkwrap.api.spec.WebArchive}
    * @param archiveFile the archiveFile to use
    * @return An {@link Assignable} view
    * @throws IllegalArgumentException If either argument is not supplied, if the specified
    * {@link File} does not exist, or is not a valid ZIP file
    * @throws org.jboss.shrinkwrap.api.importer.ArchiveImportException If an error occurred during the import process
    */
   @SuppressWarnings({"deprecation"})
   public <T extends Assignable> T createFromZipFile(final Class<T> type, final File archiveFile)
         throws IllegalArgumentException, ArchiveImportException
   {
      // Precondition checks
      if (type == null)
      {
         throw new IllegalArgumentException("Type must be specified");
      }
      if (archiveFile == null)
      {
         throw new IllegalArgumentException("file must be specified");
      }
      if (!archiveFile.exists())
      {
         throw new IllegalArgumentException("File for import exist: " + archiveFile.getAbsolutePath());
      }
      if (archiveFile.isDirectory())
      {
         throw new IllegalArgumentException("File for import must not be a directory: " + archiveFile.getAbsolutePath());
      }

      // Construct ZipFile
      final ZipFile zipFile;
      try
      {
         zipFile = new ZipFile(archiveFile);
      }
      catch (final ZipException ze)
      {
         throw new IllegalArgumentException("Does not appear to be a valid ZIP file: " + archiveFile.getAbsolutePath());
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("I/O Error in importing new archive from ZIP: " + archiveFile.getAbsolutePath(),
               ioe);
      }

      // Import
      return ShrinkWrap.create(type, archiveFile.getName()).as(ZipImporter.class).importZip(zipFile).as(type);

   }
}

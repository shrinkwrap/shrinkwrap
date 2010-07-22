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
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;

/**
 * Base support for I/O Stream-based exporters
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public abstract class AbstractStreamExporterImpl extends AssignableBase implements StreamExporter
{
   
   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Archive to import into. 
    */
   private final Archive<?> archive;
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   public AbstractStreamExporterImpl(final Archive<?> archive)
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
   
   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains an {@link OuputStream} to the provided {@link File}.
    * @param target
    * @param overwrite Whether we may overwrite an existing file
    * @return
    * @throws FileExistsException If the specified file exists and the overwrite flag is false
    * @throws IllegalArgumentException If the file target is not specified
    */
   protected final OutputStream getOutputStreamToFile(final File target, final boolean overwrite)
         throws FileExistsException, IllegalArgumentException
   {
      // Precondition checks
      if (target == null)
      {
         throw new IllegalArgumentException("Target file must be specified");
      }
      // If target exists and we're not allowed to overwrite it
      if (target.exists() && !overwrite)
      {
         throw new FileExistsException("Target exists and we haven't been flagged to overwrite it: "
               + target.getAbsolutePath());
      }

      // Get Stream
      final OutputStream out;
      try
      {
         out = new FileOutputStream(target);
      }
      catch (final FileNotFoundException e)
      {
         throw new ArchiveExportException("File could not be created: " + target);
      }

      // Return
      return out;
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.StreamExporter#export(java.io.OutputStream)
    */
   @Override
   public void export(final OutputStream target) throws ArchiveExportException, IllegalArgumentException
   {
      // Precondition checks
      if (target == null)
      {
         throw new IllegalArgumentException("Target must be specified");
      }

      // Get Stream
      final InputStream in = this.export();

      // Write out
      try
      {
         IOUtil.copyWithClose(in, target);
      }
      catch (final IOException e)
      {
         throw new ArchiveExportException("Error encountered in exporting archive to " + target, e);
      }
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.StreamExporter#export(java.io.File, boolean)
    */
   @Override
   public final void export(final File target, final boolean overwrite) throws ArchiveExportException, FileExistsException,
         IllegalArgumentException
   {
      // Get stream and perform precondition checks
      final OutputStream out = this.getOutputStreamToFile(target, overwrite);

      // Write out
      this.export(out);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.exporter.StreamExporter#export(java.io.File)
    */
   @Override
   public final void export(final File target) throws ArchiveExportException, FileExistsException, IllegalArgumentException
   {
      this.export(target, false);
   }

}

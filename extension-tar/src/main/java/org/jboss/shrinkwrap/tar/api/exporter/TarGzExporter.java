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
package org.jboss.shrinkwrap.tar.api.exporter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;

/**
 * Exporter used to represent an {@link Assignable} in TAR format encoded w/
 * GZIP compression
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @see http://www.gnu.org/software/tar/manual/html_node/Standard.html
 * @see http://www.gzip.org/
 */
public interface TarGzExporter extends Assignable
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Exports this reference as a TAR.GZ archive.
    * 
    * @return {@link InputStream} for exported TAR.GZ
    */
   InputStream exportTarGz();

   /**
    * Exports provided archive as a TAR.GZ archive, written to the 
    * specified {@link File} target.  If the target exists this call will
    * fail with {@link IllegalArgumentException}
    * 
    * @param archive
    * @return {@link InputStream} for exported Zip
    * @throws IllegalArgumentException If the target is not specified
    * @throws FileExistsException If the target already exists 
    * @throws ArchiveExportException if the export process fails
    */
   void exportTarGz(File target) throws ArchiveExportException, FileExistsException, IllegalArgumentException;

   /**
    * Exports provided archive as a TAR.GZ archive, written to the 
    * specified {@link OutputStream} target.  The specified
    * target will be closed upon completion.
    * 
    * @param target
    * @throws ArchiveExportException
    * @throws IllegalArgumentException If the target is not specified
    */
   void exportTarGz(OutputStream target) throws ArchiveExportException, IllegalArgumentException;

   /**
    * Exports provided archive as a TAR.GZ archive, written to the 
    * specified {@link File} target.  If the target both exists and the "overwrite"
    * flag is true, this call will allow the existing file to be overwritten, else
    * the invocation will fail with {@link IllegalArgumentException}
    * 
    * @param archive
    * @return {@link InputStream} for exported TAR.GZ
    * @throws IllegalArgumentException If the target is not specified 
    * @throws FileExistsException If the target both already exists and the overwrite flag is false
    * @throws ArchiveExportException if the export process fails
    */
   void exportTarGz(File target, boolean overwrite) throws ArchiveExportException, FileExistsException,
         IllegalArgumentException;
}

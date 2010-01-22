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
package org.jboss.shrinkwrap.api.exporter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.shrinkwrap.api.Assignable;

/**
 * Exporter used to represent an {@link Assignable} in ZIP format. 
 * 
 * @see http://www.pkware.com/documents/casestudies/APPNOTE.TXT
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface ZipExporter extends Assignable
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Exports this reference as a Zip archive.
    * 
    * @return {@link InputStream} for exported Zip
    */
   InputStream exportZip();

   /**
    * Exports provided archive as a ZIP archive, written to the 
    * specified {@link File} target.  If the target exists this call will
    * fail with {@link IllegalArgumentException}
    * 
    * @param archive
    * @return {@link InputStream} for exported Zip
    * @throws IllegalArgumentException If the target is not specified
    * @throws FileExistsException If the target already exists 
    * @throws ArchiveExportException if the export process fails
    */
   void exportZip(File target) throws ArchiveExportException, FileExistsException, IllegalArgumentException;

   /**
    * Exports provided archive as a ZIP archive, written to the 
    * specified {@link OutputStream} target.  The specified
    * target will be closed upon completion.
    * 
    * @param target
    * @throws ArchiveExportException
    * @throws IllegalArgumentException If the target is not specified
    */
   void exportZip(OutputStream target) throws ArchiveExportException, IllegalArgumentException;

   /**
    * Exports provided archive as a ZIP archive, written to the 
    * specified {@link File} target.  If the target both exists and the "overwrite"
    * flag is true, this call will allow the existing file to be overwritten, else
    * the invocation will fail with {@link IllegalArgumentException}
    * 
    * @param archive
    * @return {@link InputStream} for exported Zip
    * @throws IllegalArgumentException If the target is not specified 
    * @throws FileExistsException If the target both already exists and the overwrite flag is false
    * @throws ArchiveExportException if the export process fails
    */
   void exportZip(File target, boolean overwrite) throws ArchiveExportException, FileExistsException,
         IllegalArgumentException;
}

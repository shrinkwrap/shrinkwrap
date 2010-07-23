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
 * Generic exporter capable of representing an {@link Assignable}
 * as an {@link InputStream}, or writing its contents to
 * a provided {@link OutputStream} or {@link File}.
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface StreamExporter extends Assignable
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Exports this reference in an implementation-specific
    * format represented by the returned new {@link InputStream}
    * instance
    * 
    * @return A new {@link InputStream} to read the exported view
    */
   InputStream exportAsInputStream();

   /**
    * Exports provided archive in an implementation-specific format, 
    * written to the specified {@link OutputStream} target.  The specified
    * target will not be closed or flushed; this is the responsibility of the 
    * caller (who supplied the {@link OutputStream} in the first place).
    * 
    * @param target
    * @throws ArchiveExportException
    * @throws IllegalArgumentException If the target is not specified or is closed
    */
   void exportTo(OutputStream target) throws ArchiveExportException, IllegalArgumentException;

   /**
    * Exports provided archive as in an implementation-specific format, written to the 
    * specified {@link File} target.  If the target exists this call will
    * fail with {@link IllegalArgumentException}
    * 
    * @param archive
    * @throws IllegalArgumentException If the target is not specified
    * @throws FileExistsException If the target already exists 
    * @throws ArchiveExportException if the export process fails
    */
   void exportTo(File target) throws ArchiveExportException, FileExistsException, IllegalArgumentException;

   /**
    * Exports provided archive an implementation-specific format, written to the 
    * specified {@link File} target.  If the target both exists and the "overwrite"
    * flag is true, this call will allow the existing file to be overwritten, else
    * the invocation will fail with {@link IllegalArgumentException}
    * 
    * @param archive
    * @throws IllegalArgumentException If the target is not specified 
    * @throws FileExistsException If the target both already exists and the overwrite flag is false
    * @throws ArchiveExportException if the export process fails
    */
   void exportTo(File target, boolean overwrite) throws ArchiveExportException, FileExistsException,
         IllegalArgumentException;
}

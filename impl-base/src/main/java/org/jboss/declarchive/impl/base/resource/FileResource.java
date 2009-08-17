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
package org.jboss.declarchive.impl.base.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jboss.declarchive.spi.Resource;

/**
 * Loads any File.
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class FileResource implements Resource
{
   private File file;

   /**
    * Load the specified File. 
    * 
    * @param file The file to load
    * @throws IllegalArgumentException File can not be null
    * @throws IllegalArgumentException File must exist
    */
   public FileResource(File file)
   {
      // Precondition check
      if (file == null)
      {
         throw new IllegalArgumentException("File must be specified");
      }
      if (!file.exists())
      {
         throw new IllegalArgumentException("File must exist: " + file.getAbsolutePath());
      }
      this.file = file;
   }

   /**
    * Get the default name using File.getName();
    */
   @Override
   public String getDefaultName()
   {
      return file.getName();
   }

   /**
    * Opens a new FileInputStream for the given File.
    * 
    * Can throw a Runtime exception if the file has been deleted inbetween 
    * the FileResource was created and the stream is opened. 
    * 
    * @throws RuntimeException If the file is not found.
    */
   @Override
   public InputStream getStream()
   {
      try
      {
         return new FileInputStream(file);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Could not open file " + file, e);
      }
   }
}

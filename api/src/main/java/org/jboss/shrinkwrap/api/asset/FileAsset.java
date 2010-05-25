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
package org.jboss.shrinkwrap.api.asset;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;


/**
 * Implementation of an {@link Asset} backed by a {@link File}
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 */
public class FileAsset implements Asset
{
   private File file;

   /**
    * Load the specified File.
    *
    * @param file The file to load
    * @throws IllegalArgumentException File can not be null
    * @throws IllegalArgumentException File must exist
    */
   public FileAsset(File file)
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
    * Opens a new FileInputStream for the given File.
    * 
    * Can throw a Runtime exception if the file has been deleted inbetween
    * the FileResource was created and the stream is opened.
    *
    * @throws RuntimeException If the file is not found.
    */
   @Override
   public InputStream openStream()
   {
      try
      {
         return new BufferedInputStream(new FileInputStream(file), 8192);
      }
      catch (FileNotFoundException e)
      {
         throw new RuntimeException("Could not open file " + file, e);
      }
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return FileAsset.class.getSimpleName() + " [file=" + file.getAbsolutePath() + "]";
   }
}

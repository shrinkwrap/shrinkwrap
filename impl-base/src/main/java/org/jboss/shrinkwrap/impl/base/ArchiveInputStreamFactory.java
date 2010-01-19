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
package org.jboss.shrinkwrap.impl.base;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.impl.base.exporter.ZipArchiveInputStreamFactory;

/**
 * ArchiveInputStreamFactory
 * 
 * Factory used create an input stream from an Archive.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public abstract class ArchiveInputStreamFactory
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Current instance.  Should be replaced with some other structure (MAP) when we support multiple concrete implementations.
    */
   private static ArchiveInputStreamFactory instance;

   //-------------------------------------------------------------------------------------||
   // Class Methods ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Create an InputStream from an Archive instance.
    * 
    * @throws IllegalArgumentException if the Archive is null 
    */
   public static InputStream getInputStream(final Archive<?> archive)
   {
      Validate.notNull(archive, "archive was not provided");

      // Get the concrete factory to handle this archive
      ArchiveInputStreamFactory factory = getInstance(archive);

      // Delegate to concrete impl
      return factory.doGetInputStream(archive);
   }

   /**
    * Gets an instance of the {@link ArchiveInputStreamFactory}  
    * 
    * @param archive archive to determine which impl to use
    * @return {@link ArchiveInputStreamFactory} instance
    */
   private synchronized static ArchiveInputStreamFactory getInstance(Archive<?> archive)
   {
      if (instance == null)
      {
         // TODO - Use the archive to determine the correct implementation to use.
         instance = new ZipArchiveInputStreamFactory();
      }
      return instance;
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Template method required to create an InputStream from an Archive instance.
    *  
    * @param archive to create the InputStream from
    * @return InputStream for the Archive
    */
   protected abstract InputStream doGetInputStream(Archive<?> archive);

}

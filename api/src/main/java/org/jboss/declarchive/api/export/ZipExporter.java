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
package org.jboss.declarchive.api.export;

import java.io.InputStream;

import org.jboss.declarchive.api.Archive;

/**
 * ZipExporter
 * 
 * Exporter used to export an Archive as a Zip format. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public abstract class ZipExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Implementation type as a FQN to avoid direct compile-time dependency
    */
   private static final String IMPL_TYPE = "org.jboss.declarchive.impl.base.export.ZipExporterImpl";

   /**
    * Instance of ZipExporter implementation
    */
   private static ZipExporter instance;

   //-------------------------------------------------------------------------------------||
   // Class Methods ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Exports provided archive as a Zip archive.
    * 
    * @param archive
    * @return InputStram for exported Zip
    * @throws IllegalArgumentException if the archive is not valid
    * @throws ArchiveExportException if the export process fails
    */
   public static InputStream exportZip(Archive<?> archive)
   {
      return getInstance().doExportZip(archive);
   }

   /**
    * Get an instance of the ZipExporter implementation 
    * @return
    */
   private synchronized static ZipExporter getInstance()
   {
      if (instance == null)
      {
         instance = createInstance();
      }
      return instance;
   }

   /**
    * Create an instance of the ZipExporter implementation
    * 
    * @return
    */
   private static ZipExporter createInstance()
   {
      return FactoryUtil.createInstance(IMPL_TYPE, ZipExporter.class);
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Template export method for concrete implementations  
    * 
    * @param archive
    * @return InputStream for exported Zip
    */
   protected abstract InputStream doExportZip(Archive<?> archive);

}

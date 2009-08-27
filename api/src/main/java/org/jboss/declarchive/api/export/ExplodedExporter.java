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

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.jboss.declarchive.api.Archive;

/**
 * ExplodedExporter
 * 
 * Exporter used to export an Archive as an exploded directory structure. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public abstract class ExplodedExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Implementation type as a FQN to avoid direct compile-time dependency
    */
   private static final String IMPL_TYPE = "org.jboss.declarchive.impl.base.export.ExplodedExporterImpl";

   /**
    * Instance of ExplodedExporter implementation
    */
   private static ExplodedExporter instance;

   //-------------------------------------------------------------------------------------||
   // Class Methods ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Exports provided archive as an exploded directory structure.
    * 
    * @param archive
    * @param parentDirectory
    * @return File for exploded archive contents
    */
   public static File exportExploded(Archive<?> archive, File parentDirectory)
   {
      return getInstance().doExportExploded(archive, parentDirectory);
   }

   /**
    * Get an instance of the ExplodedExporter implementation 
    * @return
    */
   private synchronized static ExplodedExporter getInstance()
   {
      if (instance == null)
      {
         instance = createInstance();
      }
      return instance;
   }

   private static ExplodedExporter createInstance()
   {
      return AccessController.doPrivileged(new PrivilegedAction<ExplodedExporter>()
      {
         @SuppressWarnings("unchecked")
         public ExplodedExporter run()
         {
            try
            {
               ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
               Class<? extends ExplodedExporter> exporterImpl = (Class<? extends ExplodedExporter>) classLoader.loadClass(IMPL_TYPE);
               return exporterImpl.newInstance();
            }
            catch (Exception e)
            {
               throw new IllegalArgumentException("Unable to create ExplodedExporter implemenation.", e);
            }
         }
      });
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Template export method for concrete implementations  
    * @param archive
    * @param parentDirectory
    * @return File for exploded archive contents
    */
   protected abstract File doExportExploded(Archive<?> archive, File parentDirectory);

}

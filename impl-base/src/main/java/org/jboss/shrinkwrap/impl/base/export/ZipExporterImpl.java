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
package org.jboss.shrinkwrap.impl.base.export;

import java.io.InputStream;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.export.ZipExporter;
import org.jboss.shrinkwrap.impl.base.SpecializedBase;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * ZipExporterImpl
 * 
 * Implementation of ZipExporter used to export an Archive as a Zip format. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ZipExporterImpl extends SpecializedBase implements ZipExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipExporterImpl.class.getName());

   /**
    * Archive to import into. 
    */
   private Archive<?> archive; 
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   public ZipExporterImpl(Archive<?> archive) 
   {
      Validate.notNull(archive, "Archive must be specified");
      this.archive = archive;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.impl.base.SpecializedBase#getArchive()
    */
   @Override
   protected Archive<?> getArchive()
   {
      return archive;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - ZipExporter ---------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.export.ZipExporter#doExportZip(org.jboss.shrinkwrap.api.Archive)
    */
   @Override
   public InputStream exportZip()
   {
      // Create export delegate
      ZipExportDelegate exportDelegate = new ZipExportDelegate(archive);

      // Execute export
      exportDelegate.export();
      // Get results
      InputStream inputStream = exportDelegate.getResult(); 

      // Return input stream
      return inputStream;
   }

}

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
package org.jboss.declarchive.impl.base.export;

import java.io.InputStream;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.export.ZipExporter;
import org.jboss.declarchive.impl.base.Validate;

/**
 * ZipExporterImpl
 * 
 * Implementation of ZipExporter used to export an Archive as a Zip format. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ZipExporterImpl extends ZipExporter
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipExporterImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Required Implementations - ZipExporter ---------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.export.ZipExporter#doExportZip(org.jboss.declarchive.api.Archive)
    */
   @Override
   protected InputStream doExportZip(Archive<?> archive)
   {
      Validate.notNull(archive, "No archive provided");

      // Create export delegate
      ZipExportDelegate exportDelegate = new ZipExportDelegate(archive);

      // Execute export
      InputStream inputStream = exportDelegate.export();

      // Return input stream
      return inputStream;
   }

}

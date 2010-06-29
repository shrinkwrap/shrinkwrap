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
package org.jboss.shrinkwrap.tar.impl.exporter;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.impl.base.ArchiveInputStreamFactory;
import org.jboss.shrinkwrap.tar.api.exporter.TarGzExporter;

/**
 * Factory used create an InputStream from an Archive by exporting the archive as TAR.GZ.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class TarGzArchiveInputStreamFactory extends ArchiveInputStreamFactory
{
   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.ArchiveInputStreamFactory#doGetInputStream(Archive)
    */
   @Override
   protected InputStream doGetInputStream(final Archive<?> archive)
   {
      // Get InputStream from the ZipExporter
      final InputStream inputStream = archive.as(TarGzExporter.class).export();
      // Return input stream
      return inputStream;
   }

}

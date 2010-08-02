/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.tar.impl.importer;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.tar.api.importer.TarGzImporter;
import org.jboss.shrinkwrap.tar.impl.io.TarGzInputStream;

/**
 * Used to import existing TAR.GZ files/streams into the given {@link Archive}  
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class TarGzImporterImpl extends TarImporterBase<TarGzInputStream, TarGzImporter> implements TarGzImporter
{

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   public TarGzImporterImpl(final Archive<?> archive)
   {
      super(archive);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.tar.impl.importer.TarImporterBase#getInputStreamForRawStream(java.io.InputStream)
    */
   @Override
   TarGzInputStream getInputStreamForRawStream(final InputStream in) throws IOException
   {
      assert in != null : "Specified inputstream was null";
      return new TarGzInputStream(in);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.tar.impl.importer.TarImporterBase#getActualClass()
    */
   @Override
   Class<TarGzImporter> getActualClass()
   {
      return TarGzImporter.class;
   }

}

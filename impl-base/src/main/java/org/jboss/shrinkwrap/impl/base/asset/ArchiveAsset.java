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
package org.jboss.shrinkwrap.impl.base.asset;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * An {@link Asset} representing an {@link Archive}; a
 * specified {@link StreamExporter} type will be used to 
 * fulfill the {@link Asset#openStream()} contract.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ArchiveAsset implements Asset
{

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The archive this asset represents
    */
   private final Archive<?> archive;

   /**
    * Exporter used to represent this archive as a {@link InputStream}
    */
   private final Class<? extends StreamExporter> exporter;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new instance wrapping the specified {@link Archive}, which
    * will use the specified {@link StreamExporter} to represent the archive as
    * an {@link InputStream} in {@link Asset#openStream()}.
    * 
    * @param archive
    * @param exporter
    * @throws IllegalArgumentException If either argument is not specified 
    */
   public ArchiveAsset(final Archive<?> archive, final Class<? extends StreamExporter> exporter)
   {
      Validate.notNull(archive, "archive must be specified");
      Validate.notNull(exporter, "exporter must be specified");

      this.archive = archive;
      this.exporter = exporter;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.asset.Asset#openStream()
    */
   @Override
   public InputStream openStream()
   {
      // Export via the specified exporter
      return this.getArchive().as(this.exporter).export();
   }

   /**
    * Returns the archive this asset represents 
    * @return
    */
   public Archive<?> getArchive()
   {
      return archive;
   }

}

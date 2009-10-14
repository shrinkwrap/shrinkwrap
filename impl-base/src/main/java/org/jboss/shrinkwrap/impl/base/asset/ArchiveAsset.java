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
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.impl.base.ArchiveInputStreamFactory;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * ArchiveAsset
 * 
 * An {@link Asset} representing an {@link Archive}
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
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

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates an ArchiveAsset with and archive and a byte array of archive contents
    * @throws IllegalArgumentException if no archive is provided 
    */
   public ArchiveAsset(Archive<?> archive)
   {
      Validate.notNull(archive, "archive must be specified");

      this.archive = archive;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /*
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Asset#getStream()
    */
   @Override
   public InputStream openStream()
   {
      // Get the input stream from the ArchiveInputStreamFactory
      return ArchiveInputStreamFactory.getInputStream(getArchive());
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

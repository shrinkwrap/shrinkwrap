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
package org.jboss.declarchive.impl.base.asset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.Validate;

/**
 * ByteArrayAsset
 * 
 * Implementation of a {@link Asset} backed by a byte array
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ByteArrayAsset implements Asset
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ByteArrayAsset.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Underlying content
    */
   private final byte[] content;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new instance backed by the specified
    * byte array
    * 
    * @param content
    * @throws IllegalArgumentException If the contents were not specified
    */
   public ByteArrayAsset(final byte[] content) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(content, "content must be specified");

      // Set
      this.content = content;
      if (log.isLoggable(Level.FINER))
      {
         log.finer("Created " + this + " with backing byte array of size " + content.length + "b");
      }
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.declarchive.api.Asset#getDefaultName()
    */
   @Override
   public String getDefaultName()
   {
      // TODO 
      throw new UnsupportedOperationException(
            "Revisit the contract of getDefaultName, this impl has no idea what to do with it");
   }

   /**
    * @see org.jboss.declarchive.api.Asset#getDefaultPath()
    */
   @Override
   public Path getDefaultPath()
   {
      // TODO 
      throw new UnsupportedOperationException(
            "Revisit the contract of getDefaultPath, this impl has no idea what to do with it");
   }

   /**
    * @see org.jboss.declarchive.api.Asset#getStream()
    */
   @Override
   public InputStream getStream()
   {
      return new ByteArrayInputStream(this.content);
   }
}

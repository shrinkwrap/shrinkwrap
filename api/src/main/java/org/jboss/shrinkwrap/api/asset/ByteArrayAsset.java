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
package org.jboss.shrinkwrap.api.asset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Asset;

/**
 * Implementation of an {@link Asset} backed by a byte array
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
    * Creates a new {@link Asset} instance backed by the specified
    * byte array
    *
    * @param content
    * @throws IllegalArgumentException If the contents were not specified
    */
   public ByteArrayAsset(final byte[] content) throws IllegalArgumentException
   {
      // Precondition check
      if (content == null)
      {
         throw new IllegalArgumentException("content must be specified");
      }

      // Defensive copy on set, SHRINKWRAP-38
      final int length = content.length;
      byte[] newArray = new byte[length];
      System.arraycopy(content, 0, newArray, 0, length);

      // Set
      this.content = newArray;
      if (log.isLoggable(Level.FINER))
      {
         log.finer("Created " + this + " with backing byte array of size " + length + "b");
      }
   }

   /**
    * Creates a new {@link Asset} instance backed by the bytes
    * contained in the the specified {@link InputStream}
    *
    * @param stream
    * @throws IllegalArgumentException If the stream is not specified
    */
   public ByteArrayAsset(final InputStream stream)
   {
      // Delegate
      this(ByteArrayIOUtil.asByteArray(stream));
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.shrinkwrap.api.Asset#openStream()
    */
   @Override
   public InputStream openStream()
   {
      return new ByteArrayInputStream(this.content);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "ByteArrayAsset [content size=" + content.length + "bytes]";
   }
}

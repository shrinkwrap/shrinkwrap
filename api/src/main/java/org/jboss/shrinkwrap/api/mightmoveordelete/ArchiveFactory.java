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
package org.jboss.shrinkwrap.api.mightmoveordelete;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;

/**
 * ArchiveFactory
 * 
 * Factory to create {@link Archive} instances, backed
 * by some implementation of a storage archive (ie. File or in-memory). 
 * This removes the API dependency upon internals for the 
 * client view and additionally acts as a convenience mechanism.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
class ArchiveFactory
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ArchiveFactory.class.getName());

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Internal Constructor to prohibit external
    * instantiation
    */
   private ArchiveFactory()
   {
   }

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link Archive}
    * 
    * @param name
    * @throws IllegalArgumentException If the name is not specified
    */
   static <T extends Archive<T>> T createArchive(final Constructor<T> constructor, final T delegate)
         throws IllegalArgumentException
   {
      // Precondition check
      if (delegate == null)
      {
         throw new IllegalArgumentException("delegate must be specified");
      }

      final Class<T> actualClass = constructor.getDeclaringClass();

      // Create new instance
      final Object obj;
      try
      {
         obj = constructor.newInstance(delegate);
      }
      catch (final InstantiationException e)
      {
         throw new RuntimeException("Error in creating new " + actualClass.getName(), e);
      }
      catch (final IllegalAccessException e)
      {
         throw new RuntimeException("Error in creating new " + actualClass.getName(), e);
      }
      catch (final InvocationTargetException e)
      {
         throw new RuntimeException("Error in creating new " + actualClass.getName(), e);
      }

      // Cast 
      final T archive;
      try
      {
         archive = constructor.getDeclaringClass().cast(obj);
      }
      catch (final ClassCastException cce)
      {
         throw new RuntimeException("New instance should be of type " + actualClass.getName(), cce);
      }

      // Return
      return archive;
   }
}

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
package org.jboss.declarchive.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * VfsMemoryArchiveFactory
 * 
 * Factory to create {@link Archive} instances, backed
 * by a VFS in-memory implementation. 
 * This removes the API dependency upon internals for the 
 * client view and additionally acts as a convenience mechanism.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
/*
 * TODO
 * 
 * Break this up into a much more generic approach, then 
 * make factories for each underlying implementation type
 */
public class VfsMemoryArchiveFactory
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VfsMemoryArchiveFactory.class.getName());

   /**
    * FQN of implementation Class used in creating new archives 
    */
   private static final String CLASS_NAME_ARCHIVE_IMPL = "org.jboss.declarchive.impl.vfs.MemoryArchiveImpl";

   /**
    * FQNs of type of parameters to implementation class constructor
    */
   private static final String[] CLASS_NAMES_CTOR_PARAMETERS =
   {String.class.getName()};

   /**
    * Constructor used in creating new {@link Archive} instances
    */
   private static Constructor<?> constructor;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Internal Constructor to prohibit external
    * instantiation
    */
   private VfsMemoryArchiveFactory()
   {
   }

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link Archive} with the specified name.  
    * 
    * @param name
    * @throws IllegalArgumentException If the name is not specified
    */
   public static Archive createVirtualArchive(final String name) throws IllegalArgumentException
   {
      // Precondition check
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }

      // Get constructor
      final Constructor<?> ctor = getConstructor();

      // Create new instance
      final Object obj;
      try
      {
         obj = ctor.newInstance(name);
      }
      catch (final InstantiationException e)
      {
         throw new RuntimeException("Error in creating new " + Archive.class.getName(), e);
      }
      catch (final IllegalAccessException e)
      {
         throw new RuntimeException("Error in creating new " + Archive.class.getName(), e);
      }
      catch (final InvocationTargetException e)
      {
         throw new RuntimeException("Error in creating new " + Archive.class.getName(), e);
      }

      // Cast 
      final Archive archive;
      try
      {
         archive = Archive.class.cast(obj);
      }
      catch (final ClassCastException cce)
      {
         throw new RuntimeException("New instance should be of type " + Archive.class.getName(), cce);
      }

      // Return
      return archive;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the constructor used in creating new archive instances.
    * Uses a cached copy unless not yet initialized.
    */
   private synchronized static Constructor<?> getConstructor()
   {
      // If we haven't yet cached the ctor
      if (constructor == null)
      {
         // Load the impl class
         final String implClassName = CLASS_NAME_ARCHIVE_IMPL;
         final Class<?> implClass = getClass(implClassName);

         // Load the ctor param classes
         final List<Class<?>> paramClasses = new ArrayList<Class<?>>();
         for (final String paramClassName : CLASS_NAMES_CTOR_PARAMETERS)
         {
            paramClasses.add(getClass(paramClassName));
         }
         final Class<?>[] paramClassesArray = paramClasses.toArray(new Class<?>[]
         {});

         // Get and set the ctor
         try
         {
            constructor = SecurityActions.getConstructor(implClass, paramClassesArray);
            log.log(Level.FINE, "Set the " + Archive.class.getName() + " type constructor: " + constructor);
         }
         catch (final NoSuchMethodException nsme)
         {
            throw new RuntimeException("Could not find constructor to be used in factory creation of a new "
                  + Archive.class.getSimpleName(), nsme);
         }
      }

      // Return
      return constructor;
   }

   /**
    * Obtains the class with the specified name from the TCCL
    *  
    * @param className
    * @return
    */
   private static Class<?> getClass(final String className)
   {
      final ClassLoader cl = SecurityActions.getThreadContextClassLoader();
      try
      {
         return Class.forName(className, false, cl);
      }
      catch (ClassNotFoundException cnfe)
      {
         throw new RuntimeException("Could not find implementation class \"" + className + "\" in " + cl, cnfe);
      }
   }
}

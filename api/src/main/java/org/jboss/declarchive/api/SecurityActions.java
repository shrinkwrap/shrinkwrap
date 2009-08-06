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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

/**
 * SecurityActions
 * 
 * A set of privileged actions that are not to leak out
 * of this package 
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
class SecurityActions
{

   //-------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------||

   /**
    * No external instantiation
    */
   private SecurityActions()
   {

   }

   //-------------------------------------------------------------------------------||
   // Utility Methods --------------------------------------------------------------||
   //-------------------------------------------------------------------------------||

   /**
    * Obtains the Thread Context ClassLoader
    */
   static ClassLoader getThreadContextClassLoader()
   {
      return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>()
      {
         public ClassLoader run()
         {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   /**
    * Obtains the constructor for the specified class with the specified param types
    * according to the contract of {@link Class#getConstructor(Class...)}
    * 
    * @param clazz
    * @param paramTypes
    * @return
    * @throws NoSuchMethodException
    * @throws SecurityException
    * @throws IllegalArgumentException If the class or param types were not specified
    */
   static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... paramTypes)
         throws NoSuchMethodException, SecurityException, IllegalArgumentException
   {
      // Precondition checks
      if (clazz == null)
      {
         throw new IllegalArgumentException("class must be specified");
      }
      if (paramTypes == null)
      {
         throw new IllegalArgumentException("param types must be specified");
      }

      try
      {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<Constructor<?>>()
         {

            @Override
            public Constructor<?> run() throws Exception
            {
               return clazz.getConstructor(paramTypes);
            }
         });
      }
      catch (final PrivilegedActionException pae)
      {
         // Throw nsme and se
         final Throwable unwrapped = pae.getCause();
         if (unwrapped instanceof NoSuchMethodException)
         {
            final NoSuchMethodException nsme = (NoSuchMethodException) unwrapped;
            throw nsme;
         }
         if (unwrapped instanceof SecurityException)
         {
            final SecurityException se = (SecurityException) unwrapped;
            throw se;
         }

         // Throw the cause as encountered
         throw new RuntimeException("Error in obtaining constructor", unwrapped);

      }
   }

}

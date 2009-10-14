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
package org.jboss.shrinkwrap.api.spec;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

/**
 * FactoryUtil
 * 
 * Package-private factory utilities 
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
class FactoryUtil
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(FactoryUtil.class.getName());

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Prohibit instantiation
    */
   private FactoryUtil()
   {
      throw new UnsupportedOperationException("No instances should be created; stateless utility class");
   }

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Create an instance of the Class with the specified FQN, of the expected
    * type
    * 
    * @throws IllegalArgumentException If the specified type is not assignable to the instance
    *       of the class created from the specified class name, or if either argument is not
    *       supplied
    */
   static <T> T createInstance(final String className, final Class<T> type) throws IllegalArgumentException
   {
      // Precondition checks
      if (className == null || className.length() == 0)
      {
         throw new IllegalArgumentException("className must be specified");
      }
      if (type == null)
      {
         throw new IllegalArgumentException("type must be specified");
      }

      return AccessController.doPrivileged(new PrivilegedAction<T>()
      {
         public T run()
         {
            try
            {
               final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
               final Class<?> clazz = Class.forName(className, false, classLoader);
               final Object obj = clazz.newInstance();
               try
               {
                  return type.cast(obj);
               }
               catch (final ClassCastException cee)
               {
                  throw new IllegalArgumentException("Specified type " + type.getName() + " is not assignable to "
                        + obj);
               }
            }
            catch (Exception e)
            {
               throw new IllegalArgumentException("Unable to create implemenation: " + className, e);
            }
         }
      });
   }

}

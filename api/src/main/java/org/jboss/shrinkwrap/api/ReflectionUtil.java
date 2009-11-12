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
package org.jboss.shrinkwrap.api;

import java.lang.reflect.Constructor;

import com.sun.xml.internal.txw2.IllegalAnnotationException;

/**
 * ReflectionUtil
 * 
 * Helper class for creating new instances of a class
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
final class ReflectionUtil
{
   /**
    * Create a new instance by finding a constructor that matches the argumentTypes signature 
    * using the arguments for instantiation.
    * 
    * @param className Full classname of class to create
    * @param argumentTypes The constructor argument types
    * @param arguments The constructor arguments
    * @return a new instance
    * @throws IllegalArgumentException if className is null
    * @throws IllegalArgumentException if argumentTypes is null
    * @throws IllegalArgumentException if arguments is null
    * @throws RuntimeException if any exceptions during creation
    */
   public static Object createInstance(String className, Class<?>[] argumentTypes, Object[] arguments) 
   {
      if(className == null) 
      {
         throw new IllegalArgumentException("ClassName must be specified");
      }
      if(argumentTypes == null) 
      {
         throw new IllegalAnnotationException("ArgumentTypes must be specified. Use empty array if no arguments");
      }
      if(arguments == null) 
      {
         throw new IllegalAnnotationException("Arguments must be specified. Use empty array if no arguments");
      }
      try 
      {
         Class<?> implClass = loadClass(className); 
         Constructor<?> constructor = findConstructor(implClass, argumentTypes);
         return constructor.newInstance(arguments);
      }
      catch (Exception e) 
      {
         throw new RuntimeException(
               "Could not create new instance of " + className + ", missing package from classpath?", e);
      } 
   }

   //-------------------------------------------------------------------------------------||
   // Class Members - Internal Helpers ---------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   /**
    * Load the specified class using Thread Current Class Loader in a privileged block. 
    */
   private static Class<?> loadClass(String archiveImplClassName) throws Exception 
   {
      return SecurityActions.getThreadContextClassLoader().loadClass(archiveImplClassName);
   }
   
   /**
    * Find a constructor that match the signature of given argumentTypes.
    */
   private static Constructor<?> findConstructor(Class<?> archiveImplClazz, Class<?>... argumentTypes) throws Exception 
   {
      return archiveImplClazz.getConstructor(argumentTypes);
   }

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private ReflectionUtil()
   {
   }
}

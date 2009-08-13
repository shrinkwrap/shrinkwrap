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
package org.jboss.declarchive.impl.base.resource;

import java.io.InputStream;

import org.jboss.declarchive.spi.Resource;

/**
 * Loads the given class using the class's ClassLoader.
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class ClassResource implements Resource
{
   /**
    * Delimiter for paths while looking for resources 
    */
   private static final char DELIMITER_RESOURCE_PATH = '/';

   /**
    * Delimiter for paths in fully-qualified class names 
    */
   private static final char DELIMITER_CLASS_NAME_PATH = '.';

   /**
    * The filename extension appended to classes
    */
   private static final String EXTENSION_CLASS = ".class";

   private Class<?> clazz;

   /**
    * Load any class as a resource.
    * 
    * @param clazz The class to load
    * @throws IllegalArgumentException Class can not be null
    */
   public ClassResource(Class<?> clazz)
   {
      // Precondition check
      if (clazz == null)
      {
         throw new IllegalArgumentException("Class must be specified");
      }
      this.clazz = clazz;
   }

   /**
    * Get the default name using Class.getSimpleName().
    */
   @Override
   public String getDefaultName()
   {
      return getResourceNameOfClass(clazz);
   }

   /**
    * Converts the Class name into a Resource URL and uses the 
    * ClassloaderResource for loading the Class.
    */
   @Override
   public InputStream getStream()
   {
      return new ClassloaderResource(getResourceNameOfClass(clazz), clazz.getClassLoader()).getStream();
   }

   /**
    * Returns the name of the class such that it may be accessed via ClassLoader.getResource()
    * 
    * @param clazz The class
    * @throws IllegalArgumentException If the class was not specified
    */
   private String getResourceNameOfClass(final Class<?> clazz) throws IllegalArgumentException
   {
      // Build the name
      final String fqn = clazz.getName();
      final String nameAsResourcePath = fqn.replace(DELIMITER_CLASS_NAME_PATH, DELIMITER_RESOURCE_PATH);
      final String resourceName = nameAsResourcePath + EXTENSION_CLASS;

      // Return 
      return resourceName;
   }
}

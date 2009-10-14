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

import org.jboss.shrinkwrap.api.Archive;

/**
 * ArchiveFactory
 * 
 * Template Factory used to create {@link Archive} instances. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
abstract class ArchiveFactory<T extends Archive<T>>
{

   //-------------------------------------------------------------------------------------||
   // Class Methods ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Create an instance of an ArchiveFactory implementation
    *  
    * @return ArchiveFactory instance
    */
   protected synchronized static <T extends Archive<T>, F extends ArchiveFactory<T>> F createInstance(
         Class<F> factoryBaseType, String fqFactoryName)
   {
      try
      {
         // Create the instance
         F instance = FactoryUtil.createInstance(fqFactoryName, factoryBaseType);

         // Return the instance
         return instance;
      }
      catch (Exception e)
      {
         throw new IllegalStateException("Make sure you have the impl classes on your runtime classpath", e);
      }
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Template create method for concrete implementations  
    * 
    * @param archiveName
    * @return Archive instance
    */
   protected abstract T doCreate(String archiveName);
}

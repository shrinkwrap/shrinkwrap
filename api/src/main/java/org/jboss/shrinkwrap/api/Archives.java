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

/**
 * Generic unified factory for archive creation.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class Archives
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final String ARCHIVE_IMPL = "org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl";
   
   private static final String EXTENSION_LOADER_IMPL = "org.jboss.shrinkwrap.impl.base.ServiceExtensionLoader";
   
   private static ExtensionLoader extensionLoader = null;
   
   /**
    * Create a archive as a specific type.
    * 
    * @param archiveName The name of the archive
    * @return A {@link Assignable} archive base  
    */
   public static <T extends Assignable> T create(String archiveName, Class<T> type) 
   {
      if(archiveName == null) 
      {
         throw new IllegalArgumentException("ArchiveName must be specified");
      }
      if(type == null) 
      {
         throw new IllegalArgumentException("Type must be specified");
      }
      
      initializeExtensionLoader();
      
      Archive<?> archive = SecurityActions.newInstance(
                                 ARCHIVE_IMPL,
                                 new Class<?>[]{String.class, ExtensionLoader.class},
                                 new Object[]{archiveName, extensionLoader},
                                 Archive.class); 
      return archive.as(type);
   }

   /**
    * Override the loading of a specific Extension.
    * 
    * @param <T>
    * @param extensionClass The Extension interface
    * @param extensionImplClass The Extension implementation class
    */
   public static <T extends Assignable> void addExtensionOverride(
         Class<T> extensionClass, 
         Class<? extends T> extensionImplClass)
   {
      initializeExtensionLoader();
      extensionLoader.addOverride(extensionClass, extensionImplClass);
   }

   /**
    * 
    * @param loader The ExtensionLoader to use
    * @throws IllegalArgumentException if loader is null
    * @throws IllegalStateException if loader is already set
    */
   public synchronized static void setExtensionLoader(ExtensionLoader loader)
   {
      if(loader == null) 
      {
         throw new IllegalArgumentException("Loader must be specified");
      }
      if(extensionLoader != null) 
      {
         throw new IllegalStateException(
               "Loader already specified, call setExtensionLoader " +
         		"before calling create or addExtensionOverride");
      }
      extensionLoader = loader;
   }
   
   private synchronized static void initializeExtensionLoader() 
   {
      if(extensionLoader == null) {
         extensionLoader = SecurityActions.newInstance(
                              EXTENSION_LOADER_IMPL,
                              new Class<?>[]{}, 
                              new Object[]{}, 
                              ExtensionLoader.class);
      }
   }
   
   /**
    * Used by ArchivesTestCase to reset the static state of the Factory. 
    */
   static void resetState() {
      extensionLoader = null;
   }
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private Archives() {}
}

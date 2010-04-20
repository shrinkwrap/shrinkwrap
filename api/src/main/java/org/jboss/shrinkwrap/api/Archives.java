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
 * @deprecated Use {@link ShrinkWrap}
 * @see {@link ShrinkWrap}
 */
@Deprecated
public final class Archives
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Create a archive as a specific type.
    * 
    * @param archiveName The name of the archive
    * @return A {@link Assignable} archive base  
    * @deprecated 
    * @see {@link ShrinkWrap#create(String, Class)}
    */
   public static <T extends Assignable> T create(String archiveName, Class<T> type)
   {
      // Delegate to ShrinkWrap
      return ShrinkWrap.create(archiveName, type);
   }

   /**
    * Override the loading of a specific Extension.
    * 
    * @param <T>
    * @param extensionClass The Extension interface
    * @param extensionImplClass The Extension implementation class
    * @see {@link Configuration#getExtensionLoader()}
    */
   public static <T extends Assignable> void addExtensionOverride(Class<T> extensionClass,
         Class<? extends T> extensionImplClass)
   {
      final ExtensionLoader extensionLoader = ShrinkWrap.getDefaultDomain().getConfiguration().getExtensionLoader();
      extensionLoader.addOverride(extensionClass, extensionImplClass);
   }

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private Archives()
   {
   }
}

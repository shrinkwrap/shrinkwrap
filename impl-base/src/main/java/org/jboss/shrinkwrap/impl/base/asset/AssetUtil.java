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
package org.jboss.shrinkwrap.impl.base.asset;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * AssetUtil
 * 
 * Util class to help extract name/paths from Assets.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class AssetUtil
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The delimiter used for classes. 
    */
   public static final String DELIMITER_CLASS_NAME_PATH = "\\.";
   
   /**
    * The delimiter used for classes represented in resource form.
    */
   public static final String DELIMITER_RESOURCE_PATH = "/";
   
   /**
    * Extension applied to .class files
    */
   private static final String EXTENSION_CLASS = ".class";
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Private constructor for util class, should never be created. 
    */
   private AssetUtil() 
   {
   }
   
   //-------------------------------------------------------------------------------------||
   // External helpers -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Helper to extract a ClassloaderResources path information.
    * <br/><br/>
    * ie: /user/test/file.properties = /user/test/
    * 
    * @param resourceName The name of the resource
    * @return A Path representation of the give resource
    */
   public static ArchivePath getPathForClassloaderResource(String resourceName) 
   {
      String extractedPath = null;
      if (resourceName.lastIndexOf('/') != -1)
      {
         extractedPath = resourceName.substring(
                           0, 
                           resourceName.lastIndexOf('/'));
      }
      return new BasicPath(extractedPath);
   }

   /**
    * Helper to convert from java package name to class loader package name
    * <br/><br/>
    * ie: javax.test + my.txt = javax/test/ + my.txt
    * 
    * @param resourcePackage The base package
    * @param resourceName The resource inside the package.
    * @return {@link ClassLoader} resource location
    */
   public static String getClassLoaderResourceName(Package resourcePackage, String resourceName)
   {
      String resourcePackaeName = resourcePackage.getName().replaceAll(
            DELIMITER_CLASS_NAME_PATH, DELIMITER_RESOURCE_PATH);
      
      return resourcePackaeName + DELIMITER_RESOURCE_PATH + resourceName;
   }
   
   /**
    * Helper to extract a ClassloaderResources name.
    * <br/><br/>
    * ie: /user/test/file.properties = file.properties
    * 
    * @param resourceName The name of the resource
    * @return The name of the given resource
    */
   public static String getNameForClassloaderResource(String resourceName) 
   {
      String fileName = resourceName;
      if (resourceName.indexOf('/') != -1)
      {
         fileName = resourceName.substring(
                     resourceName.lastIndexOf('/') + 1, 
                     resourceName.length());
      }
      return fileName;
   }
   
   /**
    * Helper to extract a ClassResources full path.
    * <br/><br/>
    * ie: package.MyClass = package/MyClass.class
    * 
    * @param clazz
    * @return
    */
   public static ArchivePath getFullPathForClassResource(Class<?> clazz) 
   {
      String classResourceDelimiter = clazz.getName().replaceAll(
            DELIMITER_CLASS_NAME_PATH, DELIMITER_RESOURCE_PATH);
      String classFullPath = classResourceDelimiter + EXTENSION_CLASS;
      return new BasicPath(classFullPath);
   }
}

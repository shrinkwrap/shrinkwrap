package org.jboss.declarchive.impl.base.resource;

import java.io.InputStream;
import java.net.URL;

import org.jboss.declarchive.spi.Resource;

/**
 * Loads the content of any resource located in the Classloader.
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class ClassloaderResource implements Resource
{
   private String resourceName;
   private ClassLoader classLoader;
   
   /**
    * Load a named resource using the current threads context classloader.
    * 
    * @param resourceName The name of the resource to load
    * @throws IllegalArgumentException resourceName can not be null
    * @throws IllegalArgumentException resourceName must be found in given classloader
    */
   public ClassloaderResource(String resourceName) 
   {
      this(resourceName, SecurityActions.getThreadContextClassLoader());
   }

   /**
    * Load a named resource using the given classloader.
    * 
    * @param resourceName The name of the resource to load
    * @param classLoader The ClassLoader to use
    * @throws IllegalArgumentException resourceName can not be null
    * @throws IllegalArgumentException classloader can not be null
    * @throws IllegalArgumentException resourceName must be found in given classloader
    */
   public ClassloaderResource(String resourceName, ClassLoader classLoader) 
   {
      if(resourceName == null) 
      {
         throw new IllegalArgumentException("ResourceName must be specified");
      }
      if(classLoader == null) 
      {
         throw new IllegalArgumentException("ClassLoader must be specified");
      }
      if(classLoader.getResource(resourceName) == null) 
      {
         throw new IllegalArgumentException(resourceName + " not found in classloader " + classLoader);
      }
      this.resourceName = resourceName;
      this.classLoader = classLoader;
   }
   
   /**
    * Get the default name using Resource URL.getFile().
    * 
    * @return Returns only the file name part of a URL, not the absolute path.
    */
   @Override
   public String getDefaultName()
   {
      return extractFileName(
            classLoader.getResource(resourceName));
   }

   /**
    * Opens up the given resource as a stream.
    * 
    */
   @Override
   public InputStream getStream()
   {
      return classLoader.getResourceAsStream(resourceName);
   }
   
   /*
    * Extract the file name part of a URL excluding the directory structure.
    * ie: /user/test/file.properties = file.properties
    */
   private String extractFileName(URL url) 
   {
      String fileName = url.getFile();
      if(fileName.indexOf('/') != -1) 
      {
         return fileName.substring(
               fileName.lastIndexOf('/') +1, 
               fileName.length()); 
      }
      return fileName;
   }
}

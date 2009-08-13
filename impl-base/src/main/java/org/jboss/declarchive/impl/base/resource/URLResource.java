package org.jboss.declarchive.impl.base.resource;

import java.io.InputStream;
import java.net.URL;

import org.jboss.declarchive.spi.Resource;

/**
 * Loads the content of any URL supported by the runtime.
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class URLResource implements Resource
{
   private URL url;
   
   /**
    * Create a new resource with a URL source.
    * 
    * @param url A valid URL
    * @throws IllegalArgumentException URL can not be null
    */
   public URLResource(URL url) 
   {
      // Precondition check
      if (url == null)
      {
         throw new IllegalArgumentException("URL must be specified");
      }
      this.url = url;
   }
   
   /**
    * Get the default name using URL.getFile().
    */
   @Override
   public String getDefaultName()
   {
      return extractFileName(url);
   }

   /**
    * Open the URL stream.
    * 
    * @return A open stream with the content of the URL
    */
   @Override
   public InputStream getStream()
   {
      try
      {
         return url.openStream();
      } 
      catch (Exception e) 
      {
         throw new RuntimeException("Could not open stream for url " + url.toExternalForm(), e);
      }
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

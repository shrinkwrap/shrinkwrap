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
      if (fileName.indexOf('/') != -1)
      {
         return fileName.substring(fileName.lastIndexOf('/') + 1, fileName.length());
      }
      return fileName;
   }
}

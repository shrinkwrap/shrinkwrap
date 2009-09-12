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

import java.io.InputStream;
import java.net.URL;

import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * UrlAsset
 * 
 * Implementation of a {@link Asset} backed by a {@link URL}.  
 * The URL may be of any backing protocol supported by the runtime
 * (ie. has a handler registered).
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class UrlAsset implements Asset
{
   private URL url;

   /**
    * Create a new resource with a <code>URL</code> source.
    * 
    * @param url A valid URL
    * @throws IllegalArgumentException <Code>URL</code> can not be null
    */
   public UrlAsset(URL url)
   {
      // Precondition check
      Validate.notNull(url, "URL must be specified");
      this.url = url;
   }

   /**
    * Open the <code>URL</code> stream.
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
}

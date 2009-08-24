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
package org.jboss.declarchive.impl.base;

import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.container.WebContainer;
import org.jboss.declarchive.impl.base.asset.ClassLoaderAsset;
import org.jboss.declarchive.impl.base.path.BasicPath;

/**
 * WebContainerSupport
 * 
 * Abstract class that helps implement the WebContainer. 
 * Used by specs that extends the WebContainer.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class WebContainerBase<T extends Archive<T>> 
   extends ContainerBase<T> 
   implements WebContainer<T>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   private static final Logger log = Logger.getLogger(WebContainerBase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected WebContainerBase(Class<T> actualType, Archive<?> archive) 
   {
      super(actualType, archive);
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - WebContainer --------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Should be implemented to set the path for Manifest related
    * resources. 
    * 
    * @return Base Path for the ManifestContainer resources
    */
   protected abstract Path getWebPath();

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.WebContainer#setWebXML(java.lang.String)
    */
   @Override
   public T setWebXML(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return add(getWebPath(), "web.xml", new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.WebContainer#addWebResource(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public T addWebResource(Path target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resourceName, "ResourceName should be specified");
      
      Asset asset = new ClassLoaderAsset(resourceName);
      Path location = new BasicPath(getWebPath(), target);
      return add(location, asset);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.WebContainer#addWebResource(java.lang.String)
    */
   @Override
   public T addWebResource(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");

      Asset asset = new ClassLoaderAsset(resourceName);
      Path location = new BasicPath(getWebPath(), resourceName); 
      return add(location, asset);
   }

}

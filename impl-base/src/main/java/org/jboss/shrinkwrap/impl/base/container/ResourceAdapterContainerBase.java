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
package org.jboss.shrinkwrap.impl.base.container;

import java.io.File;
import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.container.ResourceAdapterContainer;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * ResourceAdapterContainerBase
 * 
 * Abstract class that helps implement the ResourceAdapter. 
 * Used by specs that extends the ResourceAdapter.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class ResourceAdapterContainerBase<T extends Archive<T>> extends ContainerBase<T>
      implements
         ResourceAdapterContainer<T>
{
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected ResourceAdapterContainerBase(Class<T> actualType, Archive<?> archive)
   {
      super(actualType, archive);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - ResourceAdapterContainer - Resources --------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.RContainer#setApplicationXML(java.lang.String)
    */
   @Override
   public T setResourceAdapterXML(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      return setResourceAdapterXML(new ClassLoaderAsset(resourceName));

   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceAdapterContainer#setResourceAdapterXML(java.io.File)
    */
   @Override
   public T setResourceAdapterXML(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return setResourceAdapterXML(new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceAdapterContainer#setResourceAdapterXML(java.net.URL)
    */
   @Override
   public T setResourceAdapterXML(URL resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return setResourceAdapterXML(new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceAdapterContainer#setResourceAdapterXML(org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T setResourceAdapterXML(Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return addAsManifestResource(resource, "ra.xml");
   }
   

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceAdapterContainer#setResourceAdapterXML(java.lang.Package, java.lang.String)
    */
   @Override
   public T setResourceAdapterXML(Package resourcePackage, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      
      return addAsManifestResource(resourcePackage, resourceName, "ra.xml");
   }
   

}

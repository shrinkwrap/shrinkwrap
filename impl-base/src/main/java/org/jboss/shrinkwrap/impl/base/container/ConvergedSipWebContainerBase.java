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
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.container.ConvergedSipWebContainer;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;

/**
 * ConvergedSipWebContainerBase
 * 
 * Abstract class that helps implement the ConvergedSipWebContainer. 
 * Used by specs that extends the ConvergedSipWebContainer.
 *
 * @author Jean Deruelle
 * @version $Revision: $
 */
public abstract class ConvergedSipWebContainerBase<T extends Archive<T>> 
   extends WebContainerBase<T> 
   implements ConvergedSipWebContainer<T>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected ConvergedSipWebContainerBase(Class<T> actualType, Archive<?> archive) 
   {
      super(actualType, archive);
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ConvergedSipWebContainer#setSipXML(java.lang.String)
    */
   @Override
   public T setSipXML(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return setSipXML(new ClassLoaderAsset(resourceName));
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ConvergedSipWebContainer#setSipXML(java.io.File)
    */
   @Override
   public T setSipXML(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return setSipXML(new FileAsset(resource));
   }
   
   /*
    * (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ConvergedSipWebContainer#setSipXML(java.net.URL)
    */
   @Override
   public T setSipXML(URL resource) throws IllegalArgumentException 
   {
      Validate.notNull(resource, "Resource should be specified");
      return setSipXML(new UrlAsset(resource));
   }
   
   /*
    * (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ConvergedSipWebContainer#setSipXML(org.jboss.shrinkwrap.api.asset.Asset)
    */
   @Override
   public T setSipXML(Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addWebResource(resource, "sip.xml");
   }
}

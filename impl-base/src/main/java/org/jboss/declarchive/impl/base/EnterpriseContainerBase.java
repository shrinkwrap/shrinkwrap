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
import org.jboss.declarchive.api.container.EnterpriseContainer;
import org.jboss.declarchive.impl.base.asset.AssetUtil;
import org.jboss.declarchive.impl.base.asset.ClassLoaderAsset;
import org.jboss.declarchive.impl.base.path.BasicPath;

/**
 * EnterpriseContainerSupport
 * 
 * Abstract class that helps implement the EnterpriseContainer. 
 * Used by specs that extends the EnterpriseContainer.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class EnterpriseContainerBase<T extends Archive<T>> 
   extends ContainerBase<T> 
   implements EnterpriseContainer<T>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   private static final Logger log = Logger.getLogger(EnterpriseContainerBase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected EnterpriseContainerBase(Class<T> actualType, Archive<?> archive) 
   {
      super(actualType, archive);
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - EnterpriseContainer - Resources -------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Should be implemented to set the path for Application related
    * resources. 
    * 
    * @return Base Path for the EnterpriseContainer application resources
    */
   protected abstract Path getApplicationPath();

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#setApplicationXML(java.lang.String)
    */
   @Override
   public T setApplicationXML(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      return add(getApplicationPath(), "application.xml", new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#addApplicationResource(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public T addApplicationResource(Path target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");

      Asset asset = new ClassLoaderAsset(resourceName);
      Path location = new BasicPath(getApplicationPath(), target);
      return add(location, asset);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#addApplicationResource(java.lang.String)
    */
   @Override
   public T addApplicationResource(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");

      Asset asset = new ClassLoaderAsset(resourceName);
      Path location = new BasicPath(getApplicationPath(), resourceName);
      return add(location, asset);
   }
   
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - EnterpriseContainer - Modules ---------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Should be implemented to set the path for Module related
    * resources. 
    * 
    * @return Base Path for the EnterpriseContainer module resources
    */
   protected abstract Path getModulePath();
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#addModule(org.jboss.declarchive.api.Archive)
    */
   @Override
   public T addModule(Archive<?> archive) throws IllegalArgumentException
   {
      Validate.notNull(archive, "Archive must be specified");
      
      Path location = getModulePath();
      return add(location, archive);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#addModule(java.lang.String)
    */
   @Override
   public T addModule(String resourceName)
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      
      Asset asset = new ClassLoaderAsset(resourceName);
      Path location = new BasicPath(getModulePath(), AssetUtil.getNameForClassloaderResource(resourceName));
      return add(location, asset);
   }
}

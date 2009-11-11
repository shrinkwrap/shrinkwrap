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
package org.jboss.shrinkwrap.impl.base;

import java.io.File;
import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.container.EnterpriseContainer;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.asset.FileAsset;
import org.jboss.shrinkwrap.impl.base.asset.UrlAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

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
      return setApplicationXML(new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#setApplicationXML(java.io.File)
    */
   @Override
   public T setApplicationXML(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return setApplicationXML(new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#setApplicationXML(java.net.URL)
    */
   @Override
   public T setApplicationXML(URL resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return setApplicationXML(new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#setApplicationXML(org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T setApplicationXML(Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return addApplicationResource("application.xml", resource);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#addApplicationResource(java.lang.String)
    */
   @Override
   public T addApplicationResource(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");

      return addApplicationResource(resourceName, new ClassLoaderAsset(resourceName));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.io.File)
    */
   @Override
   public T addApplicationResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");

      return addApplicationResource(resource.getName(), new FileAsset(resource));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addApplicationResource(String target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");

      return addApplicationResource(target, new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.String, java.io.File)
    */
   @Override
   public T addApplicationResource(String target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addApplicationResource(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.String, java.net.URL)
    */
   @Override
   public T addApplicationResource(String target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addApplicationResource(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.String, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addApplicationResource(String target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addApplicationResource(new BasicPath(target), resource);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#addApplicationResource(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public T addApplicationResource(Path target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");

      return addApplicationResource(target, new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(org.jboss.shrinkwrap.api.Path, java.io.File)
    */
   @Override
   public T addApplicationResource(Path target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addApplicationResource(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(org.jboss.shrinkwrap.api.Path, java.net.URL)
    */
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(org.jboss.shrinkwrap.api.Path, java.net.URL)
    */
   @Override
   public T addApplicationResource(Path target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addApplicationResource(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(org.jboss.shrinkwrap.api.Path, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addApplicationResource(Path target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      Path location = new BasicPath(getApplicationPath(), target);
      return add(location, resource);
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
      
      return add(getModulePath(), archive);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.EnterpriseContainer#addModule(java.lang.String)
    */
   @Override
   public T addModule(String resourceName)
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      
      Path location = new BasicPath(AssetUtil.getNameForClassloaderResource(resourceName));
      return addModule(location, resourceName);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.io.File)
    */
   @Override
   public T addModule(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      
      Path location = new BasicPath(resource.getName());
      return addModule(location, resource);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(org.jboss.shrinkwrap.api.Path, java.io.File)
    */
   @Override
   public T addModule(final Path targetPath, final File resource) throws IllegalArgumentException
   {
      Validate.notNull(targetPath, "Target Path must be specified");
      Validate.notNull(resource, "Resource must be specified");
      
      final Asset asset = new FileAsset(resource);
      final Path location = new BasicPath(getModulePath(), targetPath);
      return add(location, asset);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(org.jboss.shrinkwrap.api.Path, java.lang.String)
    */
   @Override
   public T addModule(final Path targetPath, final String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(targetPath, "Target Path must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      
      final Asset asset = new ClassLoaderAsset(resourceName);
      final Path location = new BasicPath(getModulePath(), targetPath);
      return add(location, asset);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(org.jboss.shrinkwrap.api.Path, java.net.URL)
    */
   @Override
   public T addModule(final Path targetPath, final URL resource) throws IllegalArgumentException
   {
      Validate.notNull(targetPath, "Target Path must be specified");
      Validate.notNull(resource, "Resource must be specified");
      
      Asset asset = new UrlAsset(resource);
      Path location = new BasicPath(getModulePath(),targetPath);
      return add(location, asset);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.lang.String, java.io.File)
    */
   @Override
   public T addModule(final String targetPath, final File resource) throws IllegalArgumentException
   {
      Validate.notNull(targetPath, "Target Path must be specified");
      Validate.notNull(resource, "Resource must be specified");
      
      return addModule(new BasicPath(targetPath), resource);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.lang.String, java.lang.String)
    */
   @Override
   public T addModule(final String targetPath, final String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(targetPath, "Target Path must be specified");
      Validate.notNull(resourceName, "Resource must be specified");

      return addModule(new BasicPath(targetPath), resourceName);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.lang.String, java.net.URL)
    */
   @Override
   public T addModule(final String targetPath, final URL resource) throws IllegalArgumentException
   {
      Validate.notNull(targetPath, "Target Path must be specified");
      Validate.notNull(resource, "Resource must be specified");
      
      return addModule(new BasicPath(targetPath), resource);
   }
}

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
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.container.WebContainer;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.asset.ServiceProviderAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * WebContainerBase
 * 
 * Abstract class that helps implement the WebContainer. 
 * Used by specs that extends the WebContainer.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class WebContainerBase<T extends Archive<T>> extends ContainerBase<T> implements WebContainer<T>
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

   protected WebContainerBase(Class<T> actualType, Archive<?> archive)
   {
      super(actualType, archive);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - WebContainer --------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Returns the path to web resources
    * 
    * @return Base Path for the web resources
    */
   protected abstract ArchivePath getWebPath();

   /**
    * Returns the path to WEB-INF 
    *
    * @return the path to WEB-INF
    */
   protected abstract ArchivePath getWebInfPath();

   /**
    * Returns the path to web container service providers
    *
    * @return the path to web container service providers
    */
   protected abstract ArchivePath getServiceProvidersPath();

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.WebContainer#setWebXML(java.lang.String)
    */
   @Override
   public T setWebXML(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return setWebXML(new ClassLoaderAsset(resourceName));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#setWebXML(java.io.File)
    */
   @Override
   public T setWebXML(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return setWebXML(new FileAsset(resource));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#setWebXML(java.net.URL)
    */
   @Override
   public T setWebXML(URL resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return setWebXML(new UrlAsset(resource));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#setWebXML(org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T setWebXML(Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addAsWebInfResource(resource, "web.xml");
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#setWebXML(java.lang.Package, java.lang.String)
    */
   @Override
   public T setWebXML(Package resourcePackage, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");

      String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      return setWebXML(new ClassLoaderAsset(classloaderResourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.WebContainer#addWebResource(java.lang.String)
    */
   @Override
   public T addAsWebResource(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");

      return addAsWebResource(new ClassLoaderAsset(resourceName), AssetUtil.getNameForClassloaderResource(resourceName));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.io.File)
    */
   @Override
   public T addAsWebResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");

      return addAsWebResource(new FileAsset(resource), resource.getName());
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addAsWebResource(String resourceName, String target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebResource(new ClassLoaderAsset(resourceName), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.io.File, java.lang.String)
    */
   @Override
   public T addAsWebResource(File resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebResource(new FileAsset(resource), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.net.URL, java.lang.String)
    */
   @Override
   public T addAsWebResource(URL resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebResource(new UrlAsset(resource), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T addAsWebResource(Asset resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebResource(resource, new BasicPath(target));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.lang.String, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsWebResource(String resourceName, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebResource(new ClassLoaderAsset(resourceName), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebResource(java.io.File, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebResource(File resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebResource(new FileAsset(resource), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.net.URL, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsWebResource(URL resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebResource(new UrlAsset(resource), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebResource(org.jboss.shrinkwrap.api.asset.Asset, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebResource(Asset resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      ArchivePath location = new BasicPath(getWebPath(), target);
      return add(resource, location);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebResources(java.lang.Package, java.lang.String[])
    */
   @Override
   public T addAsWebResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNullAndNoNullValues(resourceNames,
            "ResourceNames must be specified and can not container null values");
      for (final String resourceName : resourceNames)
      {
         addAsWebResource(resourcePackage, resourceName);
      }
      return covarientReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.lang.Package, java.lang.String)
    */
   @Override
   public T addAsWebResource(Package resourcePackage, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");

      String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      ArchivePath target = ArchivePaths.create(classloaderResourceName);

      return addAsWebResource(resourcePackage, resourceName, target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.lang.Package, java.lang.String, java.lang.String)
    */
   @Override
   public T addAsWebResource(Package resourcePackage, String resourceName, String target)
         throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      return addAsWebResource(resourcePackage, resourceName, ArchivePaths.create(target));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addWebResource(java.lang.Package, java.lang.String, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebResource(Package resourcePackage, String resourceName, ArchivePath target)
         throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      Asset resource = new ClassLoaderAsset(classloaderResourceName);

      return addAsWebResource(resource, target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.lang.String)
    */
   @Override
   public T addAsWebInfResource(final String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");

      return addAsWebInfResource(new ClassLoaderAsset(resourceName),
            AssetUtil.getNameForClassloaderResource(resourceName));
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.io.File)
    */
   @Override
   public T addAsWebInfResource(final File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");

      return addAsWebInfResource(new FileAsset(resource), resource.getName());
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addAsWebInfResource(final String resourceName, final String target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebInfResource(new ClassLoaderAsset(resourceName), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.io.File, java.lang.String)
    */
   @Override
   public T addAsWebInfResource(final File resource, final String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNullOrEmpty(target, "Target should be specified");

      return addAsWebInfResource(new FileAsset(resource), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.net.URL, java.lang.String)
    */
   @Override
   public T addAsWebInfResource(final URL resource, final String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNullOrEmpty(target, "Target should be specified");

      return addAsWebInfResource(new UrlAsset(resource), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(org.jboss.shrinkwrap.api.asset.Asset, java.lang.String)
    */
   @Override
   public T addAsWebInfResource(final Asset resource, final String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNullOrEmpty(target, "Target should be specified");

      return addAsWebInfResource(resource, ArchivePaths.create(target));
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.lang.String, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebInfResource(final String resourceName, final ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebInfResource(new ClassLoaderAsset(resourceName), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.io.File, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebInfResource(final File resource, final ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebInfResource(new FileAsset(resource), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.net.URL, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebInfResource(final URL resource, final ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      return addAsWebInfResource(new UrlAsset(resource), target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(org.jboss.shrinkwrap.api.asset.Asset, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebInfResource(final Asset resource, final ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");

      final ArchivePath location = new BasicPath(getWebInfPath(), target);
      return add(resource, location);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResources(java.lang.Package, java.lang.String[])
    */
   @Override
   public T addAsWebInfResources(final Package resourcePackage, final String... resourceNames)
         throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNullAndNoNullValues(resourceNames,
            "ResourceNames must be specified and can not container null values");
      for (String resourceName : resourceNames)
      {
         addAsWebInfResource(resourcePackage, resourceName);
      }
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.lang.Package, java.lang.String)
    */
   @Override
   public T addAsWebInfResource(final Package resourcePackage, final String resourceName)
         throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");

      final String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      final ArchivePath target = ArchivePaths.create(classloaderResourceName);

      return addAsWebInfResource(resourcePackage, resourceName, target);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.lang.Package, java.lang.String, java.lang.String)
    */
   @Override
   public T addAsWebInfResource(final Package resourcePackage, final String resourceName, final String target)
         throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNullOrEmpty(resourceName, "ResourceName must be specified");
      Validate.notNullOrEmpty(target, "Target must be specified");

      return addAsWebInfResource(resourcePackage, resourceName, ArchivePaths.create(target));
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.WebContainer#addAsWebInfResource(java.lang.Package, java.lang.String, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsWebInfResource(final Package resourcePackage, final String resourceName, ArchivePath target)
         throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNullOrEmpty(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      final String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      final Asset resource = new ClassLoaderAsset(classloaderResourceName);

      return addAsWebInfResource(resource, target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addServiceProvider(java.lang.Class, java.lang.Class<?>[])
    */
   @Override
   public T addAsServiceProvider(Class<?> serviceInterface, Class<?>... serviceImpls) throws IllegalArgumentException
   {
      Validate.notNull(serviceInterface, "ServiceInterface must be specified");
      Validate.notNullAndNoNullValues(serviceImpls, "ServiceImpls must be specified and can not contain null values");

      Asset asset = new ServiceProviderAsset(serviceImpls);
      ArchivePath path = new BasicPath(getServiceProvidersPath(), serviceInterface.getName());
      return add(asset, path);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ServiceProviderContainer#addServiceProviderAndClasses(java.lang.Class, java.lang.Class<?>[])
    */
   @Override
   public T addAsServiceProviderAndClasses(Class<?> serviceInterface, Class<?>... serviceImpls) throws IllegalArgumentException
   {
      Validate.notNull(serviceInterface, "ServiceInterface must be specified");
      Validate.notNullAndNoNullValues(serviceImpls, "ServiceImpls must be specified and can not contain null values");

      addAsServiceProvider(serviceInterface, serviceImpls);
      addClass(serviceInterface);
      return addClasses(serviceImpls);
   }
}

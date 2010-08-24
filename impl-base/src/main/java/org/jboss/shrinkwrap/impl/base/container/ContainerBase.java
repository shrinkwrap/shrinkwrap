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
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.PluggableNamedAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.impl.base.ArchiveBase;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.URLPackageScanner;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.asset.ClassAsset;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.asset.ServiceProviderAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * ContainerBase
 * 
 * Abstract class that helps implement the Archive, ManifestContainer, ResourceContainer, ClassContainer
 * and LibraryContainer. 
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class ContainerBase<T extends Archive<T>> extends AssignableBase<Archive<?>> implements 
   Archive<T>, ManifestContainer<T>, ResourceContainer<T>, ClassContainer<T>, LibraryContainer<T>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   /**
    * Secure action to obtain the Thread Context ClassLoader
    * 
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    * @version $Revision: $
    */
   private enum GetTcclAction implements PrivilegedAction<ClassLoader>{
      INSTANCE;

      @Override
      public ClassLoader run()
      {
         return Thread.currentThread().getContextClassLoader();
      }
   }
   
   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   /**
    * The exposed archive type. 
    */
   private final Class<T> actualType;
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected ContainerBase(final Class<T> actualType, final Archive<?> archive) 
   {
      super(archive);
      
      Validate.notNull(actualType, "ActualType should be specified");
      
      this.actualType = actualType;
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - Archive Delegation --------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Archive, java.lang.String, java.lang.Class)
    */
   @Override
   public T add(final Archive<?> archive, final String path, final Class<? extends StreamExporter> exporter)
   {
      this.getArchive().add(archive, path, exporter);
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.ArchivePath, java.lang.Class)
    */
   @Override
   public T add(final Archive<?> archive, final ArchivePath path, final Class<? extends StreamExporter> exporter)
   {
      this.getArchive().add(archive, path, exporter);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T add(Asset asset, ArchivePath target) throws IllegalArgumentException
   {
      this.getArchive().add(asset, target);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset, org.jboss.shrinkwrap.api.ArchivePath, java.lang.String)
    */
   @Override
   public T add(Asset asset, ArchivePath path, String name)
   {
      this.getArchive().add(asset, path, name);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset, java.lang.String, java.lang.String)
    */
   @Override
   public T add(final Asset asset, final String target, final String name) throws IllegalArgumentException
   {
      this.getArchive().add(asset, target, name);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(PluggableNamedAsset))
    */
   @Override
   public T add(PluggableNamedAsset pna)
   {
      this.archive.add(pna);
      return covarientReturn();
      
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#addDirectories(org.jboss.shrinkwrap.api.ArchivePath[])
    */
   @Override
   public T addDirectories(ArchivePath... paths) throws IllegalArgumentException
   {
      this.getArchive().addDirectories(paths);
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#addDirectories(java.lang.String[])
    */
   @Override
   public T addDirectories(String... paths) throws IllegalArgumentException
   {
      this.getArchive().addDirectories(paths);
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#addDirectory(org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addDirectory(ArchivePath path) throws IllegalArgumentException
   {
      this.getArchive().addDirectory(path);
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#addDirectory(java.lang.String)
    */
   @Override
   public T addDirectory(String path) throws IllegalArgumentException
   {
      this.getArchive().addDirectory(path);
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive)
    */
   @Override
   public T merge(Archive<?> source) throws IllegalArgumentException
   {
      this.getArchive().merge(source);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.Filter)
    */
   @Override
   public T merge(Archive<?> source, Filter<ArchivePath> filter) throws IllegalArgumentException
   {
      this.getArchive().merge(source, filter);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T merge(Archive<?> source, ArchivePath path) throws IllegalArgumentException
   {
      this.getArchive().merge(source, path);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.ArchivePath, org.jboss.shrinkwrap.api.Filter)
    */
   @Override
   public T merge(Archive<?> source, ArchivePath path, Filter<ArchivePath> filter) throws IllegalArgumentException
   {
      this.getArchive().merge(source, path, filter);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, java.lang.String, org.jboss.shrinkwrap.api.Filter)
    */
   @Override
   public T merge(final Archive<?> source, final String path, final Filter<ArchivePath> filter)
         throws IllegalArgumentException
   {
      this.getArchive().merge(source, path, filter);
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, java.lang.String)
    */
   @Override
   public T merge(final Archive<?> source, final String path) throws IllegalArgumentException
   {
      this.getArchive().merge(source, path);
      return covarientReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset, java.lang.String)
    */
   @Override
   public T add(Asset asset, String name)
   {
      this.getArchive().add(asset, name);
      return covarientReturn();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#contains(org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public boolean contains(ArchivePath path)
   {
      return this.getArchive().contains(path);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#contains(java.lang.String)
    */
   @Override
   public boolean contains(final String path) throws IllegalArgumentException
   {
      Validate.notNull(path, "Path must be specified");
      return this.contains(ArchivePaths.create(path));
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#delete(org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public Node delete(ArchivePath path)
   {
      return this.getArchive().delete(path);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#get(org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public Node get(ArchivePath path)
   {
      return this.getArchive().get(path);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#get(java.lang.String)
    */
   @Override
   public Node get(String path) throws IllegalArgumentException
   {
      return this.getArchive().get(path);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#getContent()
    */
   @Override
   public Map<ArchivePath, Node> getContent()
   {
      return this.getArchive().getContent();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#getContent(org.jboss.shrinkwrap.api.Filter)
    */
   @Override
   public Map<ArchivePath, Node> getContent(Filter<ArchivePath> filter)
   {
      return this.getArchive().getContent(filter);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#getName()
    */
   @Override
   public String getName()
   {
      return this.getArchive().getName();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#toString(boolean)
    */
   @Override
   public String toString()
   {
      return this.getArchive().toString();
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#toString(boolean)
    */
   @Override
   public String toString(final boolean verbose)
   {
      return this.getArchive().toString(verbose);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#toString(org.jboss.shrinkwrap.api.formatter.Formatter)
    */
   @Override
   public String toString(final Formatter formatter) throws IllegalArgumentException
   {
      return this.getArchive().toString(formatter);
   }
   
   @Override
   public int hashCode()
   {
      return this.getArchive().hashCode();
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj instanceof ArchiveBase<?>) {
         return this.getArchive().equals(obj);
      }
      
      if (!(obj instanceof ContainerBase))
         return false;
      
      final ContainerBase<?> other = (ContainerBase<?>) obj;
      return this.getArchive().equals(other.getArchive());
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - ManifestContainer ---------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Should be implemented to set the path for Manifest related
    * resources. 
    * 
    * @return Base Path for the ManifestContainer resources
    */
   protected abstract ArchivePath getManifestPath();
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ManifestContainer#setManifest(java.lang.String)
    */
   @Override
   public final T setManifest(String resourceName)
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return setManifest(new ClassLoaderAsset(resourceName));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#setManifest(java.io.File)
    */
   @Override
   public T setManifest(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return setManifest(new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#setManifest(java.net.URL)
    */
   @Override
   public T setManifest(URL resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return setManifest(new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#setManifest(org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T setManifest(Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addAsManifestResource(resource, "MANIFEST.MF");
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(java.lang.String)
    */
   @Override
   public final T addAsManifestResource(String resourceName)
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return addAsManifestResource(new ClassLoaderAsset(resourceName), resourceName);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File)
    */
   @Override
   public T addAsManifestResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addAsManifestResource(new FileAsset(resource), resource.getName());
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addAsManifestResource(String resourceName, String target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsManifestResource(new ClassLoaderAsset(resourceName), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File, java.lang.String)
    */
   @Override
   public T addAsManifestResource(File resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsManifestResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.net.URL, java.lang.String)
    */
   @Override
   public T addAsManifestResource(URL resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsManifestResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T addAsManifestResource(Asset resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsManifestResource(resource, new BasicPath(target));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsManifestResource(String resourceName, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsManifestResource(new ClassLoaderAsset(resourceName), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsManifestResource(File resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsManifestResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.net.URL, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsManifestResource(URL resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsManifestResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsManifestResource(Asset resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      ArchivePath location = new BasicPath(getManifestPath(), target);
      return add(resource, location);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResources(java.lang.Package, java.lang.String[])
    */
   @Override
   public T addAsManifestResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNullAndNoNullValues(resourceNames, "ResourceNames must be specified and can not container null values");
      for(String resourceName : resourceNames)
      {
         addAsManifestResource(resourcePackage, resourceName);
      }
      return covarientReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.Package, java.lang.String)
    */
   @Override
   public T addAsManifestResource(Package resourcePackage, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      
      String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      ArchivePath target = ArchivePaths.create(classloaderResourceName);
      
      return addAsManifestResource(resourcePackage, resourceName, target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.Package, java.lang.String, java.lang.String)
    */
   @Override
   public T addAsManifestResource(Package resourcePackage, String resourceName, String target) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      return addAsManifestResource(resourcePackage, resourceName, ArchivePaths.create(target));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.Package, java.lang.String, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsManifestResource(Package resourcePackage, String resourceName, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      Asset resource = new ClassLoaderAsset(classloaderResourceName);

      return addAsManifestResource(resource, target);
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
      ArchivePath path = new BasicPath("services", serviceInterface.getName());
      return addAsManifestResource(asset, path);
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - ResourceContainer ---------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Should be implemented to set the path for Resource related
    * resources. 
    * 
    * @return Base Path for the ResourceContainer resources
    */
   protected abstract ArchivePath getResourcePath();
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String)
    */
   @Override
   public final T addAsResource(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return addAsResource(new ClassLoaderAsset(resourceName), resourceName);
   }   

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.net.URL)
    */
   @Override
   public final T addAsResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addAsResource(new FileAsset(resource), resource.getName());
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, java.lang.String)
    */
   @Override
   public final T addAsResource(String resourceName, String target) throws IllegalArgumentException 
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsResource(new ClassLoaderAsset(resourceName), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.io.File, java.lang.String)
    */
   @Override
   public T addAsResource(File resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.net.URL, java.lang.String)
    */
   @Override
   public T addAsResource(URL resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T addAsResource(Asset resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsResource(resource, new BasicPath(target));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsResource(String resourceName, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsResource(new ClassLoaderAsset(resourceName), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, org.jboss.shrinkwrap.api.Path, java.lang.ClassLoader)
    */
   @Override
   public T addAsResource(String resourceName, ArchivePath target, ClassLoader classLoader) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(classLoader, "ClassLoader should be specified");
      
      return addAsResource(new ClassLoaderAsset(resourceName, classLoader), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.io.File, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsResource(File resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.net.URL, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsResource(URL resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addAsResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsResource(Asset resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      ArchivePath location = new BasicPath(getResourcePath(), target);
      return add(resource, location);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResources(java.lang.Package, java.lang.String[])
    */
   @Override
   public T addAsResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNullAndNoNullValues(resourceNames, "ResourceNames must be specified and can not container null values");
      for(String resourceName : resourceNames)
      {
         addAsResource(resourcePackage, resourceName);
      }
      return covarientReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.Package, java.lang.String)
    */
   @Override
   public T addAsResource(Package resourcePackage, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      
      String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      ArchivePath target = ArchivePaths.create(classloaderResourceName);
      
      return addAsResource(resourcePackage, resourceName, target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.Package, java.lang.String, java.lang.String)
    */
   @Override
   public T addAsResource(Package resourcePackage, String resourceName, String target) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      return addAsResource(resourcePackage, resourceName, ArchivePaths.create(target));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.Package, java.lang.String, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   public T addAsResource(Package resourcePackage, String resourceName, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resourcePackage, "ResourcePackage must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
      Asset resource = new ClassLoaderAsset(classloaderResourceName);

      return addAsResource(resource, target);
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - ClassContainer ------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Should be implemented to set the path for Class related
    * resources. 
    * 
    * @return Base Path for the ClassContainer resources
    */
   protected abstract ArchivePath getClassesPath();

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ClassContainer#addClass(java.lang.Class)
    */
   @Override
   public T addClass(Class<?> clazz) throws IllegalArgumentException
   {
      Validate.notNull(clazz, "Clazz must be specified");
     
      return addClasses(clazz);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ClassContainer#addClass(java.lang.String)
    */
   /**
    * @see #addClass(String, ClassLoader)
    */
   @Override
   public T addClass(String fullyQualifiedClassName) throws IllegalArgumentException
   {
      Validate.notNullOrEmpty(fullyQualifiedClassName, "Fully-qualified class name must be specified");

      return addClass(
            fullyQualifiedClassName, 
            AccessController.doPrivileged(GetTcclAction.INSTANCE));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ClassContainer#addClass(java.lang.String, java.lang.ClassLoader)
    */
   @Override
   public T addClass(final String fullyQualifiedClassName, final ClassLoader cl) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNullOrEmpty(fullyQualifiedClassName, "Fully-qualified class name must be specified");
      Validate.notNull(cl, "ClassLoader must be specified");
      
      // Obtain the Class
      final Class<?> clazz;
      try
      {
         clazz = Class.forName(fullyQualifiedClassName, false, cl);
      }
      catch (final ClassNotFoundException e)
      {
         throw new IllegalArgumentException("Could not load class of name " + fullyQualifiedClassName + " with "
               + cl, e);
      }

      // Delegate and return
      return this.addClass(clazz);
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ClassContainer#addClasses(java.lang.Class<?>[])
    */
   public T addClasses(Class<?>... classes) throws IllegalArgumentException 
   {
      Validate.notNull(classes, "Classes must be specified");
      
      for(final Class<?> clazz : classes) 
      {
         Asset resource = new ClassAsset(clazz);
         ArchivePath location = new BasicPath(getClassesPath(), AssetUtil.getFullPathForClassResource(clazz));
         add(resource, location);
         
         // Get all inner classes and add them
         addPackages(
               false,
               new Filter<ArchivePath>()
               {
                  /**
                   * path  = /package/MyClass$Test.class <br/>
                   * clazz = /package/MyClass.class <br/>
                   * 
                   *  
                   * @param path The added classes 
                   * @return 
                   */
                  public boolean include(ArchivePath path)
                  {
                     ArchivePath classArchivePath = AssetUtil.getFullPathForClassResource(clazz);
                     String expression = classArchivePath.get().replace(".class", "\\$.*");
                     return path.get().matches(expression);
                  };
               },
               clazz.getPackage()
         );
      }
      return covarientReturn();
   };
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ClassContainer#addPackage(java.lang.Package)
    */
   @Override
   public T addPackage(Package pack) throws IllegalArgumentException
   {
      Validate.notNull(pack, "Pack must be specified");
      
      return addPackage(pack.getName());
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ClassContainer#addPackages(boolean, java.lang.Package[])
    */
   @Override
   public T addPackages(boolean recursive, Package... packages) throws IllegalArgumentException
   {
      Validate.notNull(packages, "Packages must be specified");
      
      return addPackages(recursive, Filters.includeAll(), packages);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackages(boolean, org.jboss.shrinkwrap.api.Filter, java.lang.Package[])
    */
   @Override
   public T addPackages(final boolean recursive, final Filter<ArchivePath> filter, final Package... packages) throws IllegalArgumentException
   {
      Validate.notNull(filter, "Filter must be specified");
      Validate.notNull(packages, "Packages must be specified");
      
      String[] packageNames = new String[packages.length];
      for(int i = 0; i < packages.length; i++)
      {
         packageNames[i] = packages[i] == null ? null:packages[i].getName(); 
      }
      return addPackages(recursive, filter, packageNames);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackage(java.lang.String)
    */
   @Override
   public T addPackage(String pack) throws IllegalArgumentException
   {
      Validate.notNull(pack, "Package must be specified");
      
      return addPackages(false, pack);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackages(boolean, java.lang.String[])
    */
   @Override
   public T addPackages(boolean recursive, String... packages) throws IllegalArgumentException
   {
      Validate.notNullAndNoNullValues(packages, "Pakcages must be specified and can not container null values");
      
      return addPackages(recursive, Filters.includeAll(), packages);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackages(boolean, org.jboss.shrinkwrap.api.Filter, java.lang.String[])
    */
   @Override
   public T addPackages(boolean recursive, final Filter<ArchivePath> filter, String... packageNames) throws IllegalArgumentException
   {
      Validate.notNull(filter, "Filter must be specified");
      Validate.notNull(packageNames, "PackageNames must be specified");

      final ClassLoader classLoader = SecurityActions.getThreadContextClassLoader();
      
      for(String packageName : packageNames) 
      {
         final URLPackageScanner.Callback callback = new URLPackageScanner.Callback()
         {
            @Override
            public void classFound(String className)
            {
               ArchivePath classNamePath = AssetUtil.getFullPathForClassResource(className);
               if (!filter.include(classNamePath))
               {
                  return;
               }
               Asset asset = new ClassLoaderAsset(classNamePath.get().substring(1), classLoader);
               ArchivePath location = new BasicPath(getClassesPath(), classNamePath);
               add(asset, location);
            }
         };
         final URLPackageScanner scanner = packageName == null ? 
                  URLPackageScanner.newInstance(recursive, classLoader, callback) : 
                  URLPackageScanner.newInstance(recursive, classLoader, callback, packageName);
         scanner.scanPackage();
      }
      return covarientReturn();
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - LibraryContainer ----------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Should be implemented to set the path for Library related
    * resources. 
    * 
    * @return Base Path for the LibraryContainer resources
    */
   protected abstract ArchivePath getLibraryPath();
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(org.jboss.shrinkwrap.api.Archive)
    */
   public T addAsLibrary(final Archive<?> archive) throws IllegalArgumentException
   {
      Validate.notNull(archive, "Archive must be specified");
      // Libraries are JARs, so add as ZIP
      return add(archive, getLibraryPath(), ZipExporter.class);
   };

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.lang.String)
    */
   @Override
   public T addAsLibrary(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      return addAsLibrary(new ClassLoaderAsset(resourceName), resourceName);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.io.File)
    */
   @Override
   public T addAsLibrary(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return addAsLibrary(new FileAsset(resource), resource.getName());
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.lang.String, java.lang.String)
    */
   @Override
   public T addAsLibrary(String resourceName, String target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      return addAsLibrary(new ClassLoaderAsset(resourceName), target);
   }
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.io.File, java.lang.String)
    */
   @Override
   public T addAsLibrary(File resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      return addAsLibrary(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.net.URL, java.lang.String)
    */
   @Override
   public T addAsLibrary(URL resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      return addAsLibrary(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T addAsLibrary(Asset resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      return addAsLibrary(resource, new BasicPath(target));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsLibrary(String resourceName, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");
      
      return addAsLibrary(new ClassLoaderAsset(resourceName), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.io.File, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsLibrary(File resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");
      
      return addAsLibrary(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.net.URL, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsLibrary(URL resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");
      
      return addAsLibrary(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addAsLibrary(Asset resource, ArchivePath target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      ArchivePath location = new BasicPath(getLibraryPath(), target);
      return add(resource, location);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(java.lang.String[])
    */
   @Override
   public T addAsLibraries(String... resourceNames) throws IllegalArgumentException
   {
      Validate.notNull(resourceNames, "ResourceNames must be specified");
      for(String resourceName : resourceNames) 
      {
         addAsLibrary(resourceName);
      }
      return covarientReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(java.io.File[])
    */
   @Override
   public T addAsLibraries(File... resources) throws IllegalArgumentException
   {
      Validate.notNull(resources, "Resources must be specified");
      for(File resource : resources) 
      {
         addAsLibrary(resource);
      }
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(org.jboss.shrinkwrap.api.Archive<?>[])
    */
   @Override
   public T addAsLibraries(Archive<?>... archives) throws IllegalArgumentException 
   {
      Validate.notNull(archives, "Archives must be specified");
      for(Archive<?> archive : archives) 
      {
         addAsLibrary(archive);
      }
      return covarientReturn();
   }
   
   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected T covarientReturn() 
   {
      return getActualClass().cast(this);
   }
   
   protected Class<T> getActualClass() 
   {
      return this.actualType;
   }

}

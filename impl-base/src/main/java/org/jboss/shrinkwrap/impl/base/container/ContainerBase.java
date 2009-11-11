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
import java.util.Map;
import java.util.Set;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.impl.base.SpecializedBase;
import org.jboss.shrinkwrap.impl.base.URLPackageScanner;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.asset.ClassAsset;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.asset.FileAsset;
import org.jboss.shrinkwrap.impl.base.asset.UrlAsset;
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
public abstract class ContainerBase<T extends Archive<T>> extends SpecializedBase implements 
   Archive<T>, ManifestContainer<T>, ResourceContainer<T>, ClassContainer<T>, LibraryContainer<T> 
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The backing storage engine.
    */
   private final Archive<?> archive;
   
   /**
    * The exposed archive type. 
    */
   private final Class<T> actualType;
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   protected ContainerBase(Class<T> actualType, Archive<?> archive) 
   {
      Validate.notNull(actualType, "ActualType should be specified");
      Validate.notNull(archive, "Archive should be specified");
      
      this.actualType = actualType;
      this.archive = archive;
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - Archive Delegation --------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T add(Archive<?> archive, Path path)
   {
      this.archive.add(archive, path);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T add(Asset asset, Path target) throws IllegalArgumentException
   {
      archive.add(asset, target);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path, java.lang.String)
    */
   @Override
   public T add(Asset asset, Path path, String name)
   {
      archive.add(asset, path, name);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive)
    */
   @Override
   public T merge(Archive<?> source) throws IllegalArgumentException
   {
      archive.merge(source);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T merge(Archive<?> source, Path path) throws IllegalArgumentException
   {
      archive.merge(source, path);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T add(Asset asset, String name)
   {
      archive.add(asset, name);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#contains(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean contains(Path path)
   {
      return archive.contains(path);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#delete(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean delete(Path path)
   {
      return archive.delete(path);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#get(org.jboss.declarchive.api.Path)
    */
   @Override
   public Asset get(Path path)
   {
      return archive.get(path);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#get(java.lang.String)
    */
   @Override
   public Asset get(String path) throws IllegalArgumentException
   {
      return archive.get(path);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#getContent()
    */
   @Override
   public Map<Path, Asset> getContent()
   {
      return archive.getContent();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#getName()
    */
   @Override
   public String getName()
   {
      return archive.getName();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#toString(boolean)
    */
   @Override
   public String toString(boolean verbose)
   {
      return archive.toString(verbose);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - SpecializedBase -----------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.impl.base.SpecializedBase#getArchive()
    */
   @Override
   protected Archive<?> getArchive()
   {
      return archive;
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
   protected abstract Path getManinfestPath();
   
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
      return addManifestResource(resource, "MANIFEST.FM");
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(java.lang.String)
    */
   @Override
   public final T addManifestResource(String resourceName)
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return addManifestResource(new ClassLoaderAsset(resourceName), resourceName);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File)
    */
   @Override
   public T addManifestResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addManifestResource(new FileAsset(resource), resource.getName());
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addManifestResource(String resourceName, String target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addManifestResource(new ClassLoaderAsset(resourceName), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File, java.lang.String)
    */
   @Override
   public T addManifestResource(File resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addManifestResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.net.URL, java.lang.String)
    */
   @Override
   public T addManifestResource(URL resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addManifestResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T addManifestResource(Asset resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addManifestResource(resource, new BasicPath(target));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addManifestResource(String resourceName, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addManifestResource(new ClassLoaderAsset(resourceName), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addManifestResource(File resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addManifestResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.net.URL, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addManifestResource(URL resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addManifestResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addManifestResource(Asset resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      Path location = new BasicPath(getManinfestPath(), target);
      return add(resource, location);
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
   protected abstract Path getResourcePath();
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String)
    */
   @Override
   public final T addResource(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return addResource(new ClassLoaderAsset(resourceName), resourceName);
   }   

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.net.URL)
    */
   @Override
   public final T addResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addResource(new FileAsset(resource), resource.getName());
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, java.lang.String)
    */
   @Override
   public final T addResource(String target, String resourceName) throws IllegalArgumentException 
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resourceName, "ResourceName should be specified");
      
      return addResource(new ClassLoaderAsset(resourceName), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.io.File, java.lang.String)
    */
   @Override
   public T addResource(File resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.net.URL, java.lang.String)
    */
   @Override
   public T addResource(URL resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T addResource(Asset resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addResource(resource, new BasicPath(target));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addResource(String resourceName, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addResource(new ClassLoaderAsset(resourceName), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, org.jboss.shrinkwrap.api.Path, java.lang.ClassLoader)
    */
   @Override
   public T addResource(String resourceName, Path target, ClassLoader classLoader) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(classLoader, "ClassLoader should be specified");
      
      return addResource(new ClassLoaderAsset(resourceName, classLoader), target);
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.io.File, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addResource(File resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addResource(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.net.URL, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addResource(URL resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      return addResource(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addResource(Asset resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      Validate.notNull(target, "Target should be specified");
      
      Path location = new BasicPath(getResourcePath(), target);
      return add(resource, location);
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
   protected abstract Path getClassesPath();

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
    * @see org.jboss.declarchive.api.container.ClassContainer#addClasses(java.lang.Class<?>[])
    */
   public T addClasses(Class<?>... classes) throws IllegalArgumentException 
   {
      Validate.notNull(classes, "Classes must be specified");
      
      for(Class<?> clazz : classes) 
      {
         Asset resource = new ClassAsset(clazz);
         Path location = new BasicPath(getClassesPath(), AssetUtil.getFullPathForClassResource(clazz));
         add(resource, location);
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
      
      return addPackages(false, pack);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ClassContainer#addPackages(boolean, java.lang.Package[])
    */
   @Override
   public T addPackages(boolean recursive, Package... packages) throws IllegalArgumentException
   {
      Validate.notNull(packages, "Packages must be specified");
      
      for(Package pack : packages) 
      {
         URLPackageScanner scanner = new URLPackageScanner(
               pack, recursive, Thread.currentThread().getContextClassLoader());
         Set<Class<?>> classes = scanner.getClasses(); 
         for(Class<?> clazz : classes) 
         {
            Asset asset = new ClassAsset(clazz);
            Path location = new BasicPath(getClassesPath(), AssetUtil.getFullPathForClassResource(clazz));
            add(asset, location);
         }
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
   protected abstract Path getLibraryPath();
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.LibraryContainer#addLibrary(org.jboss.declarchive.api.Archive)
    */
   public T addLibrary(Archive<?> archive) throws IllegalArgumentException 
   {
      Validate.notNull(archive, "Archive must be specified");
      return add(archive, getLibraryPath());
   };

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.LibraryContainer#addLibrary(java.lang.String)
    */
   @Override
   public T addLibrary(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      return addLibrary(new ClassLoaderAsset(resourceName), resourceName);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.io.File)
    */
   @Override
   public T addLibrary(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return addLibrary(new FileAsset(resource), resource.getName());
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String, java.lang.String)
    */
   @Override
   public T addLibrary(String resourceName, String target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");

      return addLibrary(new ClassLoaderAsset(resourceName), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.io.File, java.lang.String)
    */
   @Override
   public T addLibrary(File resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      return addLibrary(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.net.URL, java.lang.String)
    */
   @Override
   public T addLibrary(URL resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      return addLibrary(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Asset, java.lang.String)
    */
   @Override
   public T addLibrary(Asset resource, String target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      return addLibrary(resource, new BasicPath(target));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addLibrary(String resourceName, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      Validate.notNull(target, "Target must be specified");
      
      return addLibrary(new ClassLoaderAsset(resourceName), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.io.File, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addLibrary(File resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");
      
      return addLibrary(new FileAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.net.URL, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addLibrary(URL resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");
      
      return addLibrary(new UrlAsset(resource), target);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T addLibrary(Asset resource, Path target) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      Validate.notNull(target, "Target must be specified");

      Path location = new BasicPath(getLibraryPath(), target);
      return add(resource, location);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(java.lang.String[])
    */
   @Override
   public T addLibraries(String... resourceNames) throws IllegalArgumentException
   {
      Validate.notNull(resourceNames, "ResourceNames must be specified");
      for(String resourceName : resourceNames) 
      {
         addLibrary(resourceName);
      }
      return covarientReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(java.io.File[])
    */
   @Override
   public T addLibraries(File... resources) throws IllegalArgumentException
   {
      Validate.notNull(resources, "Resources must be specified");
      for(File resource : resources) 
      {
         addLibrary(resource);
      }
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(org.jboss.shrinkwrap.api.Archive<?>[])
    */
   @Override
   public T addLibraries(Archive<?>... archives) throws IllegalArgumentException 
   {
      Validate.notNull(archives, "Archives must be specified");
      for(Archive<?> archive : archives) 
      {
         addLibrary(archive);
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

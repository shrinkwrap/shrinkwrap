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
import java.util.Map;
import java.util.Set;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.AssetNotFoundException;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
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
public abstract class ContainerBase<T extends Archive<T>> implements 
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
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Archive)
    */
   @Override
   public T add(Path path, Archive<?> archive)
   {
      this.archive.add(path, archive);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Asset)
    */
   @Override
   public T add(Path target, Asset asset) throws IllegalArgumentException
   {
      archive.add(target, asset);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public T add(Path path, String name, Asset asset)
   {
      archive.add(path, name, asset);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#merge(org.jboss.declarchive.api.Archive)
    */
   @Override
   public T merge(Archive<?> source) throws IllegalArgumentException
   {
      archive.merge(source);
      return covarientReturn();
   }
   
   @Override
   public T merge(Path path, Archive<?> source) throws IllegalArgumentException
   {
      archive.merge(path, source);
      return covarientReturn();
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#add(java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public T add(String name, Asset asset)
   {
      archive.add(name, asset);
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
   public Asset get(String path) throws AssetNotFoundException, IllegalArgumentException
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
      return addManifestResource("MANIFEST.FM", resource);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(java.lang.String)
    */
   @Override
   public final T addManifestResource(String resourceName)
   {
      Validate.notNull(resourceName, "ResourceName should be specified");
      return addManifestResource(resourceName, new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File)
    */
   @Override
   public T addManifestResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addManifestResource(resource.getName(), new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.net.URL)
    */
   @Override
   public T addManifestResource(URL resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addManifestResource(AssetUtil.getFullPathForURLResource(resource), new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addManifestResource(String target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resourceName, "ResourceName should be specified");
      
      return addManifestResource(target, new ClassLoaderAsset(resourceName));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, java.io.File)
    */
   @Override
   public T addManifestResource(String target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addManifestResource(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, java.net.URL)
    */
   @Override
   public T addManifestResource(String target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addManifestResource(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addManifestResource(String target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addManifestResource(new BasicPath(target), resource);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public T addManifestResource(Path target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resourceName, "ResourceName should be specified");
      
      return addManifestResource(target, new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Path, java.io.File)
    */
   @Override
   public T addManifestResource(Path target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addManifestResource(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Path, java.net.URL)
    */
   @Override
   public T addManifestResource(Path target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addManifestResource(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Path, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addManifestResource(Path target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      Path location = new BasicPath(getManinfestPath(), target);
      return add(location, resource);
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
      return addResource(resourceName, new ClassLoaderAsset(resourceName));
   }   

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.net.URL)
    */
   @Override
   public final T addResource(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addResource(resource.getName(), new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.net.URL)
    */
   @Override
   public final T addResource(URL resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource should be specified");
      return addResource(AssetUtil.getFullPathForURLResource(resource), new UrlAsset(resource));
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String, java.lang.String)
    */
   @Override
   public final T addResource(String target, String resourceName) throws IllegalArgumentException 
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resourceName, "ResourceName should be specified");
      
      return addResource(target, new ClassLoaderAsset(resourceName));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, java.io.File)
    */
   @Override
   public T addResource(String target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addResource(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, java.net.URL)
    */
   @Override
   public T addResource(String target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addResource(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addResource(String target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addResource(new BasicPath(target), resource);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public T addResource(Path target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resourceName, "ResourceName should be specified");
      
      return addResource(target, new ClassLoaderAsset(resourceName));
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(org.jboss.declarchive.api.Path, java.lang.String, java.lang.ClassLoader)
    */
   @Override
   public T addResource(Path target, String resourceName, ClassLoader classLoader) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resourceName, "ResourceName should be specified");
      Validate.notNull(classLoader, "ClassLoader should be specified");
      
      return addResource(target, new ClassLoaderAsset(resourceName, classLoader));
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Path, java.io.File)
    */
   @Override
   public T addResource(Path target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addResource(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(org.jboss.declarchive.api.Path, java.net.URL)
    */
   @Override
   public T addResource(Path target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      return addResource(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Path, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addResource(Path target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target should be specified");
      Validate.notNull(resource, "Resource should be specified");
      
      Path location = new BasicPath(getResourcePath(), target);
      return add(location, resource);
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
         add(location, resource);
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
            add(location, asset);
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
      return add(getLibraryPath(), archive);
   };

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.LibraryContainer#addLibrary(java.lang.String)
    */
   @Override
   public T addLibrary(String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(resourceName, "ResourceName must be specified");
      return addLibrary(resourceName, new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.io.File)
    */
   @Override
   public T addLibrary(File resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return addLibrary(resource.getName(), new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.net.URL)
    */
   @Override
   public T addLibrary(URL resource) throws IllegalArgumentException
   {
      Validate.notNull(resource, "Resource must be specified");
      return addLibrary(AssetUtil.getFullPathForURLResource(resource), new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String, java.lang.String)
    */
   @Override
   public T addLibrary(String target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");

      return addLibrary(target, new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String, java.io.File)
    */
   @Override
   public T addLibrary(String target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addLibrary(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String, java.net.URL)
    */
   @Override
   public T addLibrary(String target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addLibrary(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addLibrary(String target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      return addLibrary(new BasicPath(target), resource);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.container.LibraryContainer#addLibrary(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public T addLibrary(Path target, String resourceName) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resourceName, "ResourceName must be specified");
      
      return addLibrary(target, new ClassLoaderAsset(resourceName));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Path, java.io.File)
    */
   @Override
   public T addLibrary(Path target, File resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");
      
      return addLibrary(target, new FileAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Path, java.net.URL)
    */
   @Override
   public T addLibrary(Path target, URL resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");
      
      return addLibrary(target, new UrlAsset(resource));
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Path, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T addLibrary(Path target, Asset resource) throws IllegalArgumentException
   {
      Validate.notNull(target, "Target must be specified");
      Validate.notNull(resource, "Resource must be specified");

      Path location = new BasicPath(getLibraryPath(), target);
      return add(location, resource);
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

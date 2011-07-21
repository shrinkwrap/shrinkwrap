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
package org.jboss.shrinkwrap.api.container;

import java.io.File;
import java.net.URL;
import java.util.Collection;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Defines the contract for a component capable of storing 
 * Libraries.
 * <br/><br/>
 * The actual path to the Library resources within the {@link Archive} 
 * is up to the implementations/specifications.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface LibraryContainer<T extends Archive<T>>
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds the resource as a library to the container, returning the container itself.
    * <br/>
    * The resource will be placed into the Container Library path under the same context 
    * from which it was retrieved.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(String resourceName) throws IllegalArgumentException;

   /**
    * Adds the {@link File} as a library to the container, returning the container itself.
    * <br/>
    * The {@link File} will be placed into the Container Library path under {@link File#getName()}. 
    * 
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(File resource) throws IllegalArgumentException;

   /**
    * Adds the resource as a library to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(String resourceName, String target) throws IllegalArgumentException;

   /**
    * Adds the {@link File} as a library to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(File resource, String target) throws IllegalArgumentException;

   /**
    * Adds the {@link URL} as a library to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(URL resource, String target) throws IllegalArgumentException;

   /**
    * Adds the {@link Asset} as a library to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(Asset resource, String target) throws IllegalArgumentException;

   /**
    * Adds the resource as a library to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(String resourceName, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the {@link File} as a library to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(File resource, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the {@link URL} as a library to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(URL resource, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the {@link Asset} as a library to the container, returning the container itself.
    * 
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s library path.
    * @param resource {@link Asset} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    */
   T addAsLibrary(Asset resource, ArchivePath target) throws IllegalArgumentException;

   /**
    * Add another {@link Archive} to this {@link Archive} as a library to the container, returning the container itself.
    * <br/>
    * The {@link Archive} will be placed into the Container Library path under {@link Archive#getName()}.
    *  
    * @param archive {@link Archive} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link Archive} is null
    * @see #addAsLibrary(Asset, ArchivePath)
    */
   T addAsLibrary(Archive<?> archive) throws IllegalArgumentException;

   /**
    * Add multiple resources to this {@link Archive} as libraries to the container, returning the container itself.
    * <br/>
    * The resources will be placed into the Container Library path under the same context 
    * from which they were retrieved.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    *  
    * @param resourceNames resources to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceNames are null or empty
    * @see #addAsLibrary(String)
    */
   T addAsLibraries(String... resourceNames) throws IllegalArgumentException;

   /**
    * Add multiple {@link File} to this {@link Archive} as libraries to the container, returning the container itself.
    * <br/>
    * The {@link File}s will be placed into the Container Library path under {@link File#getName()}.
    *  
    * @param resources {@link File} resources to add
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link File} resources are null or empty
    * @see #addAsLibrary(File)
    */
   T addAsLibraries(File... resources) throws IllegalArgumentException;

   /**
    * Add multiple {@link Archive}s to this {@link Archive} as libraries to the container, returning the container itself.
    * <br/>
    * The {@link Archive}s will be placed into the Container Library path under {@link Archive#getName()}.
    *  
    * @param archives {@link Archive} resources to add
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link Archive} resources are null
    * @see #addAsLibrary(Archive)
    */
   T addAsLibraries(Archive<?>... archives) throws IllegalArgumentException;
   
   /**
    * Add multiple {@link Archive}s to this {@link Archive} as libraries to the container, returning the container itself.
    * <br/>
    * The {@link Archive}s will be placed into the Container Library path under {@link Archive#getName()}.
    *  
    * @param archives {@link Archive} resources to add
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link Collection} of archives is null
    * @see #addAsLibrary(Archive)
    */
   T addAsLibraries(Collection<? extends Archive<?>> archives) throws IllegalArgumentException;

   /**
    * Add multiple {@link Archive}s to this {@link Archive} as libraries to the container, returning the container itself.
    * <br/>
    * The {@link Archive}s will be placed into the Container Library path under {@link Archive#getName()}.
    *
    * @param archives {@link Archive} resources to add
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link Collection} of archives is null
    * @see #addAsLibrary(Archive)
    */
   T addAsLibraries(Archive<?>[]... archives) throws IllegalArgumentException;

}

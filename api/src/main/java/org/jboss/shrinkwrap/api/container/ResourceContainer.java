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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;

/**
 * ResourceContainer
 * 
 * Defines the contract for a component capable of storing 
 * a series of {@link ClassLoader} resources
 * <br/><br/>
 * The actual path to the Resources within the {@link Archive} 
 * is up to the implementations/specifications.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface ResourceContainer<T extends Archive<T>>
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds the resource as a resource to the container, returning the container itself.
    * <br/>
    * The resource will be placed into the Container Resource path under the same context 
    * from which it was retrieved.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException If the resourceName is null
    * @see #addResource(Asset, Path)
    */
   T addResource(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a resource to the container, returning the container itself.
    * <br/>
    * The {@link File} will be placed into the Container Library path under {@link File#getName()}. 
    * 
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException If the resource is null
    * @see #addResource(Asset, Path)
    */
   T addResource(File resource) throws IllegalArgumentException;

   /**
    * Adds the resource as a resource to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addResource(Asset, Path)
    */
   T addResource(String resourceName, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addResource(Asset, Path)
    */
   T addResource(File resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link URL} as a resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addResource(Asset, Path)
    */
   T addResource(URL resource, String target) throws IllegalArgumentException;

   /**
    * Adds the {@link Asset} as a resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addResource(Asset, Path)
    */
   T addResource(Asset resource, String target) throws IllegalArgumentException;

   /**
    * Adds the resource with the specified name to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param target The target within the archive into which we'll place the resource
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException If the target is null
    * @throws IllegalArgumentException If the resourceName is null
    */
   T addResource(String resourceName, Path target) throws IllegalArgumentException;
   
   /**
    * Adds the resource as a resource to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addResource(Asset, Path)
    */
   T addResource(String resourceName, Path target, ClassLoader classLoader) throws IllegalArgumentException;

   /**
    * Adds the {@link File} as a resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addResource(Asset, Path)
    */
   T addResource(File resource, Path target) throws IllegalArgumentException;

   /**
    * Adds the {@link URL} as a resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addResource(Asset, Path)
    */
   T addResource(URL resource, Path target) throws IllegalArgumentException;

   /**
    * Adds the {@link Asset} as a resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s resource path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    */
   T addResource(Asset resource, Path target) throws IllegalArgumentException;
}

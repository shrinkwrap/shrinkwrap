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
 * WebContainer
 *
 * Defines the contract for a component capable of storing 
 * Web related resources.
 * <br/><br/>
 * The actual path to the Web resources within the Archive 
 * is up to the implementations/specifications.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public interface WebContainer<T extends Archive<T>>
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds the resource as web.xml to the container, returning the container itself.
    * <br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #setWebXML(Asset)
    */
   T setWebXML(String resourceName)  throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as web.xml to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #setWebXML(Asset)
    */
   T setWebXML(File resource)  throws IllegalArgumentException;
   
   /**
    * Adds the {@link URL} as web.xml to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #setWebXML(Asset)
    */
   T setWebXML(URL resource)  throws IllegalArgumentException;
   
   /**
    * Adds the {@link Asset} as web.xml to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #addWebResource(Asset, Path)
    */
   T setWebXML(Asset resource)  throws IllegalArgumentException;
   
   /**
    * Adds the resource as a Web resource to the container, returning the container itself.
    * <br/>
    * The resource will be placed into the Container Web path under the same context 
    * from which it was retrieved.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a Web resource to the container, returning the container itself.
    * <br/>
    * The {@link File} will be placed into the Container Web path under {@link File#getName()}. 
    * 
    * @param resource resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link File} resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(File resource) throws IllegalArgumentException;
   
   /**
    * Adds the resource as a Web resource to the container, returning the container itself.
    * <br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(String resourceName, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(File resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link URL} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(URL resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link Asset} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(Asset resource, String target) throws IllegalArgumentException;

   /**
    * Adds the resource as a Web resource to the container, returning the container itself.
    * <br/>
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(String resourceName, Path target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(File resource, Path target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link URL} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addWebResource(Asset, Path)
    */
   T addWebResource(URL resource, Path target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link Asset} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    */
   T addWebResource(Asset resource, Path target) throws IllegalArgumentException;
}

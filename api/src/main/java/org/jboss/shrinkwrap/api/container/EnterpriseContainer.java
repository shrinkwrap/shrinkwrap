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
import org.jboss.shrinkwrap.api.ArchivePath;

/**
 * EnterpriseContainer
 * 
 * Defines the contract for a component capable of storing 
 * Enterprise related resources.
 * <br/><br/>
 * The actual path to the Enterprise resources within the {@link Archive} 
 * is up to the implementations/specifications.
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface EnterpriseContainer<T extends Archive<T>>
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds a resource to this {@link Archive} as application.xml.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation. 
    * <br/>
    * For instance a resourceName of "test/example.xml" could be placed in 
    * "/META-INF/application.xml"
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add 
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #setApplicationXML(Asset)
    */
   T setApplicationXML(String resourceName) throws IllegalArgumentException;

   /**
    * Adds a {@link File} to this {@link Archive} as application.xml.
    * <br/><br/>
    * For instance a {@link File} "test/example.xml" could be placed in 
    * "/META-INF/application.xml"
    * 
    * @param resource {@link File} resource to add 
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #setApplicationXML(Asset)
    */
   T setApplicationXML(File resource) throws IllegalArgumentException;
   
   /**
    * Adds a {@link URL} to this {@link Archive} as application.xml.
    * <br/><br/>
    * For instance a {@link URL} "http://my.com/example.xml" could be placed in 
    * "/META-INF/application.xml"
    * 
    * @param resource {@link URL} resource to add 
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #setApplicationXML(Asset)
    */
   T setApplicationXML(URL resource) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive} as application.xml.
    * 
    * @param resource {@link Asset} resource to add 
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    */
   T setApplicationXML(Asset resource) throws IllegalArgumentException;
   
   /**
    * Adds a resource to this {@link Archive}s application context.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation.
    * <br/>
    * For instance a resourceName of "test/example.xml" could be placed in 
    * "/META-INF/test/example.xml"
    *   
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds a {@link File} to this {@link Archive}s application context.
    * <br/><br/>
    * For instance a {@link File} of "test/example.xml" could be placed in 
    * "/META-INF/test/example.xml"
    * 
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(File resource) throws IllegalArgumentException;
   
   
   /**
    * Adds a resource to this {@link Archive}s application context.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation.
    * <br/>
    * For instance a resourceName of "test/example.xml" and a 
    * target of "example/myexample.xml" could be placed in 
    * "/META-INF/example/myexample.xml"
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(String resourceName, String target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link File} to this {@link Archive}s application context.
    * <br/><br/>
    * For instance a {@link File} of "test/example.xml" and a 
    * target of "example/myexample.xml" could be placed in 
    * "/META-INF/example/myexample.xml"
    * 
    * @param resource {@link File} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(File resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link URL} to this {@link Archive}s application context.
    * <br/><br/>
    * <br/>
    * For instance a {@link URL} of "http://my.com/example.xml" and a 
    * target of "example/myexample.xml" could be placed in 
    * "/META-INF/example/myexample.xml"
    * 
    * @param resource {@link URL} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(URL resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s application context.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(Asset resource, String target) throws IllegalArgumentException;

   /**
    * Adds a resource to this {@link Archive}s application context.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation.
    * <br/>
    * For instance a resourceName of "test/example.xml" and a 
    * target of "example/myexample.xml" could be placed in 
    * "/META-INF/example/myexample.xml"
    *   
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(String resourceName, ArchivePath target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link File} to this {@link Archive}s application context.
    * <br/><br/>
    * For instance a {@link File} of "test/example.xml" and a 
    * target of "example/myexample.xml" could be placed in 
    * "/META-INF/example/myexample.xml"
    * 
    * @param resource {@link File} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(File resource, ArchivePath target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link URL} to this {@link Archive}s application context.
    * <br/><br/>
    * For instance a {@link File} of "test/example.xml" and a 
    * target of "example/myexample.xml" could be placed in 
    * "/META-INF/example/myexample.xml"
    * 
    * @param resource {@link URL} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addApplicationResource(Asset, ArchivePath)
    */
   T addApplicationResource(URL resource, ArchivePath target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s application context.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    */
   T addApplicationResource(Asset resource, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the resources inside the package as multiple resources to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resources
    * @param resourceNames The names of the resources inside resoucePackage
    * @return This virtual archive
    * @throws IllegalArgumentException if resourcePackage is null
    * @throws IllegalArgumentException if no resourceNames are specified or containing null 
    */
   T addApplicationResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException;
   
   /**
    * Adds the resource as a resource to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resource
    * @param resourceName The name of the resource inside resoucePackage
    * @return This virtual archive
    * @throws IllegalArgumentException if resourcePackage is null
    * @throws IllegalArgumentException if resourceName is null 
    */
   T addApplicationResource(Package resourcePackage, String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds the resource as a resource to a specific path inside the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resource
    * @param resourceName The name of the resource inside resoucePackage
    * @param target The target location inside the container
    * @return This virtual archive
    * @throws IllegalArgumentException if resourcePackage is null
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null 
    */
   T addApplicationResource(Package resourcePackage, String resourceName, String target) throws IllegalArgumentException;

   /**
    * Adds the resource as a resource to a specific path inside the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resource
    * @param resourceName The name of the resource inside resoucePackage
    * @param target The target location inside the container
    * @return This virtual archive
    * @throws IllegalArgumentException if resourcePackage is null
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if target is null 
    */
   T addApplicationResource(Package resourcePackage, String resourceName, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds a archive to this {@link Archive}s module context.
    * <br/><br/>
    * The {@link Archive} name is used as path.
    *  
    * @param archive The archive to use
    * @return This virtual archive
    * @throws IllegalArgumentException if archive is null
    */
   T addModule(Archive<?> archive) throws IllegalArgumentException;
   
   /**
    * Adds a resource to this {@link Archive}s module context.
    * <br/>
    * The resource name is used as path.
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds a {@link File} to this {@link Archive}s module context.
    * <br/>
    * The {@link File} name is used as path.
    *  
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(File resource) throws IllegalArgumentException;

   /**
    * Adds a resource to this {@link Archive}s module context.
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(String resourceName, String targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link File} to this {@link Archive}s module context.
    * 
    * @param resource {@link File} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(File resource, String targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link URL} to this {@link Archive}s module context.
    * 
    * @param resource {@link URL} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(URL resource, String targetPath) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s module context.
    * 
    * @param resource {@link Asset} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(Asset resource, String targetPath) throws IllegalArgumentException;

   /**
    * Adds a resource to this {@link Archive}s module context.
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(String resourceName, ArchivePath targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link File} to this {@link Archive}s module context.
    * 
    * @param resource {@link File} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(File resource, ArchivePath targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link URL} to this {@link Archive}s module context.
    * 
    * @param resource {@link URL} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addModule(Asset, ArchivePath)
    */
   T addModule(URL resource, ArchivePath targetPath) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s module context.
    * 
    * @param resource {@link URL} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if targetPath is null
    * @throws IllegalArgumentException if resource is null
    */
   T addModule(Asset resource, ArchivePath targetPath) throws IllegalArgumentException;
}

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
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
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
    * Adds a resource inside the package to this {@link Archive} as application.xml.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation.
    * 
    * @param resourcePackage The package of the resources
    * @param resourceName The name of the resource inside resourcePackage
    * @return This virtual archive
    * @throws IllegalArgumentException if resourcePackage is null
    * @throws IllegalArgumentException if resourceName is null
    * @see #setApplicationXML(String) 
    */
   T setApplicationXML(Package resourcePackage, String resourceName) throws IllegalArgumentException;
   
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
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds a {@link File} to this {@link Archive}s application context.
    * <br/><br/>
    * For instance a {@link File} of "test/example.xml" could be placed in 
    * "/META-INF/test/example.xml"
    * 
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(File resource) throws IllegalArgumentException;
   
   
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
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(String resourceName, String target) throws IllegalArgumentException;
   
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
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(File resource, String target) throws IllegalArgumentException;
   
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
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(URL resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s application context.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(Asset resource, String target) throws IllegalArgumentException;

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
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(String resourceName, ArchivePath target) throws IllegalArgumentException;
   
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
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(File resource, ArchivePath target) throws IllegalArgumentException;
   
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
    * @see #addAsApplicationResource(Asset, ArchivePath)
    */
   T addAsApplicationResource(URL resource, ArchivePath target) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s application context.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target relative to application path within the archive into which we'll place the resource
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    */
   T addAsApplicationResource(Asset resource, ArchivePath target) throws IllegalArgumentException;

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
   T addAsApplicationResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException;
   
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
   T addAsApplicationResource(Package resourcePackage, String resourceName) throws IllegalArgumentException;
   
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
   T addAsApplicationResource(Package resourcePackage, String resourceName, String target) throws IllegalArgumentException;

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
   T addAsApplicationResource(Package resourcePackage, String resourceName, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds a archive to this {@link Archive}s module context.
    * <br/><br/>
    * The {@link Archive} name is used as path.
    *  
    * @param archive The archive to use
    * @return This virtual archive
    * @throws IllegalArgumentException if archive is null
    */
   T addAsModule(Archive<?> archive) throws IllegalArgumentException;
   
   /**
    * Adds the specified archives to this {@link Archive}s module context.
    * <br/><br/>
    * The {@link Archive} names are used as paths.
    *  
    * @param archives The archives to use
    * @return This virtual archive
    * @throws IllegalArgumentException if not archives are specified
    */
   T addAsModules(Archive<?>... archives) throws IllegalArgumentException;
   
   /**
    * Adds a resource to this {@link Archive}s module context.
    * <br/>
    * The resource name is used as path.
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds the specified resources to this {@link Archive}s module context.
    * <br/>
    * The resource names are used as paths.
    * 
    * @param resourceNames Names of the {@link ClassLoader} resources to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceNames are not specified
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModules(String... resourceNames) throws IllegalArgumentException;
   
   /**
    * Adds a {@link File} to this {@link Archive}s module context.
    * <br/>
    * The {@link File} name is used as path.
    *  
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(File resource) throws IllegalArgumentException;
   
   /**
    * Adds the specified {@link File}s to this {@link Archive}s module context.
    * <br/>
    * The {@link File} names are used as paths.
    *  
    * @param resources {@link File} resources to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resources are not specified
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModules(File... resources) throws IllegalArgumentException;

   /**
    * Adds a resource to this {@link Archive}s module context.
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(String resourceName, String targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link File} to this {@link Archive}s module context.
    * 
    * @param resource {@link File} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(File resource, String targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link URL} to this {@link Archive}s module context.
    * 
    * @param resource {@link URL} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(URL resource, String targetPath) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s module context.
    * 
    * @param resource {@link Asset} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(Asset resource, String targetPath) throws IllegalArgumentException;

   /**
    * Adds a resource to this {@link Archive}s module context.
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(String resourceName, ArchivePath targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link File} to this {@link Archive}s module context.
    * 
    * @param resource {@link File} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(File resource, ArchivePath targetPath) throws IllegalArgumentException;

   /**
    * Adds a {@link URL} to this {@link Archive}s module context.
    * 
    * @param resource {@link URL} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if targetPath is null
    * @see #addAsModule(Asset, ArchivePath)
    */
   T addAsModule(URL resource, ArchivePath targetPath) throws IllegalArgumentException;
   
   /**
    * Adds a {@link Asset} to this {@link Archive}s module context.
    * 
    * @param resource {@link URL} resource to add
    * @param targetPath The target path within the archive in which to add the resource, relative to the {@link Archive}s module path.
    * @return This virtual archive
    * @throws IllegalArgumentException if targetPath is null
    * @throws IllegalArgumentException if resource is null
    */
   T addAsModule(Asset resource, ArchivePath targetPath) throws IllegalArgumentException;
}

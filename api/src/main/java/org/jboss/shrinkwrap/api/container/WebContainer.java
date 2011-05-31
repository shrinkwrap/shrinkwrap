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
 * web-related resources.
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
    * @see #   addAsWebResource(Asset, ArchivePath)
    */
   T setWebXML(Asset resource)  throws IllegalArgumentException;

   /**
    * Adds the resource inside the package as web.xml to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation. 
    * 
    * @param resourcePackage The package of the resources
    * @param resourceName The name of the resources inside resourcePackage
    * @return This virtual archive
    * @throws IllegalArgumentException if resourcePackage is null
    * @throws IllegalArgumentException if resourceName is null 
    */
   T setWebXML(Package resourcePackage, String resourceName) throws IllegalArgumentException;
   
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
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a Web resource to the container, returning the container itself.
    * <br/>
    * The {@link File} will be placed into the Container Web path under {@link File#getName()}. 
    * 
    * @param resource resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link File} resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(File resource) throws IllegalArgumentException;
   
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
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(String resourceName, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(File resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link URL} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(URL resource, String target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link Asset} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(Asset resource, String target) throws IllegalArgumentException;

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
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(String resourceName, ArchivePath target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(File resource, ArchivePath target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link URL} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    * @see #addAsWebResource(Asset, ArchivePath)
    */
   T addAsWebResource(URL resource, ArchivePath target) throws IllegalArgumentException;
   
   /**
    * Adds the {@link Asset} as a Web resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s web path.
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @throws IllegalArgumentException if target is null
    */
   T addAsWebResource(Asset resource, ArchivePath target) throws IllegalArgumentException;

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
   T addAsWebResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException;
   
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
   T addAsWebResource(Package resourcePackage, String resourceName) throws IllegalArgumentException;
   
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
   T addAsWebResource(Package resourcePackage, String resourceName, String target) throws IllegalArgumentException;

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
   T addAsWebResource(Package resourcePackage, String resourceName, ArchivePath target) throws IllegalArgumentException;   
   
   /**
    * Adds the resource as a WEB-INF resource to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @return This {@link Archive}
    * @throws IllegalArgumentException if resourceName is not specified
    */
   T addAsWebInfResource(String resourceName) throws IllegalArgumentException;

   /**
    * Adds the {@link File} as a WEB-INF resource to the container, returning the container itself.
    * <br/>
    * The {@link File} will be placed into the Container WEB-INF path under {@link File#getName()}. 
    * 
    * @param resource Resource to add
    * @return This {@link Archive}
    * @throws IllegalArgumentException if {@link File} resource is null
    */
   T addAsWebInfResource(File resource) throws IllegalArgumentException;

   /**
    * Adds the resource as a WEB-INF resource to the container, returning the container itself.
    * <br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException if resourceName or target is not specified
    */
   T addAsWebInfResource(String resourceName, String target) throws IllegalArgumentException;

   /**
    * Adds the {@link File} as a WEB-INF resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the resource or target is not specified
    */
   T addAsWebInfResource(File resource, String target) throws IllegalArgumentException;

   /**
    * Adds the {@link URL} as a WEB-INF resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the resource or target is not specified
    */
   T addAsWebInfResource(URL resource, String target) throws IllegalArgumentException;

   /**
    * Adds the {@link Asset} as a WEB-INF resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the resource or target is not specified
    */
   T addAsWebInfResource(Asset resource, String target) throws IllegalArgumentException;

   /**
    * Adds the resource as a WEB-INF resource to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the resource or target is not specified
    */
   T addAsWebInfResource(String resourceName, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the {@link File} as a WEB-INF resource to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the resource or target is not specified
    */
   T addAsWebInfResource(File resource, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the {@link URL} as a WEB-INF resource to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the resource or target is not specified
    */
   T addAsWebInfResource(URL resource, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the {@link Asset} as a WEB-INF resource to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @param target The target path within the archive in which to add the resource, relative to the {@link Archive}s WEB-INF path.
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the resource or target is not specified
    */
   T addAsWebInfResource(Asset resource, ArchivePath target) throws IllegalArgumentException;

   /**
    * Adds the resources inside the package as multiple WEB-INF resources to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resources
    * @param resourceNames The names of the resources inside resourcePackage
    * @return This {@link Archive}
    * @throws IllegalArgumentException If resourcePackage is null, or if no resourceNames are specified or containing null 
    */
   T addAsWebInfResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException;

   /**
    * Adds the resource as a WEB-INF resource to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resource
    * @param resourceName The name of the resource inside resourcePackage
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the package or resource name is not specified 
    */
   T addAsWebInfResource(Package resourcePackage, String resourceName) throws IllegalArgumentException;

   /**
    * Adds the resource as a WEB-INF resource to a specific path inside the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resource
    * @param resourceName The name of the resource inside resoucePackage
    * @param target The target location inside the container
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the package, resource name, or target is not specified 
    */
   T addAsWebInfResource(Package resourcePackage, String resourceName, String target) throws IllegalArgumentException;

   /**
    * Adds the resource as a WEB-INF resource to a specific path inside the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourcePackage The package of the resource
    * @param resourceName The name of the resource inside resoucePackage
    * @param target The target location inside the container
    * @return This {@link Archive}
    * @throws IllegalArgumentException If the package, resource name, or target is not specified 
    */
   T addAsWebInfResource(Package resourcePackage, String resourceName, ArchivePath target)
         throws IllegalArgumentException;
}

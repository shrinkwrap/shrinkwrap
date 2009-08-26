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
package org.jboss.declarchive.api.container;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Path;

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
    * Adds a resource to this {@link Archive}s as application.xml.
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
    */
   T setApplicationXML(String resourceName) throws IllegalArgumentException;
   
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
    */
   T addApplicationResource(String resourceName) throws IllegalArgumentException;
   
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
    * @param newName New name of the resource in the container
    * @return This virtual archive
    * @throws IllegalArgumentException if target is null
    * @throws IllegalArgumentException if resourceName is null
    */
   T addApplicationResource(Path target, String resourceName) throws IllegalArgumentException;

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
    * <br/><br/>
    * The resource name is used as path.
    * 
    * @param resourceName
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    */
   T addModule(String resourceName) throws IllegalArgumentException;
}

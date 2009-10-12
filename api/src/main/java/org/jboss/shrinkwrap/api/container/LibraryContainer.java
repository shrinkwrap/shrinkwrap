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
 * LibraryContainer
 * 
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
    * Adds the resource with the specified name
    * as a library to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation.  The resource will be placed into 
    * the Container Library path under the same context from which it was retrieved.
    * <br/>
    * For instance a resourceName of "test/library.jar" could be placed
    * "/lib/test/library.jar".
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    */
   T addLibrary(String resourceName) throws IllegalArgumentException;
   
   T addLibrary(File resource) throws IllegalArgumentException;
   T addLibrary(URL resource) throws IllegalArgumentException;

   T addLibrary(String target, String resourceName) throws IllegalArgumentException;
   T addLibrary(String target, File resource) throws IllegalArgumentException;
   T addLibrary(String target, URL resource) throws IllegalArgumentException;
   T addLibrary(String target, Asset resource) throws IllegalArgumentException;


   /**
    * Adds the resource with the specified name
    * as a library to the container under the target path, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation.  The resource will be placed into 
    * the Containers Library path under the relative target path.
    * <br/>
    * For instance a resourceName of "test/library.jar" and target as "/test/example.jar" could be placed in
    * "/lib/test/example.jar".
    * 
    * @param target New name of the resource in the container
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if target is null
    * @throws IllegalArgumentException if resourceName is null
    */
   T addLibrary(Path target, String resourceName) throws IllegalArgumentException;
   
   T addLibrary(Path target, File resource) throws IllegalArgumentException;
   T addLibrary(Path target, URL resource) throws IllegalArgumentException;
   T addLibrary(Path target, Asset resource) throws IllegalArgumentException;

   /**
    * Add another {@link Archive} to this {@link Archive} as a Library.
    * <br/><br/>
    * For instance a Archive with name 'example.jar' could be placed in
    * "/lib/example.jar".
    * 
    * @param archive The {@link Archive} to be added to the Library path
    * @return This virtual archive
    * @throws IllegalArgumentException if {@link Archive} is null
    */
   T addLibrary(Archive<?> archive) throws IllegalArgumentException;
}

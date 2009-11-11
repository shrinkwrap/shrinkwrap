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
 * ManifestContainer
 * 
 * Defines the contract for a component capable of storing 
 * Manifest related resources.
 * <br/><br/>
 * The actual path to the Manifest resources within the {@link Archive} 
 * is up to the implementations/specifications.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public interface ManifestContainer<T extends Archive<T>>
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds a resource to this {@link Archive} as MEANIFEST.MF.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation. 
    * <br/>
    * For instance a resourceName of "test/example.txt" could be placed in 
    * "/META-INF/MANIFEST.MF"
    * 
    * @param resourceName Name of the {@link ClassLoader} resource to add 
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    */
   T setManifest(String resourceName) throws IllegalArgumentException;
   
   T setManifest(File resource) throws IllegalArgumentException;
   T setManifest(URL resource) throws IllegalArgumentException;
   T setManifest(Asset resource) throws IllegalArgumentException;
   
   /**
    * Adds the resource with the specified name
    * to the container, returning the container itself.
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
   T addManifestResource(String resourceName) throws IllegalArgumentException;
   
   T addManifestResource(File resource) throws IllegalArgumentException;


   T addManifestResource(String resourceName, String target) throws IllegalArgumentException;
   T addManifestResource(File resource, String target) throws IllegalArgumentException;
   T addManifestResource(URL resource, String target) throws IllegalArgumentException;
   T addManifestResource(Asset resource, String target) throws IllegalArgumentException;

   /**
    * Adds the resource with the specified name
    * to the container, returning the container itself.
    * <br/><br/>
    * The {@link ClassLoader} used to obtain the resource is up to
    * the implementation. 
    * <br/>
    * For instance a resourceName of "test/library.xml" and target of "/test/example.xml" could be placed in
    * "/META-INF/test/example.xml".
    * 
    * @param target The target relative to Manifest path within the archive into which we'll place the resource
    * @param resourceName Name of the {@link ClassLoader} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if target is null
    * @throws IllegalArgumentException if resourceName is null
    */
   T addManifestResource(String resourceName, Path target) throws IllegalArgumentException;
   T addManifestResource(File resource, Path target) throws IllegalArgumentException;
   T addManifestResource(URL resource, Path target) throws IllegalArgumentException;
   T addManifestResource(Asset resource, Path target) throws IllegalArgumentException;
}

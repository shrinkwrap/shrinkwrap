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
package org.jboss.declarchive.api;

import java.util.Map;

/**
 * Archive
 * 
 * Represents a collection of resources which may
 * be constructed declaratively / programmatically.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface Archive<T extends Archive<T>>
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the name of this archive (ie. myLibrary.jar)
    */
   String getName();

   /**
    * Adds the specified assets to the archive and returns this reference
    *  
    * @param assets
    * @return
    * @throws IllegalArgumentException If no assets were specified
    */
   T add(Asset... assets) throws IllegalArgumentException;

   /**
    * Adds the specified assets under the specified path into the
    * target context
    * 
    * @param target The context under which to add the assets 
    * @param assets
    * @return
    * @throws IllegalArgumentException If no target or assets were specified
    */
   T add(Path target, Asset... assets) throws IllegalArgumentException;

   /**
    * Adds the specified asset under the specified target (directory)
    * using the specified name.  The resultant path will be treating 
    * the specified path as a prefix namespace, then appending the name.
    * 
    * @param target The context directory under which to add the asset
    * @param name The name to assign the assent under the target namespace
    * @param asset
    * @return
    * @throws IllegalArgumentException If the target, name, or asset was not specified
    */
   T add(Path target, String name, Asset asset) throws IllegalArgumentException;

   /**
    * Adds the specified resource under the context denoted by the specified target
    * 
    * @param target
    * @param asset
    * @return
    * @throws IllegalArgumentException If either the target or asset is not specified 
    */
   T add(String target, Asset asset) throws IllegalArgumentException;

   /**
    * Obtains the asset located at the specified path
    * 
    * @param path
    * @return
    * @throws AssetNotFoundException If the specified path does not 
    *   point to any asset in the archive
    * @throws IllegalArgumentException If the path is not specified
    */
   Asset get(Path path) throws AssetNotFoundException, IllegalArgumentException;

   /**
    * Obtains the asset located at the specified path
    * 
    * @param path
    * @return
    * @throws AssetNotFoundException If the specified path does not 
    *   point to any resource in the archive
    * @throws IllegalArgumentException If the path is not specified
    */
   Asset get(String path) throws AssetNotFoundException, IllegalArgumentException;

   /**
    * Denotes whether this archive contains a resource at the specified
    * path
    * 
    * @param path
    * @return
    * @throws IllegalArgumentException If the path is not specified
    */
   boolean contains(Path path) throws IllegalArgumentException;

   /**
    * Removes the asset in the archive at the specified Path.  If the path
    * is a directory, recursively removes all contents.
    * 
    * @param path
    * @return Whether or not a deletion was made
    */
   boolean delete(Path path) throws IllegalArgumentException;

   /**
    * Obtains all assets in this archive, along with its respective Path.
    * The returned Map will be an immutable view.
    * @return
    */
   Map<Path, Asset> getContent();

   /**
    * Add an archive under a specific and maintain the archive name a context path.
    * 
    * @param path to use 
    * @param arhive to add
    * @return
    * @throws IllegalArgumentException If the path or archive are not specified 
    */
   T add(Path path, Archive<?> arhive);

   /**
    * Add the contents from an existing archive without 
    * maintaining the archive name in the context path.  
    * 
    * @param source Archive to add contents from
    * @return  
    * @throws IllegalArgumentException If the existing archive is not specified
    */
   T addContents(Archive<?> source) throws IllegalArgumentException;

   /**
    * Add the contents from an existing archive in a specific path 
    * without maintaining the archive name in the context path.
    * 
    * @param path Path to add contents to
    * @param source Archive to add contents from
    * @return  
    * @throws IllegalArgumentException If the path or existing archive is not specified
    */
   T addContents(Path path, Archive<?> source) throws IllegalArgumentException;

   /**
    * Returns a multiline "ls -l"-equse output of the contents of
    * this deployment and (recursively) its children if the verbosity 
    * flag is set to "true".  Otherwise the no-arg version is invoked
    * 
    * @return
    */
   String toString(boolean verbose);

}

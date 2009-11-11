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
package org.jboss.shrinkwrap.api;

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
public interface Archive<T extends Archive<T>> extends Specializer
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the name of this archive (ie. myLibrary.jar)
    */
   String getName();

   /**
    * Adds the specified asset under the specified path into the
    * target context
    * 
    * @param target The context under which to add the assets 
    * @param asset
    * @return
    * @throws IllegalArgumentException If no target or assets were specified
    */
   T add(Path target, Asset asset) throws IllegalArgumentException;

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
    * @return The asset, or null if nothing is found at the Path
    * @throws IllegalArgumentException If the path is not specified
    */
   Asset get(Path path) throws IllegalArgumentException;

   /**
    * Obtains the asset located at the specified path
    * 
    * @param path
    * @return The asset, or null if nothing is found at the Path
    * @throws IllegalArgumentException If the path is not specified
    */
   Asset get(String path) throws IllegalArgumentException;

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
    * Add an archive under a specific context and maintain the archive name as context path.
    * 
    * @param path to use 
    * @param archive to add
    * @return
    * @throws IllegalArgumentException If the path or archive are not specified 
    */
   T add(Path path, Archive<?> archive) throws IllegalArgumentException;

   /**
    * Merge the contents from an existing archive without 
    * maintaining the archive name in the context path.  
    * 
    * @param source Archive to add contents from
    * @return  
    * @throws IllegalArgumentException If the existing archive is not specified
    */
   T merge(Archive<?> source) throws IllegalArgumentException;

   /**
    * Merge the contents from an existing archive in a specific path 
    * without maintaining the archive name in the context path.
    * 
    * @param source Archive to add contents from
    * @param path Path to add contents to
    * @return  
    * @throws IllegalArgumentException If the path or existing archive is not specified
    */
   T merge(Archive<?> source, Path path) throws IllegalArgumentException;

   /**
    * Returns a multiline "ls -l"-equse output of the contents of
    * this deployment and (recursively) its children if the verbosity 
    * flag is set to "true".  Otherwise the no-arg version is invoked
    * 
    * @return
    */
   String toString(boolean verbose);

}

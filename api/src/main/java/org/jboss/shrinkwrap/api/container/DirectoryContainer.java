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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Path;

/**
 * Defines the contract for a component capable of storing 
 * empty directories.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface DirectoryContainer<T extends Archive<T>>
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds the specified directory to the {@link Archive}.
    * 
    * @param path The path to add
    * @return This archive
    * @throws IllegalArgumentException If no path was specified
    */
   T addDirectory(String path) throws IllegalArgumentException;

   /**
    * Adds the specified directory to the {@link Archive}.
    * 
    * @param paths The paths to add
    * @return This archive
    * @throws IllegalArgumentException If no paths were specified
    */
   T addDirectories(String... paths) throws IllegalArgumentException;

   /**
    * Adds the specified directory to the {@link Archive}.
    * 
    * @param path The path to add
    * @return This archive
    * @throws IllegalArgumentException If no path was specified
    */
   T addDirectory(Path path) throws IllegalArgumentException;

   /**
    * Adds the specified directory to the {@link Archive}.
    * 
    * @param paths The paths to add
    * @return This archive
    * @throws IllegalArgumentException If no paths were specified
    */
   T addDirectories(Path... paths) throws IllegalArgumentException;

}

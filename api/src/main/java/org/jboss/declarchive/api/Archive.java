/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
  *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.declarchive.api;

import java.net.URL;

/**
 * Archive
 * 
 * Represents a collection of resources which may
 * be constructed declaratively / programmatically.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface Archive
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds the specified Class to the archive
    * 
    * @param The class to add
    * @return This virtual deployment
    * @throws IllegalArgumentException If no class was specified
    */
   Archive addClass(Class<?> clazz) throws IllegalArgumentException;

   /**
    * Adds the specified Classes to the archive.  
    * 
    * @param classes
    * @return This virtual deployment
    * @throws IllegalArgumentException If no classes were specified
    */
   Archive addClasses(Class<?>... classes) throws IllegalArgumentException;

   /**
    * Adds the resource with the specified name to the 
    * deployment.  The resource name must be visible to the ClassLoader
    * of the archive
    * 
    * @param name
    * @return
    * @throws IllegalArgumentException If the name was not specified
    */
   Archive addResource(String name) throws IllegalArgumentException;

   /**
    * Adds the specified resource to the archive, using the specified ClassLoader
    * to load the resource
    * 
    * @param name
    * @param cl
    * @return
    * @throws IllegalArgumentException If either the name or ClassLoader is not specified
    */
   Archive addResource(String name, ClassLoader cl) throws IllegalArgumentException;

   /**
    * Adds the resource located at the specified URL to the archive.  The
    * location within the archive will be equal to the path portion of the 
    * specified URL.
    * 
    * @param location
    * @return
    * @throws IllegalArgumentException If the location is not specified
    */
   Archive addResource(URL location) throws IllegalArgumentException;

   /**
    * Adds the resource located at the specified URL to
    * the archive at the specified path.
    * 
    * @param location
    * @param newPath The new path to assign, or null if 
    *   the path portion of the location should be used
    * @return
    * @throws IllegalArgumentException If the location is not specified 
    */
   Archive addResource(URL location, String newPath) throws IllegalArgumentException;

   /**
    * Returns a multiline "ls -l"-equse output of the contents of
    * this deployment and (recursively) its children if the verbosity 
    * flag is set to "true".  Otherwise the no-arg version is invoked
    * 
    * @return
    */
   String toString(boolean verbose);

}

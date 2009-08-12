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
package org.jboss.declarchive.api.jar;

import java.io.File;
import java.net.URL;

import org.jboss.declarchive.api.Archive;

/**
 * JavaArchive
 * 
 * Traditional JAR (Java ARchive) structure.  Used in 
 * construction of libraries and applications.
 *
 * @see http://java.sun.com/j2se/1.5.0/docs/guide/jar/jar.html
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface JavaArchive extends Archive<JavaArchive>
{

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds the File with the specified path as the JAR Manifest.  This will
    * be placed into the archive as <code>META-INF/MANIFEST.MF</code>
    * 
    * @param manifestFilePath The path to the file file to use as the JAR Manifest.
    * @throws IllegalArgumentException If the path does not point to a valid file 
    *       or was not specified
    */
   JavaArchive addManifest(String manifestFilePath) throws IllegalArgumentException;

   /**
    * Adds the specified File as the JAR Manifest.  This will
    * be placed into the archive as <code>META-INF/MANIFEST.MF</code>
    * 
    * @param manifestFile The file to use as the JAR Manifest.
    * @throws IllegalArgumentException If the file does not exist or
    *       was not specified
    */
   JavaArchive addManifest(File manifestFile) throws IllegalArgumentException;

   /**
    * Adds the specified URL as the JAR Manifest.  This will
    * be placed into the archive as <code>META-INF/MANIFEST.MF</code>
    * 
    * @param manifestFile The file to use as the JAR Manifest.
    * @throws IllegalArgumentException If the URL could not be obtained or
    *       was not specified
    */
   JavaArchive addManifest(URL manifestFile) throws IllegalArgumentException;

}

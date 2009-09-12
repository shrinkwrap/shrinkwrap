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
package org.jboss.shrinkwrap.api.mightmoveordelete;

import java.io.File;
import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;

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

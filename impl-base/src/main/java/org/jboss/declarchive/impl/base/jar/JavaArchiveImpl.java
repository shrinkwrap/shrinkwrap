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
package org.jboss.declarchive.impl.base.jar;

import java.net.URL;
import java.util.Map;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.AssetNotFoundException;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.jar.JavaArchive;
import org.jboss.declarchive.impl.base.GenericArchive;
import org.jboss.declarchive.impl.base.path.BasePath;

/**
 * JavaArchiveImpl
 * 
 * Implementation of an archive with JAR-specific support.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class JavaArchiveImpl implements JavaArchive
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(JavaArchiveImpl.class.getName());

   /**
    * Path to the manifest inside of the Archive.
    */
   private static final Path PATH_MANIFEST = new BasePath("META-INF");

   /**
    * Path to the resources inside of the Archive.
    */
   private static final Path PATH_RESOURCE = new BasePath("/");

   /**
    * Path to the classes inside of the Archive.
    */
   private static final Path PATH_CLASSES = new BasePath("/");

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The underlying storage implementation of an archive
    */
   private final GenericArchive<?> archive;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    * 
    * @param archive The underlying archive storage implementation
    * to which the convenience methods of this archive
    * will delegate
    * @throws IllegalArgumentException If the delegate is not specified 
    */
   public JavaArchiveImpl(final GenericArchive<?> archive)
   {
      this.archive = archive;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /*
    * All methods below this line are delegates
    */

   /**
    * @param assets
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#add(org.jboss.declarchive.api.Asset[])
    */
   @Override
   public JavaArchive add(final Asset... assets) throws IllegalArgumentException
   {
      archive.add(assets);
      return this;
   }

   /**
    * @param classes
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#add(java.lang.Class<?>[])
    */
   @Override
   public JavaArchive add(Class<?>... classes) throws IllegalArgumentException
   {
      archive.add(classes);
      return this;
   }

   /**
    * @param packages
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#add(java.lang.Package[])
    */
   @Override
   public JavaArchive add(Package... packages) throws IllegalArgumentException
   {
      archive.add(packages);
      return this;
   }

   /**
    * @param path
    * @param assets
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#add(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Asset[])
    */
   @Override
   public JavaArchive add(Path path, Asset... assets) throws IllegalArgumentException
   {
      archive.add(path, assets);
      return this;
   }

   /**
    * @param path
    * @param name
    * @param asset
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#add(org.jboss.declarchive.api.Path, java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public JavaArchive add(Path path, String name, Asset asset) throws IllegalArgumentException
   {
      archive.add(path, name, asset);
      return this;
   }

   /**
    * @param path
    * @param asset
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#add(java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public JavaArchive add(String path, Asset asset) throws IllegalArgumentException
   {
      archive.add(path, asset);
      return this;
   }

   /**
    * @param resourceName
    * @param newName
    * @param path
    * @return
    * @see org.jboss.declarchive.impl.base.GenericArchive#addManifestResource(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public JavaArchive addManifestResource(String resourceName, String newName, String path)
   {
      archive.addManifestResource(resourceName, newName, path);
      return this;
   }

   /**
    * @param resourceName
    * @param newName
    * @return
    * @see org.jboss.declarchive.impl.base.GenericArchive#addManifestResource(java.lang.String, java.lang.String)
    */
   @Override
   public JavaArchive addManifestResource(String resourceName, String newName)
   {
      archive.addManifestResource(resourceName, newName);
      return this;
   }

   /**
    * @param resourceName
    * @return
    * @see org.jboss.declarchive.impl.base.GenericArchive#addManifestResource(java.lang.String)
    */
   @Override
   public JavaArchive addManifestResource(String resourceName)
   {
      archive.addManifestResource(resourceName);
      return this;
   }

   /**
    * @param target
    * @param name
    * @param cl
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#addResource(org.jboss.declarchive.api.Path, java.lang.String, java.lang.ClassLoader)
    */
   @Override
   public JavaArchive addResource(Path target, String name, ClassLoader cl) throws IllegalArgumentException
   {
      archive.addResource(target, name, cl);
      return this;
   }

   /**
    * @param target
    * @param resourceName
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#addResource(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public JavaArchive addResource(Path target, String resourceName) throws IllegalArgumentException
   {
      archive.addResource(target, resourceName);
      return this;
   }

   /**
    * @param resourceName
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#addResource(java.lang.String)
    */
   @Override
   public JavaArchive addResource(String resourceName) throws IllegalArgumentException
   {
      archive.addResource(resourceName);
      return this;
   }

   /**
    * @param location
    * @param newPath
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#addResource(java.net.URL, java.lang.String)
    */
   @Override
   public JavaArchive addResource(URL location, String newPath) throws IllegalArgumentException
   {
      archive.addResource(location, newPath);
      return this;
   }

   /**
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String, java.lang.String)
    */
   @Override
   public JavaArchive addResource(final String target, final String resourceName) throws IllegalArgumentException
   {
      archive.addResource(target, resourceName);
      return this;
   }

   /**
    * @param location
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#addResource(java.net.URL)
    */
   @Override
   public JavaArchive addResource(URL location) throws IllegalArgumentException
   {
      archive.addResource(location);
      return this;
   }

   /**
    * @param path
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#contains(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean contains(Path path) throws IllegalArgumentException
   {
      return archive.contains(path);
   }

   /**
    * @param path
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#delete(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean delete(Path path) throws IllegalArgumentException
   {
      return archive.delete(path);
   }

   /**
    * @param obj
    * @return
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      return archive.equals(obj);
   }

   /**
    * @param path
    * @return
    * @throws AssetNotFoundException
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#get(org.jboss.declarchive.api.Path)
    */
   @Override
   public Asset get(Path path) throws AssetNotFoundException, IllegalArgumentException
   {
      return archive.get(path);
   }

   /**
    * @param path
    * @return
    * @throws AssetNotFoundException
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.impl.base.GenericArchive#get(java.lang.String)
    */
   @Override
   public Asset get(String path) throws AssetNotFoundException, IllegalArgumentException
   {
      return archive.get(path);
   }

   /**
    * @return
    * @see org.jboss.declarchive.impl.base.GenericArchive#getContent()
    */
   @Override
   public Map<Path, Asset> getContent()
   {
      return archive.getContent();
   }

   /**
    * @return
    * @see org.jboss.declarchive.impl.base.GenericArchive#getName()
    */
   @Override
   public String getName()
   {
      return archive.getName();
   }

   /**
    * @return
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      return archive.hashCode();
   }

   /**
    * @param resourceName
    * @return
    * @see org.jboss.declarchive.impl.base.GenericArchive#setManifest(java.lang.String)
    */
   @Override
   public JavaArchive setManifest(String resourceName)
   {
      archive.setManifest(resourceName);
      return this;
   }

   /**
    * @return
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return archive.toString();
   }

   /**
    * @param verbose
    * @return
    * @see org.jboss.declarchive.impl.base.GenericArchive#toString(boolean)
    */
   @Override
   public String toString(boolean verbose)
   {
      return archive.toString(verbose);
   }

}

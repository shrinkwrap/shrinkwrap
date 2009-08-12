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
package org.jboss.declarchive.impl.base.jar;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.jar.JavaArchive;

/**
 * JavaArchiveImpl
 * 
 * Implementation of an archive with JAR-specific 
 * support.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
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
    * Path to the manifest inside of a JAR
    */
   private static final String PATH_MANIFEST = "META-INF/MANIFEST.MF";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Underlying delegate
    */
   private final Archive<?> delegate;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    * 
    * @param The underlying archive storage implementation
    * to which the convenience methods of this archive
    * will delegate
    * @throws IllegalArgumentException If the delegate is not specified 
    */
   public JavaArchiveImpl(final Archive<?> delegate)
   {
      // Precondition check
      if (delegate == null)
      {
         throw new IllegalArgumentException("delegate must be specified");
      }

      // Set properties
      this.delegate = delegate;

      // Log
      log.fine("Created new Java Archive from backing delegate: " + delegate);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @param clazz
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.api.Archive#addClass(java.lang.Class)
    */
   public JavaArchive addClass(final Class<?> clazz) throws IllegalArgumentException
   {
      delegate.addClass(clazz);
      return this;
   }

   /**
    * @param classes
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.api.Archive#addClasses(java.lang.Class<?>[])
    */
   public JavaArchive addClasses(final Class<?>... classes) throws IllegalArgumentException
   {
      delegate.addClasses(classes);
      return this;
   }

   /**
    * @param name
    * @param cl
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.api.Archive#addResource(java.lang.String, java.lang.ClassLoader)
    */
   public JavaArchive addResource(final String name, final ClassLoader cl) throws IllegalArgumentException
   {
      delegate.addResource(name, cl);
      return this;
   }

   /**
    * @param name
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.api.Archive#addResource(java.lang.String)
    */
   public JavaArchive addResource(final String name) throws IllegalArgumentException
   {
      delegate.addResource(name);
      return this;
   }

   /**
    * @param location
    * @param newPath
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.api.Archive#addResource(java.net.URL, java.lang.String)
    */
   public JavaArchive addResource(final URL location, final String newPath) throws IllegalArgumentException
   {
      delegate.addResource(location, newPath);
      return this;
   }

   /**
    * @param location
    * @return
    * @throws IllegalArgumentException
    * @see org.jboss.declarchive.api.Archive#addResource(java.net.URL)
    */
   public JavaArchive addResource(final URL location) throws IllegalArgumentException
   {
      delegate.addResource(location);
      return this;
   }

   /**
    * @param verbose
    * @return
    * @see org.jboss.declarchive.api.Archive#toString(boolean)
    */
   public String toString(final boolean verbose)
   {
      return "Java Archive (JAR): " + delegate.toString(verbose);
   }

   /**
    * @throws MalformedURLException 
    * @see org.jboss.declarchive.api.jar.JavaArchive#addManifest(java.io.File)
    */
   @Override
   public JavaArchive addManifest(final File manifestFile) throws IllegalArgumentException
   {
      // Precondition checks
      if (manifestFile == null)
      {
         throw new IllegalArgumentException("Manifest file must be specified");
      }
      if (!manifestFile.exists())
      {
         throw new IllegalArgumentException("Specified manifest file does not exist: " + manifestFile.getAbsolutePath());
      }

      // Get a URL
      final URL url;
      try
      {
         url = manifestFile.toURI().toURL();
      }
      catch (final MalformedURLException murle)
      {
         throw new RuntimeException("Unexpected error in obtaining URL from File reference: "
               + manifestFile.getAbsolutePath(), murle);
      }

      // Return
      return this.addManifest(url);
   }

   /**
    * @see org.jboss.declarchive.api.jar.JavaArchive#addManifest(java.lang.String)
    */
   @Override
   public JavaArchive addManifest(final String manifestFilePath) throws IllegalArgumentException
   {
      // Precondition check
      if (manifestFilePath == null || manifestFilePath.length() == 0)
      {
         throw new IllegalArgumentException("path must be specified");
      }

      // Get a File
      final File file = new File(manifestFilePath);

      // Return
      return this.addManifest(file);
   }

   /**
    * @see org.jboss.declarchive.api.jar.JavaArchive#addManifest(java.net.URL)
    */
   @Override
   public JavaArchive addManifest(final URL manifestFile) throws IllegalArgumentException
   {
      // Precondition checks
      if (manifestFile == null)
      {
         throw new IllegalArgumentException("Manifest file must be specified");
      }

      // Add the resource and return
      return this.addResource(manifestFile, PATH_MANIFEST);
   }
}

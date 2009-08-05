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
package org.jboss.declarchive.impl.base;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;

/**
 * ArchiveBase
 * 
 * Base implementation of {@link Archive}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public abstract class ArchiveBase implements Archive
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ArchiveBase.class.getName());

   /**
    * Extension for Java Archives 
    */
   public static final String EXTENSION_JAR = ".jar";

   /**
    * Delimiter for paths while looking for resources 
    */
   private static final char DELIMITER_RESOURCE_PATH = '/';

   /**
    * Delimiter for paths in fully-qualified class names 
    */
   private static final char DELIMITER_CLASS_NAME_PATH = '.';

   /**
    * The filename extension appended to classes
    */
   private static final String EXTENSION_CLASS = ".class";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The ClassLoader used in loading resources and classes into the virtual deployment
    */
   private final ClassLoader classLoader;

   //-------------------------------------------------------------------------------------||
   // Constructors -----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    * 
    * Creates a new instance using the Thread Context ClassLoader
    * from which we'll load resources by default
    */
   protected ArchiveBase()
   {
      // Use the TCCL 
      this(SecurityActions.getThreadContextClassLoader());
   }

   /**
    * Constructor
    * 
    * Creates a new instance using the specified ClassLoader
    * from which we'll load resources by default
    * 
    * @param The ClassLoader to use by default
    */
   protected ArchiveBase(final ClassLoader cl)
   {
      // Invoke super
      super();

      // Precondition check
      if (cl == null)
      {
         throw new IllegalArgumentException("ClassLoader must be specified");
      }

      // Set properties
      this.classLoader = cl;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.declarchive.api.Archive#addClass(java.lang.Class)
    */
   @Override
   public Archive addClass(final Class<?> clazz) throws IllegalArgumentException
   {
      // Precondition check
      if (clazz == null)
      {
         throw new IllegalArgumentException("Class must be specified");
      }

      // Get the resource name of the class
      final String name = this.getResourceNameOfClass(clazz);

      // Get the CL of the Class
      final ClassLoader cl = clazz.getClassLoader();

      // Add it as a resource
      if (log.isLoggable(Level.FINER))
      {
         log.log(Level.FINER, "Adding class as resource: " + clazz);
      }
      return this.addResource(name, cl);
   }

   /**
    * @see org.jboss.declarchive.api.Archive#addClasses(java.lang.Class<?>[])
    */
   @Override
   public Archive addClasses(final Class<?>... classes) throws IllegalArgumentException
   {
      // Precondition check
      if (classes == null || classes.length == 0)
      {
         throw new IllegalArgumentException("At least one class must be specified");
      }

      // For each class
      for (final Class<?> clazz : classes)
      {
         this.addClass(clazz);
      }

      // Return
      return this;
   }

   /**
    * @see org.jboss.declarchive.api.Archive#addResource(java.lang.String)
    */
   @Override
   public Archive addResource(final String name) throws IllegalArgumentException
   {
      return this.addResource(name, this.getClassLoader());
   }

   /**
    * @see org.jboss.declarchive.api.Archive#addResource(java.net.URL)
    */
   @Override
   public Archive addResource(final URL location) throws IllegalArgumentException
   {
      // Delegate to the other implementation
      return this.addResource(location, null);
   }

   /**
    * @see org.jboss.declarchive.api.Archive#addResource(java.lang.String, java.lang.ClassLoader)
    */
   @Override
   public final Archive addResource(final String name, final ClassLoader cl) throws IllegalArgumentException
   {
      // Precondition check
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }
      if (cl == null)
      {
         throw new IllegalArgumentException("ClassLoader must be specified");
      }

      // Get the content of the resource
      byte[] content = null;
      try
      {
         content = this.getBytesOfResource(name, cl);
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not add resource \"" + name + "\" to " + this, ioe);
      }

      // Add
      this.addContent(content, name);

      // Return
      return this;
   }

   /**
    * @see org.jboss.declarchive.api.Archive#addResource(java.net.URL, java.lang.String)
    */
   @Override
   public Archive addResource(final URL location, final String newPath) throws IllegalArgumentException
   {
      // Precondition check
      if (location == null)
      {
         throw new IllegalArgumentException("location must be specified");
      }

      // Get the content of the location
      byte[] content = null;
      try
      {
         content = this.getBytesOfResource(location);
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not add location \"" + location + "\" to " + this, ioe);
      }

      // Adjust the path if not explicitly defined
      String path = newPath;
      if (path == null)
      {
         path = location.getPath();
         if (log.isLoggable(Level.FINER))
         {
            log.log(Level.FINER, "Implicitly set new path to \"" + path + "\" while adding: " + location);
         }
      }

      // Add
      this.addContent(content, path);

      // Return
      return this;
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds the specified content to the archive at the specified location
    * 
    * @param content
    * @param location
    * @throws IllegalArgumentException
    */
   protected abstract void addContent(final byte[] content, final String location) throws IllegalArgumentException;

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Returns the name of the class such that it may be accessed via ClassLoader.getResource()
    * 
    * @param clazz The class
    * @throws IllegalArgumentException If the class was not specified
    */
   private String getResourceNameOfClass(final Class<?> clazz) throws IllegalArgumentException
   {
      // Precondition check
      if (clazz == null)
      {
         throw new IllegalArgumentException("Class must be specified");
      }

      // Build the name
      final String fqn = clazz.getName();
      final String nameAsResourcePath = fqn.replace(DELIMITER_CLASS_NAME_PATH, DELIMITER_RESOURCE_PATH);
      final String resourceName = nameAsResourcePath + EXTENSION_CLASS;

      // Return 
      return resourceName;
   }

   /**
    * Copies and returns the specified URL.  Used
    * to ensure we don't export mutable URLs
    * 
    * @param url
    * @return
    */
   protected final URL copyURL(final URL url)
   {
      // If null, return
      if (url == null)
      {
         return url;
      }

      try
      {
         // Copy 
         return new URL(url.toExternalForm());
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException("Error in copying URL", e);
      }
   }

   /**
    * Obtains the contents (bytes) of the specified location
    * 
    * @param location
    * @return
    * @throws IOException
    * @throws IllegalArgumentException If the location is not specified
    */
   private byte[] getBytesOfResource(final URL location) throws IOException, IllegalArgumentException
   {
      // Precondition check
      if (location == null)
      {
         throw new IllegalArgumentException("location must be specified");
      }

      // Open a connection and read in all the bytes
      final URLConnection connection = location.openConnection();
      final int length = connection.getContentLength();
      assert length > -1 : "Content length is not known";
      final InputStream in = connection.getInputStream();
      byte[] contents = new byte[length];
      int offset = 0;
      while (offset < length)
      {
         final int readLength = length - offset;
         int bytesRead = in.read(contents, offset, readLength);
         if (bytesRead == -1)
         {
            break; // EOF
         }
         offset += bytesRead;
      }

      // Close up the stream
      in.close();

      // Return the byte array
      if (log.isLoggable(Level.FINER))
      {
         log.log(Level.FINER, "Read " + contents.length + " bytes for: " + location);
      }
      return contents;
   }

   /**
    * Obtains the contents (bytes) of the specified resource using the 
    * specified ClassLoader
    * 
    * @param name
    * @param cl
    * @return
    * @throws IOException
    * @throws IllegalArgumentException If the name or ClassLoader is not specified
    */
   private byte[] getBytesOfResource(final String name, final ClassLoader cl) throws IOException,
         IllegalArgumentException
   {
      // Precondition check
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }
      if (cl == null)
      {
         throw new IllegalArgumentException("ClassLoader must be specified");
      }

      // Get the URL
      final URL resourceUrl = this.getResourceUrl(name, cl);

      // Return
      return this.getBytesOfResource(resourceUrl);
   }

   /**
    * Obtains the URL of the resource with the requested name.
    * The search order is described by {@link ClassLoader#getResource(String)}
    * 
    * @param name
    * @return
    * @throws IllegalArgumentException If name is not specified or could not be found, 
    *   or if the ClassLoader is not specified 
    */
   private URL getResourceUrl(final String name, final ClassLoader cl) throws IllegalArgumentException
   {
      // Precondition check
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }
      if (cl == null)
      {
         throw new IllegalArgumentException("ClassLoader must be specified");
      }

      // Find
      final URL url = cl.getResource(name);

      // Ensure found
      if (url == null)
      {
         throw new ResourceNotFoundException("Could not find resource with name \"" + name + "\" in: " + cl);
      }

      // Return
      return url;
   }

   //-------------------------------------------------------------------------------------||
   // Accessors / Mutators ---------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Returns the ClassLoader used to load classes
    * and resources into this virtual deployment
    * 
    * @return
    */
   protected final ClassLoader getClassLoader()
   {
      return this.classLoader;
   }

}

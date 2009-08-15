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
package org.jboss.declarchive.impl.base;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.AssetNotFoundException;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.container.ClassContainer;
import org.jboss.declarchive.api.container.ManifestContainer;
import org.jboss.declarchive.api.container.ResourceContainer;
import org.jboss.declarchive.api.container.WebContainer;
import org.jboss.declarchive.impl.base.asset.ClassAsset;
import org.jboss.declarchive.impl.base.asset.ClassloaderAsset;
import org.jboss.declarchive.impl.base.path.BasePath;
import org.jboss.declarchive.impl.base.path.RelativePath;

/**
 * GenericArchive
 * 
 * Base implementation of {@link Archive}.  Provides support
 * for various container types out of the box.  Subclasses 
 * may expose a limited number of container interfaces as
 * appropriate.
 * 
 * This implementation will store all {@link Asset}s in 
 * an internal map accessible in immutable form by calling
 * {@link Archive#getContent()}.  For custom behaviour, 
 * subclasses may override
 * TODO add ?
 * TODO remove ?
 * ...in order to take custom action (ie. represent the assets
 * as backing files, etc).  
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class GenericArchive<T extends Archive<T>>
      implements
         Archive<T>,
         ResourceContainer<T>,
         ClassContainer<T>,
         ManifestContainer<T>,
         WebContainer<T>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(GenericArchive.class.getName());

   /**
    * Extension for Java Archives 
    */
   public static final String EXTENSION_JAR = ".jar";

   //   /**
   //    * Delimiter for paths while looking for resources 
   //    */
   //   private static final char DELIMITER_RESOURCE_PATH = '/';
   //
   //   /**
   //    * Delimiter for paths in fully-qualified class names 
   //    */
   //   private static final char DELIMITER_CLASS_NAME_PATH = '.';
   //
   //   /**
   //    * The filename extension appended to classes
   //    */
   //   private static final String EXTENSION_CLASS = ".class";

   /**
    * Newline character
    */
   private static final char CHAR_NEWLINE = '\n';

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Name of the archive
    */
   private final String name;

   /**
    * The ClassLoader used in loading resources and classes into the archive
    */
   private final ClassLoader classLoader;

   /**
    * Underlying contents of the archive; must
    * be a Thread-safe implementation
    */
   private final Map<Path, Asset> content;

   /**
    * Actual Class used in casting
    */
   private final Class<T> actualClass;

   //-------------------------------------------------------------------------------------||
   // Constructors -----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    * 
    * Creates a new instance using the Thread Context ClassLoader
    * from which we'll load resources by default
    * 
    * @param name Name of the archive
    * @param actualClass Actual Class used in casting
    * @throws IllegalArgumentException
    */
   protected GenericArchive(final String name, final Class<T> actualClass) throws IllegalArgumentException
   {
      // Use the TCCL 
      this(name, SecurityActions.getThreadContextClassLoader(), actualClass);
   }

   /**
    * Constructor
    * 
    * Creates a new instance using the specified ClassLoader
    * from which we'll load resources by default
    * 
    * @param name Name of the archive
    * @param cl The ClassLoader to use by default
    * @param actualClass Actual Class used in casting
    */
   protected GenericArchive(final String name, final ClassLoader cl, final Class<T> actualClass)
   {
      // Invoke super
      super();

      // Precondition check
      Validate.notNull(cl, "ClassLoader must be specified");
      // Precondition check
      Validate.notNull(name, "name must be specified");

      // Set properties
      this.classLoader = cl;
      this.name = name;
      this.content = new ConcurrentHashMap<Path, Asset>();
      this.actualClass = actualClass;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.declarchive.api.Archive#addClasses(java.lang.Class<?>[])
    */
   @Override
   public T add(final Class<?>... classes) throws IllegalArgumentException
   {
      // Precondition check
      if (classes == null || classes.length == 0)
      {
         throw new IllegalArgumentException("At least one class must be specified");
      }

      // For each class
      for (final Class<?> clazz : classes)
      {
         // Add it as a resource
         if (log.isLoggable(Level.FINER))
         {
            log.log(Level.FINER, "Adding class as resource: " + clazz);
         }
         final Asset asset = new ClassAsset(clazz);
         this.add(asset);
      }

      // Return
      return this.covarientReturn();
   }

   //   /**
   //    * @see org.jboss.declarchive.api.Archive#addResource(java.lang.String)
   //    */
   //   @Override
   //   public T addResource(final String name) throws IllegalArgumentException
   //   {
   //      return this.addResource(name, this.getClassLoader());
   //   }
   //
   //   /**
   //    * @see org.jboss.declarchive.api.Archive#addResource(java.net.URL)
   //    */
   //   @Override
   //   public T addResource(final URL location) throws IllegalArgumentException
   //   {
   //      // Delegate to the other implementation
   //      return this.addResource(location, null);
   //   }
   //
   //   /**
   //    * @see org.jboss.declarchive.api.Archive#addResource(java.lang.String, java.lang.ClassLoader)
   //    */
   //   @Override
   //   public final T addResource(final String name, final ClassLoader cl) throws IllegalArgumentException
   //   {
   //      // Precondition check
   //      if (name == null || name.length() == 0)
   //      {
   //         throw new IllegalArgumentException("name must be specified");
   //      }
   //      if (cl == null)
   //      {
   //         throw new IllegalArgumentException("ClassLoader must be specified");
   //      }
   //
   //      // Get the content of the resource
   //      byte[] content = null;
   //      try
   //      {
   //         content = this.getBytesOfResource(name, cl);
   //      }
   //      catch (final IOException ioe)
   //      {
   //         throw new RuntimeException("Could not add resource \"" + name + "\" to " + this, ioe);
   //      }
   //
   //      // Add
   //      this.addContent(content, name);
   //
   //      // Return
   //      return this.covarientReturn();
   //   }
   //
   //   /**
   //    * @see org.jboss.declarchive.api.Archive#addResource(java.net.URL, java.lang.String)
   //    */
   //   @Override
   //   public T addResource(final URL location, final String newPath) throws IllegalArgumentException
   //   {
   //      // Precondition check
   //      if (location == null)
   //      {
   //         throw new IllegalArgumentException("location must be specified");
   //      }
   //
   //      // Get the content of the location
   //      byte[] content = null;
   //      try
   //      {
   //         content = this.getBytesOfResource(location);
   //      }
   //      catch (final IOException ioe)
   //      {
   //         throw new RuntimeException("Could not add location \"" + location + "\" to " + this, ioe);
   //      }
   //
   //      // Adjust the path if not explicitly defined
   //      String path = newPath;
   //      if (path == null)
   //      {
   //         path = location.getPath();
   //         if (log.isLoggable(Level.FINER))
   //         {
   //            log.log(Level.FINER, "Implicitly set new path to \"" + path + "\" while adding: " + location);
   //         }
   //      }
   //
   //      // Add
   //      this.addContent(content, path);
   //
   //      // Return
   //      return this.covarientReturn();
   //   }
   //
   //   /**
   //    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String, java.lang.String)
   //    */
   //   @Override
   //   public T addResource(final String name, final String locationWithinContainer) throws IllegalArgumentException
   //   {
   //      // Make a resource
   //      final Asset resource = new ClassloaderResource(name);
   //
   //      // Make a Path
   //      final Path path = new BasePath(locationWithinContainer);
   //
   //      // Add and Return
   //      return this.add(path, resource);
   //   }

   /**
    * @see org.jboss.declarchive.api.container.ClassContainer#add(java.lang.Package[])
    */
   @Override
   public T add(final Package... packages) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(packages, "At least one package must be specified");

      // Define the classes we'll add
      final Set<Class<?>> classes = new HashSet<Class<?>>();

      // Get the CL
      final ClassLoader cl = this.getClassLoader();

      // For each package specified
      for (final Package pkg : packages)
      {
         // Make a scanner to get the classes out of the package
         final URLPackageScanner scanner = new URLPackageScanner(pkg, false, cl);
         // Add the Classes in the package to the Set
         classes.addAll(scanner.getClasses());
      }

      // Add classes to the archive and return
      return this.add(classes.toArray(new Class<?>[]
      {}));
   }

   /**
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Asset[])
    */
   @Override
   public T add(final Asset... assets) throws IllegalArgumentException
   {
      // Add and return
      return this.add(new BasePath(), assets);
   }

   /**
    * @see org.jboss.declarchive.api.Archive#getName()
    */
   @Override
   public String getName()
   {
      return this.name;
   }

   /**
    * @see org.jboss.declarchive.api.Archive#toString(boolean)
    */
   @Override
   public String toString(final boolean verbose)
   {
      // If not verbose, use the normal toString 
      if (!verbose)
      {
         return this.toString();
      }

      // Order 
      final Map<Path, Asset> orderedMap = new TreeMap<Path, Asset>();
      orderedMap.putAll(this.getContent());

      // Get builder
      final StringBuilder sb = new StringBuilder();
      sb.append(this.getName());

      // Get keys
      final Set<Path> paths = orderedMap.keySet();
      for (final Path path : paths)
      {
         sb.append(CHAR_NEWLINE);
         sb.append(path);
      }

      // Return
      return sb.toString();
   }

   /**
    * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public T addManifestResource(String resourceName, String newName, String path)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Not Yet Implemented");
   }

   /**
    * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addManifestResource(String resourceName, String newName)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Not Yet Implemented");
   }

   /**
    * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(java.lang.String)
    */
   @Override
   public T addManifestResource(String resourceName)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Not Yet Implemented");
   }

   /**
    * @see org.jboss.declarchive.api.container.ManifestContainer#setManifest(java.lang.String)
    */
   @Override
   public T setManifest(String resourceName)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Not Yet Implemented");
   }

   /**
    * @see org.jboss.declarchive.api.container.WebContainer#setWebXML(java.lang.String)
    */
   @Override
   public T setWebXML(String resourceName)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Not Yet Implemented");
   }

   /**
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Asset[])
    */
   @Override
   public T add(final Path path, final Asset... assets) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(assets, "At least one resource must be specified");

      // Add each resource
      for (final Asset resource : assets)
      {
         this.add(path, resource);
      }

      // Return
      return this.covarientReturn();
   }

   /**
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public T add(final Path path, final String name, final Asset asset) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNull(path, "path must be specified");
      Validate.notNullOrEmpty(name, "name must be specified");

      // Construct a new path using the prefix context and name 
      final Path newPath = new RelativePath(path, name);

      // Add and return
      return this.add(newPath, asset);
   }

   /**
    * @see org.jboss.declarchive.api.Archive#add(java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public T add(final String path, final Asset asset) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNull(path, "path must be specified");
      Validate.notNull(asset, "resource must be specified");

      // Construct a Path
      final Path realPath = new BasePath(path);

      // Add and return
      return this.add(realPath, asset);
   }

   /**
    * @see org.jboss.declarchive.api.Archive#contains(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean contains(final Path path) throws IllegalArgumentException
   {
      // Return whether this path exists
      return this.getContent().containsKey(path);
   }

   /**
    * @see org.jboss.declarchive.api.Archive#delete(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean delete(final Path path) throws IllegalArgumentException
   {
      // Determine if this path directly exists
      if (this.contains(path))
      {
         final Asset deleted = this.content.remove(path);
         return deleted != null;
      }

      // Recurse
      //TODO

      // Nothing deleted
      return false;
   }

   /**
    * @see org.jboss.declarchive.api.Archive#get(org.jboss.declarchive.api.Path)
    */
   @Override
   public Asset get(final Path path) throws AssetNotFoundException, IllegalArgumentException
   {
      final Asset asset = this.getContent().get(path);
      if (asset == null)
      {
         throw new AssetNotFoundException("No resource exists at " + path);
      }
      return asset;
   }

   /**
    * @see org.jboss.declarchive.api.Archive#get(java.lang.String)
    */
   @Override
   public Asset get(final String path) throws AssetNotFoundException, IllegalArgumentException
   {
      final Path realPath = new BasePath(path);
      return this.get(realPath);
   }

   /**
    * @see org.jboss.declarchive.api.Archive#getContent()
    */
   @Override
   public Map<Path, Asset> getContent()
   {
      // Return an immutable view
      return Collections.unmodifiableMap(this.content);
   }

   /**
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(org.jboss.declarchive.api.Path, java.lang.String, java.lang.ClassLoader)
    */
   @Override
   public T addResource(Path target, String name, ClassLoader cl) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNull(target, "target must be specified");
      Validate.notNullOrEmpty(name, "name must be specified");

      // Make an assert
      final Asset asset = new ClassloaderAsset(name, cl);

      // Add and return
      return this.add(target, asset);
   }

   /**
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(org.jboss.declarchive.api.Path, java.lang.String)
    */
   @Override
   public T addResource(final Path target, final String resourceName) throws IllegalArgumentException
   {
      // Add and return
      return this.addResource(target, resourceName, SecurityActions.getThreadContextClassLoader());
   }

   /**
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String, java.lang.String)
    */
   @Override
   public T addResource(final String target, final String resourceName) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNullOrEmpty(target, "target must be specified");

      // Make a path
      final Path path = new BasePath(target);

      // Add and return
      return this.addResource(path, resourceName);
   }

   /**
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String)
    */
   @Override
   public T addResource(final String resourceName) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNullOrEmpty(resourceName, "resourceName must be specified");

      // Target is the resource name
      final Path path = new BasePath(resourceName);

      // Add and return
      return this.addResource(path, resourceName);
   }

   /**
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.net.URL, java.lang.String)
    */
   @Override
   public T addResource(URL location, String newPath) throws IllegalArgumentException
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Not Yet Implemented");
   }

   /**
    * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.net.URL)
    */
   @Override
   public T addResource(URL location) throws IllegalArgumentException
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException("Not Yet Implemented");
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Provides typesafe covarient return of this instance
    */
   protected final T covarientReturn()
   {
      try
      {
         return this.getActualClass().cast(this);
      }
      catch (final ClassCastException cce)
      {
         log.log(Level.SEVERE,
               "The class specified by getActualClass is not a valid assignment target for this instance;"
                     + " developer error");
         throw cce;
      }
   }

   /**
    * Returns the actual typed class for this instance, used in safe casting 
    * for covarient return types
    * 
    * @return
    */
   private Class<T> getActualClass()
   {
      return this.actualClass;
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

   //   /**
   //    * Obtains the contents (bytes) of the specified location
   //    * 
   //    * @param location
   //    * @return
   //    * @throws IOException
   //    * @throws IllegalArgumentException If the location is not specified
   //    */
   //   private byte[] getBytesOfResource(final URL location) throws IOException, IllegalArgumentException
   //   {
   //      // Precondition check
   //      if (location == null)
   //      {
   //         throw new IllegalArgumentException("location must be specified");
   //      }
   //
   //      // Open a connection and read in all the bytes
   //      final URLConnection connection = location.openConnection();
   //      final int length = connection.getContentLength();
   //      assert length > -1 : "Content length is not known";
   //      final InputStream in = connection.getInputStream();
   //      final byte[] contents;
   //      try
   //      {
   //         contents = new byte[length];
   //         int offset = 0;
   //         while (offset < length)
   //         {
   //            final int readLength = length - offset;
   //            int bytesRead = in.read(contents, offset, readLength);
   //            if (bytesRead == -1)
   //            {
   //               break; // EOF
   //            }
   //            offset += bytesRead;
   //         }
   //      }
   //      finally
   //      {
   //         try
   //         {
   //            // Close up the stream
   //            in.close();
   //         }
   //         catch (final IOException ignore)
   //         {
   //
   //         }
   //      }
   //
   //      // Return the byte array
   //      if (log.isLoggable(Level.FINER))
   //      {
   //         log.log(Level.FINER, "Read " + length + " bytes for: " + location);
   //      }
   //      return contents;
   //   }
   //
   //   /**
   //    * Obtains the contents (bytes) of the specified resource using the 
   //    * specified ClassLoader
   //    * 
   //    * @param name
   //    * @param cl
   //    * @return
   //    * @throws IOException
   //    * @throws IllegalArgumentException If the name or ClassLoader is not specified
   //    */
   //   private byte[] getBytesOfResource(final String name, final ClassLoader cl) throws IOException,
   //         IllegalArgumentException
   //   {
   //      // Precondition check
   //      if (name == null || name.length() == 0)
   //      {
   //         throw new IllegalArgumentException("name must be specified");
   //      }
   //      if (cl == null)
   //      {
   //         throw new IllegalArgumentException("ClassLoader must be specified");
   //      }
   //
   //      // Get the URL
   //      final URL resourceUrl = this.getResourceUrl(name, cl);
   //
   //      // Return
   //      return this.getBytesOfResource(resourceUrl);
   //   }
   //
   //   /**
   //    * Obtains the URL of the resource with the requested name.
   //    * The search order is described by {@link ClassLoader#getResource(String)}
   //    * 
   //    * @param name
   //    * @return
   //    * @throws IllegalArgumentException If name is not specified or could not be found, 
   //    *   or if the ClassLoader is not specified 
   //    */
   //   private URL getResourceUrl(final String name, final ClassLoader cl) throws IllegalArgumentException
   //   {
   //      // Precondition check
   //      if (name == null || name.length() == 0)
   //      {
   //         throw new IllegalArgumentException("name must be specified");
   //      }
   //      if (cl == null)
   //      {
   //         throw new IllegalArgumentException("ClassLoader must be specified");
   //      }
   //
   //      // Find
   //      final URL url = cl.getResource(name);
   //
   //      // Ensure found
   //      if (url == null)
   //      {
   //         throw new AssetNotFoundException("Could not find resource with name \"" + name + "\" in: " + cl);
   //      }
   //
   //      // Return
   //      return url;
   //   }

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

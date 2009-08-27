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
package org.jboss.declarchive.impl.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.AssetNotFoundException;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.Validate;
import org.jboss.declarchive.impl.base.asset.ByteArrayAsset;
import org.jboss.declarchive.impl.base.io.IOUtil;
import org.jboss.declarchive.impl.base.path.BasicPath;
import org.jboss.declarchive.spi.vfs.VfsArchive;
import org.jboss.virtual.MemoryFileFactory;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;
import org.jboss.virtual.plugins.vfs.VirtualFileURLConnection;

/**
 * VfsMemoryArchiveImpl
 * 
 * Concrete implementation of a VFS-backed virtual archive which
 * stores contents in-memory
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class VfsMemoryArchiveImpl extends VfsArchiveBase implements VfsArchive
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VfsMemoryArchiveImpl.class.getName());

   /**
    * Protocol of VFS in-memory 
    */
   private static final String PROTOCOL_VFS_MEMORY = "vfsmemory";

   /**
    * Empty String
    */
   private static final String EMPTY_STRING = "";

   /**
    * Separator
    */
   private static final char SEPARATOR = '/';

   //-------------------------------------------------------------------------------------||
   // Constructors -----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    * 
    * @param name Unique name for the deployment
    * @param cl ClassLoader to be used in loading resources and classes
    * @throws IllegalArgumentException If the name or ClassLoader was not specified
    */
   public VfsMemoryArchiveImpl(final String name) throws IllegalArgumentException
   {
      // Super impl
      super(name);

      // Precondition Check (also handled by super impls, but what the hell)
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }

      // Create the root for the archive
      VirtualFile file = null;
      URL url = null;
      try
      {
         final URL memoryRootUrl = new URL(PROTOCOL_VFS_MEMORY, name, EMPTY_STRING);
         MemoryFileFactory.createRoot(memoryRootUrl);
         final URL stubUrl = new URL(memoryRootUrl, name);
         MemoryFileFactory.createDirectory(stubUrl);
         url = stubUrl;
         file = VFS.getRoot(memoryRootUrl);
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Error in creating the root for virtual deployment \"" + name + "\"", ioe);
      }

      // Set properties for the root
      this.initialize(file, url);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.declarchive.impl.base.ArchiveBase#getActualClass()
    */
   @Override
   protected Class<VfsArchive> getActualClass()
   {
      return VfsArchive.class;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Asset)
    */
   @Override
   public VfsArchive add(final Path target, final Asset asset) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNull(target, "No target was specified");
      Validate.notNull(asset, "No asset was was specified");

      // Get a URL for the target
      final URL url = this.urlFromPath(target);

      // Get content as an array of bytes
      final InputStream in = asset.getStream();
      final byte[] content = IOUtil.asByteArray(in);

      // Put the new memory file in place
      MemoryFileFactory.putFile(url, content);
      if (log.isLoggable(Level.FINE))
      {
         log.fine("Added: " + url.toExternalForm());
      }

      // Return
      return this.covariantReturn();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#contains(org.jboss.declarchive.api.Path)
    */
   //TODO Add support into VFS for this
   @Override
   public boolean contains(final Path path) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(path, "No path was was specified");

      // Get the String form of the Path
      final URL url = this.urlFromPath(path);

      // Get the File at this Path
      final VirtualFile vf = this.getFile(url);

      // Determine if exists
      final boolean exists = vf != null;

      // Return
      return exists;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#delete(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean delete(final Path path) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(path, "No path was specified");

      // Ensure contains
      if (!this.contains(path))
      {
         if (log.isLoggable(Level.FINE))
         {
            log.fine("Not deleting non-existent path: " + path + " from " + this);
         }
         return false;
      }

      // Get the URL form of the Path
      final URL url = this.urlFromPath(path);

      // Delete and return
      final boolean deleted = MemoryFileFactory.delete(url);
      if (log.isLoggable(Level.FINE))
      {
         log.fine("Removed: " + url + " from " + this);
      }
      return deleted;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#get(org.jboss.declarchive.api.Path)
    */
   @Override
   public Asset get(final Path path) throws AssetNotFoundException, IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(path, "No path was was specified");

      // Get the URL form of the Path
      final URL url = this.urlFromPath(path);

      // Obtain the virtual file
      final VirtualFile vf = this.getFile(url);

      // Create an Asset from the contents of the file
      final Asset asset = this.getAsset(vf);

      // Return
      return asset;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#getContent()
    */
   @Override
   public Map<Path, Asset> getContent()
   {
      // Declare a Map for the content
      final Map<Path, Asset> content = new HashMap<Path, Asset>();

      // Obtain the root file
      final VirtualFile root = this.getRoot();

      // Populate the content Map
      this.populateContentMap(root, content);

      // Return
      return content;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains an Asset from the specified {@link VirtualFile}
    *  
    * @param vf
    * @throws IllegalArgumentException If the file is not specified
    */
   private Asset getAsset(final VirtualFile vf) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(vf, "No file was was specified");

      // Obtain URL of the File
      final URL rootUrl = this.getRootUrl();
      final String pathName = vf.getPathName();
      final URL url;
      try
      {
         url = new URL(rootUrl, pathName);
      }
      catch (final MalformedURLException murle)
      {
         throw new RuntimeException("Could not create URL for " + vf, murle);
      }

      // Create an Asset from the contents of the file
      final URLConnection conn = new VirtualFileURLConnection(url, vf);
      final InputStream in;
      try
      {
         in = conn.getInputStream();
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not obtain stream from " + conn, ioe);
      }
      final Asset asset = new ByteArrayAsset(in);

      // Return
      return asset;
   }

   /**
    * Populates the specified content Map with all children, recursively,
    * of the specified root
    * 
    * @param root
    * @param content
    */
   private void populateContentMap(final VirtualFile root, final Map<Path, Asset> content)
         throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNull(root, "No root was was specified");
      Validate.notNull(content, "No content map was was specified");

      // Obtain all children of this root
      final List<VirtualFile> files;
      try
      {
         files = root.getChildren();
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not obtain children for " + root, ioe);
      }

      // Define the internal root context name, which we ignore
      final String rootContextName = this.getRootUrl().getPath();
      int rootContextNameLength = rootContextName.length();

      // For each child
      for (final VirtualFile file : files)
      {
         // Don't add the internal context root
         boolean isInternalConextRoot = false;
         try
         {
            final VirtualFile parent = file.getParent();
            final VirtualFile internalRoot = this.getRoot();
            isInternalConextRoot = parent.equals(internalRoot);
         }
         catch (final IOException ioe)
         {
            throw new RuntimeException("Could not obtain parent of " + file, ioe);
         }

         // Only add stuff in this representation if this is not the 
         // internal context root
         if (!isInternalConextRoot)
         {
            // Populate the Map
            final String pathName = file.getPathName();
            // Strip out the internal context root name
            final String adjustedPathName = pathName.substring(rootContextNameLength);
            final Path path = new BasicPath(adjustedPathName);
            final Asset asset = this.getAsset(file);
            content.put(path, asset);

         }

         // ...and repeat for all children
         this.populateContentMap(file, content);
      }

   }

   /**
    * Obtains a URL internal to this archive from the
    * provided path
    * 
    * @param path
    * @return
    * @throws IllegalArgumentException
    */
   private URL urlFromPath(final Path path) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(path, "No path was was specified");

      // Get a URL for the target
      final String targetString = path.get();
      final URL rootUrl = this.getRootUrl();

      // Construct a new URL for this new memoryfile
      final URL url;
      try
      {
         final StringBuilder sb = new StringBuilder();
         sb.append(rootUrl.toExternalForm());
         sb.append(SEPARATOR);
         sb.append(targetString);
         url = new URL(sb.toString());
      }
      catch (final MalformedURLException murle)
      {
         throw new RuntimeException("Could not form URL for Path \"" + targetString + "\" in " + this, murle);
      }

      // Return
      return url;
   }

   /**
    * Obtains the VirtualFile located at the specified URL within this archive.
    * If no Asset has been added at this location, this method will return null
    *  
    * @param url
    * @return
    * @throws IllegalArgumentException If the url is not specified
    */
   private VirtualFile getFile(final URL url) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(url, "No url was specified");

      // Get the String form of the Path
      final String host = url.getHost();

      // Determine if this path exists
      final VFS vfs = MemoryFileFactory.find(host);
      if (vfs == null)
      {
         throw new AssetNotFoundException("Path does not exist: " + url);
      }

      // Obtain the virtual file
      final VirtualFile vf;
      final String urlPath = url.getPath();
      try
      {
         vf = vfs.getChild(urlPath);
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not obtain " + urlPath, ioe);
      }

      // Return
      return vf;
   }

}

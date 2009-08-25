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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.AssetNotFoundException;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.Validate;
import org.jboss.declarchive.spi.vfs.VfsArchive;
import org.jboss.virtual.MemoryFileFactory;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

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
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final int len = 1024;
      final byte[] buffer = new byte[len];
      try
      {

         while ((in.read(buffer) != -1))
         {
            out.write(buffer);
         }
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Error in adding asset to location: " + url.toExternalForm() + " in archive "
               + this.getName(), ioe);
      }
      finally
      {
         try
         {
            in.close();
         }
         catch (final IOException ignore)
         {

         }
         // We don't need to close the outstream, it's a byte array out
      }

      // Put the new memory file in place
      final byte[] content = out.toByteArray();
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
      final String pathString = url.toExternalForm();

      // Determine if this path exists
      final VFS vfs = MemoryFileFactory.find(pathString);
      final boolean contains = vfs != null;
      //      return contains;
      throw new UnsupportedOperationException("VFS " + MemoryFileFactory.class.getSimpleName()
            + " currently does not back this operation.");
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
   //TODO Add support into VFS for this
   @Override
   public Asset get(final Path path) throws AssetNotFoundException, IllegalArgumentException
   {
      throw new UnsupportedOperationException("VFS " + MemoryFileFactory.class.getSimpleName()
            + " currently does not back this operation.");
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#getContent()
    */
   //TODO Add support into VFS for this
   @Override
   public Map<Path, Asset> getContent()
   {
      throw new UnsupportedOperationException("VFS " + MemoryFileFactory.class.getSimpleName()
            + " currently does not back this operation.");
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

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

}

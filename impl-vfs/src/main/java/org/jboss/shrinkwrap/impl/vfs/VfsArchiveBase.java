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
package org.jboss.shrinkwrap.impl.vfs;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.impl.base.ArchiveBase;
import org.jboss.shrinkwrap.spi.vfs.VfsArchive;
import org.jboss.virtual.VirtualFile;

/**
 * VfsArchiveBase
 * 
 * Support for VFS-backed implementations of an {@link Archive}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
abstract class VfsArchiveBase extends ArchiveBase<VfsArchive> implements VfsArchive
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VfsArchiveBase.class.getName());

   /**
    * Newline character
    */
   private static final char NEWLINE = '\n';

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The root of the deployment
    */
   private VirtualFile root;

   /**
    * The URL to the root deployment
    */
   private URL rootUrl;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a {@link VfsArchive} with the specified name
    * 
    * @param name
    */
   protected VfsArchiveBase(final String name)
   {
      super(name);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#toString(boolean)
    */
   @Override
   public String toString(final boolean verbose)
   {
      // If we want verbose output
      if (verbose)
      {
         // Describe the root
         return this.describe(this.getRoot());
      }

      // Fall back on toString
      return this.toString();
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Initializes the archive with the specified root and root URL
    *  
    * @param root
    * @param rootUrl
    * @throws IllegalArgumentException If either the root or root URL is not specified
    */
   void initialize(final VirtualFile root, final URL rootUrl) throws IllegalArgumentException
   {
      // Precondition checks
      if (root == null)
      {
         throw new IllegalArgumentException("root is null");
      }
      if (rootUrl == null)
      {
         throw new IllegalArgumentException("rootUrl is null");
      }

      // Set properties
      this.root = root;
      this.rootUrl = rootUrl;
   }

   /**
    * Describes this file in form:
    * 
    * "/path/resource.ext - x bytes"
    * 
    * In addition to all children of the given file root.
    * 
    * @param file The root to describe
    * @return
    * @throws IllegalArgumentException If either the file or builder are not specified
    */
   private String describe(final VirtualFile file)
   {
      // Precondition checks
      if (file == null)
      {
         throw new IllegalArgumentException("file must be specified");
      }

      // Make a StringBuilder
      final StringBuilder sb = new StringBuilder();

      // Start the output
      sb.append(file);
      sb.append(NEWLINE);

      // Describe in depth
      return this.describe(file, sb);
   }

   /**
    * Describes this file in form:
    * 
    * "/path/resource.ext - x bytes"
    * 
    * In addition to all children of the given file root.
    * 
    * @param file The root to describe
    * @param sb The builder to which the description will be appended
    * @return
    * @throws IllegalArgumentException If either the file or builder are not specified
    */
   private String describe(final VirtualFile file, final StringBuilder sb) throws IllegalArgumentException
   {
      // Precondition checks
      if (file == null)
      {
         throw new IllegalArgumentException("file must be specified");
      }
      if (sb == null)
      {
         throw new IllegalArgumentException("builder must be specified");
      }

      // Get information for this root
      final String path = file.getPathName();
      final long size;
      try
      {
         size = file.getSize();
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not get size for: " + file, ioe);
      }

      // Append this information
      sb.append(path);
      sb.append(" - ");
      sb.append(size);
      sb.append(" bytes");
      sb.append(NEWLINE);

      // Recurse into any children
      final List<VirtualFile> children;
      try
      {
         children = file.getChildren();
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not obtain children for: " + file, ioe);
      }
      if (children != null && children.size() > 0)
      {
         for (final VirtualFile child : children)
         {
            this.describe(child, sb);
         }
      }

      // Return
      return sb.toString();
   }

   //-------------------------------------------------------------------------------------||
   // Accessors / Mutators ---------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @return the root
    */
   public final VirtualFile getRoot()
   {
      return root;
   }

   /**
    * Returns a copy of this root's URL (not the
    * actual reference as we want to protect internal 
    * state from mutation)
    * @return
    */
   protected final URL getRootUrl()
   {
      final String form = this.rootUrl.toExternalForm();
      try
      {
         return new URL(form);
      }
      catch (final MalformedURLException murle)
      {
         throw new RuntimeException("Error in copying URL as new URL: " + form);
      }
   }
}

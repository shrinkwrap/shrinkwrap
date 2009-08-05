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
package org.jboss.declarchive.impl.vfs;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.impl.base.ArchiveBase;
import org.jboss.virtual.VirtualFile;

/**
 * VfsArchiveBase
 * 
 * Support for VFS-backed implementations of an {@link Archive}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
abstract class VfsArchiveBase extends ArchiveBase
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
    * Constructor
    * 
    * Creates a new instance with the specified root and rootURL
    * 
    * @param root
    * @param rootUrl
    * @param cl
    */
   public VfsArchiveBase(final ClassLoader cl)
   {
      // Invoke super
      super(cl);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.embedded.core.deployment.ExtensibleVirtualDeployment#toString(boolean)
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
   final URL getRootUrl()
   {
      return this.copyURL(this.rootUrl);
   }
}

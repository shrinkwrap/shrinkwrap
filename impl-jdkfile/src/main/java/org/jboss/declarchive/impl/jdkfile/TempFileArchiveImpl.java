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
package org.jboss.declarchive.impl.jdkfile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.impl.base.ArchiveBase;
import org.jboss.declarchive.spi.jdk.file.FileArchive;

/**
 * TempFileArchiveImpl
 * 
 * Concrete implementation of an {@link Archive} which
 * stores contents in a directory structure in the temp directory
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class TempFileArchiveImpl extends ArchiveBase implements FileArchive
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(TempFileArchiveImpl.class.getName());

   /**
    * Prefix to be applied to the archive temp directory
    */
   private static final String PREFIX_TEMP = "archive";

   /**
    * Empty String
    */
   private static final String EMPTY_STRING = "";

   /**
    * Root of all virtual archives
    */
   private static final File archivesRoot;

   /*
    * Initialize the temp directory location once on load/init
    */
   static
   {
      try
      {

         // Make a new temp file to get unique namespace
         final File tempStub = File.createTempFile(PREFIX_TEMP, EMPTY_STRING);
         // Delete it
         tempStub.delete();
         // Make a new directory in the place of the temp file
         final File temp = new File(tempStub.getAbsolutePath());
         if (!temp.mkdir())
         {
            throw new RuntimeException("Could not create the temp virtual archive location at: "
                  + temp.getAbsolutePath());
         }
         // Mark the archives to delete on exit
         temp.deleteOnExit();
         log.log(Level.FINE, "Archives root: " + temp.getAbsolutePath());
         // Set the archives root
         archivesRoot = temp;
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not create the temp location for virtual archives", ioe);
      }
   }

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Root of the archive
    */
   private final File root;

   //-------------------------------------------------------------------------------------||
   // Constructors -----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    * 
    * Creates a new instance using the Thread Context  
    * ClassLoader.
    * 
    * @param name Unique name for the deployment
    * @throws IllegalArgumentException If the name was not specified
    */
   public TempFileArchiveImpl(final String name) throws IllegalArgumentException
   {
      this(name, SecurityActions.getThreadContextClassLoader());
   }

   /**
    * Constructor
    * 
    * @param name Unique name for the deployment
    * @param cl ClassLoader to be used in loading resources and classes
    * @throws IllegalArgumentException If the name or ClassLoader was not specified
    */
   public TempFileArchiveImpl(final String name, final ClassLoader cl) throws IllegalArgumentException
   {
      // Invoke super
      super(cl);

      // Precondition Check
      if (name == null || name.length() == 0)
      {
         throw new IllegalArgumentException("name must be specified");
      }
      if (cl == null)
      {
         throw new IllegalArgumentException("ClassLoader must be specified");
      }

      /*
       * Set root
       */

      // Make pointer
      final File root = new File(archivesRoot, name);
      if (!root.mkdir())
      {
         throw new RuntimeException("Could not create new temp file virtual archive root: " + root.getAbsolutePath());
      }

      // Set
      this.root = root;

      // Delete it on exit
      this.deleteOnExit(root);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /*
    * (non-Javadoc)
    * @see org.jboss.embedded.core.incubation.virtual.impl.base.AbstractVirtualArchive#addContent(byte[], java.lang.String)
    */
   @Override
   protected void addContent(final byte[] content, final String location) throws IllegalArgumentException
   {
      // Make the new pointer
      final File newFile = new File(this.getRoot(), location);
      this.deleteOnExit(newFile);
      final String newPath = newFile.getAbsolutePath();

      // Ensure the parent location exists, or we can make it
      final File parent = newFile.getParentFile();
      if (!parent.exists() && !parent.mkdirs())
      {
         throw new RuntimeException("Could not make " + parent.getAbsolutePath());
      }

      // Get an OutputStream to write into the buffer
      final BufferedOutputStream out;
      try
      {
         out = new BufferedOutputStream(new FileOutputStream(newFile));
      }
      catch (final FileNotFoundException e)
      {
         throw new RuntimeException("Could not obtain file: " + newPath, e);
      }

      // Write
      try
      {
         out.write(content);
         log.log(Level.FINE, "Wrote " + content.length + " bytes to " + newPath);
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Error in writing to file " + newPath, ioe);
      }
      // Close
      finally
      {
         try
         {
            out.close();
         }
         catch (final IOException e)
         {
            // Ignore
         }
      }
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.embedded.core.incubation.virtual.api.VirtualArchive#toString(boolean)
    */
   @Override
   public String toString(final boolean verbose)
   {
      // Short form
      if (!verbose)
      {
         return this.toString();
      }
      // Verbose
      else
      {
         //TODO
         log.log(Level.WARNING, "Must implement the verbose form of toString()");
         return this.toString();
      }
   }

   /* (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return super.toString() + ": " + this.getRoot().getAbsolutePath();
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.embedded.core.incubation.virtual.spi.jdk.VirtualJdkArchive#getRoot()
    */
   @Override
   public File getRoot()
   {
      return this.root;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Marks the specified File, and all parents of that file up to the
    * root of this archive, to delete upon JVM normal exit as documented by
    * {@link File#deleteOnExit()}
    * 
    * @param File the File, and parents to to the root, to delete
    * @throws IllegalArgumentException If the specified file is null
    * @throws IllegalStateException If the root is not yet set
    */
   private void deleteOnExit(final File file) throws IllegalArgumentException, IllegalStateException
   {
      // Precondition check
      assert file != null : "file was null";

      // Get the root
      final File root = archivesRoot;
      if (root == null)
      {
         throw new IllegalStateException("root must first be set");
      }

      // If this is the root, exit
      if (file.equals(root))
      {
         return;
      }

      // Delete the parent (if not the root)
      final File parent = file.getParentFile();
      this.deleteOnExit(parent);

      // Mark to delete
      file.deleteOnExit();
      log.log(Level.FINE, "Going to delete on exit: " + file.getAbsolutePath());
   }

}

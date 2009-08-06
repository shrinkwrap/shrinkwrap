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
import java.net.MalformedURLException;
import java.net.URL;

import org.jboss.declarchive.spi.vfs.VfsArchive;
import org.jboss.logging.Logger;
import org.jboss.virtual.MemoryFileFactory;
import org.jboss.virtual.VFS;
import org.jboss.virtual.VirtualFile;

/**
 * MemoryArchiveImpl
 * 
 * Concrete implementation of a VFS-backed virtual archive which
 * stores contents in-memory
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class MemoryArchiveImpl extends VfsArchiveBase implements VfsArchive
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(MemoryArchiveImpl.class);

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
    * Creates a new instance using the Thread Context  
    * ClassLoader.
    * 
    * @param name Unique name for the deployment
    * @throws IllegalArgumentException If the name was not specified
    */
   public MemoryArchiveImpl(final String name) throws IllegalArgumentException
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
   public MemoryArchiveImpl(final String name, final ClassLoader cl) throws IllegalArgumentException
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
    * Puts the specified content at the specified location as a memory file
    * @param content
    * @param location
    * @throws IllegalArgumentException
    */
   void addContent(final byte[] content, final URL location) throws IllegalArgumentException
   {
      // Precondition check
      if (content == null)
      {
         throw new IllegalArgumentException("content must be specified");
      }
      if (location == null)
      {
         throw new IllegalArgumentException("location must be specified");
      }

      // Put the new memory file in place
      MemoryFileFactory.putFile(location, content);
      log.debug("Added: " + location);

   }

   /*
    * (non-Javadoc)
    * @see org.jboss.embedded.core.incubation.virtual.impl.base.AbstractVirtualArchive#addContent(byte[], java.lang.String)
    */
   @Override
   protected void addContent(final byte[] content, final String location) throws IllegalArgumentException
   {
      // Precondition check
      if (content == null)
      {
         throw new IllegalArgumentException("content must be specified");
      }
      if (location == null || location.length() == 0)
      {
         throw new IllegalArgumentException("location must be specified");
      }

      // Get the root URL of the memory file
      final URL rootUrl = this.getRootUrl();

      // Construct a new URL for this new memoryfile
      URL url = null;
      try
      {
         final StringBuilder sb = new StringBuilder();
         sb.append(rootUrl.toExternalForm());
         sb.append(SEPARATOR);
         sb.append(location);
         url = new URL(sb.toString());
      }
      catch (final MalformedURLException murle)
      {
         throw new RuntimeException("Could not form URL for new resource \"" + location + "\" in " + this, murle);
      }

      // Add the content
      this.addContent(content, url);
   }

}

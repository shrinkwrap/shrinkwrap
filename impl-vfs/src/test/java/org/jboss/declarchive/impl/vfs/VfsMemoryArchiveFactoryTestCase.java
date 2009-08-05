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

import java.util.logging.Logger;

import junit.framework.Assert;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.VfsMemoryArchiveFactory;
import org.jboss.declarchive.spi.vfs.VfsArchive;
import org.jboss.virtual.VFS;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * VfsMemoryArchiveFactoryTestCase
 * 
 * Test Cases to assert that the {@link VfsMemoryArchiveFactory} is 
 * working correctly
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class VfsMemoryArchiveFactoryTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VfsMemoryArchiveFactoryTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Initializes the virtual file system
    */
   @BeforeClass
   public static void initVfs() throws Exception
   {
      // Init VFS
      VFS.init();
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Tests that using a {@link testVirtualArchiveFactory} to create 
    * an archive results in a new type backed by VFS
    */
   @Test
   public void testVirtualArchiveFactory() throws Exception
   {
      // Log
      log.info("testVirtualArchiveFactory");

      // Make an archive
      final Archive archive = VfsMemoryArchiveFactory.createVirtualArchive("testArchive.jar");
      archive.addClass(VfsMemoryArchiveFactory.class);
      log.info("Archive: " + archive.toString(true));

      // Ensure exists
      Assert.assertNotNull("Archive was not created/null", archive);
      // Ensure of expected type
      Assert.assertTrue("Created archive was not of expected type", archive instanceof VfsArchive);
   }
}

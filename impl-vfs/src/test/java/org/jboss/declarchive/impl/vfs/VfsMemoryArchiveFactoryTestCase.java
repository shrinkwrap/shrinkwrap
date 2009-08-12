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

import java.util.logging.Logger;

import junit.framework.Assert;

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
      final VfsArchive archive = VfsMemoryArchiveFactory.createVirtualArchive("testArchive.jar", VfsArchive.class);
      archive.addClass(VfsMemoryArchiveFactory.class);
      log.info("Archive: " + archive.toString(true));

      // Ensure exists
      Assert.assertNotNull("Archive was not created/null", archive);
      // Ensure of expected type
      Assert.assertTrue("Created archive was not of expected type", archive instanceof VfsArchive);
   }
}

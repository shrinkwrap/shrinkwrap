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

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.asset.ClassAsset;
import org.jboss.declarchive.impl.base.path.BasicPath;
import org.jboss.declarchive.spi.vfs.VfsArchive;
import org.jboss.virtual.VFS;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * VfsMemoryArchiveTestCase
 * 
 * Test Cases to ensure that Virtual Archives backed by in-memory VFS are
 * working as expected
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
//TODO Build upon a common test base just as the MemoryMap impl uses,
// and swap in a method to get the VFS Memory Archive impl
public class VfsMemoryArchiveTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VfsMemoryArchiveTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Initializes the virtual filesystem
    */
   @BeforeClass
   public static void initVfs() throws Exception
   {
      VFS.init();
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Used in building the impl, not a true test yet
    */
   @Test
   //TODO Implement this test
   public void testMuckingAroundPrototypesNotARealTestYet() throws Exception
   {
      // Log
      log.info("testMuckingAroundPrototypesNotARealTestYet");


      // Make a virtual archive
      final VfsArchive archive = new VfsMemoryArchiveImpl("something.jar");
      final Path path = new BasicPath("something");
      archive.add(path, new ClassAsset(this.getClass()));
      final Path elsePath = new BasicPath("somethingelse");
      archive.add(elsePath, new ClassAsset(VfsMemoryArchiveImpl.class));
      log.info(archive.toString(true));
      archive.delete(elsePath);
      log.info(archive.toString(true));
      final Asset retrieved = archive.get(path);
      final boolean exists = archive.contains(path);
      log.info(path + " exists: " + exists);
      final Path fakePath = new BasicPath("shouldntexist");
      log.info(fakePath + " exists: " + archive.contains(fakePath));
      log.info(retrieved.toString());
      log.info("Contents: "+ archive.getContent());
   }
}

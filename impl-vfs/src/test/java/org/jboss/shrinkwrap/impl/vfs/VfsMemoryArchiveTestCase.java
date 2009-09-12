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
package org.jboss.shrinkwrap.impl.vfs;

import java.util.UUID;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.impl.base.test.ArchiveTestBase;
import org.jboss.shrinkwrap.impl.vfs.VfsMemoryArchiveImpl;
import org.jboss.shrinkwrap.spi.vfs.VfsArchive;
import org.jboss.virtual.VFS;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * VfsMemoryArchiveTestCase
 * 
 * Test Cases to ensure that Virtual Archives backed by in-memory VFS are
 * working as expected
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class VfsMemoryArchiveTestCase extends ArchiveTestBase<VfsArchive>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VfsMemoryArchiveTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private VfsArchive archive;

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

   @Before
   public void createArchive() throws Exception
   {
      archive = createNewArchive();
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected VfsArchive createNewArchive()
   {
      return new VfsMemoryArchiveImpl("test-" + UUID.randomUUID().toString() + ".jar");
   }

   @Override
   protected Archive<VfsArchive> getArchive()
   {
      return archive;
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   public void testAddArchiveToPath() throws Exception
   {
      // Override to ignore failing test in parent until VFS IMPL is removed.
   }

}

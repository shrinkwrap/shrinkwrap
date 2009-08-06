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

import java.net.URL;

import org.jboss.declarchive.api.Archive;
import org.jboss.logging.Logger;
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
public class VfsMemoryArchiveTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VfsMemoryArchiveTestCase.class);

   /**
    * Path to "WEB-INF" 
    */
   private static final String PATH_WEB_INF = "WEB-INF";

   /**
    * Filename of web.xml
    */
   private static final String FILENAME_WEB_XML = "web.xml";

   /**
    * Path, relative to the resources base, of a test XML file
    */
   private static final String PATH_SOMETHING_XML = "xml/something.xml";

   /**
    * Separator character within archives
    */
   private static final char SEPARATOR = '/';

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

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
    * Tests that a resource may be added to an archive from a given location, 
    * and assigned a new path within the archive
    */
   @Test
   public void testAddResourceExplicitPathNameMemory() throws Exception
   {
      // Log
      log.info("testAddResourceExplicitPathNameMemory");

      // Get the base
      final URL base = this.getBase();

      // Get the path to the test XML file
      final URL location = new URL(base, PATH_SOMETHING_XML);

      // Define the new path
      final String newPath = PATH_WEB_INF + SEPARATOR + FILENAME_WEB_XML;

      // Make a virtual archive
      final Archive archive = new MemoryArchiveImpl("something.war").addResource(location, newPath);
      log.info(archive.toString(true));

      //TODO Actually test something when we have better hooks to examine archive contents
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the test resources base
    */
   private URL getBase() throws Exception
   {
      return this.getClass().getProtectionDomain().getCodeSource().getLocation();
   }
}

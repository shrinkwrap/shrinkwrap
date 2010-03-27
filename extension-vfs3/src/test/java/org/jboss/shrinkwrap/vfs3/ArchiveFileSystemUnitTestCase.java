/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.vfs3;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.vfs.TempDir;
import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests to ensure the {@link ArchiveFileSystem}
 * is working as contracted
 * 
 * @author <a href="jbailey@redhat.com">John Bailey</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ArchiveFileSystemUnitTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ArchiveFileSystemUnitTestCase.class.getName());

   /**
    * Name of the test archive
    */
   private static final String NAME_ARCHIVE = "test.jar";

   private static TempFileProvider tempFileProvider = null;

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Handles to close when test is done
    */
   private final List<Closeable> vfsHandles = new ArrayList<Closeable>();

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Sets up the temporary file provider
    */
   @BeforeClass
   public static void createTempFileProvider() throws IOException
   {
      tempFileProvider = TempFileProvider.create("shrinkwrap-", Executors.newSingleThreadScheduledExecutor());
   }

   /**
    * Closes handles marked open
    */
   @After
   public void closeHandles()
   {
      for (final Closeable handle : vfsHandles)
      {
         try
         {
            handle.close();
         }
         catch (final IOException ioe)
         {
            log.warning("Could not close " + handle + ": " + ioe);
         }
      }
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Adds some files/directories to an {@link Archive}, then reads via the 
    * {@link ArchiveFileSystem} 
    */
   @Test
   public void testArchiveFileSystem() throws Exception
   {
      final JavaArchive archive = Archives.create(NAME_ARCHIVE, JavaArchive.class);

      // Back VFS by a temp directory
      final TempDir tempDir = tempFileProvider.createTempDir(archive.getName());
      VirtualFile virtualFile = VFS.getChild(UUID.randomUUID().toString()).getChild(archive.getName());
      vfsHandles.add(VFS.mount(virtualFile, new ArchiveFileSystem(archive, tempDir)));

      // Add to the archive
      archive.addResource("META-INF/test", "META-INF/test");
      archive.addResource("test", "test");
      log.info("Using archive: " + archive.toString(true));

      // Perform assertions
      Assert.assertTrue("Could not read file", virtualFile.getChild("test").isFile());
      Assert.assertTrue("Could not read directory", virtualFile.getChild("META-INF").isDirectory());
      Assert.assertTrue("Could not read file in a directory", virtualFile.getChild("META-INF").getChild("test")
            .isFile());

   }
}

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
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.TestIOUtil;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Assert;

/**
 * ExportTestBase
 * 
 * Base support for the exporter test cases 
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public abstract class ExportTestBase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ExportTestBase.class.getName());

   /**
    * Name of an Archive
    */
   protected static final String NAME_ARCHIVE = "testArchive";

   /**
    * Name of a properties file upon the test CP
    */
   protected static final String NAME_TEST_PROPERTIES = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

   /**
    * Name of another properties file upon the test CP
    */
   protected static final String NAME_TEST_PROPERTIES_2 = "org/jboss/shrinkwrap/impl/base/asset/Test2.properties";

   /**
    * Path of for nested content
    */
   protected static final ArchivePath NESTED_PATH = new BasicPath("nested");

   /**
    * Name of a nested archive
    */
   protected static final String NAME_NESTED_ARCHIVE = "nestedArchive";

   /**
    * Name of another nested archive
    */
   protected static final String NAME_NESTED_ARCHIVE_2 = "nestedArchive2";

   /** 
    * Asset used for testing
    */
   protected static final Asset ASSET_ONE = new ClassLoaderAsset(NAME_TEST_PROPERTIES);

   /** 
    * Path used for testing
    */
   protected static final ArchivePath PATH_ONE = new BasicPath("Test.properties");

   /** 
    * Another asset used for testing
    */
   protected static final Asset ASSET_TWO = new ClassLoaderAsset(NAME_TEST_PROPERTIES_2);

   /** 
   * Another path used for testing
   */
   protected static final ArchivePath PATH_TWO = new BasicPath(NESTED_PATH, "Test2.properties");

   /**
    * Path to a nested empty directory
    */
   protected static final ArchivePath PATH_EMPTY_NESTED_DIR = ArchivePaths.create("/empty");

   /**
    * Path to an empty directory, a child of the nested
    */
   protected static final ArchivePath PATH_EMPTY_TOPLEVEL_DIR = ArchivePaths.create("/empty/directory");

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /** 
    * Returns a temp directory for a test.  Needs the test
    */
   protected File createTempDirectory(String testName) throws IOException
   {
      // Qualify the temp directory by test case
      File tempDirectoryParent = new File(this.getTarget(), this.getClass().getSimpleName());
      // Qualify the temp directory by test name
      File tempDirectory = new File(tempDirectoryParent, testName);
      log.info("Temp Directory: " + tempDirectory.getCanonicalPath());
      if (tempDirectory.exists())
      {
         TestIOUtil.deleteDirectory(tempDirectory);
      }
      Assert.assertTrue("Temp directory should be clear before start", !tempDirectory.exists());
      final boolean created = tempDirectory.mkdirs();
      Assert.assertEquals("Could not create temp directory for tests: " + tempDirectory.getAbsolutePath(), true,
            created);
      return tempDirectory;
   }

   /**
    * Returns the target directory 
    */
   protected File getTarget()
   {
      try
      {
         return new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()).getParent());
      }
      catch (final URISyntaxException urise)
      {
         throw new RuntimeException("Could not obtain the target URI", urise);
      }
   }

   /**
    * Create an archive instance and add some assets
    */
   protected Archive<?> createArchiveWithAssets()
   {
      // Create an archive
      Archive<?> archive = ShrinkWrap.create(JavaArchive.class, NAME_ARCHIVE + getArchiveExtension());
      // Add some content
      addContent(archive);
      // Return archive
      return archive;
   }

   /**
    * Obtains the extension for created archives
    * @return
    */
   protected abstract String getArchiveExtension();

   /**
    * Returns the exporter type
    * @return
    */
   protected abstract Class<? extends StreamExporter> getExporterClass();

   /**
    * Create an archive instance and add some assets and some nested archives
    */
   protected Archive<?> createArchiveWithNestedArchives()
   {
      // Create an archive
      Archive<?> archive = createArchiveWithAssets();

      // Create a nested archive
      Archive<?> nestedArchive = ShrinkWrap.create(JavaArchive.class, NAME_NESTED_ARCHIVE + getArchiveExtension());

      // Add some content
      addContent(nestedArchive);

      // Add nested archive
      archive.add(nestedArchive, ArchivePaths.root(), this.getExporterClass());

      // Add an archive nested in a directory
      Archive<?> nestedArchiveTwo = ShrinkWrap.create(JavaArchive.class, NAME_NESTED_ARCHIVE_2 + getArchiveExtension());

      // Add some content
      addContent(nestedArchiveTwo);

      // Add the archive under a nested path
      archive.add(nestedArchiveTwo, NESTED_PATH, this.getExporterClass());

      // Add empty directories
      archive.addAsDirectory(PATH_EMPTY_NESTED_DIR);
      archive.addAsDirectory(PATH_EMPTY_TOPLEVEL_DIR);

      // Return archive
      return archive;
   }

   /**
    * Add basic contents to the archive
    * 
    * @param archive
    */
   protected void addContent(Archive<?> archive)
   {
      archive.add(ASSET_ONE, PATH_ONE);
      archive.add(ASSET_TWO, PATH_TWO);
   }

}

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
package org.jboss.shrinkwrap.impl.base.importer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Test;


/**
 * ZipImporterImplTest
 * 
 * TestCase to verify the ZipImporter functionality.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ZipImporterImplTestCase
{
   
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipImporterImplTestCase.class.getName());

   private static final String EXISTING_ZIP_RESOURCE = "org/jboss/shrinkwrap/impl/base/importer/test.zip";
   
   private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";
   
   /**
    * Name of the expected empty directory
    */
   private static final String EXPECTED_EMPTY_DIR ="empty_dir/";
   
   /**
    * Name of the expected nested directory
    */
   private static final String EXPECTED_NESTED_EMPTY_DIR ="parent/empty_dir/";
   
   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   @Test
   public void shouldBeAbleToimportZipFile() throws Exception 
   {
      ZipFile testZip = new ZipFile(
            new File(
                  SecurityActions.getThreadContextClassLoader().getResource(EXISTING_ZIP_RESOURCE).toURI()));
      
      Archive<?> archive = Archives.create("test.jar", ZipImporter.class)
                                 .importZip(testZip)
                              .as(JavaArchive.class);
      
      Assert.assertNotNull("Should not return a null archive", archive);
      
      assertContent(
            archive, 
            SecurityActions.getThreadContextClassLoader().getResource(EXISTING_ZIP_RESOURCE).toURI());
   }
   

   @Test
   public void shouldBeAbleToImportAddAndExport() throws Exception
   {
      ZipInputStream stream = new ZipInputStream(
            SecurityActions.getThreadContextClassLoader().getResourceAsStream(EXISTING_ZIP_RESOURCE));
      
      Archive<?> archive = Archives.create("test.jar", ZipImporter.class)
                                 .importZip(stream)
                              .as(JavaArchive.class);

      Assert.assertNotNull("Should not return a null archive", archive);

      archive.add(new ClassLoaderAsset(EXISTING_RESOURCE), new BasicPath("test.properties"));
      
      File tempFile = new File("target/test.zip");
      tempFile.deleteOnExit();
      InputStream zipStream = archive.as(ZipExporter.class).exportZip();
      IOUtil.copyWithClose(zipStream, new FileOutputStream(tempFile));
      
      assertContent(archive, tempFile.toURI());
   }
   
   @Test
   public void shouldBeAbleToImportZipInputStream() throws Exception
   {
      ZipInputStream stream = new ZipInputStream(
            SecurityActions.getThreadContextClassLoader().getResourceAsStream(EXISTING_ZIP_RESOURCE));
      
      Archive<?> archive = Archives.create("test.jar", ZipImporter.class)
                                 .importZip(stream)
                              .as(JavaArchive.class);

      Assert.assertNotNull("Should not return a null archive", archive);
      
      assertContent(
            archive, 
            SecurityActions.getThreadContextClassLoader().getResource(EXISTING_ZIP_RESOURCE).toURI());
   }
   
   /**
    * Compare the content of the original file and what was imported.
    * 
    * @param importedArchive The archive used for import
    * @param originalSource The original classpath resource file
    */
   private void assertContent(Archive<?> importedArchive, URI originalSource) throws Exception
   {
      Assert.assertFalse(
            "Should have imported something",
            importedArchive.getContent().isEmpty());
      
      ZipFile testZip = new ZipFile(
            new File(originalSource));

      List<? extends ZipEntry> entries = Collections.list(testZip.entries());
      
      Assert.assertFalse(
            "Test zip should contain data", 
            entries.isEmpty());
      Assert.assertEquals(
            "Should have imported all files and directories",
            entries.size(),
            importedArchive.getContent().size());
      
      
      boolean containsEmptyDir = false;
      boolean containsEmptyNestedDir = false;
      
      for(ZipEntry originalEntry : entries) 
      {
         
         if(originalEntry.isDirectory()) 
         {
            // Check for expected empty dirs
            if (originalEntry.getName().equals(EXPECTED_EMPTY_DIR))
            {
               containsEmptyDir = true;
            }
            if (originalEntry.getName().equals(EXPECTED_NESTED_EMPTY_DIR))
            {
               containsEmptyNestedDir = true;
            }
            continue;
         }

         Assert.assertTrue(
               "Importer should have imported " + originalEntry.getName() + " from " + originalSource,
               importedArchive.contains(new BasicPath(originalEntry.getName())));
         
         byte[] originalContent = IOUtil.asByteArray(testZip.getInputStream(originalEntry));
         byte[] importedContent = IOUtil.asByteArray(
               importedArchive.get(new BasicPath(originalEntry.getName())).openStream());

         log.fine(
               Arrays.equals(importedContent, originalContent) + "\t" +
               originalContent.length + "\t" +
               importedContent.length + "\t" +
               originalEntry.getName());
         
         Assert.assertTrue(
               "The content of " + originalEntry.getName() + " should be equal to the imported content",
               Arrays.equals(importedContent, originalContent));
      }
      
      // Ensure empty directories have come in cleanly
      Assert.assertTrue("Empty directory not imported", containsEmptyDir);
      Assert.assertTrue("Empty nested directory not imported", containsEmptyNestedDir);
   }
}

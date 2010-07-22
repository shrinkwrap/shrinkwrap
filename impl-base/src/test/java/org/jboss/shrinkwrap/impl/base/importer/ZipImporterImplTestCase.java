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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Test;

/**
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
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(ZipImporterImplTestCase.class.getName());

   private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

   /**
    * Delegate for performing ZIP content assertions
    */
   private static final ZipContentAssertionDelegate delegate = new ZipContentAssertionDelegate();

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Test
   public void shouldBeAbleToimportZipFile() throws Exception
   {
      final File testFile = delegate.getExistingZipResource();
      ZipFile testZip = new ZipFile(testFile);

      Archive<?> archive = ShrinkWrap.create(ZipImporter.class, "test.jar").importZip(testZip).as(JavaArchive.class);

      Assert.assertNotNull("Should not return a null archive", archive);

      delegate.assertContent(archive, testFile);
   }

   @Test
   public void shouldBeAbleToImportAddAndExport() throws Exception
   {
      final File testFile = delegate.getExistingZipResource();
      ZipInputStream stream = new ZipInputStream(new FileInputStream(testFile));

      final Archive<?> archive;
      try
      {
         archive = ShrinkWrap.create(ZipImporter.class, "test.jar").importZip(stream).as(JavaArchive.class);
      }
      finally
      {
         stream.close();
      }

      Assert.assertNotNull("Should not return a null archive", archive);

      archive.add(new ClassLoaderAsset(EXISTING_RESOURCE), new BasicPath("test.properties"));

      File tempFile = new File("target/test.zip");
      tempFile.deleteOnExit();
      final InputStream zipStream = archive.as(ZipExporter.class).export();
      IOUtil.copyWithClose(zipStream, new FileOutputStream(tempFile));

      delegate.assertContent(archive, tempFile);
   }

   @Test
   public void shouldBeAbleToImportZipInputStream() throws Exception
   {
      final File testFile = delegate.getExistingZipResource();
      ZipInputStream stream = new ZipInputStream(new FileInputStream(testFile));

      final Archive<?> archive;
      try
      {
         archive = ShrinkWrap.create(ZipImporter.class, "test.jar").importZip(stream).as(JavaArchive.class);
      }
      finally
      {
         stream.close();
      }

      Assert.assertNotNull("Should not return a null archive", archive);

      delegate.assertContent(archive, testFile);
   }

   /**
    * Ensures that an import of {@link InputStream} results in {@link ArchiveImportException}
    * if an unexpected error occurred.
    * @throws Exception
    */
   @Test(expected = ArchiveImportException.class)
   public void shouldThrowExceptionOnErrorInImportFromStream() throws Exception
   {
      ZipInputStream stream = new ZipInputStream(new InputStream()
      {
         @Override
         public int read() throws IOException
         {
            throw new IOException("Mock exception");
         }
      });
      try
      {
         ShrinkWrap.create(ZipImporter.class, "test.jar").importZip(stream).as(JavaArchive.class);
      }
      finally
      {
         stream.close();
      }
   }

   /**
    * Ensures that an import of {@link ZipFile} results in {@link ArchiveImportException}
    * if an unexpected error occurred.
    * @throws Exception
    */
   @Test(expected = ArchiveImportException.class)
   public void shouldThrowExceptionOnErrorInImportFromFile() throws Exception
   {
      final File testFile = delegate.getExistingZipResource();
      ZipFile testZip = new ZipFile(testFile)
      {
         @Override
         public Enumeration<? extends ZipEntry> entries()
         {
            throw new IllegalStateException("mock  exception");
         }
      };
      ShrinkWrap.create(ZipImporter.class, "test.jar").importZip(testZip).as(JavaArchive.class);
   }
}

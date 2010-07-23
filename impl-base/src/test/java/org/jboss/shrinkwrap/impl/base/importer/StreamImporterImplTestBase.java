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
import java.util.logging.Logger;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.Test;

/**
 * Base upon which tests of {@link StreamImporter}
 * implementations may build
 *
 * @param <T> Type of importer under test
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public abstract class StreamImporterImplTestBase<T extends StreamImporter<?, T>>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(StreamImporterImplTestBase.class.getName());

   /**
    * Name of an existing resource on the ClassPath
    */
   private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the delegate used in asserting imported content is as expected 
    */
   protected abstract ContentAssertionDelegateBase getDelegate();

   /**
    * Obtains the importer type used by these tests
    * @return
    */
   protected abstract Class<T> getImporterClass();

   /**
    * Exporter used for roundtrip testing import/export/import
    * @return
    */
   protected abstract Class<? extends StreamExporter> getExporterClass();

   /**
    * Imports the specified generic {@link InputStream} using the
    * specified importer as an archive, returning the importer
    * @param importer
    * @param in
    * @return
    * @throws IllegalArgumentException If either argument was not specified
    */
   protected abstract T importFromStream(T importer, InputStream in) throws IllegalArgumentException;

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that we may import a file and create an archive 
    * with matching structure
    */
   @Test
   public void shouldBeAbleToImportFile() throws Exception
   {
      // Get the delegate
      final ContentAssertionDelegateBase delegate = this.getDelegate();
      assert delegate != null : "Delegate must be specified by implementations";
      final File testFile = delegate.getExistingResource();

      // Import
      final Class<? extends StreamImporter<?, ?>> importerClass = this.getImporterClass();
      assert importerClass != null : "Importer class must be specified by implementations";
      Archive<?> archive = ShrinkWrap.create(importerClass, "test.jar").importFrom(testFile).as(JavaArchive.class);

      // Ensure we don't have a null archive
      Assert.assertNotNull("Should not return a null archive", archive);

      // Validate the contents of the imported archive match that of the file from
      // which it was created
      delegate.assertContent(archive, testFile);
   }

   /**
    * Ensures that we may import an archive, add content to it, 
    * export, and that the added content has been reflected
    * to the exported view.
    * @throws Exception
    */
   @Test
   public void shouldBeAbleToImportAddAndExport() throws Exception
   {
      // Get the delegate
      final ContentAssertionDelegateBase delegate = this.getDelegate();
      assert delegate != null : "Delegate must be specified by implementations";
      final File testFile = delegate.getExistingResource();

      // Import from file
      final Class<? extends StreamImporter<?, ?>> importerClass = this.getImporterClass();
      assert importerClass != null : "Importer class must be specified by implementations";
      final Archive<?> archive = ShrinkWrap.create(importerClass, "test.jar").importFrom(testFile)
            .as(JavaArchive.class);
      Assert.assertNotNull("Should not return a null archive", archive);

      // Add a new resource
      archive.add(new ClassLoaderAsset(EXISTING_RESOURCE), ArchivePaths.create("test.properties"));

      // Export
      File tempFile = new File("target/testOutput");
      tempFile.deleteOnExit();
      final Class<? extends StreamExporter> exporterClass = this.getExporterClass();
      Assert.assertNotNull("Exporter class must be specified by implementations", exporterClass);
      final InputStream stream = archive.as(exporterClass).exportAsInputStream();
      IOUtil.copyWithClose(stream, new FileOutputStream(tempFile));

      // Ensure the exported view matches that of the archive
      delegate.assertContent(archive, tempFile);
   }

   /**
    * Ensures that we may import an archive as a stream,
    * and the contents will be as expected
    * @throws Exception
    */
   @Test
   public void shouldBeAbleToImportInputStream() throws Exception
   {
      // Get the delegate
      final ContentAssertionDelegateBase delegate = this.getDelegate();
      assert delegate != null : "Delegate must be specified by implementations";
      final File testFile = delegate.getExistingResource();

      // Get the input as a stream
      InputStream stream = new FileInputStream(testFile);

      // Get the importer
      final Class<T> importerClass = this.getImporterClass();
      assert importerClass != null : "Importer class must be specified by implementations";

      // Import as a stream
      final T importer = ShrinkWrap.create(importerClass, "test.jar");
      final Archive<?> archive;
      try
      {
         archive = this.importFromStream(importer, stream).as(GenericArchive.class);
      }
      finally
      {
         stream.close();
      }
      Assert.assertNotNull("Should not return a null archive", archive);

      // Ensure the archive matches the file input
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
      final InputStream exceptionIn = new InputStream()
      {
         @Override
         public int read() throws IOException
         {
            throw new IOException("Mock exception");
         }
      };

      // Get the importer
      final Class<T> importerClass = this.getImporterClass();
      assert importerClass != null : "Importer class must be specified by implementations";
      final T importer = ShrinkWrap.create(importerClass, "test.jar");
      try
      {
         this.importFromStream(importer, exceptionIn).as(GenericArchive.class);
      }
      finally
      {
         exceptionIn.close();
      }
   }
}

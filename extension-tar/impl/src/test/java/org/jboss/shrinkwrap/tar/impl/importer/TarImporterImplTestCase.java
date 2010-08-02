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
package org.jboss.shrinkwrap.tar.impl.importer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.impl.base.importer.ContentAssertionDelegateBase;
import org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase;
import org.jboss.shrinkwrap.tar.api.exporter.TarExporter;
import org.jboss.shrinkwrap.tar.api.importer.TarImporter;
import org.jboss.shrinkwrap.tar.impl.io.TarInputStream;

/**
 * TestCase to verify the {@link TarImporterImpl} functionality.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class TarImporterImplTestCase extends StreamImporterImplTestBase<TarImporter>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   @SuppressWarnings("unused")
   private static final Logger log = Logger.getLogger(TarImporterImplTestCase.class.getName());

   /**
    * Delegate for performing TAR content assertions
    */
   private static final TarContentAssertionDelegate delegate = new TarContentAssertionDelegate();

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getDelegate()
    */
   @Override
   protected ContentAssertionDelegateBase getDelegate()
   {
      return delegate;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getImporterClass()
    */
   @Override
   protected Class<TarImporter> getImporterClass()
   {
      return TarImporter.class;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getExporterClass()
    */
   @Override
   protected Class<? extends StreamExporter> getExporterClass()
   {
      return TarExporter.class;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getExceptionThrowingInputStream()
    */
   @Override
   protected TarInputStream getExceptionThrowingInputStream()
   {
      try
      {
         return ExceptionThrowingTarInputStream.create();
      }
      catch (final IOException e)
      {
         throw new RuntimeException("Should not occur in test setup", e);
      }
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Members ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Test {@link TarInputStream} extension which throws errors when read
    * in order to test exception handling of the import process
    * 
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    */
   private static final class ExceptionThrowingTarInputStream extends TarInputStream
   {

      static ExceptionThrowingTarInputStream create() throws IOException
      {
         final byte[] test = "Something".getBytes();
         final InputStream in = new ByteArrayInputStream(test);
         return new ExceptionThrowingTarInputStream(in);
      }

      private ExceptionThrowingTarInputStream(final InputStream in) throws IOException
      {
         super(in);
      }

      /**
       * Generates an exception when read
       * @see org.jboss.javatar.TarInputStream#read()
       */
      @Override
      public int read() throws IOException
      {
         throw new RuntimeException("Mock Exception, should be wrapped in the import process");
      }

      @Override
      public int read(byte[] buf) throws IOException
      {
         return this.read();
      }

      @Override
      public int read(byte[] buf, int offset, int numToRead) throws IOException
      {
         return this.read();
      }

   }
}

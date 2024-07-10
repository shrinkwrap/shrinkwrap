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

import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.TarBz2Exporter;
import org.jboss.shrinkwrap.api.importer.TarBz2Importer;
import org.jboss.shrinkwrap.impl.base.io.tar.TarBzInputStream;
import org.jboss.shrinkwrap.impl.base.io.tar.bzip.BZip2CompressorOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * TestCase to verify the {@link org.jboss.shrinkwrap.impl.base.importer.tar.TarBz2ImporterImpl} functionality.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 */
public class TarBz2ImporterImplTestCase extends StreamImporterImplTestBase<TarBz2Importer> {

    // -------------------------------------------------------------------------------------||
    // Class Members -----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Delegate for performing TAR.BZ2 content assertions
     */
    private static final TarBz2ContentAssertionDelegate delegate = new TarBz2ContentAssertionDelegate();

    // -------------------------------------------------------------------------------------||
    // Required Implementations ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getDelegate()
     */
    @Override
    protected ContentAssertionDelegateBase getDelegate() {
        return delegate;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getImporterClass()
     */
    @Override
    protected Class<TarBz2Importer> getImporterClass() {
        return TarBz2Importer.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getExporterClass()
     */
    @Override
    protected Class<? extends StreamExporter> getExporterClass() {
        return TarBz2Exporter.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getExceptionThrowingInputStream()
     */
    @Override
    protected TarBzInputStream getExceptionThrowingInputStream() {
        try {
            return ExceptionThrowingTarBzInputStream.create();
        } catch (final IOException e) {
            throw new RuntimeException("Should not occur in test setup", e);
        }
    }

    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    protected ArchiveFormat getArchiveFormat(){
        return ArchiveFormat.TAR_BZ;
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Members -------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Test {@link TarBzInputStream} extension which throws errors when read in order to test exception handling of the
     * import process
     *
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
     */
    private static final class ExceptionThrowingTarBzInputStream extends TarBzInputStream {

        static ExceptionThrowingTarBzInputStream create() throws IOException {
            // First provide real BZIP content so we don't err out when initialized
            final byte[] test = "Something more realistic than a single word".getBytes();
            final ByteArrayOutputStream stream = new ByteArrayOutputStream();
            BZip2CompressorOutputStream compressorOutputStream = new BZip2CompressorOutputStream(stream);
            compressorOutputStream.write(test);
            compressorOutputStream.close();
            final InputStream in = new ByteArrayInputStream(stream.toByteArray());
            return new ExceptionThrowingTarBzInputStream(in);
        }

        private ExceptionThrowingTarBzInputStream(final InputStream in) throws IOException {
            super(in);
        }

        /**
         * Generates an exception when read
         *
         * @see org.jboss.shrinkwrap.impl.base.io.tar.TarInputStream#read()
         */
        @Override
        public int read() {
            throw new RuntimeException("Mock Exception, should be wrapped in the import process");
        }

    }
}

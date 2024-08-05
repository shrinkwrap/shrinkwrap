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
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestCase to verify the ZipImporter functionality.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class ZipImporterImplTestCase extends StreamImporterImplTestBase<ZipImporter> {

    // -------------------------------------------------------------------------------------||
    // Class Members -----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Delegate for performing ZIP content assertions
     */
    private static final ZipContentAssertionDelegate delegate = new ZipContentAssertionDelegate();

    // -------------------------------------------------------------------------------------||
    // Tests -------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that an import of {@link ZipFile} results in {@link ArchiveImportException} if an unexpected error
     * occurred.
     *
     * @throws Exception
     */
    @Test(expected = ArchiveImportException.class)
    public void shouldThrowExceptionOnErrorInImportFromFile() throws Exception {
        final ContentAssertionDelegateBase delegate = this.getDelegate();
        assert delegate != null : "Delegate must be specified by implementations";
        final File testFile = delegate.getExistingResource();

        ZipFile testZip = new ZipFile(testFile) {
            @Override
            public Enumeration<? extends ZipEntry> entries() {
                throw new IllegalStateException("mock exception");
            }
        };
        ShrinkWrap.create(ZipImporter.class, "test.jar").importFrom(testZip).as(JavaArchive.class);
    }

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
    protected Class<ZipImporter> getImporterClass() {
        return ZipImporter.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getExporterClass()
     */
    @Override
    protected Class<? extends StreamExporter> getExporterClass() {
        return ZipExporter.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.importer.StreamImporterImplTestBase#getExceptionThrowingInputStream()
     */
    @Override
    protected InputStream getExceptionThrowingInputStream() {
        return new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Mock exception");
            }
        };
    }


    /**
     * {@inheritDoc}
     * @return
     */
    @Override
    protected ArchiveFormat getArchiveFormat(){
        return ArchiveFormat.ZIP;
    }

    // -------------------------------------------------------------------------------------||
    // Tests -------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * SHRINKWRAP-259
     */
    @Test
    public void createZipImporter() {
        final GenericArchive importer = ShrinkWrap.create(ZipImporter.class).as(GenericArchive.class);
        Assert.assertTrue("Archive did not have expected suffix", importer.getName().endsWith(".jar"));
    }
}

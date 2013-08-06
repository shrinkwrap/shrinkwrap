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
import java.io.FileInputStream;
import java.io.IOException;

import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.TarBzExporter;
import org.jboss.shrinkwrap.api.importer.TarBzImporter;
import org.jboss.shrinkwrap.impl.base.io.tar.TarInputStream;
import org.jboss.shrinkwrap.impl.base.io.tar.bzip.BZip2CompressorInputStream;

/**
 * TestCase to ensure that the {@link TarBzExporter} correctly exports archives to TAR.BZ2 format.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 * @version $Revision: $
 */
public final class TarBzExporterTestCase extends TarExporterTestBase<TarBzImporter> {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Extension for archives
     */
    private static final String EXTENSION = ".tar.bz2";

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.ExportTestBase#getStreamExporter()
     */
    @Override
    protected Class<? extends StreamExporter> getExporterClass() {
        return TarBzExporter.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#getImporterClass()
     */
    @Override
    protected Class<TarBzImporter> getImporterClass() {
        return TarBzImporter.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.ExportTestBase#getArchiveExtension()
     */
    @Override
    protected String getArchiveExtension() {
        return EXTENSION;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.TarExporterTestBase#getTarInputStreamFromFile(java.io.File)
     */
    @Override
    protected TarInputStream getTarInputStreamFromFile(final File archive) throws IOException {
        return new TarInputStream(new BZip2CompressorInputStream(new FileInputStream(archive)));
    }

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // Inherited
}

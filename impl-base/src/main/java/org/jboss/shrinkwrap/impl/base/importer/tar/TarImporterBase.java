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
package org.jboss.shrinkwrap.impl.base.importer.tar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.io.tar.TarEntry;
import org.jboss.shrinkwrap.impl.base.io.tar.TarInputStream;

/**
 * Base of implementations used to import existing TAR files/streams into the given {@link Archive}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
abstract class TarImporterBase<S extends TarInputStream, I extends StreamImporter<I>> extends
    AssignableBase<Archive<?>> implements StreamImporter<I> {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(TarImporterBase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    public TarImporterBase(final Archive<?> archive) {
        super(archive);
    }

    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Returns the actual class for this implementation
     */
    abstract Class<I> getActualClass();

    /**
     * Obtains the correct {@link InputStream} wrapper type for the specified raw data input
     *
     * @param in
     * @return
     * @throws IOException
     */
    abstract S getInputStreamForRawStream(InputStream in) throws IOException;

    // -------------------------------------------------------------------------------------||
    // Functional Methods -----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Provides covarient return
     */
    private I covarientReturn() {
        return this.getActualClass().cast(this);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.InputStream)
     */
    @Override
    public I importFrom(final InputStream stream) throws ArchiveImportException {
        return importFrom(stream, Filters.includeAll());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.InputStream, Filter)
     */
    @Override
    public I importFrom(final InputStream stream, Filter<ArchivePath> filter) throws ArchiveImportException {
        Validate.notNull(stream, "Stream must be specified");
        Validate.notNull(filter, "Filter must be specified");
        final S tarStream;
        try {
            tarStream = this.getInputStreamForRawStream(stream);
        } catch (final RuntimeException re) {
            throw new ArchiveImportException("Could not wrap raw input with TAR stream", re);
        } catch (final IOException e) {
            throw new ArchiveImportException("Could not wrap raw input with TAR stream", e);
        }
        return this.importFrom(tarStream, filter);
    }

    private I importFrom(final S stream, Filter<ArchivePath> filter) throws ArchiveImportException {
        Validate.notNull(stream, "Stream must be specified");
        try {
            TarEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                // Get the name
                String entryName = entry.getName();
                if(!filter.include(ArchivePaths.create(entryName))) {
                    continue;
                }

                final Archive<?> archive = this.getArchive();

                // Handle directories separately
                if (entry.isDirectory()) {
                    archive.addAsDirectory(entryName);
                    continue;
                }

                ByteArrayOutputStream output = new ByteArrayOutputStream(8192);
                byte[] content = new byte[4096];
                int readBytes;
                while ((readBytes = stream.read(content, 0, content.length)) != -1) {
                    output.write(content, 0, readBytes);
                }
                archive.add(new ByteArrayAsset(output.toByteArray()), entryName);
            }
        } catch (final RuntimeException re) {
            throw new ArchiveImportException("Could not import stream", re);
        } catch (IOException e) {
            throw new ArchiveImportException("Could not import stream", e);
        }
        return this.covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.File)
     */
    @Override
    public I importFrom(final File file) throws ArchiveImportException {
        return importFrom(file, Filters.includeAll());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.File, Filter)
     */
    @Override
    public I importFrom(final File file, Filter<ArchivePath> filter) throws ArchiveImportException {
        Validate.notNull(file, "File must be specified");
        if (!file.exists()) {
            throw new IllegalArgumentException("Specified file for import does not exist: " + file);
        }
        if (file.isDirectory()) {
            throw new IllegalArgumentException("Specified file for import is a directory: " + file);
        }

        final S archive;
        try {
            archive = this.getInputStreamForFile(file);
        } catch (final IOException e) {
            throw new ArchiveImportException("Could not read archive file " + file, e);
        }

        return this.importFrom(archive, filter);

    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains an implementation-specific stream to the specified {@link File}
     *
     * @param file
     *            To open a stream to, must be specified
     * @return
     * @throws IOException
     *             If there was a problem getting an instream to the file
     */
    private S getInputStreamForFile(File file) throws IOException {
        assert file != null : "File must be specified";
        return this.getInputStreamForRawStream(new FileInputStream(file));
    }

}
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
package org.jboss.shrinkwrap.impl.base.importer.zip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.ZipFileEntryAsset;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * Used to import existing Zip files/streams into the given {@link Archive}
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ZipImporterImpl extends AssignableBase<Archive<?>> implements ZipImporter {

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    public ZipImporterImpl(final Archive<?> archive) {
        super(archive);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.ZipImporter#importZip(java.util.zip.ZipInputStream)
     */
    @Override
    @Deprecated
    public ZipImporter importZip(final ZipInputStream stream) {
        // Delegate
        return this.importFrom(stream);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.ZipImporter#importZip(java.util.zip.ZipFile)
     */
    @Deprecated
    @Override
    public ZipImporter importZip(ZipFile file) {
        // Delegate
        return this.importFrom(file);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.InputStream)
     */
    @Override
    public ZipImporter importFrom(final InputStream stream) throws ArchiveImportException {
        return importFrom(stream, Filters.includeAll());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.InputStream, Filter)
     */
    @Override
    public ZipImporter importFrom(InputStream stream, Filter<ArchivePath> filter) throws ArchiveImportException {
        Validate.notNull(stream, "Stream must be specified");
        Validate.notNull(filter, "Filter must be specified");

        try {
            // Create a temporary file to act as the buffer
            final File tempFile = File.createTempFile("shrinkwrap-buffer", ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream output = new FileOutputStream(tempFile)) {
                IOUtil.copy(stream, output);
            }

            return importFrom(tempFile, filter);

        } catch (IOException e) {
            throw new ArchiveImportException("Could not import stream", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.File)
     */
    @Override
    public ZipImporter importFrom(final File file) throws ArchiveImportException {
        return importFrom(file, Filters.includeAll());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.File, Filter)
     */
    @Override
    public ZipImporter importFrom(File file, Filter<ArchivePath> filter) throws ArchiveImportException {
        Validate.notNull(file, "File must be specified");
        if (file.isDirectory()) {
            throw new IllegalArgumentException("File to import as ZIP must not be a directory: "
                + file.getAbsolutePath());
        }
        Validate.notNull(filter, "Filter must be specified");

        final ZipFile zipFile;
        try {
            zipFile = new ZipFile(file);
        } catch (final IOException ioe) {
            throw new ArchiveImportException("Could not obtain ZIP File from File", ioe);
        }

        // Delegate
        return this.importFrom(zipFile, filter);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.importer.StreamImporter#importFrom(java.io.File)
     */
    @Override
    public ZipImporter importFrom(final ZipFile file) throws ArchiveImportException {
        return importFrom(file, Filters.includeAll());
    }

    private ZipImporter importFrom(final ZipFile file, Filter<ArchivePath> filter) throws ArchiveImportException {
        Validate.notNull(file, "File must be specified");

        try {
            Enumeration<? extends ZipEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();

                // Get the entry (path) name
                final String entryName = entry.getName();
                if(!filter.include(ArchivePaths.create(entryName))) {
                    continue;
                }
                // Get the archive
                final Archive<?> archive = this.getArchive();

                // Handle directories separately
                if (entry.isDirectory()) {
                    archive.addAsDirectory(entryName);
                    continue;
                }

                archive.add(new ZipFileEntryAsset(new File(file.getName()), entry), new BasicPath(entryName));
            }
        } catch (Exception e) {
            throw new ArchiveImportException("Could not import file", e);
        } finally {
            try {
                file.close();
            } catch (IOException e) {
                //no-op
            }
        }
        return this;
    }
}

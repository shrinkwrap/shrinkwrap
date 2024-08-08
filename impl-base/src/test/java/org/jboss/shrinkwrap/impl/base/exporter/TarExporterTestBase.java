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
import java.io.InputStream;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.io.tar.TarEntry;
import org.jboss.shrinkwrap.impl.base.io.tar.TarInputStream;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.junit.jupiter.api.Assertions;

/**
 * TestCase to ensure that the TAR exporters are working as contracted
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public abstract class TarExporterTestBase<T extends StreamImporter<T>> extends StreamExporterTestBase<T> {
    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains a stream capable of reading the specified {@link File } in the appropriate format
     *
     * @param archive
     * @return
     * @throws IOException
     */
    protected abstract TarInputStream getTarInputStreamFromFile(final File archive) throws IOException;

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#ensureInExpectedForm(java.io.File)
     */
    @Override
    protected final void ensureInExpectedForm(final File file) throws IOException {
        // Validate entries were written out
        assertAssetInTar(file, PATH_ONE, ASSET_ONE);
        assertAssetInTar(file, PATH_TWO, ASSET_TWO);

        // Validate all paths were written
        // SHRINKWRAP-94
        getEntryFromTarFile(file, NESTED_PATH);

        // Ensure we don't write the root Path
        // SHRINKWRAP-96
        InputStream rootEntry = this.getEntryFromTarFile(file, ArchivePaths.root());
        Assertions.assertNull(rootEntry, "TAR.GZ should not have explicit root path written (SHRINKWRAP-96)");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#getContentsFromExportedFile(java.io.File,
     *      org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    protected final InputStream getContentsFromExportedFile(final File file, final ArchivePath path) throws IOException {
        // Precondition checks
        assert file != null : "file must be specified";
        assert path != null : "path must be specified";

        // Get as TAR.GZ
        return this.getEntryFromTarFile(file, path);
    }

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // Inherited

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Assert an asset is actually in the file
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void assertAssetInTar(final File archive, final ArchivePath path, final Asset asset)
        throws IllegalArgumentException, IOException {
        final InputStream in = this.getEntryFromTarFile(archive, path);
        byte[] expectedContents = IOUtil.asByteArray(asset.openStream());
        byte[] actualContents = IOUtil.asByteArray(in);
        Assertions.assertArrayEquals(expectedContents, actualContents);
    }

    /**
     * Obtains an {@link InputStream} to an entry of specified name from the specified TAR.GZ file, or null if not
     * found. We have to iterate through all entries for a matching name, as the instream does not support random
     * access.
     *
     * @param path
     * @return
     * @throws IllegalArgumentException
     * @throws IOException
     */
    private InputStream getEntryFromTarFile(final File archive, final ArchivePath path)
        throws IllegalArgumentException, IOException {
        String entryPath = PathUtil.optionallyRemovePrecedingSlash(path.get());
        final TarInputStream in = this.getTarInputStreamFromFile(archive);
        TarEntry currentEntry;
        while ((currentEntry = in.getNextEntry()) != null) {
            final String entryName = currentEntry.getName();
            if (currentEntry.isDirectory()) {
                entryPath = PathUtil.optionallyAppendSlash(entryPath);
            } else {
                entryPath = PathUtil.optionallyRemoveFollowingSlash(entryPath);
            }
            if (entryName.equals(entryPath)) {
                return in;
            }
        }
        // Not found
        return null;
    }

}

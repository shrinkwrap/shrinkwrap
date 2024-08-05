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

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.ZipStoredExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * TestCase to ensure that the {@link ZipStoredExporter} correctly exports archives to ZIP format.
 *
 * @author <a href="mailto:asotobu@gmail.com">Alex Soto</a>
 */
public final class ZipStoredExporterTestCase extends StreamExporterTestBase<ZipImporter> {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Extension for archives
     */
    private static final String EXTENSION = ".jar";

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#getExporterClass()
     */
    @Override
    protected Class<? extends StreamExporter> getExporterClass() {
        return ZipStoredExporter.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#getImporterClass()
     */
    @Override
    protected Class<ZipImporter> getImporterClass() {
        return ZipImporter.class;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#ensureInExpectedForm(java.io.File)
     */
    @Override
    protected void ensureInExpectedForm(final File file) throws IOException {
        // Precondition check
        assert file != null : "file must be specified";

        // Get as ZipFile
        final ZipFile zip = new ZipFile(file);

        // Validate
        this.ensureZipFileInExpectedForm(zip);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#getContentsFromExportedFile(java.io.File,
     *      org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    protected InputStream getContentsFromExportedFile(final File file, final ArchivePath path) throws IOException {
        // Precondition checks
        assert file != null : "file must be specified";
        assert path != null : "path must be specified";

        // Get as Zip File
        final ZipFile zipFile = new ZipFile(file);
        final ZipEntry entry = zipFile.getEntry(PathUtil.optionallyRemovePrecedingSlash(path.get()));
        if (entry == null) {
            return null;
        }
        final byte[] actualContents = IOUtil.asByteArray(zipFile.getInputStream(entry));
        return new ByteArrayInputStream(actualContents);
    }

    /**
     * {@inheritDoc
     * @see org.jboss.shrinkwrap.impl.base.exporter.ExportTestBase#getArchiveExtension()
     */
    @Override
    protected String getArchiveExtension() {
        return EXTENSION;
    }

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // --------------------------------------

    @Test
    public void shouldExportToStoredZip() throws IOException {

        final File target = new File("target");

        ShrinkWrap.create(JavaArchive.class).addClass(ZipStoredExporterTestCase.class)
                .add(new StringAsset("My file content"), "content.txt")
                .as(ZipStoredExporter.class).exportTo(new File(target, "test-exporter.zip"), true);

        final File generatedFile = new File(target, "test-exporter.zip");
        Assertions.assertTrue(generatedFile.length() > 0);

        final InputStream contentsFromExportedFile = getContentsFromExportedFile(generatedFile, ArchivePaths.create("/org/jboss/shrinkwrap/impl/base/exporter/ZipStoredExporterTestCase.class"));
        final byte[] content = IOUtil.asByteArray(contentsFromExportedFile);
        Assertions.assertTrue(content.length > 0);

        final InputStream contentsFromExportedFile2 = getContentsFromExportedFile(generatedFile, ArchivePaths.create("/content.txt"));
        final byte[] content2 = IOUtil.asByteArray(contentsFromExportedFile2);
        Assertions.assertTrue(content.length > 0);
        Assertions.assertEquals("My file content", new String(content2));
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that the specified {@link ZipFile} contains entries in the expected form
     *
     * @param expectedZip
     * @throws IOException
     */
    private void ensureZipFileInExpectedForm(final ZipFile expectedZip) throws IOException {
        // Validate entries were written out
        assertAssetInZip(expectedZip, PATH_ONE, ASSET_ONE);
        assertAssetInZip(expectedZip, PATH_TWO, ASSET_TWO);

        // Validate all paths were written
        // SHRINKWRAP-94
        getEntryFromZip(expectedZip, NESTED_PATH);

        // Ensure we don't write the root Path
        // SHRINKWRAP-96
        ZipEntry rootEntry = expectedZip.getEntry("/");
        Assertions.assertNull(rootEntry, "ZIP should not have explicit root path written (SHRINKWRAP-96)");
    }

    /**
     * Assert an asset is actually in the Zip file
     *
     * @throws IOException
     * @throws IllegalArgumentException
     */
    private void assertAssetInZip(ZipFile expectedZip, ArchivePath path, Asset asset) throws IllegalArgumentException,
            IOException {
        final ZipEntry entry = this.getEntryFromZip(expectedZip, path);
        final byte[] expectedContents = IOUtil.asByteArray(asset.openStream());
        final byte[] actualContents = IOUtil.asByteArray(expectedZip.getInputStream(entry));
        Assertions.assertArrayEquals(expectedContents, actualContents);
    }

    /**
     * Obtains the entry from the specified ZIP file at the specified Path, ensuring it exists along the way
     *
     * @param expectedZip
     * @param path
     * @return
     * @throws IllegalArgumentException
     */
    private ZipEntry getEntryFromZip(final ZipFile expectedZip, final ArchivePath path)
            throws IllegalArgumentException {
        final String entryPath = PathUtil.optionallyRemovePrecedingSlash(path.get());
        final ZipEntry entry = expectedZip.getEntry(entryPath);
        Assertions.assertNotNull(entry,"Expected path not found in ZIP: " + path);
        return entry;
    }
}

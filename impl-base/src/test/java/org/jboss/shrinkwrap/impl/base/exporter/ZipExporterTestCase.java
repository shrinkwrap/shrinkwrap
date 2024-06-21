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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestCase to ensure that the {@link ZipExporter} correctly exports archives to ZIP format.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @version $Revision: $
 */
public final class ZipExporterTestCase extends StreamExporterTestBase<ZipImporter> {
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
        return ZipExporter.class;
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
    // -------------------------------------------------------------------------------------||

    /**
     * Test to ensure that the {@link ZipExporter} does not accept an empty archive as input
     *
     * SHRINKWRAP-93
     *
     */
    @Test(expected = IllegalArgumentException.class)
    public void exportEmptyArchiveAsZip() {
        // Attempt to export an empty archive, should fail
        ShrinkWrap.create(JavaArchive.class, NAME_ARCHIVE).as(ZipExporter.class).exportAsInputStream();
    }

    /**
     * SHRINKWRAP-377
     */
    @Test
    public void exportShouldFailIfTargetExist() throws IOException {
        File target = new File(createTempDirectory("exportShouldFailIfTargetExist").getAbsolutePath()
            + "exportShouldFailIfTargetExist.jar");
        if (target.exists()) {
            Assert.assertTrue(target.delete());
        }
        Assert.assertFalse(target.exists());
        createArchiveWithAssets().as(ZipExporter.class).exportTo(target);
        Assert.assertTrue(target.exists());
        try {
            createArchiveWithAssets().as(ZipExporter.class).exportTo(target); // this line should throw a
                                                                              // FileExistsException
            Assert.fail("Expected a FileExistsException when exporting to an existing path");
        } catch (Exception e) {
            Assert.assertEquals(org.jboss.shrinkwrap.api.exporter.FileExistsException.class, e.getClass());
        }
    }

    /**
     * Archive -> WAR -> Archive -> WAR. SHRINKWRAP-444
     */
    @Test
    public void testExportImportExport() {
        // Preconditions
        final File target = new File("target");
        final File testClasses = new File(target, "test-classes");
        final File hsqldbJar = new File(testClasses, "hsqldb.jar");
        Assert.assertTrue("test JAR must exist to run this test", hsqldbJar.exists() && !hsqldbJar.isDirectory());

        final File file1 = new File(target, "testExportImportExport1.war");
        final File file2 = new File(target, "testExportImportExport2.war");
        final WebArchive webArchive = ShrinkWrap.create(WebArchive.class).add(new FileAsset(hsqldbJar),
            "/WEB-INF/lib/hsqldb.jar");
        webArchive.as(ZipExporter.class).exportTo(file1, true);

        // when
        final WebArchive webArchive2 = ShrinkWrap.createFromZipFile(WebArchive.class, file1);
        webArchive2.as(ZipExporter.class).exportTo(file2, true);

        // then compare sizes
        Assert.assertEquals(file1.length(), file2.length());
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
        Assert.assertNull("ZIP should not have explicit root path written (SHRINKWRAP-96)", rootEntry);
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
        Assert.assertArrayEquals(expectedContents, actualContents);
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
        Assert.assertNotNull("Expected path not found in ZIP: " + path, entry);
        return entry;
    }
}

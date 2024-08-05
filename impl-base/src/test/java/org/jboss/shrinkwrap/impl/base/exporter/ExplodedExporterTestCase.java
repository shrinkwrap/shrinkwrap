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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.impl.base.TestIOUtil;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ExplodedExporterTestCase
 *
 * TestCase to ensure that the {@link ExplodedExporter} correctly exports archive.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExplodedExporterTestCase extends ExportTestBase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ExplodedExporterTestCase.class.getName());

    /**
     * Extension for exploded archives
     */
    private static final String EXTENSION = ".jar";

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

/**
    * {@inheritDoc
    * @see org.jboss.shrinkwrap.impl.base.exporter.ExportTestBase#getArchiveExtension()
    */
    @Override
    protected String getArchiveExtension() {
        return EXTENSION;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.ExportTestBase#getExporterClass()
     */
    @Override
    protected Class<? extends StreamExporter> getExporterClass() {
        return ZipExporter.class;
    }

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensure an archive can be exported to an exploded directory.
     *
     * @throws Exception
     */
    @Test
    public void testExportExploded() throws Exception {
        log.info("testExportExploded");

        // Get a temp directory
        File tempDirectory = createTempDirectory("testExportExploded");

        // Get an archive instance
        Archive<?> archive = createArchiveWithAssets();

        // Export as Exploded directory
        File explodedDirectory = archive.as(ExplodedExporter.class).exportExploded(tempDirectory);

        // Validate the exploded directory was created
        Assertions.assertNotNull(explodedDirectory);

        // Assert the directory has the correct name
        File expectedDirectory = new File(tempDirectory, archive.getName());
        Assertions.assertEquals(expectedDirectory, explodedDirectory);

        // Validate entries were written out
        assertAssetInExploded(explodedDirectory, PATH_ONE, ASSET_ONE);
        assertAssetInExploded(explodedDirectory, PATH_TWO, ASSET_TWO);
    }

    /**
     * Ensure an archive can be exported to an exploded directory.
     *
     * @throws Exception
     */
    @Test
    public void testExportExplodedChangingExplodedDirectoryName() throws Exception {
        log.info("testExportExploded");

        // Get a temp directory
        File tempDirectory = createTempDirectory("testExportExplodedChangingExplodedDirectoryName");

        // Get an archive instance
        Archive<?> archive = createArchiveWithAssets();

        // Export as Exploded directory
        File explodedDirectory = archive.as(ExplodedExporter.class).exportExploded(tempDirectory,
            "EXPLODED" + NAME_ARCHIVE);

        // Validate the exploded directory was created
        Assertions.assertNotNull(explodedDirectory);

        // Assert the directory has the correct name
        File expectedDirectory = new File(tempDirectory, "EXPLODED" + NAME_ARCHIVE);
        Assertions.assertEquals(expectedDirectory, explodedDirectory);

        // Validate entries were written out
        assertAssetInExploded(explodedDirectory, PATH_ONE, ASSET_ONE);
        assertAssetInExploded(explodedDirectory, PATH_TWO, ASSET_TWO);
    }

    /**
     * Ensure an archive exported to an exploded directory properly explodes nested archives.
     *
     * @throws Exception
     */
    @Test
    public void testExportNestedExploded() throws Exception {
        log.info("testExportNestedExploded");

        // Get a temp directory
        File tempDirectory = createTempDirectory("testExportNestedExploded");

        // Get an archive instance
        Archive<?> archive = createArchiveWithNestedArchives();

        // Export as Exploded directory
        File explodedDirectory = archive.as(ExplodedExporter.class).exportExploded(tempDirectory);

        // Validate the exploded directory was created
        Assertions.assertNotNull(explodedDirectory);

        // Assert the directory has the correct name
        File expectedDirectory = new File(tempDirectory, archive.getName());
        Assertions.assertEquals(expectedDirectory, explodedDirectory);

        // Validate nested archive entries were written out
        ArchivePath nestedArchivePath = new BasicPath(NAME_NESTED_ARCHIVE + this.getArchiveExtension());

        assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, PATH_ONE), ASSET_ONE);
        assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePath, PATH_TWO), ASSET_TWO);

        ArchivePath nestedArchivePathTwo = new BasicPath(NESTED_PATH, NAME_NESTED_ARCHIVE_2
            + this.getArchiveExtension());

        assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePathTwo, PATH_ONE), ASSET_ONE);
        assertAssetInExploded(explodedDirectory, new BasicPath(nestedArchivePathTwo, PATH_TWO), ASSET_TWO);
    }

    @Test
    public void testExportExplodedIntoDirectory() throws Exception {
        log.info("testExportExplodedIntoDirectory");

        // Get a temp directory
        File tempDirectory = createTempDirectory("testExportExploded");

        // Get an archive instance
        Archive<?> archive = createArchiveWithAssets();

        // Export as Exploded directory
        File explodedDirectory = archive.as(ExplodedExporter.class).exportExplodedInto(tempDirectory);

        // Validate the exploded directory was created
        Assertions.assertNotNull(explodedDirectory);

        // Validate the exploded directory was created in same directory
        Assertions.assertEquals(tempDirectory, explodedDirectory);

        // Validate entries were written out
        assertAssetInExploded(explodedDirectory, PATH_ONE, ASSET_ONE);
        assertAssetInExploded(explodedDirectory, PATH_TWO, ASSET_TWO);
    }

    /**
     * Ensure an baseDirectory is required to export.
     *
     */
    @Test
    public void testExportExplodedRequiresBaseDirectory() {
        log.info("testExportExplodedRequiresBaseDirectory");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShrinkWrap.create(ExplodedExporter.class, "test.jar").exportExploded(null));
    }

    /**
     * Ensure an baseDirectory must exist is required to export.
     *
     */
    @Test
    public void testExportExplodedRequiresExistingDirectory() {
        log.info("testExportExplodedRequiresExisitingDirectroy");

        final File directory = this.getNonexistantDirectory();
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShrinkWrap.create(ExplodedExporter.class, "test.jar").exportExploded(directory));
    }

    /**
     * Ensure ExpolodedExporter requires a directory
     */
    @Test
    public void testExportExplodedRequiresValidDirectory() {
        log.info("testExportExplodedRequiresValidDirectory");
        final File nonDirectory = new File(this.getTarget(), "tempFile.txt");
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShrinkWrap.create(ExplodedExporter.class, "test.jar").exportExploded(nonDirectory));
    }

    /**
     * Ensure an ArchiveExportException is thrown when output directory can not be created
     */
    @Test
    public void testExportExplodedOutpuDirCreationFails() throws Exception {
        log.info("testExportExplodedOutpuDirCreationFails");
        final File directory = createTempDirectory("testExportExplodedOutpuDirCreationFails");
        directory.deleteOnExit();

        Assertions.assertThrows(ArchiveExportException.class,
                () -> ShrinkWrap.create(ExplodedExporter.class, "test/" + NAME_ARCHIVE).exportExploded(directory));
    }

    /**
     * Ensure ArchiveException is thrown if Asset can not be written
     */
    @Test
    public void testExportExplodedThrowsExceptionOnAssetWrite() throws Exception {
        log.info("testExportExplodedThrowsExceptionOnAssetWrite");
        Archive<?> archive = createArchiveWithAssets();
        archive.add(() -> {
            throw new RuntimeException("Mock Exception getting Stream");
        }, new BasicPath("badAsset"));
        final File directory = createTempDirectory("testExportExplodedThrowsExceptionOnAssetWrite");

        Assertions.assertThrows(ArchiveExportException.class,
                () -> archive.as(ExplodedExporter.class).exportExploded(directory));
    }

    /**
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-84 <br/>
     * Should be able to use a existing directory as parent directory for ExplodedExports
     */
    @Test
    public void testShouldBeAbleToUseExistingDirectoryAsParent() {
        Archive<?> archive = createArchiveWithAssets();

        File existingParentFolder = new File("target/");
        existingParentFolder.mkdirs();
        Assertions.assertTrue(existingParentFolder.exists(),
                "Internal error, the directory need to exist for test case to work");

        File archiveFolder = new File(existingParentFolder, archive.getName());
        archiveFolder.mkdirs();
        Assertions.assertTrue(existingParentFolder.exists(),
                "Internal error, the directory need to exist for test case to work");

        archive.as(ExplodedExporter.class).exportExploded(existingParentFolder);

        Assertions.assertTrue(new File(existingParentFolder, archive.getName()).exists(),
                "A subfolder with archive name should have been created");
    }

    /**
     * https://jira.jboss.org/jira/browse/SHRINKWRAP-86 Ensure an IllegalArgumentException is thrown when output
     * directory is a file
     */
    @Test
    public void testExportExplodedOutpuDirIsAFile() throws Exception {
        log.info("testExportExplodedOutpuDirIsAFile");
        final File directory = createTempDirectory("testExportExplodedOutpuDirIsAFile");
        // Will cause the creation of Archive directory to fail
        final File existingFile = new File(directory, NAME_ARCHIVE + this.getArchiveExtension());
        final boolean created = existingFile.createNewFile();

        IOUtil.copyWithClose(new ByteArrayInputStream("test-test".getBytes()), new FileOutputStream(existingFile));

        Assertions.assertTrue(created, "Could not create test file");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> createArchiveWithAssets().as(ExplodedExporter.class).exportExploded(directory));
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains a reference to a directory that does not exist
     */
    private File getNonexistantDirectory() {
        final File directory = new File(this.getTarget(), "someNonExistentDirectory");
        if (directory.exists()) {
            TestIOUtil.deleteDirectory(directory);
        }
        Assertions.assertFalse(directory.exists(), "Precondition Failure: Directory should not exist: " + directory);
        return directory;
    }

    /**
     * Assert an asset is actually in the exploded directory
     *
     * @throws FileNotFoundException
     */
    private void assertAssetInExploded(File explodedDirectory, ArchivePath path, Asset asset) throws FileNotFoundException {
        File assetFile = new File(explodedDirectory, path.get());
        Assertions.assertNotNull(assetFile);
        Assertions.assertTrue(assetFile.exists());
        byte[] expectedContents = IOUtil.asByteArray(asset.openStream());

        InputStream inputStream = new FileInputStream(assetFile);

        byte[] actualContents = IOUtil.asByteArray(inputStream);
        Assertions.assertArrayEquals(expectedContents, actualContents);
    }
}

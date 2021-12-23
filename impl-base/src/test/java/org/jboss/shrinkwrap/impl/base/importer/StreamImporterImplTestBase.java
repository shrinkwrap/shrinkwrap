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

import junit.framework.Assert;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * Base upon which tests of {@link StreamImporter} implementations may build
 *
 * @param <T>
 *            Type of importer under test
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public abstract class StreamImporterImplTestBase<T extends StreamImporter<T>> {

    // -------------------------------------------------------------------------------------||
    // Class Members -----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(StreamImporterImplTestBase.class.getName());

    /**
     * Name of an existing resource on the ClassPath
     */
    private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

    // -------------------------------------------------------------------------------------||
    // Contracts ---------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains the delegate used in asserting imported content is as expected
     */
    protected abstract ContentAssertionDelegateBase getDelegate();

    /**
     * Obtains the importer type used by these tests
     *
     * @return
     */
    protected abstract Class<T> getImporterClass();

    /**
     * Exporter used for roundtrip testing import/export/import
     *
     * @return
     */
    protected abstract Class<? extends StreamExporter> getExporterClass();

    /**
     * Obtains an {@link InputStream} used to throw an exception for testing
     * {@link StreamImporterImplTestBase#shouldThrowExceptionOnErrorInImportFromStream()}
     *
     * @return
     */
    protected abstract InputStream getExceptionThrowingInputStream();

    /**
     * Obtains the {@link org.jboss.shrinkwrap.api.ArchiveFormat} to be used
     * @return
     */
    protected abstract ArchiveFormat getArchiveFormat();

    // -------------------------------------------------------------------------------------||
    // Tests -------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that we may import a file and create an archive with matching structure
     */
    @Test
    public void shouldBeAbleToImportFile() throws Exception {
        // Get the delegate
        final ContentAssertionDelegateBase delegate = this.getDelegate();
        assert delegate != null : "Delegate must be specified by implementations";
        final File testFile = delegate.getExistingResource();

        // Import
        final Class<? extends StreamImporter<?>> importerClass = this.getImporterClass();
        assert importerClass != null : "Importer class must be specified by implementations";
        Archive<?> archive = ShrinkWrap.create(importerClass, "test.jar").importFrom(testFile).as(JavaArchive.class);

        // Ensure we don't have a null archive
        Assert.assertNotNull("Should not return a null archive", archive);

        // Validate the contents of the imported archive match that of the file from
        // which it was created
        delegate.assertContent(archive, testFile);
    }

    /**
     * Ensures that we may import a file and create an archive with matching structure with filter
     */
    @Test
    public void shouldBeAbleToImportFileWithFilter() throws Exception {
        // Get the delegate
        final ContentAssertionDelegateBase delegate = this.getDelegate();
        assert delegate != null : "Delegate must be specified by implementations";
        final File testFile = delegate.getExistingResource();

        // Import
        final Class<? extends StreamImporter<?>> importerClass = this.getImporterClass();
        assert importerClass != null : "Importer class must be specified by implementations";
        Archive<?> archive = ShrinkWrap.create(importerClass, "test.jar")
                .importFrom(testFile, Filters.include(".*MANIFEST\\.MF")).as(JavaArchive.class);

        // Ensure we don't have a null archive
        Assert.assertNotNull("Should not return a null archive", archive);

        // Validate the contents of the imported only contain filtered content
        Assert.assertEquals(2, archive.getContent().size());
        Assert.assertTrue(archive.contains(ArchivePaths.create("META-INF/MANIFEST.MF")));
    }

    /**
     * Ensures an attempt to import a directory fails w/ {@link IllegalArgumentException}
     */
    @Test
    public void shouldNotBeAbleToImportDirectory() throws Exception {
        // Get the delegate
        final ContentAssertionDelegateBase delegate = this.getDelegate();
        assert delegate != null : "Delegate must be specified by implementations";
        final File testDir = delegate.getExistingResource().getParentFile();

        // Import
        final Class<? extends StreamImporter<?>> importerClass = this.getImporterClass();
        assert importerClass != null : "Importer class must be specified by implementations";
        try {
            ShrinkWrap.create(importerClass, "test.jar").importFrom(testDir);
        }
        // Expected
        catch (final IllegalArgumentException iae) {
            // Good
            return;
        }

        Assert.fail("Should have received " + IllegalArgumentException.class.getSimpleName()
            + " on attempt to import a dir");

    }

    /**
     * Ensures that we may import an archive, add content to it, export, and that the added content has been reflected
     * to the exported view.
     *
     * @throws Exception
     */
    @Test
    public void shouldBeAbleToImportAddAndExport() throws Exception {
        // Get the delegate
        final ContentAssertionDelegateBase delegate = this.getDelegate();
        assert delegate != null : "Delegate must be specified by implementations";
        final File testFile = delegate.getExistingResource();

        // Import from file
        final Class<? extends StreamImporter<?>> importerClass = this.getImporterClass();
        assert importerClass != null : "Importer class must be specified by implementations";
        final Archive<?> archive = ShrinkWrap.create(importerClass, "test.jar").importFrom(testFile)
            .as(JavaArchive.class);
        Assert.assertNotNull("Should not return a null archive", archive);

        // Add a new resource
        archive.add(new ClassLoaderAsset(EXISTING_RESOURCE), ArchivePaths.create("test.properties"));

        // Export
        File tempFile = new File("target/testOutput");
        tempFile.deleteOnExit();
        final Class<? extends StreamExporter> exporterClass = this.getExporterClass();
        Assert.assertNotNull("Exporter class must be specified by implementations", exporterClass);
        final InputStream stream = archive.as(exporterClass).exportAsInputStream();
        IOUtil.copyWithClose(stream, new FileOutputStream(tempFile));

        // Ensure the exported view matches that of the archive
        delegate.assertContent(archive, tempFile);
    }

    /**
     * Ensures that we may import an archive as a stream, and the contents will be as expected
     *
     * @throws Exception
     */
    @Test
    public void shouldBeAbleToImportInputStream() throws Exception {
        // Get the delegate
        final ContentAssertionDelegateBase delegate = this.getDelegate();
        assert delegate != null : "Delegate must be specified by implementations";
        final File testFile = delegate.getExistingResource();

        // Get the input as a stream
        InputStream stream = new FileInputStream(testFile);

        // Get the importer
        final Class<T> importerClass = this.getImporterClass();
        assert importerClass != null : "Importer class must be specified by implementations";

        // Import as a stream
        final T importer = ShrinkWrap.create(importerClass, "test.jar");
        final Archive<?> archive;
        try {
            archive = importer.importFrom(stream).as(GenericArchive.class);
        } finally {
            stream.close();
        }
        Assert.assertNotNull("Should not return a null archive", archive);

        // Ensure the archive matches the file input
        delegate.assertContent(archive, testFile);
    }

    /**
     * Ensures that we may import an archive as a stream, and the contents will be as expected with filter
     *
     * @throws Exception
     */
    @Test
    public void shouldBeAbleToImportInputStreamWithFilter() throws Exception {
        // Get the delegate
        final ContentAssertionDelegateBase delegate = this.getDelegate();
        assert delegate != null : "Delegate must be specified by implementations";
        final File testFile = delegate.getExistingResource();

        // Get the input as a stream
        InputStream stream = new FileInputStream(testFile);

        // Get the importer
        final Class<T> importerClass = this.getImporterClass();
        assert importerClass != null : "Importer class must be specified by implementations";

        // Import as a stream
        final T importer = ShrinkWrap.create(importerClass, "test.jar");
        final Archive<?> archive;
        try {
            archive = importer.importFrom(stream, Filters.include(".*MANIFEST\\.MF")).as(GenericArchive.class);
        } finally {
            stream.close();
        }
        Assert.assertNotNull("Should not return a null archive", archive);

        // Validate the contents of the imported only contain filtered content
        Assert.assertEquals(2, archive.getContent().size());
        Assert.assertTrue(archive.contains(ArchivePaths.create("META-INF/MANIFEST.MF")));
    }

    /**
     * Ensures that an import of {@link InputStream} results in {@link ArchiveImportException} if an unexpected error
     * occurred.
     *
     * @throws Exception
     */
    @Test(expected = ArchiveImportException.class)
    public void shouldThrowExceptionOnErrorInImportFromStream() throws Exception {
        final InputStream exceptionIn = this.getExceptionThrowingInputStream();

        // Get the importer
        final Class<T> importerClass = this.getImporterClass();
        assert importerClass != null : "Importer class must be specified by implementations";
        final T importer = ShrinkWrap.create(importerClass, "test.jar");
        try {
            final GenericArchive archive = importer.importFrom(exceptionIn).as(GenericArchive.class);
            log.info("Imported: " + archive.toString(true));
        } finally {
            exceptionIn.close();
        }
    }

    /**
     * SHRINKWRAP-474
     */
    @Test
    public void canRoundTrip() {

        // Define a path/name for the archive and the file it contains
        final String embeddedArchiveName = "lib.jar";
        final String emptyFileName = "empty.file";

        // Create an archive which contains another archive in the lib dir
        final JavaArchive embeddedArchive = ShrinkWrap.create(JavaArchive.class, embeddedArchiveName)
                .add(EmptyAsset.INSTANCE, emptyFileName);
        final WebArchive outerArchive = ShrinkWrap.create(WebArchive.class).addAsLibraries(embeddedArchive);

        // Now pull the embedded archive back out
        final JavaArchive roundtrip = outerArchive.getAsType(JavaArchive.class,
                ArchivePaths.create("WEB-INF/lib", embeddedArchiveName),
                this.getArchiveFormat());

        Assert.assertTrue("roundtrip could not be obtained", roundtrip != null);
        Assert.assertTrue(
                "contents of embedded archive are not intact after roundtrip",
                roundtrip.contains(emptyFileName));
    }
}

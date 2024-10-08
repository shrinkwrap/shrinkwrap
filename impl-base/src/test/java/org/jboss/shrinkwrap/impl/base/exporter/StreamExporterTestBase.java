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
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.FileExistsException;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Base support for testing stream-based exporters
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public abstract class StreamExporterTestBase<T extends StreamImporter<T>> extends ExportTestBase {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(StreamExporterTestBase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures the contents of the specified {@link File} are as expected
     *
     * @param file
     *             The {@link File} to be validated.
     * @throws IOException
     *             If an I/O error occurred
     */
    protected abstract void ensureInExpectedForm(File file) throws IOException;

    /**
     * Obtains the {@link Asset} located at the specified {@link ArchivePath} in the specified {@link File}, or null if
     * nothing is found at the specified path
     *
     * @param file
     *             The {@link File} from which to retrieve the {@link Asset}.
     * @param path
     *             The {@link ArchivePath} specifying the location of the {@link Asset}.
     * @return An {@link InputStream} representing the contents of the {@link Asset} at the specified path, or null if
     *         nothing is found.
     */
    protected abstract InputStream getContentsFromExportedFile(File file, ArchivePath path) throws IOException;

    /**
     * Obtains the type of {@link StreamImporter} used for this test
     */
    protected abstract Class<T> getImporterClass();

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Test to make sue an archive can be exported and all contents are correctly located.
     */
    @Test
    public void testExport() throws Exception {
        log.info("testExport");

        // Get an archive instance
        Archive<?> archive = createArchiveWithAssets();

        // Export as InputStream
        try (final InputStream exportStream = this.exportAsInputStream(archive)) {
            // Validate
            final File tempDirectory = createTempDirectory("testExport");
            final File serialized = new File(tempDirectory, archive.getName());
            try (final FileOutputStream out = new FileOutputStream(serialized)) {
                IOUtil.copyWithClose(exportStream, out);
                ensureInExpectedForm(serialized);
            }
        }
    }

    /**
     * Test to ensure that the export process accepts an archive with only directories, no assets.
     */
    @Test
    public void testExportArchiveWithOnlyDirectories() throws IOException {
        // Create an archive with directories
        final ArchivePath path = ArchivePaths.create("/test/game");
        final Archive<?> archive = ShrinkWrap.create(JavaArchive.class, NAME_ARCHIVE).addAsDirectories(path);

        // Fully export by reading all content (export is on-demand)
        try (final InputStream content = this.exportAsInputStream(archive);
             final ByteArrayOutputStream exportedContents = new ByteArrayOutputStream()) {
            IOUtil.copyWithClose(content, exportedContents);

            final GenericArchive roundtrip = ShrinkWrap.create(this.getImporterClass(), "roundtrip.zip")
                    .importFrom(new ByteArrayInputStream(exportedContents.toByteArray())).as(GenericArchive.class);
            log.info(roundtrip.toString(true));
            Assertions.assertTrue(roundtrip.contains(path));
        }
    }

    /**
     * Test to make sure an archive can be exported to file and all contents are correctly located.
     */
    @Test
    public void testExportToFile() throws IOException {
        log.info("testExportToFile");

        // Get a temp directory for the test
        File tempDirectory = createTempDirectory("testExportToFile");

        // Get an archive instance
        Archive<?> archive = createArchiveWithAssets();

        // Export as File
        final File exported = new File(tempDirectory, archive.getName());
        this.exportAsFile(archive, exported, true);

        // Roundtrip assertion
        this.ensureInExpectedForm(exported);
    }

    /**
     * Ensures that we get an {@link IllegalArgumentException} if we attempt to export to a directory
     */
    @Test
    public void testExportToDirectoryFails() throws IOException {
        log.info("testExportToDirectoryFails");

        // Get a temp directory for the test
        File tempDirectory = createTempDirectory("testExportToDirectoryFails");

        // Get an archive instance
        Archive<?> archive = createArchiveWithAssets();

        Assertions.assertThrows(IllegalArgumentException.class, () -> this.exportAsFile(archive, tempDirectory, true),
                "Should have encountered IllegalArgumentException exporting to a dir");
    }

    /**
     * Test to make sure an archive can be exported to a {@link OutputStream} and all contents are correctly located.
     */
    @Test
    public void testExportToOutStream() throws IOException {
        log.info("testExportToOutStream");

        // Get a temp directory for the test
        final File tempDirectory = createTempDirectory("testExportToOutStream");

        // Get an archive instance
        final Archive<?> archive = createArchiveWithAssets();

        // Export as OutStream and flush to a file manually
        final File serializedArchive = new File(tempDirectory, archive.getName());
        try (final OutputStream out = new FileOutputStream(serializedArchive)) {
            this.exportToOutputStream(archive, out);
        }

        // Validate
        this.ensureInExpectedForm(serializedArchive);
    }

    /**
     * Test to make sure an archive can be exported to file and all contents are correctly located.
     */
    @Test
    public void testExportToExistingFileFails() throws IOException {
        log.info("testExportToExistingFileFails");

        // Get a temp directory for the test
        final File tempDirectory = createTempDirectory("testExportToExistingFileFails");

        // Get an archive instance
        final Archive<?> archive = createArchiveWithAssets();

        // Export as File
        final File alreadyExists = new File(tempDirectory, archive.getName());
        try (final OutputStream alreadyExistsOutputStream = Files.newOutputStream(alreadyExists.toPath())) {
            alreadyExistsOutputStream.write(new byte[] {});
        }
        Assertions.assertTrue(alreadyExists.exists(), "The test setup is incorrect; an empty file should exist before writing the archive");

        Assertions.assertThrows(FileExistsException.class, () -> this.exportAsFile(archive, alreadyExists, false),
                "Should get FileExistsException when exporting to an existing file when overwrite is false");
    }

    /**
     * Test to make sue an archive can be exported and nested archives are also in exported.
     */
    @Test
    public void testExportNested() throws Exception {
        log.info("testExportNested");

        // Get a temp directory for the test
        final File tempDirectory = createTempDirectory("testExportNested");

        // Get an archive instance
        final Archive<?> archive = createArchiveWithNestedArchives();

        // Export as InputStream
        try (final InputStream exportStream = this.exportAsInputStream(archive)) {
            // Write out and retrieve as exported file
            final File exported = new File(tempDirectory, NAME_ARCHIVE + this.getArchiveExtension());
            try (final OutputStream exportedOut = Files.newOutputStream(exported.toPath())) {
                IOUtil.copyWithClose(exportStream, exportedOut);
            }
            // Validate entries were written out
            this.ensureAssetInExportedFile(exported, PATH_ONE, ASSET_ONE);
            this.ensureAssetInExportedFile(exported, PATH_TWO, ASSET_TWO);

            // Validate nested archive entries were written out
            final ArchivePath nestedArchivePath = ArchivePaths.create(NAME_NESTED_ARCHIVE + this.getArchiveExtension());

            // Get input stream for entry
            try (final InputStream nestedArchiveStream = this.getContentsFromExportedFile(exported, nestedArchivePath)) {
                // Write out and retrieve nested contents
                final File nestedFile = new File(tempDirectory, NAME_NESTED_ARCHIVE + this.getArchiveExtension());
                try (final OutputStream nestedOut = Files.newOutputStream(nestedFile.toPath())) {
                    IOUtil.copyWithClose(nestedArchiveStream, nestedOut);
                }

                // Ensure contents are in the nested
                this.ensureAssetInExportedFile(nestedFile, PATH_ONE, ASSET_ONE);
                this.ensureAssetInExportedFile(nestedFile, PATH_TWO, ASSET_TWO);
            }

            // Validate nested archive entries were written out
            final ArchivePath nestedArchiveTwoPath = ArchivePaths.create(NESTED_PATH,
                    NAME_NESTED_ARCHIVE_2 + this.getArchiveExtension());
            this.getContentsFromExportedFile(exported, nestedArchiveTwoPath);
            try (final InputStream nestedArchiveTwoStream = this.getContentsFromExportedFile(exported, nestedArchiveTwoPath)) {
                // Write out and retrieve second nested contents
                final File nestedTwoFile = new File(tempDirectory, NAME_NESTED_ARCHIVE_2 + this.getArchiveExtension());
                try (final OutputStream nestedTwoOut = Files.newOutputStream(nestedTwoFile.toPath())) {
                    IOUtil.copyWithClose(nestedArchiveTwoStream, nestedTwoOut);
                }

                // Ensure contents are in the second nested
                this.ensureAssetInExportedFile(nestedTwoFile, PATH_ONE, ASSET_ONE);
                this.ensureAssetInExportedFile(nestedTwoFile, PATH_TWO, ASSET_TWO);
            }
        }
    }

    @Test
    public void testExportThrowsArchiveExceptionOnAssetWriteFailure() throws IOException {
        log.info("testExportThrowsArchiveExceptionOnAssetWriteFailure");
        Archive<?> archive = createArchiveWithAssets();

        // Check if the path already contains a node, so we remove it from the parent's children
        if (archive.contains(PATH_ONE)) {
            archive.delete(PATH_ONE);
        }

        archive.add(() -> {
            throw new RuntimeException("Mock Exception from an Asset write");
        }, PATH_ONE);

        // Export
        try (final InputStream in = this.exportAsInputStream(archive);
             // Read in the full content (to in turn empty the underlying buffer and ensure we complete)
             final OutputStream sink = new OutputStream() {
                 @Override
                 public void write(int b) {
                 }
             }) {
            Assertions.assertThrows(ArchiveExportException.class, () -> IOUtil.copyWithClose(in, sink));
        }
    }

    // -------------------------------------------------------------------------------------||
    // Helper Methods ---------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Exports the specified archive as an {@link InputStream}
     */
    private InputStream exportAsInputStream(final Archive<?> archive) {
        assert archive != null : "archive must be specified";
        final Class<? extends StreamExporter> exporter = this.getExporterClass();
        assert exporter != null : "Exporter class must be specified";
        return archive.as(this.getExporterClass()).exportAsInputStream();
    }

    /**
     * Exports the specified archive as a {@link File}, overwriting an existing one is specified
     *
     *
     * @param archive
     *             The {@link Archive} to be exported.
     * @param file
     *             The {@link File} to which the archive will be exported.
     * @param overwrite
     *             A boolean indicating whether to overwrite the file if it already exists.
     */
    private void exportAsFile(final Archive<?> archive, final File file, final boolean overwrite) {
        // Precondition checks
        assert file != null : "file must be specified";
        assert archive != null : "archive must be specified";

        // Export
        final Class<? extends StreamExporter> exporter = this.getExporterClass();
        assert exporter != null : "Exporter class must be specified";
        archive.as(exporter).exportTo(file, overwrite);
    }

    /**
     * Exports the specified archive to an {@link OutputStream}
     *
     * @param archive
     *             The {@link Archive} to be exported.
     * @param out
     *             The {@link OutputStream} to which the archive will be exported.
     */
    private void exportToOutputStream(final Archive<?> archive, final OutputStream out) {
        assert archive != null : "archive must be specified";
        assert out != null : "outstream must be specified";

        // Export
        final Class<? extends StreamExporter> exporter = this.getExporterClass();
        assert exporter != null : "Exporter class must be specified";
        try {
            archive.as(exporter).exportTo(out);
        } finally {
            try {
                out.close();
            } catch (final IOException ioe) {
                log.warning("Could not close " + out + ": " + ioe);
            }
        }
    }

    /**
     * Test implementation of an {@link ExecutorService} which counts all jobs submitted.
     *
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     * @version $Revision: $
     */
    private static class CountingExecutorService implements ExecutorService {

        private final ExecutorService delegate;

        int counter = 0;

        public CountingExecutorService() {
            delegate = Executors.newSingleThreadExecutor();
        }

        @Override
        public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
            return delegate.awaitTermination(timeout, unit);
        }

        @Override
        public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
            return delegate.invokeAll(tasks);
        }

        @Override
        public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout,
            final TimeUnit unit) throws InterruptedException {
            return delegate.invokeAll(tasks, timeout, unit);
        }

        @Override
        public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException,
            ExecutionException {
            return delegate.invokeAny(tasks);
        }

        @Override
        public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
            return delegate.invokeAny(tasks, timeout, unit);
        }

        @Override
        public boolean isShutdown() {
            return delegate.isShutdown();
        }

        @Override
        public boolean isTerminated() {
            return delegate.isTerminated();
        }

        @Override
        public void shutdown() {
            delegate.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            return delegate.shutdownNow();
        }

        @Override
        public <T> Future<T> submit(final Callable<T> task) {
            counter++;
            return delegate.submit(task);
        }

        @Override
        public Future<?> submit(final Runnable task) {
            counter++;
            return delegate.submit(task);
        }

        @Override
        public <T> Future<T> submit(final Runnable task, final T result) {
            counter++;
            return delegate.submit(task, result);
        }

        @Override
        public void execute(final Runnable command) {
            counter++;
            delegate.execute(command);
        }

    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#ensureAssetInExportedFile(java.io.File,
     *      org.jboss.shrinkwrap.api.ArchivePath, org.jboss.shrinkwrap.api.asset.Asset)
     */
    protected final void ensureAssetInExportedFile(final File file, final ArchivePath path, final Asset asset)
        throws IOException {
        // Precondition checks
        assert file != null : "file must be specified";
        assert path != null : "path must be specified";
        assert asset != null : "asset must be specified";

        // Get as Exported File
        try (final InputStream actualStream = this.getContentsFromExportedFile(file, path)) {
            assert actualStream != null : "No contents found at path " + path + " in " + file.getAbsolutePath();
            byte[] actualContents = IOUtil.asByteArray(actualStream);
            byte[] expectedContents = IOUtil.asByteArray(asset.openStream());
            Assertions.assertArrayEquals(expectedContents, actualContents);
        }
    }
}

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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;

/**
 * ExplodedExporterDelegate
 * <p>
 * Delegate used to export an archive into an exploded directory structure.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExplodedExporterDelegate extends AbstractExporterDelegate<File> {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ExplodedExporterDelegate.class.getName());

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Output directory to write the exploded content to.
     */
    private final File outputDirectory;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new exploded exporter delegate for the provided {@link Archive}
     */
    public ExplodedExporterDelegate(Archive<?> archive, File outputDirectory) {
        super(archive);
        this.outputDirectory = outputDirectory;

        validateOutputDirectory(outputDirectory);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#processNode(ArchivePath, Node)
     */
    @Override
    protected void processNode(ArchivePath path, Node node) {
        // Get path to file
        final String assetFilePath = path.get();

        // Create a file for the asset
        final File assetFile = new File(outputDirectory, assetFilePath);

        // Get the assets parent directory and make sure it exists
        final File assetParent = assetFile.getParentFile();
        if (!assetParent.exists()) {
            if (!assetParent.mkdirs()) {
                throw new ArchiveExportException("Failed to write asset.  Unable to create parent directory.");
            }
        }

        // Handle Archive assets separately
        if (node != null && node.getAsset() instanceof ArchiveAsset) {
            ArchiveAsset nesteArchiveAsset = ArchiveAsset.class.cast(node.getAsset());
            processArchiveAsset(assetParent, nesteArchiveAsset);
            return;
        }

        // Handle directory assets separately
        try {
            final boolean isDirectory = (node.getAsset() == null);
            if (isDirectory) {
                // If it doesn't already exist
                if (!assetFile.exists()) {
                    // Attempt a creation
                    if (!assetFile.mkdirs()) {
                        // Some error in writing
                        throw new ArchiveExportException("Failed to write directory: " + assetFile.getAbsolutePath());
                    }
                }
            }
            // Only handle non-directory assets, otherwise the path is handled above
            else {
                try {
                    if (log.isLoggable(Level.FINE)) {
                        log.fine("Writing asset " + path.get() + " to " + assetFile.getAbsolutePath());
                    }
                    // Get the asset streams
                    final InputStream assetInputStream = node.getAsset().openStream();
                    final FileOutputStream assetFileOutputStream = new FileOutputStream(assetFile);
                    final BufferedOutputStream assetBufferedOutputStream = new BufferedOutputStream(
                        assetFileOutputStream, 8192);

                    // Write contents
                    IOUtil.copyWithClose(assetInputStream, assetBufferedOutputStream);
                } catch (final Exception e) {
                    // Provide a more detailed exception than the outer block
                    throw new ArchiveExportException("Failed to write asset " + path + " to " + assetFile, e);
                }
            }
        } catch (final Exception e) {
            throw new ArchiveExportException("Unexpected error encountered in export of " + node, e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate#getResult()
     */
    @Override
    protected File getResult() {
        return outputDirectory;
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Processes a nested archive by delegating to the {@link ExplodedExporter} to export the archive contents
     * into the specified parent directory.
     *
     * @param parentDirectory
     *            The directory to which the nested archive will be exported.
     * @param nestedArchiveAsset
     *            The nested archive asset that will be processed and exported.
     */
    private void processArchiveAsset(File parentDirectory, ArchiveAsset nestedArchiveAsset) {
        // Get the nested archive
        Archive<?> nestedArchive = nestedArchiveAsset.getArchive();
        nestedArchive.as(ExplodedExporter.class).exportExploded(parentDirectory);
    }

    /**
     * Validates and initializes the output directory by creating it if it doesn't exist and ensuring it's a directory.
     *
     * @param outputDirectory
     *            The directory to be validated and initialized.
     * @return The validated output directory.
     * @throws ArchiveExportException
     *            If the directory cannot be created.
     * @throws IllegalArgumentException
     *            If the output directory is an existing file.
     */
    private File validateOutputDirectory(File outputDirectory) {
        // Create output directory
        if (!outputDirectory.mkdir() && !outputDirectory.exists()) {
            throw new ArchiveExportException("Unable to create archive output directory - " + outputDirectory);
        }
        if (outputDirectory.isFile()) {
            throw new IllegalArgumentException("Unable to export exploded directory to "
                + outputDirectory.getAbsolutePath() + ", it points to a existing file");
        }

        return outputDirectory;
    }

}

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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * ExplodedExporterImpl
 *
 * Implementation of ExplodedExporter used to export an Archive as an exploded directory structure.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExplodedExporterImpl extends AssignableBase<Archive<?>> implements ExplodedExporter {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ExplodedExporterImpl.class.getName());

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    public ExplodedExporterImpl(final Archive<?> archive) {
        super(archive);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - ExplodedExporter ----------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.exporter.ExplodedExporter#exportExploded(java.io.File)
     */
    @Override
    public File exportExploded(final File baseDirectory) {
        return exportExploded(baseDirectory, this.getArchive().getName());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.exporter.ExplodedExporter#exportExploded(java.io.File, java.lang.String)
     */
    @Override
    public File exportExploded(File baseDirectory, String directoryName) {
        final Archive<?> archive = this.getArchive();
        Validate.notNull(archive, "No archive provided");
        Validate.notNull(baseDirectory, "No baseDirectory provided");

        // Directory must exist
        if (!baseDirectory.exists()) {
            throw new IllegalArgumentException("Parent directory does not exist");
        }
        // Must be a directory
        if (!baseDirectory.isDirectory()) {
            throw new IllegalArgumentException("Provided parent directory is not a valid directory");
        }

        return export(archive, new File(baseDirectory, directoryName));
    }

    @Override
    public File exportExplodedInto(File directory) {
        final Archive<?> archive = this.getArchive();
        Validate.notNull(archive, "No archive provided");

        return export(archive, directory);
    }

    private File export(final Archive<?> archive, File outputDirectory) {
        // Get the export delegate
        final ExplodedExporterDelegate exporterDelegate = new ExplodedExporterDelegate(archive, outputDirectory);

        // Run the export and get the result
        final File explodedDirectory = exporterDelegate.export();

        if (log.isLoggable(Level.FINE)) {
            log.fine("Created Exploded Archive: " + explodedDirectory.getAbsolutePath());
        }
        // Return the exploded dir
        return explodedDirectory;
    }
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.base;

import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.TarBzExporter;
import org.jboss.shrinkwrap.api.exporter.TarExporter;
import org.jboss.shrinkwrap.api.exporter.TarGzExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.api.importer.TarBzImporter;
import org.jboss.shrinkwrap.api.importer.TarGzImporter;
import org.jboss.shrinkwrap.api.importer.TarImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

/**
 * Value object binding the {@code StreamExporter} and the {@code StreamImporter} implementations of the same archive
 * format.
 *
 * @author Davide D'Alto
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 * @version $Revision: $
 * @see StreamImporter
 * @see StreamExporter
 */
class ArchiveFormatStreamBindings {

    private final Class<? extends StreamImporter<?>> importer;

    private final Class<? extends StreamExporter> exporter;

    ArchiveFormatStreamBindings(final ArchiveFormat format) {
        // Initialize the stream importer/exporters based upon the type passed in
        switch (format) {
            case TAR:
                this.importer = TarImporter.class;
                this.exporter = TarExporter.class;
                break;
            case TAR_GZ:
                this.importer = TarGzImporter.class;
                this.exporter = TarGzExporter.class;
                break;
            case TAR_BZ:
                this.importer = TarBzImporter.class;
                this.exporter = TarBzExporter.class;
                break;
            case ZIP:
                this.importer = ZipImporter.class;
                this.exporter = ZipExporter.class;
                break;
            default:
                throw new IllegalArgumentException("Unknown format specified: " + format);
        }
    }

    public Class<? extends StreamExporter> getExporter() {
        return exporter;
    }

    public Class<? extends StreamImporter<?>> getImporter() {
        return importer;
    }
}
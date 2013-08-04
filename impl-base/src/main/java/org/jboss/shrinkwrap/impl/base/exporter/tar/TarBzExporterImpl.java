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
package org.jboss.shrinkwrap.impl.base.exporter.tar;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.TarBzExporter;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractStreamExporterImpl;

/**
 * Implementation of {@link TarBzExporter} used to export an Archive as a TAR format encoded in BZip2.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 */
public class TarBzExporterImpl extends AbstractStreamExporterImpl implements
        TarBzExporter {

    /**
     * Creates a new exporter for the specified archive
     */
    public TarBzExporterImpl(final Archive<?> archive) {
        super(archive);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.exporter.StreamExporter#exportAsInputStream()
     */
    @Override
    public InputStream exportAsInputStream() {
        // Create export delegate
        final AbstractExporterDelegate<InputStream> exportDelegate = new TarBzExporterDelegate(this.getArchive());

        // Execute export
        return exportDelegate.export();
    }

}

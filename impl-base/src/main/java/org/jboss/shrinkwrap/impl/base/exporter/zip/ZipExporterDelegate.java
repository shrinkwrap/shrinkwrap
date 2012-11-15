/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.base.exporter.zip;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractExporterDelegate;

import java.util.zip.ZipOutputStream;

import java.io.InputStream;

/**
 * Implementation of a ZIP exporter. Cannot handle archives with no content (as there'd be no
 * {@link java.util.zip.ZipEntry} s to write to the {@link ZipOutputStream}
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
class ZipExporterDelegate extends AbstractExporterDelegate<InputStream> {

    protected ZipExporterDelegate(final Archive<?> archive) {
        super(archive);

        // Precondition check
        if (archive.getContent().isEmpty()) {
            throw new IllegalArgumentException(
                "[SHRINKWRAP-93] Cannot use this JDK-based implementation to export as ZIP an archive with no content: "
                    + archive.toString());
        }
    }

    @Override
    protected void processNode(final ArchivePath path, final Node node) {
        // do nothing
    }

    @Override
    protected InputStream getResult() {
        return new ZipOnDemandInputStream(getArchive());
    }
}

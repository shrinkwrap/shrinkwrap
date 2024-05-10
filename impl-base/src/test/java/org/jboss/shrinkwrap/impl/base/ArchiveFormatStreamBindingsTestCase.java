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

import static junit.framework.Assert.assertEquals;

import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.exporter.TarBz2Exporter;
import org.jboss.shrinkwrap.api.exporter.TarExporter;
import org.jboss.shrinkwrap.api.exporter.TarGzExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.TarBz2Importer;
import org.jboss.shrinkwrap.api.importer.TarGzImporter;
import org.jboss.shrinkwrap.api.importer.TarImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.junit.Test;

/**
 * @author Davide D'Alto
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 */
public class ArchiveFormatStreamBindingsTestCase {
    @Test
    public void testZipImporter() {
        assertEquals(ZipImporter.class, new ArchiveFormatStreamBindings(ArchiveFormat.ZIP).getImporter());
    }

    @Test
    public void testZipExporter() {
        assertEquals(ZipExporter.class, new ArchiveFormatStreamBindings(ArchiveFormat.ZIP).getExporter());
    }

    @Test
    public void testTarImporter() {
        assertEquals(TarImporter.class, new ArchiveFormatStreamBindings(ArchiveFormat.TAR).getImporter());
    }

    @Test
    public void testTarExporter() {
        assertEquals(TarExporter.class, new ArchiveFormatStreamBindings(ArchiveFormat.TAR).getExporter());
    }

    @Test
    public void testTarGzImporter() {
        assertEquals(TarGzImporter.class, new ArchiveFormatStreamBindings(ArchiveFormat.TAR_GZ).getImporter());
    }

    @Test
    public void testTarGzExporter() {
        assertEquals(TarGzExporter.class, new ArchiveFormatStreamBindings(ArchiveFormat.TAR_GZ).getExporter());
    }

    @Test
    public void testTarBz2Importer() {
        assertEquals(TarBz2Importer.class, new ArchiveFormatStreamBindings(ArchiveFormat.TAR_BZ).getImporter());
    }

    @Test
    public void testTarBz2Exporter() {
        assertEquals(TarBz2Exporter.class, new ArchiveFormatStreamBindings(ArchiveFormat.TAR_BZ).getExporter());
    }
}

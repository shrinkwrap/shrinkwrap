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
package org.jboss.shrinkwrap.api;

import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.TarExporter;
import org.jboss.shrinkwrap.api.exporter.TarGzExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.StreamImporter;
import org.jboss.shrinkwrap.api.importer.TarGzImporter;
import org.jboss.shrinkwrap.api.importer.TarImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

/**
 * Binds the {@code StreamExporter} and the {@code StreamImporter} implementations of the same archive format.
 *
 * @author Davide D'Alto
 * @version $Revision: $
 * @see StreamImporter
 * @see StreamExporter
 */
public enum ArchiveFormat {
   ZIP(ZipImporter.class, ZipExporter.class),
   TAR(TarImporter.class, TarExporter.class),
   TAR_GZ(TarGzImporter.class, TarGzExporter.class);

   private final Class<? extends StreamImporter<?>> importer;

   private final Class<? extends StreamExporter> exporter;

   private ArchiveFormat(Class<? extends StreamImporter<?>> importer, Class<? extends StreamExporter> exporter)
   {
      this.importer = importer;
      this.exporter = exporter;
   }

   public Class<? extends StreamExporter> getExporter()
   {
      return exporter;
   }

   public Class<? extends StreamImporter<?>> getImporter()
   {
      return importer;
   }
}
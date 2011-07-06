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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.jboss.shrinkwrap.api.ArchiveFormat.TAR;
import static org.jboss.shrinkwrap.api.ArchiveFormat.TAR_GZ;
import static org.jboss.shrinkwrap.api.ArchiveFormat.ZIP;
import static org.jboss.shrinkwrap.api.ArchiveFormat.values;

import org.jboss.shrinkwrap.api.exporter.TarExporter;
import org.jboss.shrinkwrap.api.exporter.TarGzExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.TarGzImporter;
import org.jboss.shrinkwrap.api.importer.TarImporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.junit.Test;

/**
 * @author Davide D'Alto
 *
 * @version $Revision: $
 */
public class ArchiveFormatTestCase
{
   @Test
   public void testNotNullImporters() throws Exception
   {
      for (ArchiveFormat format : values())
      {
         assertNotNull("Importer class for " + format + " should not be null", format.getImporter());
      }
   }

   @Test
   public void testNotNullExporters() throws Exception
   {
      for (ArchiveFormat format : values())
      {
         assertNotNull("Exporter class for " + format + " should not be null", format.getExporter());
      }
   }

   @Test
   public void testZipImporter() throws Exception
   {
      assertEquals(ZipImporter.class, ZIP.getImporter());
   }

   @Test
   public void testZipExporter() throws Exception
   {
      assertEquals(ZipExporter.class, ZIP.getExporter());
   }

   @Test
   public void testTarImporter() throws Exception
   {
      assertEquals(TarImporter.class, TAR.getImporter());
   }

   @Test
   public void testTarExporter() throws Exception
   {
      assertEquals(TarExporter.class, TAR.getExporter());
   }

   @Test
   public void testTarGzImporter() throws Exception
   {
      assertEquals(TarGzImporter.class, TAR_GZ.getImporter());
   }

   @Test
   public void testTarGzExporter() throws Exception
   {
      assertEquals(TarGzExporter.class, TAR_GZ.getExporter());
   }
}

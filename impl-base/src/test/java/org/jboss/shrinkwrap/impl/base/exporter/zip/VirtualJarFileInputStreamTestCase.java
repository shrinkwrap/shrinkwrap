/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.shrinkwrap.impl.base.exporter.zip;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.junit.Assert.assertEquals;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class VirtualJarFileInputStreamTestCase
{
   @Test
   public void testClass()
   {
      Archive<?> archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
         .addClass(VirtualJarFileInputStreamTestCase.class);
      InputStream in = archive.as(ZipExporter.class).exportAsInputStream();
      Archive<?> imported = ShrinkWrap.create(JavaArchive.class);
      imported.as(ZipImporter.class).importFrom(in);
   }

   @Test
   public void testSimple() throws IOException
   {
      Archive<?> archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
         .add(new StringAsset("Hello world"), "test.txt");
      ArchiveJarZipInputStream virtualIn = new ArchiveJarZipInputStream(new ArchiveJarInputStream(archive));
      JarInputStream in = new JarInputStream(virtualIn);
      JarEntry entry = in.getNextJarEntry();
      assertEquals("test.txt", entry.getName());
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int c;
      while((c = in.read()) != -1)
         out.write(c);
      String contents = out.toString();
      assertEquals("Hello world", contents);
   }

   @Test
   public void testImport() throws IOException
   {
      Archive<?> archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
         .addAsDirectory("test")
         .add(new StringAsset("Hello world"), "test.txt");
      InputStream in = archive.as(ZipExporter.class).exportAsInputStream();
      Archive<?> imported = ShrinkWrap.create(JavaArchive.class);
      imported.as(ZipImporter.class).importFrom(in);
   }
}

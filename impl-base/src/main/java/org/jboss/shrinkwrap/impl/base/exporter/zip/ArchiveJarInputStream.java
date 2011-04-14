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
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;

import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.*;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * Virtual JarInputStream used for representing any Archive as a JarInputStream.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 * @author <a href="baileyje@gmail.com">John Bailey</a>
 */
class ArchiveJarInputStream extends JarInputStream
{
   private static final InputStream EMPTY_STREAM = new InputStream()
   {
      public int read() throws IOException
      {
         return -1;
      }
   };

   private static final String META_INF_DIR = "META-INF";

   // use LinkedList, Deque is only available on 1.6
   private final LinkedList<Iterator<Node>> entryItr = new LinkedList<Iterator<Node>>();

   private final Node root;

   private final Manifest manifest;

   private InputStream currentEntryStream = EMPTY_STREAM;

   private boolean closed;

   public ArchiveJarInputStream(final Archive<?> archive) throws IOException
   {
      super(EMPTY_STREAM);
      this.root = archive.get(ArchivePaths.root());
      // TODO
      this.manifest = null;
      entryItr.add(root.getChildren().iterator());
   }

   /*
    * Converts Java time to DOS time.
    */
   static long javaToDosTime(long time)
   {
      Date d = new Date(time);
      int year = d.getYear() + 1900;
      if (year < 1980)
      {
         return (1 << 21) | (1 << 16);
      }
      return (year - 1980) << 25 | (d.getMonth() + 1) << 21 | d.getDate() << 16 | d.getHours() << 11
            | d.getMinutes() << 5 | d.getSeconds() >> 1;
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public ZipEntry getNextEntry() throws IOException
   {
      return getNextJarEntry();
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public JarEntry getNextJarEntry() throws IOException
   {
      closeEntry();

      final Iterator<Node> topItr = (entryItr.size() > 0 ? entryItr.getFirst() : null);
      if (topItr == null)
      {
         return null;
      }
      if (!topItr.hasNext())
      {
         entryItr.removeFirst();
         return getNextJarEntry();
      }

      final Node nextEntry = topItr.next();
      String entryName = getEntryName(nextEntry);
      // is a directory
      if (nextEntry.getAsset() == null)
      {
         Collection<Node> children = nextEntry.getChildren();
         if (entryName.equalsIgnoreCase(META_INF_DIR))
         {
            //children = nextEntry.getChildren(MANIFEST_FILTER);
            children = nextEntry.getChildren();
         }
         entryItr.add(children.iterator());
         entryName = fixDirectoryName(entryName);
      }
      openCurrent(nextEntry);

      Attributes attributes = null;
      final Manifest manifest = getManifest();
      if (manifest != null)
      {
         attributes = manifest.getAttributes(entryName);
      }
      return new VirtualJarEntry(entryName, nextEntry, attributes);
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public Manifest getManifest()
   {
      return manifest;
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public int read() throws IOException
   {
      ensureOpen();
      return checkForEoSAndReturn(currentEntryStream.read());
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public int read(byte[] b) throws IOException
   {
      return read(b, 0, b.length);
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public int read(byte[] b, int off, int len) throws IOException
   {
      ensureOpen();
      return checkForEoSAndReturn(currentEntryStream.read(b, off, len));
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public int available() throws IOException
   {
      ensureOpen();
      return currentEntryStream.available() > 0 ? 1 : 0;
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public void close() throws IOException
   {
      closed = true;
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public void closeEntry() throws IOException
   {
      if (currentEntryStream != null)
      {
         currentEntryStream.close();
      }
   }

   /**
    * {@inheritDoc} *
    */
   @Override
   public long skip(long n) throws IOException
   {
      ensureOpen();
      return currentEntryStream.skip(n);
   }

   /**
    * {@inheritDoc} *
    */
   private void ensureOpen() throws IOException
   {
      if (closed)
      {
         throw new IOException("Stream is closed");
      }
   }

   /**
    * Check to see if the result is the EOF and if so exchange the current entry stream with the empty stream.
    *
    * @param result
    * @return int result
    * @throws IOException
    */
   private int checkForEoSAndReturn(int result) throws IOException
   {
      if (result == -1)
      {
         closeEntry();
         currentEntryStream = EMPTY_STREAM;
      }
      return result;
   }

   /**
    * Open the current virtual file as the current JarEntry stream.
    *
    * @param current
    * @throws IOException
    */
   private void openCurrent(Node current) throws IOException
   {
      // is a directory
      if (current.getAsset() == null)
      {
         currentEntryStream = EMPTY_STREAM;
      }
      else
      {
         try
         {
            currentEntryStream = current.getAsset().openStream();
         }
         catch (RuntimeException e)
         {
            // yuck
            throw new ArchiveExportException(e);
         }
      }
   }

   /**
    * Get the entry name from a VirtualFile.
    *
    * @param entry
    * @return
    */
   private String getEntryName(Node entry)
   {
      return entry.getPath().get().substring(1);
   }

   /**
    * Make sure directory names end with a trailing slash
    *
    * @param name
    * @return
    */
   private String fixDirectoryName(String name)
   {
      if (!name.endsWith("/"))
      {
         return name + "/";
      }
      return name;
   }

   /**
    * Virtual JarEntry used for representing a child VirtualFile as a JarEntry.
    *
    * @author <a href="baileyje@gmail.com">John Bailey</a>
    * @version $Revision: 1.1 $
    */
   public static class VirtualJarEntry extends JarEntry
   {
      private final Node virtualFile;

      private final Attributes attributes;

      /**
       * Construct a new
       *
       * @param name
       * @param virtualFile
       * @param attributes
       */
      public VirtualJarEntry(String name, Node virtualFile, Attributes attributes)
      {
         super(name);
         this.virtualFile = virtualFile;
         this.attributes = attributes;
      }

      /**
       * {@inheritDoc} *
       */
      @Override
      public Attributes getAttributes() throws IOException
      {
         return attributes;
      }

      @Override
      public long getTime()
      {
         //return virtualFile.getLastModified();
         //throw new UnsupportedOperationException("getLastModified()");
         return javaToDosTime(System.currentTimeMillis());
      }

      /**
       * {@inheritDoc} *
       */
      @Override
      public boolean isDirectory()
      {
         return virtualFile.getAsset() == null;
      }

      /**
       * {@inheritDoc} *
       */
      @Override
      public Certificate[] getCertificates()
      {
         final CodeSigner[] signers = getCodeSigners();
         if (signers == null)
         {
            return null;
         }
         final List<Certificate> certs = new ArrayList<Certificate>();
         for (CodeSigner signer : signers)
         {
            certs.addAll(signer.getSignerCertPath().getCertificates());
         }
         return certs.toArray(new Certificate[certs.size()]);
      }

      /**
       * {@inheritDoc} *
       */
      @Override
      public CodeSigner[] getCodeSigners()
      {
         //return virtualFile.getCodeSigners();
         throw new UnsupportedOperationException("getCodeSigners()");
      }
   }
}

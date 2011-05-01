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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.zip.CRC32;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;

import static org.jboss.shrinkwrap.impl.base.exporter.zip.ArchiveJarInputStream.CENHDR;
import static org.jboss.shrinkwrap.impl.base.exporter.zip.ArchiveJarInputStream.javaToDosTime;

/**
 * An {@link InputStream} that can be used to wrap an {@link ArchiveJarInputStream} 
 * (so any {@link Archive}) can produce a byte stream following the ZIP standard.
 *
 * @author <a href="mailto:jbailey@redhat.com">John Bailey</a>
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
class ArchiveJarZipInputStream extends InputStream
{
   // Needs to be sufficiently sized to allow local and central file headers with a single entry name
   private static final int MINIMUM_BUFFER_LENGTH = 1024;

   private final ArchiveJarInputStream virtualJarInputStream;

   private State currentState = State.NOT_STARTED;

   private final List<ProcessedEntry> processedEntries = new LinkedList<ProcessedEntry>();

   private ProcessedEntry currentEntry;

   private final ByteBuffer buffer;

   private final CRC32 crc = new CRC32();

   private int currentCentralEntryIdx;

   private long centralOffset;

   private long totalRead;

   private CRCInputStream currentCRCInputStream;

   private InputStream currentInputStream;

   /**
    * Create with the minimum put length
    *
    * @param virtualJarInputStream The virtual jar input stream to base the stream off of
    */
   public ArchiveJarZipInputStream(final ArchiveJarInputStream virtualJarInputStream)
   {
      this(virtualJarInputStream, MINIMUM_BUFFER_LENGTH);
   }

   /**
    * Create with the a specified put size
    *
    * @param virtualJarInputStream The virtual jar input stream to base the stream off of
    * @param bufferLength          The length of put to use
    */
   public ArchiveJarZipInputStream(final ArchiveJarInputStream virtualJarInputStream, int bufferLength)
   {
      if (virtualJarInputStream == null)
         throw new IllegalArgumentException("virtualJarInputStream is required");
      if (bufferLength < MINIMUM_BUFFER_LENGTH)
         throw new IllegalArgumentException("The totalBufferLength must be larger than " + MINIMUM_BUFFER_LENGTH);

      this.virtualJarInputStream = virtualJarInputStream;

      buffer = new ByteBuffer(bufferLength);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int read() throws IOException
   {
      int readByte = -1;
      while (currentState != null && (readByte = currentState.read(this)) == -1)
      {
         currentState = currentState.getNextState(this);
      }
      if (readByte != -1)
         totalRead++;
      return readByte;
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException
   {
      int r = -1;
      while (currentState != null && (r = currentState.read(this, b, off, len)) == -1)
      {
         currentState = currentState.getNextState(this);
      }
      if (r != -1)
         totalRead += r;
      return r;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void close() throws IOException
   {
      //VFSUtils.safeClose(virtualJarInputStream);
      virtualJarInputStream.close();
      super.close();
   }

   /**
    * Close the current entry, and calculate the crc value.
    *
    * @throws IOException if any problems occur
    */
   private void closeCurrent() throws IOException
   {
      virtualJarInputStream.closeEntry();
      currentEntry.crc = crc.getValue();
      currentEntry.uncompressedSize = currentCRCInputStream.getBytesProcessed();
      crc.reset();
   }

   /**
    * Buffer the content of the local file header for a single entry.
    *
    * @return true if the next local file header was buffered
    * @throws IOException if any problems occur
    */
   private boolean bufferLocalFileHeader() throws IOException
   {
      buffer.reset();
      JarEntry jarEntry = virtualJarInputStream.getNextJarEntry();

      if (jarEntry == null)
         return false;

      currentEntry = new ProcessedEntry(jarEntry, totalRead);
      processedEntries.add(currentEntry);

      bufferInt(ZipEntry.LOCSIG); // Local file header signature
      bufferShort(10); // Extraction version
      // adding a data descriptor if DEFLATED
      bufferShort(8); // Flags
      // TODO: use STORED when size is known
      //bufferShort(ZipEntry.STORED); // Compression type
      bufferShort(ZipEntry.DEFLATED);
      bufferInt(jarEntry.getTime()); // Entry time
      bufferInt(0); // CRC
      bufferInt(0); // Compressed size
      bufferInt(0); // Uncompressed size
      byte[] nameBytes = jarEntry.getName().getBytes("UTF8");
      bufferShort(nameBytes.length); // Entry name length
      bufferShort(0); // Extra length
      buffer(nameBytes);
      return true;
   }

   private void bufferDataDescriptor()
   {
      buffer.reset();

      bufferInt(ZipEntry.EXTSIG);
      bufferInt(currentEntry.crc);
      bufferInt(currentEntry.compressedSize); // compressed size
      long uncompressedSize = currentEntry.uncompressedSize;
      assert uncompressedSize != -1;
      bufferInt(uncompressedSize); // uncompressed size

      //crc.reset();
   }

   /**
    * Buffer the central file header record for a single entry.
    *
    * @return true if the next central file header was buffered
    * @throws IOException if any problems occur
    */
   private boolean bufferNextCentralFileHeader() throws IOException
   {
      buffer.reset();

      if (currentCentralEntryIdx == processedEntries.size())
         return false;

      ProcessedEntry entry = processedEntries.get(currentCentralEntryIdx++);

      JarEntry jarEntry = entry.jarEntry;
      bufferInt(ZipEntry.CENSIG); // Central file header signature
      bufferShort(10); // Version made by
      bufferShort(10); // Extraction version
      // see bufferLocalFileHeader for comments
      bufferShort(8); // Flags
      //bufferShort(ZipEntry.STORED); // Compression type
      bufferShort(ZipEntry.DEFLATED);
      bufferInt(javaToDosTime(jarEntry.getTime())); // Entry time
      bufferInt(entry.crc); // CRC
      bufferInt(entry.compressedSize); // Compressed size
      bufferInt(entry.uncompressedSize); // Uncompressed size
      byte[] nameBytes = jarEntry.getName().getBytes("UTF8");
      bufferShort(nameBytes.length); // Entry name length
      bufferShort(0); // Extra field length
      bufferShort(0); // File comment length
      bufferShort(0); // Disk number start
      bufferShort(0); // Internal file attributes
      bufferInt(0); // External file attributes
      bufferInt(entry.offset); // Relative offset of local header
      buffer(nameBytes);
      assert CENHDR + nameBytes.length == buffer.size() : "invalid CEN header (bad header size)";
      return true;
   }

   /**
    * Write the central file header records. This is repeated
    * until all entries have been added to the central file header.
    *
    * @throws IOException if any problem occur
    */
   private void bufferCentralDirectoryEnd() throws IOException
   {
      buffer.reset();
      long lengthOfCentral = totalRead - centralOffset;

      int count = processedEntries.size();
      bufferInt(JarEntry.ENDSIG); // End of central directory signature
      bufferShort(0); // Number of this disk
      bufferShort(0); // Start of central directory disk
      bufferShort(count); // Number of processedEntries on disk
      bufferShort(count); // Total number of processedEntries
      bufferInt(lengthOfCentral); // Size of central directory
      bufferInt(centralOffset); // Offset of start of central directory
      bufferShort(0); // Comment Length
   }

   /**
    * Buffer a 32-bit integer in little-endian
    *
    * @param i A long representation of a 32 bit int
    */
   private void bufferInt(long i)
   {
      buffer((byte) (i & 0xff));
      buffer((byte) ((i >>> 8) & 0xff));
      buffer((byte) ((i >>> 16) & 0xff));
      buffer((byte) ((i >>> 24) & 0xff));
   }

   /**
    * Buffer a 16-bit short in little-endian
    *
    * @param i An int representation of a 16 bit short
    */
   private void bufferShort(int i)
   {
      buffer((byte) (i & 0xff));
      buffer((byte) ((i >>> 8) & 0xff));
   }

   /**
    * Buffer a single byte
    *
    * @param b The byte
    */
   private void buffer(byte b)
   {
      if (buffer.hasCapacity())
         buffer.put(b);
      else
         throw new IllegalStateException("Buffer does not have enough capacity");
   }

   /**
    * Buffer a byte array
    *
    * @param bytes The bytes
    */
   private void buffer(byte[] bytes)
   {
      for (byte b : bytes)
         buffer(b);
   }

   private class ProcessedEntry
   {
      private final JarEntry jarEntry;

      private final long offset;

      private long crc;

      private long compressedSize;

      private long uncompressedSize;

      private ProcessedEntry(final JarEntry jarEntry, final long offset)
      {
         this.jarEntry = jarEntry;
         this.offset = offset;
      }
   }

   /**
    * Basic state machine that will allow the process to transition between the different process states.
    * <p/>
    * The following describes the process flow:
    * [NOT_STARTED] - Initial state
    * - Does not provide content
    * - Transitions [LOCAL_ENTRY_HEADER]
    * [LOCAL_ENTRY_HEADER] - The phase for reading the Local Directory Header
    * - Provides content of the local directory header by populating and feeding off a buffer
    * - Transitions to [ENTRY_CONTENT] if the header was written
    * - Transitions to [START_CENTRAL_DIRECTORY] if this is the last local entry header
    * [ENTRY_CONTENT] - The phase for reading the content of an entry
    * - Provides content of the entry using the VirtualJarInputStream
    * - Transitions to [LOCAL_ENTRY_HEADER]
    * [START_CENTRAL_DIRECTORY] - Phased used to transition into the central directory
    * - Does not provide content
    * - Transitions to [CENTRAL_ENTRY_HEADER]
    * [CENTRAL_ENTRY_HEADER] - The phase for reading the content of a single central directory header
    * - Provides content for the central directory header by feeding off a buffer
    * - Transitions to [CENTRAL_ENTRY_HEADER]
    * - Transitions to [CENTRAL_END] if there are no more entries
    * [CENTRAL_END] - The phase for reading the contents of the central directory end
    * - Provides content for central directory end by feeing off a buffer
    * - Transitions to NULL to terminate the processing
    */
   private enum State {
      NOT_STARTED {
         @Override
         State transition(ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            return LOCAL_ENTRY_HEADER;
         }
      },
      LOCAL_ENTRY_HEADER {
         boolean buffered;

         @Override
         void init(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            buffered = jarFileInputStream.bufferLocalFileHeader();
         }

         @Override
         int read(ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            final ByteBuffer buffer = jarFileInputStream.buffer;
            if (buffered && buffer.hasRemaining())
               return buffer.get();
            return -1;
         }

         @Override
         State transition(final ArchiveJarZipInputStream virtualJarFileInputStream) throws IOException
         {
            if (buffered)
               return ENTRY_CONTENT;
            return START_CENTRAL_DIRECTORY;
         }
      },
      ENTRY_CONTENT {
         @Override
         void init(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            jarFileInputStream.crc.reset();
            jarFileInputStream.currentCRCInputStream = new CRCInputStream(jarFileInputStream.virtualJarInputStream,
                  jarFileInputStream.crc);
            jarFileInputStream.currentInputStream = new DeflaterInputStream(jarFileInputStream.currentCRCInputStream,
                  new Deflater(Deflater.DEFAULT_COMPRESSION, true));
         }

         @Override
         int read(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            final InputStream in = jarFileInputStream.currentInputStream;
            final int c = in.read();
            if (c != -1)
            {
               jarFileInputStream.currentEntry.compressedSize++;
            }
            return c;
         }

         @Override
         int read(final ArchiveJarZipInputStream jarFileInputStream, final byte b[], final int off, final int len)
               throws IOException
         {
            final InputStream in = jarFileInputStream.currentInputStream;
            final int r = in.read(b, off, len);
            if (r != -1)
            {
               jarFileInputStream.currentEntry.compressedSize += r;
            }
            return r;
         }

         @Override
         State transition(final ArchiveJarZipInputStream virtualJarFileInputStream) throws IOException
         {
            virtualJarFileInputStream.closeCurrent();
            return DATA_DESCRIPTOR;
         }
      },
      DATA_DESCRIPTOR {
         @Override
         void init(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            jarFileInputStream.bufferDataDescriptor();
         }

         @Override
         int read(ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            final ByteBuffer buffer = jarFileInputStream.buffer;
            if (buffer.hasRemaining())
               return buffer.get();
            return -1;
         }

         @Override
         State transition(final ArchiveJarZipInputStream virtualJarFileInputStream) throws IOException
         {
            return LOCAL_ENTRY_HEADER;
         }
      },
      START_CENTRAL_DIRECTORY {
         @Override
         void init(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            jarFileInputStream.centralOffset = jarFileInputStream.totalRead;
         }

         @Override
         State transition(final ArchiveJarZipInputStream virtualJarFileInputStream) throws IOException
         {
            return CENTRAL_ENTRY_HEADER;
         }
      },
      CENTRAL_ENTRY_HEADER {
         boolean buffered;

         @Override
         void init(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            buffered = jarFileInputStream.bufferNextCentralFileHeader();
         }

         @Override
         int read(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            final ByteBuffer buffer = jarFileInputStream.buffer;
            if (buffered && buffer.hasRemaining())
               return buffer.get() & 0xFF;
            return -1;
         }

         @Override
         State transition(final ArchiveJarZipInputStream virtualJarFileInputStream) throws IOException
         {
            if (buffered)
               return CENTRAL_ENTRY_HEADER;
            return CENTRAL_END;
         }
      },
      CENTRAL_END {
         @Override
         void init(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            jarFileInputStream.bufferCentralDirectoryEnd();
         }

         @Override
         int read(final ArchiveJarZipInputStream jarFileInputStream) throws IOException
         {
            final ByteBuffer buffer = jarFileInputStream.buffer;
            if (buffer.hasRemaining())
               return buffer.get();
            return -1;
         }

         @Override
         State transition(final ArchiveJarZipInputStream virtualJarFileInputStream) throws IOException
         {
            return null;
         }
      };

      void init(ArchiveJarZipInputStream jarFileInputStream) throws IOException
      {
      }

      abstract State transition(ArchiveJarZipInputStream virtualJarFileInputStream) throws IOException;

      int read(ArchiveJarZipInputStream jarFileInputStream) throws IOException
      {
         return -1;
      }

      int read(ArchiveJarZipInputStream jarFileInputStream, byte b[], int off, int len) throws IOException
      {
         // see InputStream.read(byte[],int,int)
         int c = read(jarFileInputStream);
         if (c == -1)
         {
            return -1;
         }
         b[off] = (byte) c;

         int i = 1;
         try
         {
            for (; i < len; i++)
            {
               c = read(jarFileInputStream);
               if (c == -1)
               {
                  break;
               }
               b[off + i] = (byte) c;
            }
         }
         catch (IOException ee)
         {
         }
         return i;
      }

      State getNextState(ArchiveJarZipInputStream jarFileInputStream) throws IOException
      {
         State nextState = transition(jarFileInputStream);
         if (nextState != null)
            nextState.init(jarFileInputStream);
         return nextState;
      }
   }

   private static class ByteBuffer
   {

      private final int bufferLength;

      private final byte[] buffer;

      private int bufferPosition;

      private int bufferDepth;

      private ByteBuffer(final int bufferLength)
      {
         this.buffer = new byte[bufferLength];
         this.bufferLength = bufferLength;
      }

      private boolean hasRemaining()
      {
         return bufferPosition < bufferDepth;
      }

      private boolean hasCapacity()
      {
         return bufferDepth < bufferLength;
      }

      private byte get()
      {
         return buffer[bufferPosition++];
      }

      private void put(byte b)
      {
         buffer[bufferDepth++] = b;
      }

      private void reset()
      {
         bufferPosition = 0;
         bufferDepth = 0;
      }

      private int size()
      {
         return bufferDepth;
      }
   }
}
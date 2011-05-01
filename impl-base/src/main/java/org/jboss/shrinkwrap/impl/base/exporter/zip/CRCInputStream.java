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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
class CRCInputStream extends FilterInputStream
{
   private final CRC32 crc;

   private long bytesProcessed = 0;

   protected CRCInputStream(final InputStream in, final CRC32 crc)
   {
      super(in);
      this.crc = crc;
   }

   protected long getBytesProcessed()
   {
      return bytesProcessed;
   }

   @Override
   public int read() throws IOException
   {
      int c = super.read();
      if (c != -1)
      {
         crc.update(c);
         bytesProcessed++;
      }
      return c;
   }

   @Override
   public int read(byte[] b) throws IOException
   {
      final int len = super.read(b);
      if (len != -1)
      {
         crc.update(b, 0, len);
         bytesProcessed += len;
      }
      return len;
   }

   @Override
   public int read(byte[] b, int off, int len) throws IOException
   {
      len = super.read(b, off, len);
      if (len != -1)
      {
         crc.update(b, off, len);
         bytesProcessed += len;
      }
      return len;
   }
}

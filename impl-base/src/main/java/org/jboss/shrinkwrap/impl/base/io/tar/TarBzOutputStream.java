/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.base.io.tar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.jboss.shrinkwrap.impl.base.io.tar.bzip.BZip2CompressorOutputStream;

// we extend TarOutputStream to have the same type,
// BUT, we don't use ANY methods. It's all about
// typing.

/**
 * Outputs tar.bz2 files. Added functionality that it doesn't need to know the size of an entry. If an entry has zero
 * size when it is put in the Tar, then it buffers it until it's closed and it knows the size.
 *
 * @author "Bay" <bayard@generationjava.com>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 */

public class TarBzOutputStream extends TarOutputStreamImpl {
    private TarOutputStreamImpl tos = null;
    private BZip2CompressorOutputStream bzip = null;
    private ByteArrayOutputStream bos = null;
    private TarEntry currentEntry = null;

    public TarBzOutputStream(OutputStream out) throws IOException {
        super(null);
        this.bzip = new BZip2CompressorOutputStream(out);
        this.tos = new TarOutputStreamImpl(this.bzip);
        this.bos = new ByteArrayOutputStream();
    }

    // proxy all methods, but buffer if unknown size

    public void setDebug(boolean b) {
        this.tos.setDebug(b);
    }

    public void setBufferDebug(boolean b) {
        this.tos.setBufferDebug(b);
    }

    public void finish() throws IOException {
        if (this.currentEntry != null) {
            closeEntry();
        }

        this.tos.finish();
    }

    public void close() throws IOException {
        this.flush();
        this.tos.close();
        this.bzip.finish();
    }

    public int getRecordSize() {
        return this.tos.getRecordSize();
    }

    public void putNextEntry(TarEntry entry) throws IOException {
        if (entry.getSize() != 0) {
            this.tos.putNextEntry(entry);
        } else {
            this.currentEntry = entry;
        }
    }

    public void closeEntry() throws IOException {
        if (this.currentEntry == null) {
            this.tos.closeEntry();
        } else {
            this.currentEntry.setSize(bos.size());
            this.tos.putNextEntry(this.currentEntry);
            this.bos.writeTo(this.tos);
            this.tos.closeEntry();
            this.currentEntry = null;
            this.bos = new ByteArrayOutputStream();
        }
    }

    public void write(int b) throws IOException {
        if (this.currentEntry == null) {
            this.tos.write(b);
        } else {
            this.bos.write(b);
        }
    }

    public void write(byte[] b) throws IOException {
        if (this.currentEntry == null) {
            this.tos.write(b);
        } else {
            this.bos.write(b);
        }
    }

    public void write(byte[] b, int start, int length) throws IOException {
        if (this.currentEntry == null) {
            this.tos.write(b, start, length);
        } else {
            this.bos.write(b, start, length);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.io.FilterOutputStream#flush()
     */
    @Override
    public void flush() throws IOException {
        this.bos.flush();
    }

}

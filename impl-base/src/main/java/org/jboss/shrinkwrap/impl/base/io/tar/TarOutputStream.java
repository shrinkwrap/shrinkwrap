/*
 ** Contributed by "Bay" <bayard@generationjava.com>
 **
 ** This code has been placed into the public domain.
 */

package org.jboss.shrinkwrap.impl.base.io.tar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

// we extend TarOutputStream to have the same type,
// BUT, we don't use ANY methods. It's all about
// typing.

/**
 * Outputs TAR files; essentially a copy (i.e. hack) of {@link TarGzOutputStream}, except the output is *not* encoded
 * with a {@link GZIPOutputStream} wrapper. In place temporarily until we determine a way to properly write entries and
 * automatically handle the proper "next entry" logic for TAR just as is done for TAR.GZ. Likely well centralize a lot
 * of this logic into a common base class.
 *
 * @author "Bay" <bayard@generationjava.com>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */

public class TarOutputStream extends TarOutputStreamImpl {
    private final TarOutputStreamImpl tos;

    private ByteArrayOutputStream bos;

    private TarEntry currentEntry = null;

    public TarOutputStream(OutputStream out) {
        super(null);
        this.tos = new TarOutputStreamImpl(out);
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

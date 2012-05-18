package org.jboss.shrinkwrap.impl.base;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:tommy.tynja@diabol.se">Tommy Tynj&auml;</a>
 */
public class ArchiveWriteToTestCase {

    private static final String TEST_ARCHIVE = "org/jboss/shrinkwrap/impl/base/importer/test.zip";

    @Test
    public void shouldBufferWritesCorrectly() throws IOException {
        MockOutputStream outputStream = new MockOutputStream();
        byte[] content = new byte[9202];
        for (int i = 0; i < content.length; i++) {
            content[i] = (i + "").getBytes()[0];
        }
        IOUtil.bufferedWriteWithFlush(outputStream, content);
        Assert.assertArrayEquals("Inconsistent writes?", content, outputStream.getContents());
    }

    @Test
    public void archiveWriteToShouldWriteToStream() throws Exception {
        MockOutputStream outputStream = new MockOutputStream();

        final File testFile = TestIOUtil.createFileFromResourceName(TEST_ARCHIVE);
        final Archive<?> archive = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile);

        byte[] archiveToString = archive.toString(Formatters.SIMPLE).getBytes();
        archive.writeTo(outputStream, Formatters.SIMPLE);

        Assert.assertArrayEquals("Inconsistent writes?", archiveToString, outputStream.getContents());

        outputStream = new MockOutputStream();
        archiveToString = archive.toString(Formatters.VERBOSE).getBytes();
        archive.writeTo(outputStream, Formatters.VERBOSE);

        Assert.assertArrayEquals("Inconsistent writes?", archiveToString, outputStream.getContents());
    }

    private class MockOutputStream extends PrintStream {
        ArrayList<Byte> contents = new ArrayList<Byte>();

        public MockOutputStream() {
            super(System.out);
        }

        @Override
        public void write(final byte[] buf, final int off, final int len) {
            super.write(buf, off, len);
            for (int i = off; i < off + len; i = i + 1) {
                contents.add(buf[i]);
            }
        }

        public byte[] getContents() {
            byte[] bytes = new byte[contents.size()];
            for (int i = 0; i < bytes.length; i = i + 1) {
                bytes[i] = contents.get(i);
            }
            return bytes;
        }
    }
}

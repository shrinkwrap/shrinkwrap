/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api.asset;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.nio2.file.SeekableInMemoryByteChannelTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to assert that the {@link MemoryAsset} is working as contracted. In many cases, the tests here will counter
 * {@link SeekableInMemoryByteChannelTestCase} to ensure that delegation is wired correctly.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MemoryAssetTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MemoryAssetTestCase.class.getName());

    private static final String CONTENTS_BUFFER = "Andrew Lee Rubinger";

    private static final String UTF8 = "UTF-8";

    /**
     * Instance under test
     */
    private MemoryAsset asset;

    private ByteBuffer buffer;

    @Before
    public void init() {
        this.asset = new MemoryAsset();
        try {
            buffer = ByteBuffer.wrap(CONTENTS_BUFFER.getBytes(UTF8));
        } catch (final UnsupportedEncodingException uee) {
            throw new RuntimeException(uee);
        }
    }

    @After
    public void closeChannel() throws IOException {
        if (this.asset.isOpen()) {
            this.asset.close();
        }
    }

    @Test
    public void isOpenTrue() throws IOException {
        Assert.assertTrue("Channel should report open before it's closed", this.asset.isOpen());
    }

    @Test
    public void read() throws IOException {
        this.asset.write(buffer);
        final int newPosition = 2;
        final byte[] contents = new byte[2];
        // Read 2 bytes from the new position
        final SeekableByteChannel channel = this.asset.position(newPosition);
        Assert.assertEquals("Setting position should return the asset", this.asset, channel);
        final int numBytesRead = channel.read(ByteBuffer.wrap(contents));
        final String expected = "dr";
        final String contentsRead = new String(contents, UTF8);
        Assert.assertEquals("Read should report correct number of bytes read", contents.length, numBytesRead);
        Assert.assertEquals("Channel should respect explicit position during reads", expected, contentsRead);
    }

    @Test
    public void openStream() throws IOException {
        this.asset.write(buffer);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.asset.openStream()));
        final String contents = reader.readLine();
        Assert.assertEquals("Contents read were not as expected", CONTENTS_BUFFER, contents);

    }

    @Test
    public void write() throws IOException {
        this.asset.write(buffer);
        final int newPosition = 2;
        final int numBytesWritten = this.asset.position(newPosition).write(ByteBuffer.wrap("DR".getBytes(UTF8)));
        // Read 3 bytes from the new position
        final byte[] contents = new byte[3];
        this.asset.position(newPosition).read(ByteBuffer.wrap(contents));
        final String expected = "DRe";
        final String read = new String(contents, UTF8);
        Assert.assertEquals("Write should report correct number of bytes written", 2, numBytesWritten);
        Assert.assertEquals("Channel should respect explicit position during writes", expected, read);
    }

    @Test
    public void size() throws IOException {
        this.asset.write(buffer);
        Assert.assertEquals("Channel should report correct size", this.buffer.clear().remaining(),
            this.asset.size());
    }

    @Test
    public void truncate() throws IOException {
        this.asset.write(buffer);
        final int newSize = (int) this.asset.size() - 3;
        final SeekableByteChannel channel = this.asset.truncate(newSize);
        Assert.assertEquals("Truncating should return the asset", this.asset, channel);
        // Correct size?
        Assert.assertEquals("Channel should report correct size after truncate", newSize, this.asset.size());
        // Correct position?
        Assert.assertEquals("Channel should report adjusted position after truncate", newSize, this.asset.position());
    }
}

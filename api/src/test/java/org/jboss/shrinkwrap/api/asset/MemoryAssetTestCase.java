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
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;

import org.jboss.shrinkwrap.api.nio2.file.SeekableInMemoryByteChannelTestCase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests to assert that the {@link MemoryAsset} is working as contracted. In many cases, the tests here will counter
 * {@link SeekableInMemoryByteChannelTestCase} to ensure that delegation is wired correctly.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MemoryAssetTestCase {

    private static final String CONTENTS_BUFFER = "Andrew Lee Rubinger";

    /**
     * Instance under test
     */
    private MemoryAsset asset;

    private ByteBuffer buffer;

    @BeforeEach
    public void init() {
        this.asset = new MemoryAsset();
        buffer = ByteBuffer.wrap(CONTENTS_BUFFER.getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    public void closeChannel() throws IOException {
        if (this.asset.isOpen()) {
            this.asset.close();
        }
    }

    @Test
    public void isOpenTrue() {
        Assertions.assertTrue(this.asset.isOpen(), "Channel should report open before it's closed");
    }

    @Test
    public void read() throws IOException {
        this.asset.write(buffer);
        final int newPosition = 2;
        final byte[] contents = new byte[2];
        // Read 2 bytes from the new position
        try (final SeekableByteChannel channel = this.asset.position(newPosition)) {
            Assertions.assertEquals(this.asset, channel, "Setting position should return the asset");
            final int numBytesRead = channel.read(ByteBuffer.wrap(contents));
            final String expected = "dr";
            final String contentsRead = new String(contents, StandardCharsets.UTF_8);
            Assertions.assertEquals(contents.length, numBytesRead, "Read should report correct number of bytes read");
            Assertions.assertEquals(expected, contentsRead, "Channel should respect explicit position during reads");
        }
    }

    @Test
    public void openStream() throws IOException {
        this.asset.write(buffer);
        try (final InputStreamReader inputStreamReader = new InputStreamReader(this.asset.openStream());
             final BufferedReader reader = new BufferedReader(inputStreamReader) ) {
            final String contents = reader.readLine();
            Assertions.assertEquals(CONTENTS_BUFFER, contents, "Contents read were not as expected");
        }
    }

    @Test
    public void write() throws IOException {
        this.asset.write(buffer);
        final int newPosition = 2;
        final int numBytesWritten = this.asset.position(newPosition).write(ByteBuffer.wrap("DR".getBytes(StandardCharsets.UTF_8)));
        // Read 3 bytes from the new position
        final byte[] contents = new byte[3];
        this.asset.position(newPosition).read(ByteBuffer.wrap(contents));
        final String expected = "DRe";
        final String read = new String(contents, StandardCharsets.UTF_8);
        Assertions.assertEquals(2, numBytesWritten, "Write should report correct number of bytes written");
        Assertions.assertEquals(expected, read, "Channel should respect explicit position during writes");
    }

    @Test
    public void size() throws IOException {
        this.asset.write(buffer);
        Assertions.assertEquals(this.buffer.clear().remaining(), this.asset.size(), "Channel should report correct size");
    }

    @Test
    public void truncate() throws IOException {
        this.asset.write(buffer);
        final int newSize = (int) this.asset.size() - 3;
        try (final SeekableByteChannel channel = this.asset.truncate(newSize)) {
            Assertions.assertEquals(this.asset, channel, "Truncating should return the asset");
            // Correct size?
            Assertions.assertEquals(newSize, this.asset.size(), "Channel should report correct size after truncate");
            // Correct position?
            Assertions.assertEquals(newSize, this.asset.position(), "Channel should report adjusted position after truncate");
        }
    }
}

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
package org.jboss.shrinkwrap.api.nio2.file;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.StandardCharsets;

/**
 * Tests to assert that the {@link SeekableInMemoryByteChannel} is working as contracted.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class SeekableInMemoryByteChannelTestCase {

    private static final String CONTENTS_SMALLER_BUFFER = "Andrew Lee Rubinger";
    private static final String CONTENTS_BIGGER_BUFFER = "Andrew Lee Rubinger, JBoss by Red Hat";

    /**
     * Instance under test
     */
    private SeekableInMemoryByteChannel channel;

    private ByteBuffer smallerBuffer;
    private ByteBuffer biggerBuffer;

    @BeforeEach
    public void init() {
        this.channel = new SeekableInMemoryByteChannel();
        smallerBuffer = ByteBuffer.wrap(CONTENTS_SMALLER_BUFFER.getBytes(StandardCharsets.UTF_8));
        biggerBuffer = ByteBuffer.wrap(CONTENTS_BIGGER_BUFFER.getBytes(StandardCharsets.UTF_8));
    }

    @AfterEach
    public void closeChannel() {
        if (this.channel.isOpen()) {
            this.channel.close();
        }
    }

    @Test
    public void readAfterCloseThrowsException() throws IOException {
        this.channel.close();
        Assertions.assertThrows(ClosedChannelException.class, () -> this.channel.read(ByteBuffer.wrap(new byte[] {})));
    }

    @Test
    public void writeAfterCloseThrowsException() throws IOException {
        this.channel.close();
        Assertions.assertThrows(ClosedChannelException.class, () -> this.channel.write(ByteBuffer.wrap(new byte[] {})));
    }

    @Test
    public void isOpenTrue() {
        Assertions.assertTrue(this.channel.isOpen(), "Channel should report open before it's closed");
    }

    @Test
    public void isOpenFalseAfterClose() {
        this.channel.close();
        Assertions.assertFalse(this.channel.isOpen(), "Channel should report not open after close");
    }

    @Test
    public void positionInit0() {
        Assertions.assertEquals(0, this.channel.position(), "Channel should init to position 0");
    }

    @Test
    public void sizeInit0() {
        Assertions.assertEquals(0, this.channel.size(), "Channel should init to size 0");
    }

    @Test
    public void readRequiresBuffer() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.channel.read(null));
    }

    @Test
    public void writeRequiresBuffer() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.channel.write(null));
    }

    @Test
    public void read() throws IOException {
        this.channel.write(smallerBuffer);
        final int newPosition = 2;
        final byte[] contents = new byte[2];
        // Read 2 bytes from the new position
        final int numBytesRead = this.channel.position(newPosition).read(ByteBuffer.wrap(contents));
        final String expected = "dr";
        final String contentsRead = new String(contents, StandardCharsets.UTF_8);
        Assertions.assertEquals(contents.length, numBytesRead, "Read should report correct number of bytes read");
        Assertions.assertEquals(expected, contentsRead, "Channel should respect explicit position during reads");
    }

    @Test
    public void getContents() throws IOException {
        this.channel.write(smallerBuffer);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.channel.getContents()));
        final String contents = reader.readLine();
        Assertions.assertEquals(CONTENTS_SMALLER_BUFFER, contents, "Contents read were not as expected");
    }

    @Test
    public void readDestinationBiggerThanChannel() throws IOException {
        this.channel.write(smallerBuffer);
        final ByteBuffer destination = biggerBuffer;
        Assertions.assertTrue(destination.remaining() > this.channel.size(),
                "Test setup incorrect, should be trying to read into a buffer greater than our size");
        // Read more bytes than we currently have size
        final int numBytesRead = this.channel.position(0).read(destination);
        Assertions.assertEquals(this.channel.size(), numBytesRead,
                "Read to a buffer greater than our size should read only up to our size");
    }

    @Test
    public void nothingToRead() throws IOException {
        this.channel.write(smallerBuffer);
        // Read a byte from a position past the size
        final int numBytesRead = this.channel.position(this.channel.size() + 3).read(ByteBuffer.wrap(new byte[1]));
        Assertions.assertEquals(-1, numBytesRead, "Read on position > size should return -1");
    }

    @Test
    public void write() throws IOException {
        this.channel.write(smallerBuffer);
        final int newPosition = 2;
        final int numBytesWritten = this.channel.position(newPosition).write(ByteBuffer.wrap("DR".getBytes(StandardCharsets.UTF_8)));
        // Read 3 bytes from the new position
        final byte[] contents = new byte[3];
        this.channel.position(newPosition).read(ByteBuffer.wrap(contents));
        final String expected = "DRe";
        final String read = new String(contents, StandardCharsets.UTF_8);
        Assertions.assertEquals(2, numBytesWritten, "Write should report correct number of bytes written");
        Assertions.assertEquals(expected, read, "Channel should respect explicit position during writes");
    }

    @Test
    public void writeWithPositionPastSize() throws IOException {
        this.channel.write(smallerBuffer);
        smallerBuffer.clear();
        final int gap = 5;
        // Write again, after a gap past the current size
        this.channel.position(this.channel.size() + gap).write(smallerBuffer);
        smallerBuffer.clear();
        Assertions.assertEquals(smallerBuffer.remaining() * 2 + gap, this.channel.size(),
                "Channel size should be equal to the size of the writes we put in, plus "
                + "the gap when we set the position tpo be greater than the size");
    }

    @Test
    public void positionSetPastSize() {
        final int newPosition = 30;
        this.channel.position(newPosition);
        Assertions.assertEquals(newPosition, this.channel.position(), "Channel should be able to be set past size");
    }

    @Test
    public void negativePositionProhibited() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.channel.position(-1));
    }

    @Test
    public void exceedMaxIntegerPositionProhibited() {
        final long newPosition = Integer.MAX_VALUE + 1L;
        Assertions.assertTrue(newPosition > Integer.MAX_VALUE, "Didn't set up new position to be out of int bounds");
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.channel.position(newPosition));
    }

    @Test
    public void negativeTruncateProhibited() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.channel.truncate(-1));
    }

    @Test
    public void exceedMaxIntegerTruncateProhibited() {
        final long truncateValue = Integer.MAX_VALUE + 1L;
        Assertions.assertTrue(truncateValue > Integer.MAX_VALUE, "Didn't set up new truncate to be out of int bounds");
        Assertions.assertThrows(IllegalArgumentException.class, () -> this.channel.truncate(truncateValue));
    }

    @Test
    public void size() throws IOException {
        this.channel.write(smallerBuffer);
        Assertions.assertEquals(this.smallerBuffer.clear().remaining(),this.channel.size(), "Channel should report correct size");
    }

    @Test
    public void truncate() throws IOException {
        this.channel.write(smallerBuffer);
        final int newSize = (int) this.channel.size() - 3;
        this.channel.truncate(newSize);
        // Correct size?
        Assertions.assertEquals(newSize, this.channel.size(), "Channel should report correct size after truncate");
        // Correct position?
        Assertions.assertEquals(newSize, this.channel.position(), "Channel should report adjusted position after truncate");
    }

    @Test
    public void truncateLargerThanSizeRepositions() throws IOException {
        this.channel.write(smallerBuffer);
        final int oldSize = (int) this.channel.size();
        final int newSize = oldSize + 3;
        this.channel.truncate(newSize);
        // Size unchanged?
        Assertions.assertEquals(oldSize, this.channel.size(), "Channel should report unchanged size after truncate to bigger value");
        // Correct position, beyond size?
        Assertions.assertEquals(oldSize, this.channel.position(),
                "Channel should report unchanged position after truncate to bigger value");
    }

}

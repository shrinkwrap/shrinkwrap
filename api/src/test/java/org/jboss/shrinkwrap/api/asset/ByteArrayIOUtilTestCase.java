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
package org.jboss.shrinkwrap.api.asset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * ByteArrayIOUtilTest
 * <p/>
 * Test Cases for the {@link org.jboss.shrinkwrap.api.asset.ByteArrayIOUtil}
 *
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public class ByteArrayIOUtilTestCase {

    @Test
    public void shouldThrowExceptionOnNullParameter() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ByteArrayIOUtil.asByteArray(null));
    }

    @Test
    public void shouldReturnByteArrayWithSameContentAsPassedIntoInputStream() throws IOException {
        final int length = 10;
        final byte[] contents = new byte[length];
        for (int i = 0; i < length; i++) {
            contents[i] = (byte) i;
        }
        try (final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(contents)) {
            byte[] output = ByteArrayIOUtil.asByteArray(byteArrayInputStream);
            for (int i = 0; i < output.length; i++) {
                Assertions.assertEquals(output[i], contents[i], "output content did not equal input content");
            }
        }
    }
}

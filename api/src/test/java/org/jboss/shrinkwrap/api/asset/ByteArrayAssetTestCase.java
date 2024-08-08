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
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * ByteArrayAssetTestCase
 * 
 * Test Cases for the {@link ByteArrayAsset}
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ByteArrayAssetTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ByteArrayAssetTestCase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that the contents of the asset match that which was passed in, and that the state of the asset can not be
     * mutated from the outside
     */
    @Test
    public void testRoundtripAndExternalMutationGuard() throws Exception {
        // Log
        log.info("testRoundtrip");

        // Make contents
        final int length = 10;
        final byte[] contents = new byte[length];
        for (int i = 0; i < length; i++) {
            contents[i] = (byte) i;
        }
        log.info("Inbound contents: " + Arrays.toString(contents));

        // Make Asset
        final ByteArrayAsset asset = new ByteArrayAsset(contents);

        // Change the contents passed in (so we ensure we protect against mutation, SHRINKWRAP-38)
        contents[0] = 0x1;
        log.info("Contents after change: " + Arrays.toString(contents));

        // Get the contents back out of the asset
        final InputStream stream = asset.openStream();
        final ByteArrayOutputStream out = new ByteArrayOutputStream(length);
        int read;
        while ((read = stream.read()) != -1) {
            out.write(read);
        }
        byte[] roundtrip = out.toByteArray();
        log.info("Roundtrip contents: " + Arrays.toString(roundtrip));

        // Ensure the roundtrip matches the input (index number)
        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(i, roundtrip[i], "Roundtrip did not equal passed in contents");
        }

    }
    
    @Test
    public void shouldBeAbleToReturnByteArray() {
    	// Make contents
        final int length = 10;
        final byte[] contents = new byte[length];
        for (int i = 0; i < length; i++) {
            contents[i] = (byte) i;
        }

        // Make Asset
        final ByteArrayAsset asset = new ByteArrayAsset(contents);
        final byte[] contentFromGetSource = asset.getSource();
        
        Assertions.assertEquals(asset.getSource().length, contents.length);
     
        // Ensure the roundtrip matches the input (index number)
        for (int i = 0; i < length; i++) {
            Assertions.assertEquals(i, contentFromGetSource[i], "getSource() did not equal passed in contents");
        }
    }
}

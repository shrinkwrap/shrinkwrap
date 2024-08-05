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
import java.util.logging.Logger;

/**
 * Test Cases for the {@link StringAsset}
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:dan.j.allen@gmail.com">Dan Allen</a>
 * @version $Revision: $
 */
public class StringAssetTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(StringAssetTestCase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that the contents of the asset match that which was passed in.
     */
    @Test
    public void testRoundtrip() throws Exception {
        // Log
        log.info("testRoundtrip");

        // Make contents
        String contents = StringAsset.class.getSimpleName();

        // Make Asset
        final StringAsset asset = new StringAsset(contents);

        // Get the contents back out of the asset
        final InputStream stream = asset.openStream();
        final ByteArrayOutputStream out = new ByteArrayOutputStream(contents.length());
        int read;
        while ((read = stream.read()) != -1) {
            out.write(read);
        }
        String roundtrip = out.toString();
        log.info("Roundtrip contents: " + roundtrip);

        Assertions.assertEquals(contents, roundtrip, "Roundtrip did not equal passed in contents");
    }    

    @Test
    public void shouldBeAbleToReturnString() {
    	final String contents = StringAsset.class.getSimpleName();
    	final StringAsset asset = new StringAsset(contents);
        
        Assertions.assertEquals(contents, asset.getSource());
    }
}

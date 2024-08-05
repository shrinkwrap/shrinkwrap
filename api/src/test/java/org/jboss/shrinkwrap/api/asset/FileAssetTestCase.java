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

import java.io.File;
import java.io.InputStream;

/**
 * Test to ensure that we can use a File as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * 
 */
public class FileAssetTestCase {
    private static final String BASE_PATH = "src/test/resources/org/jboss/shrinkwrap/api/asset/";

    private static final String EXISTING_FILE = BASE_PATH + "Test.properties";

    private static final String NON_EXISTING_FILE = BASE_PATH + "NoFileShouldBePlacedHere.properties";

    @Test
    public void shouldBeAbleToReadFile() throws Exception {
        Asset asset = new FileAsset(new File(EXISTING_FILE));
        InputStream io = asset.openStream();

        Assertions.assertNotNull(io);
        Assertions.assertEquals("shrinkwrap=true", ApiTestUtils.convertToString(io),
                "Should be able to read the content of the resource");
    }

    @Test
    public void shouldThrowExceptionOnNullFile() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileAsset(null),
                "A null file argument should result in a IllegalArgumentException");
    }

    @Test
    public void shouldThrowExceptionOnMissingFile() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new FileAsset(new File(NON_EXISTING_FILE)),
                "A non existing file should result in a IllegalArgumentException");
    }
    
    @Test
    public void shouldBeAbleToReturnFile() {
    	final File exitingFile = new File(EXISTING_FILE);
    	final Asset asset = new FileAsset(exitingFile);
    	
        Assertions.assertTrue(exitingFile.equals(((FileAsset)asset).getSource()));
    }
}

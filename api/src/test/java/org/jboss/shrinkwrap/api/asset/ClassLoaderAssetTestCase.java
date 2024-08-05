/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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

import java.io.InputStream;

/**
 * Test to ensure that we are can use a ClassLoader Resource as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * 
 */
public class ClassLoaderAssetTestCase {
    private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/api/asset/Test.properties";

    private static final String NON_EXISTING_RESOURCE = "org/jboss/shrinkwrap/api/asset/NoFileShouldBePlacedHere.properties";

    @Test
    public void shouldBeAbleToReadResource() throws Exception {
        Asset asset = new ClassLoaderAsset(EXISTING_RESOURCE);
        InputStream io = asset.openStream();

        Assertions.assertNotNull(io);
        Assertions.assertEquals("shrinkwrap=true", ApiTestUtils.convertToString(io),
                "Should be able to read the content of the resource");
    }

    @Test
    public void shouldThrowExceptionOnNullName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ClassLoaderAsset(null),
                "A null resourceName argument should result in a IllegalArgumentException");
    }

    @Test
    public void shouldThrowExceptionOnNullClassloader() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ClassLoaderAsset(EXISTING_RESOURCE, null),
                "A null classLoader argument should result in a IllegalArgumentException");
    }

    @Test
    public void shouldThrowExceptionOnMissingResource() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ClassLoaderAsset(NON_EXISTING_RESOURCE),
                "A resource that is not found in the classLoader should result in a IllegalArgumentException");
    }
    
    @Test
    public void shouldBeAbleToReturnResource() {
        final Asset asset = new ClassLoaderAsset(EXISTING_RESOURCE);
        
        Assertions.assertEquals(((ClassLoaderAsset)asset).getSource(), EXISTING_RESOURCE);
    }
}

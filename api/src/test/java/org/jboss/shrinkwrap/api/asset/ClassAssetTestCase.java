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
 * Test to ensure that we are able to use Classes as Resources.
 * <p>
 * <a href="https://issues.redhat.com/browse/TMPARCH-5">TMPARCH-5</a>
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class ClassAssetTestCase {

    @Test
    public void shouldBeAbleToReadThisClass() throws Exception {
        Class<?> clazz = ClassAssetTestCase.class;
        Asset asset = new ClassAsset(clazz);

        try (InputStream io = asset.openStream()) {
            Assertions.assertNotNull(io);
            Assertions.assertEquals(ApiTestUtils.findLengthOfStream(io), ApiTestUtils.findLengthOfClass(clazz),
                    "Loaded class should have the same size");
        }
    }

    /**
     * <a href="https://issues.redhat.com/browse/TMPARCH-19">TMPARCH-19</a> <br/>
     * <br/>
     * A {@link Class} loaded by the Bootstrap ClassLoader will return a null {@link ClassLoader}, should use
     * {@link Thread} current context {@link ClassLoader} instead.
     *
     * @throws Exception
     */
    @Test
    public void shouldBeAbleAddBootstrapClass() throws Exception {
        Class<?> bootstrapClass = Class.class;
        Asset asset = new ClassAsset(bootstrapClass);

        try (InputStream io = asset.openStream()) {
            Assertions.assertNotNull(io);
            Assertions.assertEquals(ApiTestUtils.findLengthOfStream(io), ApiTestUtils.findLengthOfClass(bootstrapClass),
                    "Loaded class should have the same size");
        }
    }

    @Test
    public void shouldThrowExceptionOnNullClass() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new ClassAsset(null),
                "A null clazz argument should result in a IllegalArgumentException");
    }
    
    @Test
    public void shouldBeAbleToReturnThisClass() {
        final Class<?> clazz = ClassAssetTestCase.class;
        final ClassAsset asset = new ClassAsset(clazz);
        
        Assertions.assertEquals(clazz.getName(), asset.getSource().getName());
    }
}

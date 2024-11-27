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

import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * Test to ensure that we can use a URL as a resource.
 * <p>
 * <a href="https://issues.redhat.com/browse/TMPARCH-5">TMPARCH-5</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 */
public class UrlAssetTestCase {
    private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/api/asset/Test.properties";

    @Test
    public void shouldBeAbleToReadURL() throws Exception {
        Asset asset = new UrlAsset(getThreadContextClassLoader().getResource(EXISTING_RESOURCE));

        try (InputStream io = asset.openStream()) {
            Assertions.assertNotNull(io);
            Assertions.assertEquals("shrinkwrap=true", ApiTestUtils.convertToString(io),
                    "Should be able to read the content of the resource");
        }
    }

    @Test
    public void shouldThrowExceptionOnNullURL() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new UrlAsset(null),
                "A null url argument should result in a IllegalArgumentException");
    }

    @Test
    public void shouldCreateDefensiveCopyOfURLOnConstruction() throws Exception {
        URL mutableURL = getThreadContextClassLoader().getResource(EXISTING_RESOURCE);
        Asset asset = new UrlAsset(mutableURL);

        // mutate the URL - can't be sure that some malicious code or user won't do this?
        try {
            mutateUrlField(mutableURL, "host", "");
            mutateUrlField(mutableURL, "port");
            mutateUrlField(mutableURL, "path", "/UNKNOWN_FILE");
            mutateUrlField(mutableURL, "file", "file");
            mutateUrlField(mutableURL, "hashCode");
            mutateUrlField(mutableURL, "authority", "");
        } catch (final UnsupportedOperationForThisJREException e) {
            // We're all good; this URL can't be mutated in this JDK so ignore it and let the test finish
        }

        // now try to get a stream to read the asset
        InputStream io = null;

        try {
            io = asset.openStream();
        } catch (Exception e) {
            Assertions.fail("Mutated URL leaked into the UrlAsset");
        }

        Assertions.assertNotNull(io);
        Assertions
            .assertEquals("shrinkwrap=true", ApiTestUtils.convertToString(io), "Mutated URL leaked into the UrlAsset");
    }

    /**
     * Uses reflection to modify fields in a URL with String values.
     */
    private void mutateUrlField(URL mutableURL, String fieldName, String setValue) throws Exception {
        try {
            Field field = URL.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(mutableURL, setValue);
        } catch (Exception e) {
            handleInaccessibleFieldException(e);
        }
    }

    /**
     * Uses reflection to modify fields in a URL with int values.
     */
    private void mutateUrlField(URL mutableURL, String fieldName) throws Exception {
        try {
            Field field = URL.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(mutableURL, -1);
        } catch (Exception e) {
            handleInaccessibleFieldException(e);
        }
    }

    /**
     * Handles exceptions related to inaccessible fields.
     */
    private void handleInaccessibleFieldException(Exception e) throws Exception {
        if (isInaccessibleObjectException(e) || e instanceof IllegalAccessException) {
            throw new UnsupportedOperationForThisJREException(e);
        } else {
            throw e;
        }
    }

    /**
     * Helper method to check for InaccessibleObjectException by class name.
     * This avoids compilation issues on Java 8.
     */
    private boolean isInaccessibleObjectException(Exception e) {
        return e.getClass().getName().equals("java.lang.reflect.InaccessibleObjectException");
    }

    /**
     * Indicates that an operation is unsupported for this JRE.
     * <p>
     */
    private static class UnsupportedOperationForThisJREException extends Exception {
        UnsupportedOperationForThisJREException(Exception e) {
            super(e);
        }
    }

    @Test
    public void shouldBeAbleToReturnURL() {
        final URL url = getThreadContextClassLoader().getResource(EXISTING_RESOURCE);
        final UrlAsset asset = new UrlAsset(url);

        Assertions.assertTrue(url.sameFile(asset.getSource()));
    }

    /**
     * Obtains the Thread Context ClassLoader
     */
    static ClassLoader getThreadContextClassLoader() {
        return AccessController.doPrivileged(GetTcclAction.INSTANCE);
    }

    // -------------------------------------------------------------------------------||
    // Inner Classes ----------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * Single instance to get the TCCL
     */
    private enum GetTcclAction implements PrivilegedAction<ClassLoader> {
        INSTANCE;

        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }

    }
}

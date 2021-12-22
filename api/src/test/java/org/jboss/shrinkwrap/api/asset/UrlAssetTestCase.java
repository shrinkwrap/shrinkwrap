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

import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test to ensure that we can use a URL as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * 
 */
public class UrlAssetTestCase {
    private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/api/asset/Test.properties";

    @Test
    public void shouldBeAbleToReadURL() throws Exception {
        Asset asset = new UrlAsset(getThreadContextClassLoader().getResource(EXISTING_RESOURCE));

        InputStream io = asset.openStream();

        Assert.assertNotNull(io);
        Assert.assertEquals("Should be able to read the content of the resource", "shrinkwrap=true",
            ApiTestUtils.convertToString(io));
    }

    @Test
    public void shouldThrowExceptionOnNullURL() throws Exception {
        try {
            new UrlAsset(null);
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (Exception e) {
            Assert.assertEquals("A null url argument should result in a IllegalArgumentException",
                IllegalArgumentException.class, e.getClass());
        }
    }

    @Test
    public void shouldCreateDefensiveCopyOfURLOnConstruction() throws Exception {
        URL mutableURL = getThreadContextClassLoader().getResource(EXISTING_RESOURCE);
        Asset asset = new UrlAsset(mutableURL);

        // mutate the URL - can't be sure that some malicious code or user won't do this?
        try {
            mutateURL(mutableURL);
        } catch (final UnsupportedOperationForThisJREException e) {
            // We're all good; this URL can't be mutated in this JDK so ignore it and let the test finish
        }

        // now try to get a stream to read the asset
        InputStream io = null;

        try {
            io = asset.openStream();
        } catch (Exception e) {
            Assert.fail("Mutated URL leaked into the UrlAsset");
        }

        Assert.assertNotNull(io);
        Assert
            .assertEquals("Mutated URL leaked into the UrlAsset", "shrinkwrap=true", ApiTestUtils.convertToString(io));
    }

    /*
     * Ugly reflection needed to mutate a URL - not 100% sure how to do this other than using reflection, but seems
     * possible that other libraries may be doing this same thing so we must protect for it.
     */
    private void mutateURL(final URL mutableURL) throws Exception {
        Class<?>[] parameterTypes = { String.class, String.class, Integer.TYPE, String.class, String.class };
        final Method m;
        try {
            m = URL.class.getDeclaredMethod("set", parameterTypes);
        } catch (final NoSuchMethodException nsme) {
            // This is OK; we're in a JDK that cannot mutate URLs. Throw this so the test can recognize that.
            throw new UnsupportedOperationForThisJREException(nsme);
        }

        Object[] arguments = { "file", "", -1, "/UNKNOWN_FILE", null };
        m.setAccessible(true);
        m.invoke(mutableURL, arguments);
    }

    /**
     * Indicates that an operation is unsupported for this JRE.
     * <p>
     * Used because we want to protect against mutable URLs in JDK8, JDK11. But
     * JDK 17 removes the "set" method, thus making URLs immutable
     */
    private static class UnsupportedOperationForThisJREException extends Exception {
        UnsupportedOperationForThisJREException(final NoSuchMethodException nsme) {
            super(nsme);
        }
    }

    @Test
    public void shouldBeAbleToReturnURL() throws Exception {
    	final URL url = getThreadContextClassLoader().getResource(EXISTING_RESOURCE);
        final Asset asset = new UrlAsset(url);
        
        Assert.assertTrue(url.sameFile(((UrlAsset)asset).getSource()));
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

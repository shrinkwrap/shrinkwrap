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

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Implementation of an {@link Asset} backed by a {@link URL}. The URL may be of any backing protocol supported by the
 * runtime (i.e. has a handler registered).
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class UrlAsset implements Asset {
    private final URL url;

    /**
     * Create a new resource with a <code>URL</code> source.
     *
     * @param url
     *            A valid URL
     * @throws IllegalArgumentException
     *             <Code>URL</code> can not be null
     */
    public UrlAsset(final URL url) {
        // Precondition check
        if (url == null) {
            throw new IllegalArgumentException("URL must be specified");
        }
        // create a defensible copy
        try {
            this.url = new URL(url.toString());
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("URL is malformed " + e.getLocalizedMessage());
        }
    }

    /**
     * Open the <code>URL</code> stream.
     *
     * @return An open stream with the content of the URL
     */
    @Override
    public InputStream openStream() {
        try {
            return new BufferedInputStream(url.openStream(), 8192);
        } catch (Exception e) {
            throw new RuntimeException("Could not open stream for url " + url.toExternalForm(), e);
        }
    }

    /**
     * Returns the loaded URL.
     *
     */
    public URL getSource() {
        return url;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return UrlAsset.class.getSimpleName() + " [url=" + url.toExternalForm() + "]";
    }
}

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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of an {@link Asset} backed by a String
 *
 * @author <a href="mailto:dan.j.allen@gmail.com">Dan Allen</a>
 * @version $Revision: $
 */
public class StringAsset implements Asset {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(StringAsset.class.getName());

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Underlying content.
     */
    private final String content;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new {@link Asset} instance backed by the specified String
     *
     * @param content
     *            The content represented as a String
     * @throws IllegalArgumentException
     *             If the contents were not specified
     */
    public StringAsset(final String content) {
        // Precondition check
        if (content == null) {
            throw new IllegalArgumentException("content must be specified");
        }
        // don't need to copy since String is immutable
        this.content = content;
        if (log.isLoggable(Level.FINER)) {
            log.finer("Created " + this + " with backing String of size " + content.length() + "b");
        }
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * @see org.jboss.shrinkwrap.api.asset.Asset#openStream()
     */

    @Override
    public InputStream openStream() {
        return new ByteArrayInputStream(content.getBytes());
    }

    /**
     * Returns the underlying content.
     *
     */
    public String getSource() {
        return content;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return StringAsset.class.getSimpleName() + " [content size=" + content.length() + " bytes]";
    }

}

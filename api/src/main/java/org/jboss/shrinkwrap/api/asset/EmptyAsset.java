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

/**
 * Implementation of a {@link Asset} having empty content.
 *
 * @author <a href="mailto:dan.j.allen@gmail.com">Dan Allen</a>
 * @version $Revision: $
 */
public enum EmptyAsset implements Asset {
    INSTANCE;

    /**
     * Empty contents
     */
    final byte[] content = new byte[0];

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * @see org.jboss.shrinkwrap.api.asset.Asset#openStream()
     */
    @Override
    public InputStream openStream() {
        return new ByteArrayInputStream(content);
    }

    /**
     * Returns the underlying content.
     *
     */
    public byte[] getSource() {
        return content;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "EmptyAsset";
    }

}
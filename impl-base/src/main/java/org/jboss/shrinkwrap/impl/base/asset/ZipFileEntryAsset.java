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
package org.jboss.shrinkwrap.impl.base.asset;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * Holds a reference to the ZipFile and the ZipEntry this Asset represents for lazy loading.
 *
 * Used by the ZipImporter.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 */
public class ZipFileEntryAsset implements Asset {
    private final File file;
    private final ZipEntry entry;

    public ZipFileEntryAsset(final File file, final ZipEntry entry) {
        Validate.notNull(file, "File must be specified");
        Validate.notNull(entry, "Entry must be specified");

        this.file = file;
        this.entry = entry;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.Asset#getStream()
     */
    @Override
    // TODO: create AssetStreamException ?
    public InputStream openStream() {
        try {
            final ZipFile file = new ZipFile(this.file);
            return new InputStreamWrapper(file, file.getInputStream(entry));
        } catch (final Exception e) {
            throw new RuntimeException("Could not open zip file stream", e);
        }
    }

    private static class InputStreamWrapper extends InputStream {

        private final ZipFile file;
        private final InputStream is;

        public InputStreamWrapper(final ZipFile file, final InputStream is) {
            this.file = file;
            this.is = is;
        }

        @Override
        public int read() throws IOException {
            return this.is.read();
        }

        @Override
        public void close() throws IOException {
            try {
                try {
                    this.is.close();
                } finally {
                    file.close();
                }
            } finally {
                super.close();
            }
        }
    }
}

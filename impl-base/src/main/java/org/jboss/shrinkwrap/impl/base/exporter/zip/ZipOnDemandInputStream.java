/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.base.exporter.zip;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractOnDemandInputStream;

/**
 * ZIP on demand input stream.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
class ZipOnDemandInputStream extends AbstractOnDemandInputStream<ZipOutputStream> {


    private final boolean compressed;
    private static final long SYSTIME = System.currentTimeMillis();

    /**
     * Creates stream directly from archive with compression.
     *
     * @param archive
     */
    ZipOnDemandInputStream(final Archive<?> archive) {
        super(archive);
        compressed = true;
    }

    /**
     * Creates stream directly from archive.
     *
     * @param archive
     * @param compressed
     */
    ZipOnDemandInputStream(final Archive<?> archive, final boolean compressed) {
        super(archive);
        this.compressed = compressed;
    }

    @Override
    protected ZipOutputStream createOutputStream(final OutputStream outputStream) {
        ZipOutputStream zos = new ZipOutputStream(outputStream);

        if (!compressed) {
            zos.setLevel(ZipOutputStream.STORED);
        }

        return zos;
    }

    @Override
    protected void closeEntry(final ZipOutputStream outputStream) throws IOException {
        outputStream.closeEntry();
    }

    @Override
    protected void putNextEntry(final ZipOutputStream outputStream, final String context, final Asset asset) throws IOException {

        ZipEntry zipEntry = new ZipEntry(context);

        if (!compressed) {
            zipEntry.setMethod(ZipEntry.STORED);
            zipEntry.setTime(SYSTIME);

            long contentSize = 0;
            long crc = 0;

            // If it is not a directory
            if (asset != null) {

                // Calculates the CRC
                CRC32 crc32 = new CRC32();

                byte[] buf = new byte[1024];
                int len;
                try (InputStream is = new BufferedInputStream(asset.openStream())) {
                    while ((len = is.read(buf, 0, buf.length)) != -1) {
                        crc32.update(buf, 0, len);

                        // Updates the size of the file
                        contentSize += len;
                    }
                }
                // Gets calculated value
                crc = crc32.getValue();
            }

            zipEntry.setCrc(crc);
            zipEntry.setSize(contentSize);
        }

        outputStream.putNextEntry(zipEntry);
    }
}

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
package org.jboss.shrinkwrap.impl.base.io.tar;

import java.io.IOException;
import java.io.InputStream;

import org.jboss.shrinkwrap.impl.base.io.tar.bzip.BZip2CompressorInputStream;

/**
 * The {@link TarBzInputStream} reads a UNIX TAR archive, further encoded in BZIP compression, as an InputStream.
 * Methods are provided to position at each successive entry in the archive, and the read each entry as a normal input
 * stream using read().
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 */
public class TarBzInputStream extends TarInputStream {

    /**
     * @param is
     *             The {@link InputStream} to read from.
     */
    public TarBzInputStream(InputStream is) throws IOException {
        super(new BZip2CompressorInputStream(is));
    }

    /**
     * @param is
     *             The {@link InputStream} to read from.
     * @param blockSize
     *             The block size for the BZIP2 decompressor.
     */
    public TarBzInputStream(InputStream is, int blockSize) throws IOException {
        super(new BZip2CompressorInputStream(is), blockSize);
    }

    /**
     * @param is
     *             The {@link InputStream} to read from.
     * @param blockSize
     *             The block size for the BZIP2 decompressor.
     * @param recordSize
     *             The record size for the TAR input stream.
     */
    public TarBzInputStream(InputStream is, int blockSize, int recordSize) throws IOException {
        super(new BZip2CompressorInputStream(is), blockSize, recordSize);
    }

}

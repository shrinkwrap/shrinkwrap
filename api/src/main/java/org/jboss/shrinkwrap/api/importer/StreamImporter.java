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
package org.jboss.shrinkwrap.api.importer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;

/**
 * Generic importer capable of representing an {@link Assignable} as an entity capable of reading from an
 * {@link InputStream}, or file type.
 *
 * @param <I>
 *            Concrete type used in covariant return
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public interface StreamImporter<I extends StreamImporter<I>> extends Assignable {
    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||
    /**
     * Imports provided stream as a {@link Archive}. It remains the responsibility of the caller to close the stream.
     *
     * @param stream
     *            the stream to import; should be a raw type, not wrapped in any implementation-specific encoding (ie.
     *            {@link FileInputStream} is appropriate, but {@link ZipInputStream} or {@link GZIPInputStream} is not).
     * @return Archive of the imported stream
     * @throws ArchiveImportException
     *             If an error occurred during the import process
     * @throws IllegalArgumentException
     *             If no stream is specified
     */
    I importFrom(InputStream stream) throws ArchiveImportException;

    /**
     * Imports provided File as a {@link Archive}.
     *
     * @param file
     *            the file to import
     * @return Archive of the imported Zip
     * @throws ArchiveImportException
     *             If an error occurred during the import process
     * @throws IllegalArgumentException
     *             If no file is specified or if the file is a directory
     */
    I importFrom(File file) throws ArchiveImportException;
}

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
package org.jboss.shrinkwrap.api.importer;

import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;

/**
 * {@link Assignable} type capable of importing ZIP content.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface ZipImporter extends StreamImporter<ZipImporter> {
    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||
    /**
     * Imports provided {@link ZipInputStream} as a {@link Archive}. It remains the responsibility of the caller to
     * close the {@link ZipInputStream}.
     *
     * @param stream
     *            the stream to import
     * @return Archive of the imported Zip
     * @throws ArchiveImportException
     *             If an error occurred during the import process
     * @throws IllegalArgumentException
     *             If no stream is specified
     * @deprecated Use {@link ZipImporter#importFrom(ZipInputStream)}
     */
    @Deprecated
    ZipImporter importZip(ZipInputStream stream) throws ArchiveImportException;

    /**
     * Imports provided {@link ZipFile} as a {@link Archive}.
     *
     * @param file
     *            the file to import
     * @return Archive of the imported Zip
     * @throws ArchiveImportException
     *             If an error occurred during the import process
     * @throws IllegalArgumentException
     *             If no file is specified
     * @deprecated Use {@link ZipImporter#importFrom(ZipFile)}
     */
    @Deprecated
    ZipImporter importZip(ZipFile file) throws ArchiveImportException;

    /**
     * Imports provided {@link ZipFile} as a {@link Archive}.
     *
     * @param file
     *            the file to import
     * @return Archive of the imported Zip
     * @throws ArchiveImportException
     *             If an error occurred during the import process
     * @throws IllegalArgumentException
     *             If no file is specified
     */
    ZipImporter importFrom(ZipFile file) throws ArchiveImportException;
}

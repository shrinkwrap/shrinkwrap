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

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.Filter;

/**
 * Importer used to import Exploded directory structures into a {@link Archive}
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface ExplodedImporter extends Assignable {
    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||
    /**
     * Import a directory structure as a archive.
     *
     * @param file
     *            The directory to import
     * @return this
     * @throws IllegalArgumentException
     *             if file is null
     * @throws IllegalArgumentException
     *             if file is not a directory
     */
    ExplodedImporter importDirectory(File file);

    /**
     * Import a directory structure as a archive.
     *
     * @param file
     *            The directory to import
     * @param filter
     *            The filter control which files or directories will be imported
     * @return this
     * @throws IllegalArgumentException
     *             if file is null
     * @throws IllegalArgumentException
     *             if file is not a directory
     */
    ExplodedImporter importDirectory(File file, Filter<ArchivePath> filter);

    /**
     * Import a directory structure as a archive.
     *
     * @param fileName
     *            The name of the directory to import
     * @return this
     * @throws IllegalArgumentException
     *             if file is null
     * @throws IllegalArgumentException
     *             if file is not a directory
     */
    ExplodedImporter importDirectory(String fileName);

    /**
     * Import a directory structure as a archive.
     *
     * @param fileName
     *            The name of the directory to import
     * @param filter
     *            The filter control which files or directories will be imported
     * @return this
     * @throws IllegalArgumentException
     *             if file is null
     * @throws IllegalArgumentException
     *             if file is not a directory
     */
    ExplodedImporter importDirectory(String fileName, Filter<ArchivePath> filter);

}
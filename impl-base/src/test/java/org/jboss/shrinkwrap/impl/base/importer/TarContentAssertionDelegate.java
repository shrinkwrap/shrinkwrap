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
package org.jboss.shrinkwrap.impl.base.importer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.io.tar.TarEntry;
import org.jboss.shrinkwrap.impl.base.io.tar.TarInputStream;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.junit.jupiter.api.Assertions;

/**
 * Delegate class for asserting that TAR contents may be imported as expected
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class TarContentAssertionDelegate extends ContentAssertionDelegateBase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(TarContentAssertionDelegate.class.getName());

    /**
     * ClassLoader resource of a static TAR we'll use to test importing
     */
    private static final String EXISTING_TAR_RESOURCE = "test.tar";

    // -------------------------------------------------------------------------------------||
    // Functional Methods -----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Compare the content of the original file and what was imported.
     *
     * @param importedArchive
     *            The archive used for import
     * @param originalSource
     *            The original classpath resource file
     */
    public void assertContent(Archive<?> importedArchive, File originalSource) throws Exception {
        Assertions.assertFalse(importedArchive.getContent().isEmpty(), "Should have imported something");

        boolean containsEmptyDir = false;
        boolean containsEmptyNestedDir = false;

        try (final FileInputStream fileInputStream = new FileInputStream(originalSource);
             final TarInputStream stream = new TarInputStream(fileInputStream)) {
            TarEntry originalEntry;
            while ((originalEntry = (stream.getNextEntry())) != null) {
                if (originalEntry.isDirectory()) {
                    // TAR impl doesn't report dirs with trailing slashes, so adjust
                    final String originalEntryName = PathUtil.optionallyAppendSlash(originalEntry.getName());
                    log.info(originalEntryName);

                    // Check for expected empty dirs
                    if (originalEntryName.equals(EXPECTED_EMPTY_DIR)) {
                        containsEmptyDir = true;
                    }
                    if (originalEntryName.equals(EXPECTED_NESTED_EMPTY_DIR)) {
                        containsEmptyNestedDir = true;
                    }
                    continue;
                }

                // Ensure the archive contains the current entry as read from the file
                final ArchivePath entryName = ArchivePaths.create(originalEntry.getName());
                Assertions.assertTrue(importedArchive.contains(entryName),
                        "Importer should have imported " + entryName.get() + " from " + originalSource);

                // Check contents
                try (final ByteArrayOutputStream output = new ByteArrayOutputStream(8192)) {
                    byte[] content = new byte[4096];
                    int readBytes;
                    while ((readBytes = stream.read(content, 0, content.length)) != -1) {
                        output.write(content, 0, readBytes);
                    }
                    byte[] originalContent = output.toByteArray();
                    final Node node = importedArchive.get(entryName);
                    try (final InputStream inputStreamAsset = node.getAsset().openStream()) {
                        byte[] importedContent = IOUtil.asByteArray(inputStreamAsset);
                        Assertions.assertArrayEquals(importedContent, originalContent,
                                "The content of " + originalSource.getName() + " should be equal to the imported content");
                    }
                }
            }
        }

        // Ensure empty directories have come in cleanly
        Assertions.assertTrue(containsEmptyDir, "Empty directory not imported");
        Assertions.assertTrue(containsEmptyNestedDir, "Empty nested directory not imported");
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.importer.ContentAssertionDelegateBase#getExistingResourceName()
     */
    @Override
    protected String getExistingResourceName() {
        return EXISTING_TAR_RESOURCE;
    }
}

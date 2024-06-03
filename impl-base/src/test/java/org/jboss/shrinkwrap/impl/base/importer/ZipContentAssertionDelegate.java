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

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * Delegate class for asserting that ZIP contents may be imported as expected
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ZipContentAssertionDelegate extends ContentAssertionDelegateBase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * ClassLoader resource of a static ZIP we'll use to test importing
     */
    private static final String EXISTING_ZIP_RESOURCE = "org/jboss/shrinkwrap/impl/base/importer/test.zip";

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
        Assert.assertFalse("Should have imported something", importedArchive.getContent().isEmpty());

        ZipFile testZip = new ZipFile(originalSource);

        List<? extends ZipEntry> entries = Collections.list(testZip.entries());

        Assert.assertFalse("Test zip should contain data", entries.isEmpty());
        Assert.assertEquals("Should have imported all files and directories", entries.size(), importedArchive
            .getContent().size());

        boolean containsEmptyDir = false;
        boolean containsEmptyNestedDir = false;

        for (ZipEntry originalEntry : entries) {
            if (originalEntry.isDirectory()) {
                // Check for expected empty dirs
                if (originalEntry.getName().equals(EXPECTED_EMPTY_DIR)) {
                    containsEmptyDir = true;
                }
                if (originalEntry.getName().equals(EXPECTED_NESTED_EMPTY_DIR)) {
                    containsEmptyNestedDir = true;
                }
                continue;
            }

            Assert.assertTrue("Importer should have imported " + originalEntry.getName() + " from " + originalSource,
                importedArchive.contains(new BasicPath(originalEntry.getName())));

            byte[] originalContent = IOUtil.asByteArray(testZip.getInputStream(originalEntry));
            final Node node = importedArchive.get(new BasicPath(originalEntry.getName()));
            byte[] importedContent = IOUtil.asByteArray(node.getAsset().openStream());

            Assert.assertTrue("The content of " + originalEntry.getName() + " should be equal to the imported content",
                Arrays.equals(importedContent, originalContent));
        }

        // Ensure empty directories have come in cleanly
        Assert.assertTrue("Empty directory not imported", containsEmptyDir);
        Assert.assertTrue("Empty nested directory not imported", containsEmptyNestedDir);
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
        return EXISTING_ZIP_RESOURCE;
    }
}

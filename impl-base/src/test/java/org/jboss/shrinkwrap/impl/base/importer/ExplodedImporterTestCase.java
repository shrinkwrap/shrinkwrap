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
package org.jboss.shrinkwrap.impl.base.importer;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ExplodedExporter;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * TestCase to ensure the correctness of the ExplodedImporter
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExplodedImporterTestCase {

    private static final String EXISTING_DIRECTORY_RESOURCE = "exploded_import_test";

    private static final String EXISTING_FILE_RESOURCE = "exploded_import_test/Test.properties";

    /**
     * Name of the empty directory
     */
    private static final String EMPTY_DIR_NAME = "empty_dir";

    /**
     * Name of a parent directory
     */
    private static final String PARENT_DIR_NAME = "parent";

    /**
     * Creates the empty directories for this test, as Git cannot store empty dirs in SCM
     *
     * @throws IOException
     * @throws URISyntaxException
     */
    @BeforeClass
    public static void makeEmptyDirectories() throws IOException, URISyntaxException {
        final File root = new File(ExplodedImporterTestCase.class.getProtectionDomain().getCodeSource().getLocation()
            .toURI());
        final File exlodedImportTest = new File(root, EXISTING_DIRECTORY_RESOURCE);
        Assert.assertTrue("Import test folder does not exist: " + exlodedImportTest.getAbsolutePath(),
            exlodedImportTest.exists());
        final File empty = new File(exlodedImportTest, EMPTY_DIR_NAME);
        Assert.assertTrue("Could not create the empty directory", empty.mkdir());
        final File parent = new File(exlodedImportTest, PARENT_DIR_NAME);
        final File parentEmpty = new File(parent, EMPTY_DIR_NAME);
        Assert.assertTrue("Could not create the parent empty directory", parentEmpty.mkdirs());
        parentEmpty.deleteOnExit();
        empty.deleteOnExit();
    }

    @Test
    public void shouldBeAbleToImportADirectory() throws Exception {

        Archive<?> archive = ShrinkWrap
            .create(ExplodedImporter.class, "test.jar")
            .importDirectory(
                SecurityActions.getThreadContextClassLoader().getResource(EXISTING_DIRECTORY_RESOURCE).toURI()
                    .getPath()).as(JavaArchive.class);
        Logger.getLogger(ExplodedImporterTestCase.class.getName()).info(archive.toString(true));
        Assert.assertTrue("Root files should be imported", archive.contains(new BasicPath("/Test.properties")));

        Assert.assertTrue("Nested files should be imported", archive.contains(new BasicPath("/META-INF/MANIFEST.FM")));

        Assert.assertTrue("Nested files should be imported",
            archive.contains(new BasicPath("/org/jboss/Test.properties")));

        Assert.assertTrue("Empty directories should be imported", archive.contains(new BasicPath("/empty_dir")));

        Assert.assertTrue("Nested empty directories should be imported",
            archive.contains(new BasicPath("/parent/empty_dir")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfImportingAFile() throws Exception {

        ShrinkWrap.create(ExplodedImporter.class, "test.jar").importDirectory(
            SecurityActions.getThreadContextClassLoader().getResource(EXISTING_FILE_RESOURCE).toURI().getPath());
    }
    
    @Test
    public void shouldBeAbleToImportADirectoryWithIncludeFilter() throws Exception {
        String fileName = SecurityActions.getThreadContextClassLoader().getResource(EXISTING_DIRECTORY_RESOURCE).toURI().getPath();
        
        Archive<?> archive = ShrinkWrap
            .create(ExplodedImporter.class, "test.jar")
            .importDirectory(fileName, Filters.include(".*META-INF.*"))
            .as(JavaArchive.class);
        
        Logger.getLogger(ExplodedImporterTestCase.class.getName()).info(archive.toString(true));

        Assert.assertEquals("Archive should contains only 2 paths", 2, archive.getContent().size());
        Assert.assertTrue("Nested files should be imported", archive.contains(new BasicPath("/META-INF/MANIFEST.FM")));
    }
    
    @Test
    public void shouldBeAbleToImportADirectoryWithExcludeFilter() throws Exception {
        String fileName = SecurityActions.getThreadContextClassLoader().getResource(EXISTING_DIRECTORY_RESOURCE).toURI().getPath();
        
        Archive<?> archive = ShrinkWrap
            .create(ExplodedImporter.class, "test.jar")
            .importDirectory(fileName, Filters.exclude(".*META-INF.*"))
            .as(JavaArchive.class);
        
        Logger.getLogger(ExplodedImporterTestCase.class.getName()).info(archive.toString(true));
        
        Assert.assertTrue("Root files should be imported", archive.contains(new BasicPath("/Test.properties")));
        Assert.assertTrue("Nested files should be imported", archive.contains(new BasicPath("/org/jboss/Test.properties")));
        Assert.assertTrue("Empty directories should be imported", archive.contains(new BasicPath("/empty_dir")));
        Assert.assertTrue("Nested empty directories should be imported", archive.contains(new BasicPath("/parent/empty_dir")));
    }
    
    @Test // SHRINKWRAP-392
    public void shouldHaveEmptyDefaultExtension() {
        ExplodedImporter explodedImporter = ShrinkWrap.create(ExplodedImporter.class);
        String explodedImporterArchiveName = explodedImporter.as(JavaArchive.class).getName();
        Assert.assertFalse(explodedImporterArchiveName.contains("."));
    }

    @Test // SHRINKWRAP-453
    public void shouldImportDirectory() throws Exception {
        shouldImportDirectory("arbitraryDirectory");
    }

    @Test // SHRINKWRAP-453
    public void shouldImportDirectoryWithNameThatCouldCauseRegexPatternSyntaxException() throws Exception {
        shouldImportDirectory("[deploymentExportPath");
    }

    @Test // SHRINKWRAP-453
    public void shouldImportDirectoryWithBracketsInPath() throws Exception {
        shouldImportDirectory("[deploymentExportPath]");
    }

    // SHRINKWRAP-453
    public void shouldImportDirectory(final String subdirectory) throws Exception {
        String fileName = SecurityActions.getThreadContextClassLoader().getResource(EXISTING_DIRECTORY_RESOURCE).toURI().getPath();
        final File importDirectory = new File(fileName, subdirectory);
        if (!importDirectory.exists()) {
            Assert.assertTrue("Unable to create test folder: " + importDirectory.getAbsolutePath(), importDirectory.mkdir());
        }
        Assert.assertTrue("Import test folder does not exist: " + importDirectory.getAbsolutePath(), importDirectory.exists());
        importDirectory.deleteOnExit();

        ShrinkWrap.create(WebArchive.class, "test.war")
                .addClass(ExplodedImporter.class)
                .setWebXML(new StringAsset("<web-app></web-app>"))
                .as(ExplodedExporter.class).exportExploded(importDirectory);

        // test import of exploded directory
        WebArchive war = ShrinkWrap.create(WebArchive.class, "SHRINKWRAP-453.war")
                .as(ExplodedImporter.class).importDirectory(importDirectory)
                .as(WebArchive.class);
        Assert.assertTrue("Expected imported web archive to be present at web archive root", war.contains("/test.war"));
        Assert.assertTrue("Expected imported web archive to include web.xml", war.contains("/test.war/WEB-INF/web.xml"));
    }


}

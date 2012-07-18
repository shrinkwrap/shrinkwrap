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
package org.jboss.shrinkwrap.impl.nio.file;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link FileSystemProvider} is working as expected via
 * the {@link Paths} convenience API.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class PathsTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(PathsTestCase.class.getName());

    /**
     * {@link FileSystem} under test
     */
    private FileSystem fs;

    /**
     * Archive mounted as a {@link FileSystem}
     */
    private Archive<?> archive;

    @Before
    public void createFileSystem() throws IOException {
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        this.fs = ShrinkWrapFileSystems.newFileSystem(archive);
        this.archive = archive;
    }

    @After
    public void closeFileSystem() throws IOException {
        if (this.fs.isOpen()) {
            this.fs.close();
        }
    }

    @Test
    public void get() throws URISyntaxException {
        // Get a previously-opened filesystem by passing in a mounted URI
        final Path path = Paths.get(ShrinkWrapFileSystems.getRootUri(archive));
        Assert.assertTrue("Wrong Path implementation returned", path instanceof ShrinkWrapPath);
        Assert.assertEquals("Path returned is not correct", ArchivePaths.root().get(), path.toString());
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void getNonexistantFilesystem() throws URISyntaxException {
        Paths.get(new URI(ShrinkWrapFileSystems.PROTOCOL + "://fakeId"));
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void getClosedFilesystem() throws URISyntaxException, IOException {
        fs.close();
        Paths.get(ShrinkWrapFileSystems.getRootUri(archive));
    }

}

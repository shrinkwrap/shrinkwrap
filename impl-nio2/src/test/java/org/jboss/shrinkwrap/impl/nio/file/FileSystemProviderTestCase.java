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
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.spi.FileSystemProvider;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

/**
 * Test cases to assert the ShrinkWrap implementation of the NIO.2 {@link FileSystemProvider} is working as contracted.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class FileSystemProviderTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(FileSystemProviderTestCase.class.getName());

    @Test(expected = IllegalArgumentException.class)
    public void nullEnvironmentMakesIllegalArgumentException() throws IOException {
        FileSystems.newFileSystem(URI.create(ShrinkWrapFileSystems.PROTOCOL + "://myArchive.jar"), null);
    }

    @Test
    public void mountExistingArchive() throws Exception {
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        final FileSystem fs = ShrinkWrapFileSystems.newFileSystem(archive);

        Assert.assertNotNull(fs);
        Assert.assertTrue(fs instanceof ShrinkWrapFileSystem);
    }
}

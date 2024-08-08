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
package org.jboss.shrinkwrap.impl.base.nio2.file;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.util.HashMap;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.nio2.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Test cases to assert that the {@link ShrinkWrapFileSystems} convenience API is working as contracted.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ShrinkWrapFileSystemsTestCase {

    @Test
    public void newFileSystemArchiveRequired() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ShrinkWrapFileSystems.newFileSystem(null));
    }

    @Test
    public void getRootUriArchiveRequired() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ShrinkWrapFileSystems.getRootUri(null));
    }

    @Test
    public void protocol() {
        Assertions.assertEquals("shrinkwrap", ShrinkWrapFileSystems.PROTOCOL, "Protocol is not as expected");
    }

    @Test
    public void fsEnvKeyArchive() {
        Assertions.assertEquals("archive", ShrinkWrapFileSystems.FS_ENV_KEY_ARCHIVE, "FS environment key for archives is not as expected");
    }

    @Test
    public void getRootUri() {
        final GenericArchive archive = ShrinkWrap.create(GenericArchive.class);
        final URI uri = ShrinkWrapFileSystems.getRootUri(archive);
        final String expected = "shrinkwrap://" + archive.getId() + "/";
        Assertions.assertEquals(expected, uri.toString(), "Root URI is not as expected");
    }

    @Test
    public void newFileSystem() throws IOException {
        final GenericArchive archive = ShrinkWrap.create(GenericArchive.class);
        final ShrinkWrapFileSystem fs = (ShrinkWrapFileSystem) ShrinkWrapFileSystems.newFileSystem(archive);
        Assertions.assertNotNull(fs, "Did not obtain a new File System as expected");
        Assertions.assertSame(archive, fs.getArchive(), "Backing archive was not as expected");
    }

    @Test
    public void noArchiveInEnvShouldResultInIAE() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> FileSystems.newFileSystem(ShrinkWrapFileSystems.getRootUri(
                                ShrinkWrap.create(JavaArchive.class)), new HashMap()));
    }

}

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
import org.junit.Assert;
import org.junit.Test;

/**
 * Test cases to assert that the {@link ShrinkWrapFileSystems} convenience API is working as contracted.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ShrinkWrapFileSystemsTestCase {

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemArchiveRequired() throws IOException {
        ShrinkWrapFileSystems.newFileSystem(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getRootUriArchiveRequired() {
        ShrinkWrapFileSystems.getRootUri(null);
    }

    @Test
    public void protocol() {
        Assert.assertEquals("Protocol is not as expected", "shrinkwrap", ShrinkWrapFileSystems.PROTOCOL);
    }

    @Test
    public void fsEnvKeyArchive() {
        Assert.assertEquals("FS environment key for archives is not as expected", "archive",
            ShrinkWrapFileSystems.FS_ENV_KEY_ARCHIVE);
    }

    @Test
    public void getRootUri() {
        final GenericArchive archive = ShrinkWrap.create(GenericArchive.class);
        final URI uri = ShrinkWrapFileSystems.getRootUri(archive);
        final String expected = "shrinkwrap://" + archive.getId() + "/";
        Assert.assertEquals("Root URI is not as expected", expected, uri.toString());
    }

    @Test
    public void newFileSystem() throws IOException {
        final GenericArchive archive = ShrinkWrap.create(GenericArchive.class);
        final ShrinkWrapFileSystem fs = (ShrinkWrapFileSystem) ShrinkWrapFileSystems.newFileSystem(archive);
        Assert.assertNotNull("Did not obtain a new File System as expected", fs);
        Assert.assertTrue("Backing archive was not as expected", archive == fs.getArchive());
    }

    @Test(expected = IllegalArgumentException.class)
    public void noArchiveInEnvShouldResultInIAE() throws Exception {
        FileSystems.newFileSystem(
            ShrinkWrapFileSystems.getRootUri(
                    ShrinkWrap.create(JavaArchive.class)), new HashMap());
    }

}

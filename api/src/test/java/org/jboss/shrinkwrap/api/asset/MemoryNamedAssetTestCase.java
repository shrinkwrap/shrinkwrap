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
package org.jboss.shrinkwrap.api.asset;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.logging.Logger;

import org.junit.Assert;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.junit.Test;

/**
 * Tests to assert that the {@link MemoryNamedAsset} is working as contracted by the {@link NamedAsset} API
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MemoryNamedAssetTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MemoryNamedAssetTestCase.class.getName());

    @Test
    public void nameViaString() {
        final String name = "ALR";
        final MemoryNamedAsset asset = new MemoryNamedAsset(name);
        final String roundtrip = asset.getName();
        Assert.assertEquals(name, roundtrip);
        try {
            asset.close();
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void nameViaStringNull() {
        try (final MemoryNamedAsset asset = new MemoryNamedAsset((String) null)) {
            // The initialization is expected to fail with IAE for null
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (final IOException ignored) {
            // IOException due to try-with-resources, test will fail as it is not the expected exception
        }
    }

    @Test
    public void nameViaArchivePath() {
        final String name = "ALR";
        final ArchivePath path = new ArchivePath() {

            @Override
            public int compareTo(ArchivePath o) {
                return 0;
            }

            @Override
            public ArchivePath getParent() {
                return null;
            }

            @Override
            public String get() {
                return name;
            }
        };
        final MemoryNamedAsset asset = new MemoryNamedAsset(path);
        final String roundtrip = asset.getName();
        Assert.assertEquals(name, roundtrip);
        try {
            asset.close();
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void nameViaArchivePathNull() {
        try (final MemoryNamedAsset asset = new MemoryNamedAsset((ArchivePath) null)) {
            // The initialization is expected to fail with IAE for null
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (final IOException ignored) {
            // IOException due to try-with-resources, test will fail as it is not the expected exception
        }
    }

    @Test
    public void nameViaPath() {
        final String name = "ALR";
        // Only implement "toString" for this Path impl
        final Path path = new Path() {

            @Override
            public String toString() {
                return name;
            }

            @Override
            public URI toUri() {
                return null;
            }

            @Override
            public Path toRealPath(LinkOption... options) {
                return null;
            }

            @Override
            public File toFile() {
                return null;
            }

            @Override
            public Path toAbsolutePath() {
                return null;
            }

            @Override
            public Path subpath(int beginIndex, int endIndex) {
                return null;
            }

            @Override
            public boolean startsWith(String other) {
                return false;
            }

            @Override
            public boolean startsWith(Path other) {
                return false;
            }

            @Override
            public Path resolveSibling(String other) {
                return null;
            }

            @Override
            public Path resolveSibling(Path other) {
                return null;
            }

            @Override
            public Path resolve(String other) {
                return null;
            }

            @Override
            public Path resolve(Path other) {
                return null;
            }

            @Override
            public Path relativize(Path other) {
                return null;
            }

            @Override
            public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) {
                return null;
            }

            @Override
            public WatchKey register(WatchService watcher, Kind<?>... events) {
                return null;
            }

            @Override
            public Path normalize() {
                return null;
            }

            @Override
            public Iterator<Path> iterator() {
                return null;
            }

            @Override
            public boolean isAbsolute() {
                return false;
            }

            @Override
            public Path getRoot() {
                return null;
            }

            @Override
            public Path getParent() {
                return null;
            }

            @Override
            public int getNameCount() {
                return 0;
            }

            @Override
            public Path getName(int index) {
                return null;
            }

            @Override
            public FileSystem getFileSystem() {
                return null;
            }

            @Override
            public Path getFileName() {
                return null;
            }

            @Override
            public boolean endsWith(String other) {
                return false;
            }

            @Override
            public boolean endsWith(Path other) {
                return false;
            }

            @Override
            public int compareTo(Path other) {
                return 0;
            }

        };
        final MemoryNamedAsset asset = new MemoryNamedAsset(path);
        final String roundtrip = asset.getName();
        Assert.assertEquals(name, roundtrip);
        try {
            asset.close();
        } catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void nameViaPathNull() {
        try (final MemoryNamedAsset asset = new MemoryNamedAsset((Path) null)) {
            // The initialization is expected to fail with IAE for null
            Assert.fail("Should have thrown IllegalArgumentException");
        } catch (final IOException ignored) {
            // IOException due to try-with-resources, test will fail as it is not the expected exception
        }
    }

}

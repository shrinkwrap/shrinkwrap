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
package org.jboss.shrinkwrap.api.nio2.file;

import org.jboss.shrinkwrap.api.Archive;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

/**
 * Convenience API bridge to the NIO.2 {@link FileSystems} support for ShrinkWrap {@link Archive}s.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public final class ShrinkWrapFileSystems {

    /**
     * Protocol portion of a {@link URI} to ShrinkWrap {@link FileSystem}s
     */
    public static final String PROTOCOL = "shrinkwrap";

    /**
     * {@link Map} key used to store a {@link Archive} when creating a new {@link FileSystem} via
     * {@link FileSystems#newFileSystem(URI, Map)}
     */
    public static final String FS_ENV_KEY_ARCHIVE = "archive";

    /**
     * Protocol suffix before ID portion of ShrinkWrap {@link URI}s
     */
    private static final String URI_PROTOCOL_SUFFIX = "://";

    private ShrinkWrapFileSystems() {
        throw new UnsupportedOperationException("Class with only static methods");
    }

    /**
     * Creates a new file system for the given {@link Archive}; in effect invoking this method is equal to invoking
     * {@link FileSystems#newFileSystem(URI, Map)}, passing the value of
     * {@link ShrinkWrapFileSystems#getRootUri(Archive)} as the {@link URI} and the specified archive as a value in a
     * {@link Map} under the key {@link ShrinkWrapFileSystems#FS_ENV_KEY_ARCHIVE}
     *
     * @param archive
     *             The {@link Archive} for which a new file system is to be created. Must not be {@code null}.
     * @return A new {@link FileSystem} associated with the given {@link Archive}.
     * @throws IllegalArgumentException
     *             If the archive is not specified
     * @throws IOException
     *             If an error was encountered during creation of the new {@link FileSystem} via
     *             {@link FileSystems#newFileSystem(URI, Map)}
     */
    public static FileSystem newFileSystem(final Archive<?> archive) throws IllegalArgumentException, IOException {
        if (archive == null) {
            throw new IllegalArgumentException("Archive must be specified");
        }
        final Map<String, Archive<?>> environment = new HashMap<>();
        environment.put(FS_ENV_KEY_ARCHIVE, archive);
        final URI uri = getRootUri(archive);
        return FileSystems.newFileSystem(uri, environment);
    }

    /**
     * Constructs a new {@link URI} with the form:
     * <p>
     * <code>shrinkwrap://{archive.getId()}/</code>
     *
     * @param archive
     *             The {@link Archive} for which the root URI is to be constructed. Must not be {@code null}.
     * @return A new {@link URI} representing the root of the given {@link Archive}.
     * @throws IllegalArgumentException
     *             If the archive is not specified
     */
    public static URI getRootUri(final Archive<?> archive) throws IllegalArgumentException {
        if (archive == null) {
            throw new IllegalArgumentException("Archive must be specified");
        }
        String sb = PROTOCOL +
                URI_PROTOCOL_SUFFIX +
                archive.getId() +
                '/';
        return URI.create(sb);
    }

}

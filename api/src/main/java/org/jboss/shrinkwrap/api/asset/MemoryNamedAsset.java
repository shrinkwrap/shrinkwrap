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

import java.nio.file.Path;

import org.jboss.shrinkwrap.api.ArchivePath;

/**
 * {@link MemoryAsset} implementation complying to the {@link NamedAsset} API; thread-safe.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class MemoryNamedAsset extends MemoryAsset implements NamedAsset {

    private final String name;

    /**
     * Sets the name of this {@link MemoryNamedAsset} to the specified (required) {@link String} name
     *
     * @param name
     *             The name of this {@link MemoryNamedAsset}. Must not be {@code null}.
     * @throws IllegalArgumentException
     *             If the name is not specified
     */
    public MemoryNamedAsset(final String name) throws IllegalArgumentException {
        if (name == null) {
            throw new IllegalArgumentException("Name must be specified");
        }
        this.name = name;
    }

    /**
     * Sets the name of this {@link MemoryNamedAsset} via {@link Path#toString()} of the specified (required)
     * {@link Path}
     *
     * @param path
     *             The {@link Path} whose {@link Path#toString()} will be used as the name of {@link MemoryNamedAsset}. Must not be {@code null}.
     * @throws IllegalArgumentException
     *             If the path is not specified
     */
    public MemoryNamedAsset(final Path path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("Path must be specified");
        }
        this.name = path.toString();
    }

    /**
     * Sets the name of this {@link MemoryNamedAsset} via {@link ArchivePath#get()} of the specified (required)
     * {@link ArchivePath}
     *
     * @param path
     *             The {@link ArchivePath} whose {@link ArchivePath#get()} will be used as the name of this {@link MemoryNamedAsset}. Must not be {@code null}.
     * @throws IllegalArgumentException
     *             If the path is not specified
     */
    public MemoryNamedAsset(final ArchivePath path) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("Path must be specified");
        }
        this.name = path.get();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.asset.NamedAsset#getName()
     */
    @Override
    public String getName() {
        return name;
    }

}

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
package org.jboss.shrinkwrap.impl.base;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;

/**
 * Implementation of a {@link GenericArchive}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class GenericArchiveImpl extends ContainerBase<GenericArchive> implements GenericArchive {

    /**
     * Unsupported operation
     */
    private static final UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException(
        GenericArchive.class.getSimpleName() + " does not support container spec paths.");

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new instance
     *
     * @param delegate
     *            The storage backing.
     */
    public GenericArchiveImpl(final Archive<?> delegate) {
        super(GenericArchive.class, delegate);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getClassesPath()
     */
    @Override
    protected ArchivePath getClassesPath() {
        throw UNSUPPORTED;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getLibraryPath()
     */
    @Override
    protected ArchivePath getLibraryPath() {
        throw UNSUPPORTED;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getManifestPath()
     */
    @Override
    protected ArchivePath getManifestPath() {
        throw UNSUPPORTED;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getResourcePath()
     */
    @Override
    protected ArchivePath getResourcePath() {
        throw UNSUPPORTED;
    }
}

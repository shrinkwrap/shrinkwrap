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

import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;

/**
 * Implementation of a {@link MockArchive} type
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class MockArchiveImpl extends ContainerBase<MockArchive> implements MockArchive {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(MockArchiveImpl.class.getName());

    /**
     * Path to the manifests inside of the Archive.
     */
    private static final ArchivePath PATH = ArchivePaths.root();

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Create a new archive with any type storage engine as backing.
     *
     * @param delegate
     *            The storage backing.
     */
    public MockArchiveImpl(final Archive<?> delegate) {
        super(MockArchive.class, delegate);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see Archive#shallowCopy()
     */
    @Override
    public MockArchiveImpl shallowCopy() {
        MockArchiveImpl newInstance = new MockArchiveImpl(getArchive().shallowCopy());
        ShallowCopy.shallowCopyContentTo(this, newInstance);
        return newInstance;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getManifestPath()
     */
    @Override
    protected ArchivePath getManifestPath() {
        return PATH;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getClassesPath()
     */
    @Override
    protected ArchivePath getClassesPath() {
        return PATH;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.container.ContainerBase#getResourcePath()
     */
    @Override
    protected ArchivePath getResourcePath() {
        return PATH;
    }

    /**
     * Libraries are not supported by JavaArchive.
     *
     * @throws UnsupportedOperationException
     *             Libraries are not supported by JavaArchive
     */
    @Override
    public ArchivePath getLibraryPath() {
        throw new UnsupportedOperationException("MockArchive spec does not support Libraries");
    }
}

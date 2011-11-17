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
package org.jboss.shrinkwrap.impl.base.spec;

import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.impl.base.container.EnterpriseContainerBase;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * EnterpriseArchiveImpl
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class EnterpriseArchiveImpl extends EnterpriseContainerBase<EnterpriseArchive> implements EnterpriseArchive {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(JavaArchiveImpl.class.getName());

    /**
     * Path to the manifests inside of the Archive.
     */
    private static final ArchivePath PATH_MANIFEST = new BasicPath("META-INF");

    /**
     * Path to the resources inside of the Archive.
     */
    private static final ArchivePath PATH_RESOURCE = PATH_MANIFEST;

    /**
     * Path to the application resources.
     */
    private static final ArchivePath PATH_APPLICATION = PATH_MANIFEST;

    /**
     * Path to the application libraries.
     */
    private static final ArchivePath PATH_LIBRARY = new BasicPath("lib");

    /**
     * Path to the application modules.
     */
    private static final ArchivePath PATH_MODULE = new BasicPath("/");

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Create a new EnterpriseArchive with any type storage engine as backing.
     *
     * @param delegate
     *            The storage backing.
     */
    public EnterpriseArchiveImpl(final Archive<?> delegate) {
        super(EnterpriseArchive.class, delegate);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.impl.base.ContainerBase#getLibraryPath()
     */
    @Override
    public ArchivePath getLibraryPath() {
        return PATH_LIBRARY;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.impl.base.ContainerBase#getResourcePath()
     */
    @Override
    protected ArchivePath getResourcePath() {
        return PATH_RESOURCE;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.impl.base.ContainerBase#getManifestPath()
     */
    @Override
    protected ArchivePath getManifestPath() {
        return PATH_MANIFEST;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.impl.base.EnterpriseContainerBase#getApplicationPath()
     */
    @Override
    protected ArchivePath getApplicationPath() {
        return PATH_APPLICATION;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.impl.base.EnterpriseContainerBase#getModulePath()
     */
    @Override
    protected ArchivePath getModulePath() {
        return PATH_MODULE;
    }

    /**
     * Classes are not supported by EnterpriseArchive.
     *
     * @throws UnsupportedOperationException
     *             EnterpriseArchive does not support classes
     */
    @Override
    protected ArchivePath getClassesPath() {
        throw new UnsupportedOperationException("EnterpriseArchive does not support classes");
    }
}

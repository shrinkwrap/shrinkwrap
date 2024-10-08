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
package org.jboss.shrinkwrap.impl.base.path;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;

/**
 * PathsTestCase
 * <p>
 * Tests to ensure that the {@link ArchivePaths} implementation creates Paths as expected from various specified
 * contexts
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class PathsTestCase extends PathsTestBase {

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.path.PathsTestBase#createPath(java.lang.String)
     */
    @Override
    ArchivePath createPath(final String context) {
        return ArchivePaths.create(context);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.path.PathsTestBase#createPath(org.jboss.shrinkwrap.api.ArchivePath,
     *      org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    ArchivePath createPath(ArchivePath base, ArchivePath context) {
        return ArchivePaths.create(base, context);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.path.PathsTestBase#createPath(org.jboss.shrinkwrap.api.ArchivePath,
     *      java.lang.String)
     */
    @Override
    ArchivePath createPath(ArchivePath base, String context) {
        return ArchivePaths.create(base, context);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.path.PathsTestBase#createPath(java.lang.String, java.lang.String)
     */
    @Override
    ArchivePath createPath(String base, String context) {
        return ArchivePaths.create(base, context);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.path.PathsTestBase#createPath(java.lang.String,
     *      org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    ArchivePath createPath(final String base, final ArchivePath context) {
        return ArchivePaths.create(base, context);
    }
}

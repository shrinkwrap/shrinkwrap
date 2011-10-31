/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api;

/**
 * Encapsulates a shared {@link Configuration} to be used by all {@link Archive}s created by this {@link Domain}'s
 * {@link ArchiveFactory}. New domains are created via {@link ShrinkWrap#createDomain()} (for a default configuration
 * isolated from the {@link ShrinkWrap#getDefaultDomain()}), or {@link ShrinkWrap#createDomain(Configuration)} and
 * {@link ShrinkWrap#createDomain(ConfigurationBuilder)} (to supply an explicit configuration property set).
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public final class Domain {

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Configuration for this Domain
     */
    private final Configuration configuration;

    /**
     * Factory for creating archives within this {@link Domain}
     */
    private final ArchiveFactory archiveFactory;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new instance backed by the supplied {@link Configuration}
     *
     * @param configuration
     *            backing the new instance
     * @throws IllegalArgumentException
     *             If the configuration is not supplied
     */
    Domain(final Configuration configuration) throws IllegalArgumentException {
        // Precondition checks
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must be specified");
        }

        // Set
        this.configuration = configuration;
        this.archiveFactory = new ArchiveFactory(configuration);
    }

    // -------------------------------------------------------------------------------------||
    // Functional Methods ----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains the {@link Configuration} associated with this {@link Domain}
     *
     * @return the configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Obtains the {@link ArchiveFactory} for this domain. All {@link Archive}s created from the factory will be backed
     * by this domain's configuration.
     *
     * @return the archiveFactory
     */
    public ArchiveFactory getArchiveFactory() {
        return archiveFactory;
    }

}

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

import java.io.File;

import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.api.spec.WebArchive;

/**
 * Main entry point into the ShrinkWrap system. Each {@link Archive} has an associated {@link Configuration} provided at
 * construction by the {@link Domain} under which the archive was created. {@link ShrinkWrap} provides static access to
 * the default {@link Domain} (and by extension the default {@link Configuration}), as well as a shortcut mechanism to
 * create {@link Archive}s under these defaults by way of {@link ShrinkWrap#create(Class, String)}. Additionally, this
 * class is the hook to create new {@link Domain}s via {@link ShrinkWrap#createDomain()},
 * {@link ShrinkWrap#createDomain(ConfigurationBuilder)} or {@link ShrinkWrap#createDomain(Configuration)}.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public final class ShrinkWrap {
    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Internal constructor; not to be called as this class provides static utilities only
     */
    private ShrinkWrap() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    // -------------------------------------------------------------------------------------||
    // Functional Methods ----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new {@link Domain} containing a default {@link Configuration}. {@link ArchiveFactory}s created from
     * this domain will have isolated configuration from archive factories created from other domains. Likewise, all
     * {@link ArchiveFactory}s and {@link Archive}s created from the returned domain will share the same configuration.
     *
     * @return A new {@link Domain} with default configuration
     */
    public static Domain createDomain() {
        return createDomain(new ConfigurationBuilder());
    }

    /**
     * Creates a new {@link Domain} containing configuration properties from the supplied {@link ConfigurationBuilder}.
     * {@link ArchiveFactory}s created from this domain will have isolated configuration from archive factories created
     * from other domains. Likewise, all {@link ArchiveFactory}s and {@link Archive}s created from the returned domain
     * will share the same configuration.
     *
     * @param builder
     *            Builder with which we should create a {@link Configuration} for this {@link Domain}
     * @return A new {@link Domain} with default configuration
     * @throws IllegalArgumentException
     *             If the builder is not supplied
     */
    public static Domain createDomain(final ConfigurationBuilder builder) throws IllegalArgumentException {
        if (builder == null) {
            throw new IllegalArgumentException("builder must be supplied");
        }
        return createDomain(builder.build());
    }

    /**
     * Creates a new {@link Domain} containing configuration properties from the supplied {@link Configuration}.
     * {@link ArchiveFactory}s created from this domain will have isolated configuration from archive factories created
     * from other domains. Likewise, all {@link ArchiveFactory}s and {@link Archive}s created from the returned domain
     * will share the same configuration.
     *
     * @param configuration
     *            {@link Configuration} for this {@link Domain}
     * @return A new {@link Domain} with default configuration
     * @throws IllegalArgumentException
     *             If the configuration is not supplied
     */
    public static Domain createDomain(final Configuration configuration) throws IllegalArgumentException {
        if (configuration == null) {
            throw new IllegalArgumentException("configuration must be supplied");
        }
        return new Domain(configuration);
    }

    /**
     * Returns a single domain with default configuration for use in applications with no explicit configuration or
     * isolation requirements.
     *
     * @return default {@link Domain}
     */
    public static Domain getDefaultDomain() {
        return DefaultDomainWrapper.SINGLETON.getDefaultDomain();
    }

    /**
     * Creates a new archive of the specified type. The archive will be be backed by the default {@link Configuration}.
     * specific to this {@link ArchiveFactory}. Generates a random name for the archive and adds proper extension based
     * on the service descriptor properties file if extension property is present (e.g.
     * shrinkwrap/impl-base/src/main/resources/META-INF/services/org.jboss.shrinkwrap.api.spec.JavaArchive)
     *
     * Invoking this method is functionally equivalent to calling {@link ArchiveFactory#create(Class)} upon
     * {@link Domain#getArchiveFactory()} upon the domain returned by {@link ShrinkWrap#getDefaultDomain()}.
     *
     * @param type
     *            The type of the archive e.g. {@link WebArchive}
     * @return An {@link Assignable} archive base
     * @throws IllegalArgumentException
     *             if type is not specified
     * @throws UnknownExtensionTypeException
     *             If no extension mapping is found for the specified type
     */
    public static <T extends Assignable> T create(final Class<T> type) throws IllegalArgumentException,
        UnknownExtensionTypeException {
        // Precondition checks
        if (type == null) {
            throw new IllegalArgumentException("Type must be specified");
        }

        // Delegate to the default domain's archive factory for creation
        return ShrinkWrap.getDefaultDomain().getArchiveFactory().create(type);
    }

    /**
     * Creates a new archive of the specified type. The archive will be be backed by the default {@link Configuration}.
     * Invoking this method is functionally equivalent to calling {@link ArchiveFactory#create(Class, String)} upon
     * {@link Domain#getArchiveFactory()} upon the domain returned by {@link ShrinkWrap#getDefaultDomain()}.
     *
     * @param type
     *            The type of the archive e.g. {@link WebArchive}
     * @param archiveName
     *            The name of the archive
     * @return An {@link Assignable} archive base
     * @throws IllegalArgumentException
     *             either argument is not specified
     */
    public static <T extends Assignable> T create(final Class<T> type, final String archiveName)
        throws IllegalArgumentException {
        // Precondition checks
        if (type == null) {
            throw new IllegalArgumentException("Type must be specified");
        }
        if (archiveName == null || archiveName.isEmpty()) {
            throw new IllegalArgumentException("ArchiveName must be specified");
        }

        // Delegate to the default domain's archive factory for creation
        return ShrinkWrap.getDefaultDomain().getArchiveFactory().create(type, archiveName);
    }

    /**
     * Creates a new archive of the specified type as imported from the specified {@link File}. The file is expected to
     * be encoded as ZIP (ie. JAR/WAR/EAR). The name of the archive will be set to {@link File#getName()}. The archive
     * will be be backed by the {@link Configuration} within the {@link ShrinkWrap#getDefaultDomain()}
     *
     * @param type
     *            The type of the archive e.g. {@link org.jboss.shrinkwrap.api.spec.WebArchive}
     * @param archiveFile
     *            the archiveName to use
     * @return An {@link Assignable} view
     * @throws IllegalArgumentException
     *             If either argument is not supplied, if the specified {@link File} does not exist, or is not a valid
     *             ZIP file
     * @throws org.jboss.shrinkwrap.api.importer.ArchiveImportException
     *             If an error occurred during the import process
     */
    public static <T extends Assignable> T createFromZipFile(final Class<T> type, final File archiveFile)
        throws IllegalArgumentException, ArchiveImportException {
        // Delegate
        return getDefaultDomain().getArchiveFactory().createFromZipFile(type, archiveFile);
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Members ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Singleton wrapper to encapsulate a default domain
     *
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     * @version $Revision: $
     */
    private enum DefaultDomainWrapper {
        SINGLETON;

        /**
         * The wrapped default domain
         */
        private transient Domain domain = ShrinkWrap.createDomain();

        /**
         * Obtains the default domain for the system
         *
         * @return the default domain for the system
         */
        private Domain getDefaultDomain() {
            return domain;
        }
    }

}

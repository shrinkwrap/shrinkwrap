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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

/**
 * Encapsulates all configuration for a given {@link Domain}. Each {@link Archive} created by the domain's
 * {@link ArchiveFactory} will consult the configuration internally. An {@link Archive}'s configuration may not be
 * changed after construction; if a new config is required it must be created under a new domain. The default
 * configuration is defined by {@link ConfigurationBuilder}, and new configurations are created via
 * {@link ConfigurationBuilder#build()}. Note that while the {@link Configuration} is immutable, its properties may have
 * internal state that may be changed. For true isolation, use separate {@link Domain}s when creating {@link Archive}s.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public class Configuration {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Loader mapping archive types to the appropriate underlying implementation
     */
    private final ExtensionLoader extensionLoader;

    /**
     * {@link ExecutorService} used for all asynchronous operations
     */
    private final ExecutorService executorService;

    /**
     * {@link ClassLoader}s used for extension loading
     */
    private final Iterable<ClassLoader> classLoaders;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new configuration instance using properties contained in the specified {@link ConfigurationBuilder}.
     *
     * @param builder
     *            Construction object encapsulating the properties to use in this configuration
     * @throws IllegalArgumentException
     *             If the builder was not specified
     */
    Configuration(final ConfigurationBuilder builder) throws IllegalArgumentException {
        // Precondition checks
        if (builder == null) {
            throw new IllegalArgumentException("builder must be specified");
        }

        // Let the builder set defaults
        builder.setDefaults();

        // Set
        this.extensionLoader = builder.getExtensionLoader();
        this.executorService = builder.getExecutorService();
        // Defensive copy
        Collection<ClassLoader> cls = new ArrayList<ClassLoader>();
        if (builder.getClassLoaders() instanceof Collection) {
            cls = (Collection<ClassLoader>) builder.getClassLoaders();
        } else {
            for (final ClassLoader cl : builder.getClassLoaders()) {
                cls.add(cl);
            }
        }
        this.classLoaders = Collections.unmodifiableCollection(cls);
    }

    // -------------------------------------------------------------------------------------||
    // Accessors --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * @return the extensionLoader
     */
    public ExtensionLoader getExtensionLoader() {
        return extensionLoader;
    }

    /**
     * @return the executorService
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }

    /**
     * @return The {@link ClassLoader}s to be used in this configuration; used in extension loading and adding CL
     *         resources to the archive, etc
     */
    public Iterable<ClassLoader> getClassLoaders() {
        return classLoaders;
    }
}

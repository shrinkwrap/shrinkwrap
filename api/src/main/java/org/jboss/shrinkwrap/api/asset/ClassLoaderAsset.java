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
package org.jboss.shrinkwrap.api.asset;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * ClassloaderAsset
 *
 * Implementation of a {@link Asset} backed by a resource located in the Classloader.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 */
public class ClassLoaderAsset implements Asset {
    private String resourceName;

    private ClassLoader classLoader;

    /**
     * Load a named resource using the current threads context classloader.
     *
     * @param resourceName
     *            The name of the resource to load
     * @throws IllegalArgumentException
     *             resourceName can not be null
     * @throws IllegalArgumentException
     *             resourceName must be found in given classloader
     */
    public ClassLoaderAsset(String resourceName) {
        this(resourceName, SecurityActions.getThreadContextClassLoader());
    }

    /**
     * Load a named resource using the given classloader.
     *
     * @param resourceName
     *            The name of the resource to load
     * @param classLoader
     *            The ClassLoader to use
     * @throws IllegalArgumentException
     *             resourceName can not be null
     * @throws IllegalArgumentException
     *             classloader can not be null
     * @throws IllegalArgumentException
     *             resourceName must be found in given classloader
     */
    public ClassLoaderAsset(String resourceName, ClassLoader classLoader) {
        if (resourceName == null) {
            throw new IllegalArgumentException("ResourceName must be specified");
        }
        if (classLoader == null) {
            throw new IllegalArgumentException("ClassLoader must be specified");
        }
        if (classLoader.getResource(resourceName) == null) {
            throw new IllegalArgumentException(resourceName + " not found in classloader " + classLoader);
        }

        this.resourceName = resourceName;
        this.classLoader = classLoader;
    }

    /**
     * Returns the loaded resource.
     *
     */
    public String getSource() {
        return this.resourceName;
    }

    /**
     * Opens up the given resource as a stream.
     *
     */
    @Override
    public InputStream openStream() {
        return new BufferedInputStream(classLoader.getResourceAsStream(resourceName), 8192);
    }

}

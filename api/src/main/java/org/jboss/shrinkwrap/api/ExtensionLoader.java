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
package org.jboss.shrinkwrap.api;

/**
 * ExtensionLoader
 * <p>
 * Describes a way for the {@link Archive} to load extensions. If an implementation is not set in the {@link Domain}'s
 * {@link Configuration}, a default strategy will be used to load extensions.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public interface ExtensionLoader {

    /**
     * Load a Extension.
     *
     * @param <T>
     *            The type of extension to be loaded.
     * @param extensionClass
     *            The Extension interface
     * @param baseArchive
     *            The base archive to use
     * @return a
     */
    <T extends Assignable> T load(Class<T> extensionClass, Archive<?> baseArchive);

    /**
     * Add an Override to the normal Extension loading.
     * <p>
     * If a specific class is found to be overridden, the class will not be loaded using the normal strategy.
     *
     * @param <T>
     *            The type of Extension
     * @param extensionClass
     *            The Extension interface class
     * @param extensionImplClass
     *            The Extension implementation class
     * @return this ExtensionLoader
     */
    <T extends Assignable> ExtensionLoader addOverride(Class<T> extensionClass,
        Class<? extends T> extensionImplClass);

    /**
     * Gets the extension for the given type from the extensionMapping
     *
     * @param extensionClass
     *            The Extension interface class
     * @param <T>
     *            The type of Extension
     * @return the filename extension
     */
    <T extends Assignable> String getExtensionFromExtensionMapping(Class<T> extensionClass);

    /**
     * Gets the {@link org.jboss.shrinkwrap.api.ArchiveFormat} for the given type from the extensionMapping
     *
     * @param extensionClass
     *            The Extension interface class
     * @param <T>
     *            The type of Extension
     * @return the archive format
     */
    <T extends Archive<T>> ArchiveFormat getArchiveFormatFromExtensionMapping(Class<T> extensionClass);

}

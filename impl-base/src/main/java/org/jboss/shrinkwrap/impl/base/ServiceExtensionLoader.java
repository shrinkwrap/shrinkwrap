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

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.ClassLoaderSearchUtilDelegator;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.ExtensionLoader;
import org.jboss.shrinkwrap.api.UnknownExtensionTypeException;
import org.jboss.shrinkwrap.api.UnknownExtensionTypeExceptionDelegator;

/**
 * ServiceExtensionLoader
 * <p>
 * This class is the default strategy to load extensions when an instance of {@link ExtensionLoader} is not provided to
 * the {@link ConfigurationBuilder} and the {@link ConfigurationBuilder#build()} method is invoked. If the
 * {@link ConfigurationBuilder} doesn't provide any {@link ClassLoader}, {@link ConfigurationBuilder#build()} defaults
 * to a one-element collection holding the TCCL. The {@link ServiceExtensionLoader#classLoaders} are used to find the
 * provider-configuration file for the extension to be loaded in META-INF/services/. This provider-configuration file is
 * used to make an instance of the SPI implementation and cached in {@link ServiceExtensionLoader#cache}.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public class ServiceExtensionLoader implements ExtensionLoader {

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private final Map<Class<?>, Class<?>> cache = new HashMap<>();
    private final Map<Class<?>, ExtensionWrapper> extensionMappings = new HashMap<>();

    /**
     * ClassLoader used for loading extensions
     */
    private final Iterable<ClassLoader> classLoaders;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new instance, using the specified {@link ClassLoader}s to create extensions
     *
     * @param classLoaders
     *             The class loaders to be used to load extension implementations
     * @throws IllegalArgumentException
     *             If the {@link ClassLoader} is not specified
     */
    public ServiceExtensionLoader(final Iterable<ClassLoader> classLoaders) throws IllegalArgumentException {
        if (classLoaders == null) {
            throw new IllegalArgumentException("ClassLoader must be specified");
        }
        this.classLoaders = classLoaders;
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - ExtensionLoader -----------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.ExtensionLoader#load(java.lang.Class, org.jboss.shrinkwrap.api.Archive)
     */
    @Override
    public <T extends Assignable> T load(Class<T> extensionClass, Archive<?> baseArchive)
        throws UnknownExtensionTypeException {
        if (isCached(extensionClass)) {
            return createFromCache(extensionClass, baseArchive);
        }
        T object = createFromLoadExtension(extensionClass, baseArchive);

        addToCache(extensionClass, object.getClass());

        return object;
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods - Cache ----------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    boolean isCached(Class<?> extensionClass) {
        return cache.containsKey(extensionClass);
    }

    private <T extends Assignable> T createFromCache(Class<T> extensionClass, Archive<?> archive) {
        Class<T> extensionImplClass = getFromCache(extensionClass);
        return createExtension(extensionImplClass, archive);
    }

    void addToCache(Class<?> extensionClass, Class<?> extensionImplClass) {
        cache.put(extensionClass, extensionImplClass);
    }

    @SuppressWarnings("unchecked")
    <T extends Assignable> Class<T> getFromCache(Class<T> extensionClass) {
        return (Class<T>) cache.get(extensionClass);
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods - Override -------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.ExtensionLoader#addOverride(java.lang.Class, java.lang.Class)
     */
    public <T extends Assignable> ServiceExtensionLoader addOverride(final Class<T> extensionClass,
        final Class<? extends T> extensionImplClass) {
        addToCache(extensionClass, extensionImplClass);
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.ExtensionLoader#getExtensionFromExtensionMapping(java.lang.Class)
     */
    public <T extends Assignable> String getExtensionFromExtensionMapping(final Class<T> type) {
        ExtensionWrapper extensionWrapper = extensionMappings.get(type);
        if (extensionWrapper == null) {
            loadExtensionMapping(type);
        }
        extensionWrapper = extensionMappings.get(type);
        if (extensionWrapper == null) {
            throw UnknownExtensionTypeExceptionDelegator.newExceptionInstance(type);
        }
        return extensionWrapper.getProperty("extension");
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.ExtensionLoader#getArchiveFormatFromExtensionMapping(java.lang.Class)
     */
    public <T extends Archive<T>> ArchiveFormat getArchiveFormatFromExtensionMapping(final Class<T> type) {
        ExtensionWrapper extensionWrapper = extensionMappings.get(type);
        if (extensionWrapper == null) {
            loadExtensionMapping(type);
        }
        extensionWrapper = extensionMappings.get(type);
        if (extensionWrapper == null) {
            throw UnknownExtensionTypeExceptionDelegator.newExceptionInstance(type);
        }
        String archiveFormat = extensionWrapper.getProperty("archiveFormat");
        return ArchiveFormat.valueOf(archiveFormat);
    }

    /**
     * Check to see if a specific extension interface is being overloaded
     *
     * @param extensionClass
     *        The ExtensionType interface class
     * @return true if found
     */
    public boolean isOverriden(Class<?> extensionClass) {
        return isCached(extensionClass);
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods - Loading --------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new instance of a <code>extensionClass</code> implementation. The implementation class is found in a
     * provider-configuration file in META-INF/services/
     *
     * @param <T>
     *        The type of the extension class that extends {@link Assignable}.
     * @param extensionClass
     *        The class of the extension to be created.
     * @param archive
     *        The archive used to create the extension instance.
     * @return an instance of the <code>extensionClass</code> implementation.
     */
    private <T extends Assignable> T createFromLoadExtension(Class<T> extensionClass, Archive<?> archive) {
        ExtensionWrapper extensionWrapper = loadExtensionMapping(extensionClass);
        if (extensionWrapper == null) {
            throw new RuntimeException("Failed to load ExtensionMapping");
        }

        Class<T> extensionImplClass = loadExtension(extensionWrapper);

        if (!extensionClass.isAssignableFrom(extensionImplClass)) {
            throw new RuntimeException("Found extension impl class " + extensionImplClass.getName()
                + " not assignable to extension interface " + extensionClass.getName());
        }
        return createExtension(extensionImplClass, archive);
    }

    /**
     * Loads the implementation class hold in {@link ExtensionWrapper#implementingClassName}
     *
     * @param <T>
     *        The type of the extension class that extends {@link Assignable}.
     * @param extensionWrapper
     *        The wrapper containing the name of the implementing class.
     * @return The {@link Class} object for the implementation class specified in the {@code extensionWrapper}.
     */
    private <T extends Assignable> Class<T> loadExtension(ExtensionWrapper extensionWrapper) {
        return loadExtensionClass(extensionWrapper.implementingClassName);
    }

    /**
     * Finds the SPI configuration, wraps it into a {@link ExtensionWrapper} and loads it to
     * {@link ServiceExtensionLoader#extensionMappings}.
     *
     * @param <T>
     *        The type of the extension class that extends {@link Assignable}.
     * @param extensionClass
     *        The class of the extension to be mapped.
     * @return An {@link ExtensionWrapper} containing the SPI configuration for the specified {@code extensionClass}.
     */
    private <T extends Assignable> ExtensionWrapper loadExtensionMapping(Class<T> extensionClass) {
        final InputStream extensionStream = findExtensionImpl(extensionClass);

        ExtensionWrapper extensionWrapper = loadExtensionWrapper(extensionStream, extensionClass);
        this.extensionMappings.put(extensionClass, extensionWrapper);
        return extensionWrapper;
    }

    /**
     * Iterates through the classloaders to load the provider-configuration file for <code>extensionClass</code> in
     * META-INF/services/ using its binary name.
     *
     * @param <T>
     *         The type of the extension class that extends {@link Assignable}.
     * @param extensionClass
     *         SPI type for which the configuration file is looked for
     * @return An {@link InputStream} representing <code>extensionClass</code>'s configuration file
     * @throws RuntimeException
     *         if it doesn't find a provider-configuration file for <code>extensionClass</code>
     * @throws UnknownExtensionTypeExceptionDelegator
     */
    private <T extends Assignable> InputStream findExtensionImpl(final Class<T> extensionClass) {
        try {
            // Add all extension impls found in all CLs
            for (final ClassLoader cl : this.getClassLoaders()) {
                final InputStream stream = cl.getResourceAsStream("META-INF/services/" + extensionClass.getName());
                if (stream != null) {
                    return stream;
                }
            }

            // None found
            throw new RuntimeException("No extension implementation found for " + extensionClass.getName()
                + ", please verify classpath or add a extensionOverride");
        } catch (Exception e) {
            throw UnknownExtensionTypeExceptionDelegator.newExceptionInstance(extensionClass);
        }
    }

    /**
     * Wraps the provider-configuration file <code>extensionStream</code>, the SPI <code>extensionClass</code> and its
     * implementation class name into a {@link ExtensionWrapper} instance.
     *
     * @param <T>
     *        The type of the extension class that extends {@link Assignable}.
     * @param extensionStream
     *            - a bytes stream representation of the provider-configuration file
     * @param extensionClass
     *            - SPI type
     * @return a {@link ExtensionWrapper} instance
     */
    private <T extends Assignable> ExtensionWrapper loadExtensionWrapper(final InputStream extensionStream,
        Class<T> extensionClass) {
        Properties properties = new Properties();
        try {
            properties.load(extensionStream);
        } catch (IOException e) {
            throw new RuntimeException("Could not open stream for extensionURL " + extensionStream, e);
        }
        String implementingClassName = (String) properties.get("implementingClassName");
        if (implementingClassName == null) {
            throw new RuntimeException("Property implementingClassName is not present in " + extensionStream);
        }
        final Map<String, String> map = new HashMap<>(properties.size());
        final Enumeration<Object> keys = properties.keys();
        while (keys.hasMoreElements()) {
            final String key = (String) keys.nextElement();
            final String value = (String) properties.get(key);
            map.put(key, value);
        }
        return new ExtensionWrapper(implementingClassName, map, extensionClass);
    }

    /**
     * Delegates class loading of <code>extensionClassName</code> to
     * {@link ClassLoaderSearchUtilDelegator#findClassFromClassLoaders(String, Iterable)} passing the
     * <code>extensionClassName</code> and the instance's <code>classLoaders</code>.
     *
     * @param <T>
     *        The type of the extension class that extends {@link Assignable}.
     * @param extensionClassName
     *        The binary name of the class to be loaded.
     * @return
     *        The {@link Class} object for the specified extension class name.
     */
    @SuppressWarnings("unchecked")
    private <T extends Assignable> Class<T> loadExtensionClass(String extensionClassName) {
        try {
            return (Class<T>) ClassLoaderSearchUtilDelegator.findClassFromClassLoaders(extensionClassName,
                getClassLoaders());
        } catch (final ClassNotFoundException e) {
            throw new RuntimeException("Could not load class " + extensionClassName, e);
        }
    }

    /**
     * Creates an instance of <code>extensionImplClass</code> using <code>archive</code> as the parameter for its
     * one-argument list constructor.
     *
     * @param <T>
     *        The type of the extension class that extends {@link Assignable}.
     * @param extensionImplClass
     *        The class of the extension implementation to be instantiated.
     * @param archive
     *        The archive object to be passed to the constructor of the extension implementation.
     * @return
     *        An instance of <code>extensionImplClass</code> created using <code>archive</code> as a parameter.

     */
    private <T extends Assignable> T createExtension(Class<T> extensionImplClass, Archive<?> archive) {

        T extension;
        Constructor<T> extensionImplConstructor = findConstructor(extensionImplClass);

        @SuppressWarnings("unchecked")
        Class<T> constructorArg = (Class<T>) extensionImplConstructor.getParameterTypes()[0];
        try {

            if (constructorArg.isInstance(archive)) {
                extension = extensionImplConstructor.newInstance(archive);
            } else {
                extension = extensionImplConstructor.newInstance(load(constructorArg, archive));
            }
        } catch (InstantiationException e) {
            throw new ExtensionLoadingException("Failed to instantiate class of type " + archive.getClass()
                + ". The underlying class can not be abstract.", e);
        } catch (IllegalAccessException e) {
            throw new ExtensionLoadingException("Failed to instantiate class of type " + archive.getClass()
                + ". The underlying constructor is inaccessible.", e);
        } catch (InvocationTargetException e) {
            throw new ExtensionLoadingException("Failed to instantiate class of type " + archive.getClass()
                + ". The underlying constructor threw an exception.", e);
        }

        return extension;
    }

    /**
     * Finds a constructor with a one-argument list's element which implements {@link Archive}.
     *
     * @param <T>
     *        The type of the class that extends {@link Assignable}.
     * @param extensionImplClass
     *            - Implementation of {@link Assignable} with a one-argument list's element which implements
     *            {@link Archive}.
     * @return The {@link Constructor} of type <code>T</code> that has a single argument of type {@link Archive}.
     */
    @SuppressWarnings("unchecked")
    private <T extends Assignable> Constructor<T> findConstructor(Class<T> extensionImplClass) {
        Constructor<?>[] constructors = SecurityActions.getConstructors(extensionImplClass);
        for (Constructor<?> constructor : constructors) {
            Class<?>[] parameters = constructor.getParameterTypes();
            if (parameters.length != 1) {
                continue;
            }
            Class<?> parameter = parameters[0];
            if (Archive.class.isAssignableFrom(parameter)) {
                return (Constructor<T>) constructor;
            }
        }
        throw new ExtensionLoadingException("No constructor with a single argument of type " + Archive.class.getName()
            + " could be found");
    }

    private Iterable<ClassLoader> getClassLoaders() {
        return this.classLoaders;
    }
}

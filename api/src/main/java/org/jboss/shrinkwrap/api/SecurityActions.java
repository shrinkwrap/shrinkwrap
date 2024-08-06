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

import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Arrays;

/**
 * A set of privileged actions that are not to leak out of this package
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
final class SecurityActions {

    // -------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * No instantiation
     */
    private SecurityActions() {
        throw new UnsupportedOperationException("No instantiation");
    }

    // -------------------------------------------------------------------------------||
    // Utility Methods --------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * Obtains the Thread Context ClassLoader
     */
    static ClassLoader getThreadContextClassLoader() {
        return AccessController.doPrivileged(GetTcclAction.INSTANCE);
    }

    /**
     * Obtains the Constructor specified from the given Class and argument types
     *
     *
     * @param clazz
     *         The class from which to obtain the constructor
     * @param argumentTypes
     *         The types of the constructor arguments
     * @return The Constructor object corresponding to the requested constructor
     * @throws NoSuchMethodException
     *         If matching method is not found.
     */
    static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... argumentTypes)
        throws NoSuchMethodException {
        try {
            return AccessController.doPrivileged((PrivilegedExceptionAction<Constructor<?>>) () -> clazz.getConstructor(argumentTypes));
        }
        // Unwrap
        catch (final PrivilegedActionException pae) {
            final Throwable t = pae.getCause();
            // Rethrow
            if (t instanceof NoSuchMethodException) {
                throw (NoSuchMethodException) t;
            } else {
                // No other checked Exception thrown by Class.getConstructor
                try {
                    throw (RuntimeException) t;
                }
                // Just in case we've really messed up
                catch (final ClassCastException cce) {
                    throw new RuntimeException("Obtained unchecked Exception; this code should never be reached", t);
                }
            }
        }
    }

    /**
     * Creates a new instance of the specified {@link Class} using the specified construction arguments. Casts and
     * returns as the specified expected type
     *
     * @param <T>
     *         The type of the object to be returned
     * @param clazz
     *         The class of the object to be created
     * @param argumentTypes
     *         The types of the constructor arguments
     * @param arguments
     *         The values of the constructor arguments
     * @param expectedType
     *         The expected type of the created instance
     * @return A new instance of the specified class, cast to the expected type
     */
    static <T> T newInstance(final Class<?> clazz, final Class<?>[] argumentTypes, final Object[] arguments,
        final Class<T> expectedType) {
        // Precondition checks
        if (clazz == null) {
            throw new IllegalArgumentException("Class must be specified");
        }
        if (argumentTypes == null) {
            throw new IllegalArgumentException("ArgumentTypes must be specified. Use empty array if no arguments");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must be specified. Use empty array if no arguments");
        }
        if (expectedType == null) {
            throw new IllegalArgumentException("Expected type must be specified");
        }

        // Get the ctor
        final Constructor<?> constructor;
        try {
            constructor = getConstructor(clazz, argumentTypes);
        } catch (final NoSuchMethodException e) {
            throw new RuntimeException("Could not create new instance of " + clazz + " using ctor argument types "
                + Arrays.asList(argumentTypes), e);
        }

        // Create the instance
        final Object obj;
        try {
            obj = constructor.newInstance(arguments);
        } catch (final Exception e) {
            throw new RuntimeException("Could not create a new instance of " + clazz + " using arguments "
                + Arrays.asList(arguments), e);
        }

        // Cast
        try {
            return expectedType.cast(obj);
        } catch (final ClassCastException cce) {
            // Reconstruct so we get some useful information
            throw new ClassCastException("Incorrect expected type, " + expectedType.getName() + ", defined for "
                + obj.getClass().getName());
        }
    }

    /**
     * Create a new instance by finding a constructor that matches the argumentTypes signature using the arguments for
     * instantiation.
     *
     * @param className
     *            Full class name of class to create
     * @param argumentTypes
     *            The constructor argument types
     * @param arguments
     *            The constructor arguments
     * @param cl
     *            The ClassLoader to use in constructing the new instance
     * @return a new instance
     * @throws IllegalArgumentException
     *             if className, argumentTypes, ClassLoader, or arguments are null
     * @throws RuntimeException
     *             if any exceptions during creation
     * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     */
    static <T> T newInstance(final String className, final Class<?>[] argumentTypes, final Object[] arguments,
        final Class<T> expectedType, final ClassLoader cl) {
        // Precondition checks
        if (className == null) {
            throw new IllegalArgumentException("ClassName must be specified");
        }
        if (argumentTypes == null) {
            throw new IllegalArgumentException("ArgumentTypes must be specified. Use empty array if no arguments");
        }
        if (arguments == null) {
            throw new IllegalArgumentException("Arguments must be specified. Use empty array if no arguments");
        }
        if (expectedType == null) {
            throw new IllegalArgumentException("Expected type must be specified");
        }
        if (cl == null) {
            throw new IllegalArgumentException("CL must be specified");
        }

        final Class<?> implClass;
        try {
            implClass = Class.forName(className, false, cl);
        } catch (ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Could not find class named " + className + " in the specified CL: "
                + cl, cnfe);
        }

        // Delegate
        return newInstance(implClass, argumentTypes, arguments, expectedType);
    }

    // -------------------------------------------------------------------------------||
    // Inner Classes ----------------------------------------------------------------||
    // -------------------------------------------------------------------------------||

    /**
     * Single instance to get the TCCL
     */
    private enum GetTcclAction implements PrivilegedAction<ClassLoader> {
        INSTANCE;

        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }

    }

}

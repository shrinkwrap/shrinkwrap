/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities to search a series of {@link ClassLoader}s for a {@link Class} by name.
 * <p>
 * Not to be granted visibility outside of this package, unless scoped out to internals (this is not part of the public
 * user API)
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
class ClassLoaderSearchUtil {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ClassLoaderSearchUtil.class.getName());

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * No instances, keep private
     */
    private ClassLoaderSearchUtil() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    // -------------------------------------------------------------------------------------||
    // Functional Methods -----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Finds a {@link Class} by name using a series of {@link ClassLoader}s as the search path
     *
     * @param className
     *             The fully qualified name of the class to be found
     * @param classLoaders
     *             An {@link Iterable} of {@link ClassLoader}s to be used as the search path
     * @return The {@link Class} object for the class with the specified name, found using one of the provided {@link ClassLoader}s
     * @throws ClassNotFoundException
     *             If the {@link Class} could not be found in any of the specified CLs
     */
    static Class<?> findClassFromClassLoaders(final String className, final Iterable<ClassLoader> classLoaders)
        throws ClassNotFoundException, IllegalArgumentException {
        // Precondition checks
        assert className != null && !className.isEmpty() : "Class Name must be specified";
        assert classLoaders != null : "ClassLoaders as search path must be specified";

        // Find the class by searching through the CLs in order
        Class<?> clazz = null;
        for (final ClassLoader cl : classLoaders) {
            try {
                clazz = Class.forName(className, false, cl);
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Found requested class " + clazz.getName() + " from ClassLoader "
                        + clazz.getClassLoader());
                }
            } catch (final ClassNotFoundException cnfe) {
                // Ignore here, try the next
            }

        }
        // If we haven't got a class yet, now we need to show we've got CNFE
        if (clazz == null) {
            throw new ClassNotFoundException("Could not find requested class \"" + className
                + "\" in any of the associated ClassLoaders: " + classLoaders);
        }

        // Return
        return clazz;
    }
}

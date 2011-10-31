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

/**
 * Widens visibility such that any package within ShrinkWrap impl-base may access the utilities provided by
 * {@link ClassLoaderSearchUtil}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ClassLoaderSearchUtilDelegator {
    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * No instances, keep private
     */
    private ClassLoaderSearchUtilDelegator() {
        throw new UnsupportedOperationException("No instances permitted");
    }

    // -------------------------------------------------------------------------------------||
    // Functional Methods -----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Finds a {@link Class} by name using a series of {@link ClassLoader}s as the search path
     *
     * @param className
     * @param classLoaders
     * @return
     * @throws ClassNotFoundException
     *             If the {@link Class} could not be found in any of the specified CLs
     */
    public static Class<?> findClassFromClassLoaders(final String className, final Iterable<ClassLoader> classLoaders)
        throws ClassNotFoundException, IllegalArgumentException {
        // Delegate
        return ClassLoaderSearchUtil.findClassFromClassLoaders(className, classLoaders);
    }
}

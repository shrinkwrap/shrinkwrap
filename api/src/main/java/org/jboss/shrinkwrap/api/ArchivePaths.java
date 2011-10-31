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
 * A Factory for {@link ArchivePath} creation. Instances using this shorthand class will be created using the
 * {@link ClassLoader} associated with the default {@link Domain}'s {@link Configuration}.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class ArchivePaths {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private static final String PATH_IMPL = "org.jboss.shrinkwrap.impl.base.path.BasicPath";

    /**
     * Creates a new {@link ArchivePath} representing the root path (/).
     *
     * @return a new root path
     */
    public static ArchivePath root() {
        return RootPathWrapper.INSTANCE.getRoot();
    }

    /**
     * Creates a new {@link ArchivePath} with the specified context
     *
     * @param context
     *            The context which this path represents. Null or blank represents the root. Relative paths will be
     *            adjusted to absolute form.
     * @return a new path
     */
    public static ArchivePath create(String context) {
        return createInstance(new Class<?>[] { String.class }, new Object[] { context });
    }

    /**
     * Creates a new {@link ArchivePath} using the specified base and specified relative context.
     *
     * @param basePath
     *            A absolute path
     * @param context
     *            A relative path to basePath
     * @return a new path
     */
    public static ArchivePath create(String basePath, String context) {
        return createInstance(new Class<?>[] { String.class, String.class }, new Object[] { basePath, context });
    }

    /**
     * Creates a new {@link ArchivePath} using the specified base and specified relative context.
     *
     * @param basePath
     *            A absolute path
     * @param context
     *            A relative path to basePath
     * @return a new path
     */
    public static ArchivePath create(final String basePath, final ArchivePath context) {
        return createInstance(new Class<?>[] { String.class, ArchivePath.class }, new Object[] { basePath, context });
    }

    /**
     * Creates a new {@link ArchivePath} using the specified base and specified relative context.
     *
     * @param basePath
     *            A absolute path
     * @param context
     *            A relative path to basePath
     * @return a new path
     */
    public static ArchivePath create(ArchivePath basePath, String context) {
        return createInstance(new Class<?>[] { ArchivePath.class, String.class }, new Object[] { basePath, context });
    }

    /**
     * Creates a new {@link ArchivePath} using the specified base and specified relative context.
     *
     * @param basePath
     *            A absolute path
     * @param context
     *            A relative path to basePath
     * @return a new path
     */
    public static ArchivePath create(ArchivePath basePath, ArchivePath context) {
        return createInstance(new Class<?>[] { ArchivePath.class, ArchivePath.class },
            new Object[] { basePath, context });
    }

    // -------------------------------------------------------------------------------------||
    // Class Members - Internal Helpers ---------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private static ArchivePath createInstance(final Class<?>[] argumentTypes, final Object[] arguments) {
        // Get the impl class
        final Class<?> archivePathImplClass;
        try {
            archivePathImplClass = ClassLoaderSearchUtil.findClassFromClassLoaders(PATH_IMPL, ShrinkWrap
                .getDefaultDomain().getConfiguration().getClassLoaders());
        } catch (final ClassNotFoundException cnfe) {
            throw new IllegalStateException("Could not find the archive path implementation class " + PATH_IMPL
                + " in any configured ClassLoader", cnfe);
        }

        // Create and return
        return SecurityActions.newInstance(archivePathImplClass, argumentTypes, arguments, ArchivePath.class);
    }

    /**
     * Singleton wrapper to obtain a root {@link ArchivePath}
     *
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     * @version $Revision: $
     */
    private enum RootPathWrapper {
        INSTANCE;
        private ArchivePath root = create(null);

        private ArchivePath getRoot() {
            return root;
        }
    }

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * No instantiation
     */
    private ArchivePaths() {
    }

}

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

import javax.annotation.processing.Filer;
import java.util.Collection;
import java.util.regex.Pattern;

/**
 * Factory class for the creation of new {@link Filter} instances. Filter instances using this shorthand class will be
 * created using the {@link ClassLoader} associated with the {@link Configuration} supplied to the constructor.
 *
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class FilterFactory {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private static final String IMPL_CLASS_NAME_INCLUDE_ALL_PATHS = "org.jboss.shrinkwrap.impl.base.filter.IncludeAllPaths";

    private static final String IMPL_CLASS_NAME_INCLUDE_REGEXP_PATHS = "org.jboss.shrinkwrap.impl.base.filter.IncludeRegExpPaths";

    private static final String IMPL_CLASS_NAME_EXCLUDE_REGEXP_PATHS = "org.jboss.shrinkwrap.impl.base.filter.ExcludeRegExpPaths";

    private static final String IMPL_CLASS_NAME_INCLUDE_PATHS = "org.jboss.shrinkwrap.impl.base.filter.IncludePaths";

    private static final String IMPL_CLASS_NAME_EXCLUDE_PATHS = "org.jboss.shrinkwrap.impl.base.filter.ExcludePaths";

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Configuration for all archives created from this factory
     */
    private final Configuration configuration;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new {@link FilterFactory} which will use the supplied {@link Configuration} for each new
     * {@link Filter} it creates.
     *
     * @param configuration
     *            the {@link Configuration} to use
     * @throws IllegalArgumentException
     *             if configuration is not supplied
     */
    FilterFactory(final Configuration configuration) throws IllegalArgumentException {
        // Precondition checks
        assert configuration != null : "configuration must be supplied";

        // Set
        this.configuration = configuration;
    }

    // -------------------------------------------------------------------------------------||
    // Functional Methods -----------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@link Filter} that includes all {@link ArchivePath}s.
     *
     * Only meant to be used internally.
     *
     * @return A {@link Filter} that always return true
     */
    public Filter<ArchivePath> includeAll() {
        return getFilterInstance(IMPL_CLASS_NAME_INCLUDE_ALL_PATHS, new Class<?>[] {}, new Object[] {});
    }

    /**
     * {@link Filer} that include all {@link ArchivePath}s that match the given Regular Expression {@link Pattern}.
     *
     * @param regexp
     *            The expression to include
     * @return A Regular Expression based include {@link Filter}
     */
    public Filter<ArchivePath> include(String regexp) {
        return getFilterInstance(IMPL_CLASS_NAME_INCLUDE_REGEXP_PATHS, new Class<?>[] { String.class },
            new Object[] { regexp });
    }

    /**
     * {@link Filter} that exclude all {@link ArchivePath}s that match a given Regular Expression {@link Pattern}.
     *
     * @param regexp
     *            The expression to exclude
     * @return A Regular Expression based exclude {@link Filter}
     */
    public Filter<ArchivePath> exclude(final String regexp) {
        return getFilterInstance(IMPL_CLASS_NAME_EXCLUDE_REGEXP_PATHS, new Class<?>[] { String.class },
            new Object[] { regexp });
    }

    /**
     * {@link Filer} that include all {@link ArchivePath}s that match the given List of paths..
     *
     * @param paths
     *            The paths to included
     * @return A Path list based include {@link Filter}
     */
    public Filter<ArchivePath> includePaths(final String... paths) {
        return getFilterInstance(IMPL_CLASS_NAME_INCLUDE_PATHS, new Class<?>[] { String[].class },
            new Object[] { paths });
    }

    /**
     * {@link Filer} that include all {@link ArchivePath}s that match the given List of paths..
     *
     * @param paths
     *            The paths to included
     * @return A Path list based include {@link Filter}
     */
    public Filter<ArchivePath> includePaths(final Collection<String> paths) {
        return getFilterInstance(IMPL_CLASS_NAME_INCLUDE_PATHS, new Class<?>[] { Collection.class },
            new Object[] { paths });
    }

    /**
     * {@link Filter} that exclude all {@link ArchivePath}s that match the given List of paths.
     *
     * @param paths
     *            The paths to exclude
     * @return A Path list based exclude {@link Filter}
     */
    public Filter<ArchivePath> excludePaths(final String... paths) {
        return getFilterInstance(IMPL_CLASS_NAME_EXCLUDE_PATHS, new Class<?>[] { String[].class },
            new Object[] { paths });
    }

    /**
     * {@link Filter} that exclude all {@link ArchivePath}s that match the given List of paths.
     *
     * @param paths
     *            The paths to exclude
     * @return A Path list based exclude {@link Filter}
     */
    public Filter<ArchivePath> excludePaths(final Collection<String> paths) {
        return getFilterInstance(IMPL_CLASS_NAME_EXCLUDE_PATHS, new Class<?>[] { Collection.class },
            new Object[] { paths });
    }

    /**
     * {@link Filter} that includes listed {@link Package}.
     *
     * @param packages
     *            To be included
     * @return
     */
    public Filter<ArchivePath> exclude(Package... packages) {
        return createRegExpFilter(IMPL_CLASS_NAME_EXCLUDE_REGEXP_PATHS, packages);
    }

    /**
     * {@link Filter} that excludes listed {@link Package}.
     *
     * @param packages
     *            To be excluded
     * @return
     */
    public Filter<ArchivePath> include(Package... packages) {
        return createRegExpFilter(IMPL_CLASS_NAME_INCLUDE_REGEXP_PATHS, packages);
    }

    private Filter<ArchivePath> createRegExpFilter(String filterClassName, Package... packages) {
        StringBuilder classExpression = new StringBuilder();
        for (Package pack : packages) {
            classExpression.append("|");
            classExpression.append("(.*" + pack.getName().replaceAll("\\.", "\\.") + ".*)");
        }
        classExpression.deleteCharAt(0);

        return getFilterInstance(filterClassName, new Class<?>[] { String.class },
            new Object[] { classExpression.toString() });
    }

    /**
     * {@link Filter} that includes listed {@link Class}.
     *
     * @param classes
     *            To be included
     * @return
     */
    public Filter<ArchivePath> include(Class<?>... classes) {
        return createRegExpFilter(IMPL_CLASS_NAME_INCLUDE_REGEXP_PATHS, classes);
    }

    /**
     * {@link Filter} that excludes listed {@link Class}.
     *
     * @param classes
     *            To be excluded
     * @return
     */
    public Filter<ArchivePath> exclude(Class<?>... classes) {
        return createRegExpFilter(IMPL_CLASS_NAME_EXCLUDE_REGEXP_PATHS, classes);
    }

    private Filter<ArchivePath> createRegExpFilter(String regExpFilterImplName, Class<?>... classes) {
        StringBuilder classExpression = new StringBuilder();
        for (Class<?> clazz : classes) {
            classExpression.append("|");
            classExpression.append("(.*" + clazz.getName().replaceAll("\\.", "\\.") + "\\.class)");
        }
        classExpression.deleteCharAt(0);

        return getFilterInstance(regExpFilterImplName, new Class<?>[] { String.class },
            new Object[] { classExpression.toString() });
    }

    /**
     * Creates a new {@link Filter} instance using the given impl class name, constructor arguments and type
     *
     * @param filterClassName
     * @param ctorTypes
     * @param ctorArguments
     * @return
     */
    @SuppressWarnings("unchecked")
    private Filter<ArchivePath> getFilterInstance(final String filterClassName, final Class<?>[] ctorTypes,
        final Object[] ctorArguments) {
        // Precondition checks
        assert filterClassName != null && filterClassName.length() > 0 : "Filter class name must be specified";
        assert ctorTypes != null : "Construction types must be specified";
        assert ctorArguments != null : "Construction arguments must be specified";
        assert ctorTypes.length == ctorArguments.length : "The number of ctor arguments and their types must match";

        // Find the filter impl class in the configured CLs
        final Class<Filter<ArchivePath>> filterClass;
        try {
            filterClass = (Class<Filter<ArchivePath>>) ClassLoaderSearchUtil.findClassFromClassLoaders(filterClassName,
                configuration.getClassLoaders());
        } catch (final ClassNotFoundException cnfe) {
            throw new IllegalStateException("Could not find filter implementation class " + filterClassName
                + " in any of the configured CLs", cnfe);
        }

        // Make the new instance
        return SecurityActions.newInstance(filterClass, ctorTypes, ctorArguments, Filter.class);
    }

}

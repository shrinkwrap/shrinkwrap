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

import java.util.Collection;
import java.util.regex.Pattern;

import javax.annotation.processing.Filer;

/**
 * Factory class for the creation of new {@link Filter} instances. Filter instances using this shorthand class will be
 * created using the {@link ClassLoader} associated with the default {@link Domain}'s {@link Configuration}.
 *
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class Filters {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@link Filter} that includes all {@link ArchivePath}s.
     *
     * Only meant to be used internally.
     *
     * @return A {@link Filter} that always return true
     */
    public static Filter<ArchivePath> includeAll() {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().includeAll();
    }

    /**
     * {@link Filer} that include all {@link ArchivePath}s that match the given Regular Expression {@link Pattern}.
     *
     * @param regexp
     *            The expression to include
     * @return A Regular Expression based include {@link Filter}
     */
    public static Filter<ArchivePath> include(String regexp) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().include(regexp);
    }

    /**
     * {@link Filter} that exclude all {@link ArchivePath}s that match a given Regular Expression {@link Pattern}.
     *
     * @param regexp
     *            The expression to exclude
     * @return A Regular Expression based exclude {@link Filter}
     */
    public static Filter<ArchivePath> exclude(final String regexp) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().exclude(regexp);
    }

    /**
     * {@link Filer} that include all {@link ArchivePath}s that match the given List of paths..
     *
     * @param paths
     *            The paths to included
     * @return A Path list based include {@link Filter}
     */
    public static Filter<ArchivePath> includePaths(final String... paths) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().includePaths(paths);
    }

    /**
     * {@link Filer} that include all {@link ArchivePath}s that match the given List of paths..
     *
     * @param paths
     *            The paths to included
     * @return A Path list based include {@link Filter}
     */
    public static Filter<ArchivePath> includePaths(final Collection<String> paths) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().includePaths(paths);
    }

    /**
     * {@link Filter} that exclude all {@link ArchivePath}s that match the given List of paths.
     *
     * @param paths
     *            The paths to exclude
     * @return A Path list based exclude {@link Filter}
     */
    public static Filter<ArchivePath> excludePaths(final String... paths) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().excludePaths(paths);
    }

    /**
     * {@link Filter} that exclude all {@link ArchivePath}s that match the given List of paths.
     *
     * @param paths
     *            The paths to exclude
     * @return A Path list based exclude {@link Filter}
     */
    public static Filter<ArchivePath> excludePaths(final Collection<String> paths) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().excludePaths(paths);
    }

    /**
     * {@link Filter} that includes listed {@link Package}.
     *
     * @param packages
     *            To be included
     * @return
     */
    public static Filter<ArchivePath> exclude(Package... packages) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().exclude(packages);
    }

    /**
     * {@link Filter} that excludes listed {@link Package}.
     *
     * @param packages
     *            To be excluded
     * @return
     */
    public static Filter<ArchivePath> include(Package... packages) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().include(packages);
    }

    /**
     * {@link Filter} that includes listed {@link Class}.
     *
     * @param classes
     *            To be included
     * @return
     */
    public static Filter<ArchivePath> include(Class<?>... classes) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().include(classes);
    }

    /**
     * {@link Filter} that excludes listed {@link Class}.
     *
     * @param classes
     *            To be excluded
     * @return
     */
    public static Filter<ArchivePath> exclude(Class<?>... classes) {
        return ShrinkWrap.getDefaultDomain().getFilterFactory().exclude(classes);
    }

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * No instantiation
     */
    private Filters() {
    }
}

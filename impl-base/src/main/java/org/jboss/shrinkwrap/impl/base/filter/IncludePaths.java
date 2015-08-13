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
package org.jboss.shrinkwrap.impl.base.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;

/**
 * IncludePaths
 *
 * Filter to include all {@link ArchivePath}s that match the given List of paths.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class IncludePaths implements Filter<ArchivePath> {
    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private Set<String> paths;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    public IncludePaths(String... paths) {
        Validate.notNull(paths, "Paths must be specified");
        this.paths = adjust(paths);
    }

    public IncludePaths(Collection<String> paths) {
        Validate.notNull(paths, "Paths must be specified");
        this.paths = adjust(paths.toArray(new String[0]));
    }

    private Set<String> adjust(String... paths) {
        Set<String> adjusted = new HashSet<String>();
        for(String path : paths) {
            adjusted.add(PathUtil.optionallyPrependSlash(path));
        }
        return adjusted;
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.Filter#includePaths(String[])
     */
    @Override
    public boolean include(ArchivePath path) {
        if (paths.contains(path.get())) {
            return true;
        }
        return false;
    }
}

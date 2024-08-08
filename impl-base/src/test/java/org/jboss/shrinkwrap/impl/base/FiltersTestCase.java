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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * FiltersTestCase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class FiltersTestCase {

    @Test
    public void shouldIncludeAll() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/META-INF/"), ArchivePaths.create("/WEB-INF/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.includeAll());

        Assertions.assertArrayEquals(paths.toArray(), filteredPaths.toArray(), "Should include all paths");
    }

    @Test
    public void shouldIncludePathRegExp() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/META-INF/"), ArchivePaths.create("/WEB-INF/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.include(".*META-INF.*"));

        Assertions.assertEquals(1, filteredPaths.size(), "Should only contain one");

        Assertions.assertEquals(ArchivePaths.create("/META-INF/"), filteredPaths.get(0), "Should only contain metainf");
    }

    @Test
    public void shouldExcludePathRegExp() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/META-INF/"), ArchivePaths.create("/WEB-INF/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.exclude(".*META-INF.*"));

        Assertions.assertEquals(1, filteredPaths.size(), "Should only contain one");

        Assertions.assertEquals(ArchivePaths.create("/WEB-INF/"), filteredPaths.get(0), "Should only contain webinf");
    }

    @Test
    public void shouldIncludePathsStringArray() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.includePaths("A", "B/"));

        Assertions.assertEquals(2, filteredPaths.size(), "Should contain two");
        Assertions.assertEquals(ArchivePaths.create("/A"), filteredPaths.get(0), "Should contain A");
        Assertions.assertEquals(ArchivePaths.create("/B"), filteredPaths.get(1), "Should contain B");
    }

    @Test
    public void shouldIncludePathsCollection() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.includePaths(Arrays.asList("A", "B/")));

        Assertions.assertEquals(2, filteredPaths.size(), "Should contain two");
        Assertions.assertEquals(ArchivePaths.create("/A"), filteredPaths.get(0), "Should contain A");
        Assertions.assertEquals(ArchivePaths.create("/B"), filteredPaths.get(1), "Should contain B");
    }

    @Test
    public void shouldExcludePathsStringArray() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.excludePaths("/A", "/B/"));

        Assertions.assertEquals(1, filteredPaths.size(), "Should only contain one");
        Assertions.assertEquals(ArchivePaths.create("/C"), filteredPaths.get(0), "Should only contain C");
    }

    @Test
    public void shouldExcludePathsCollection() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.excludePaths(Arrays.asList("/A", "/B/")));

        Assertions.assertEquals(1, filteredPaths.size(), "Should only contain one");
        Assertions.assertEquals(ArchivePaths.create("/C"), filteredPaths.get(0), "Should only contain C");
    }

    private <T> List<T> executeFilter(Class<T> clazz, List<T> items, Filter<T> filter) {
        List<T> result = new ArrayList<>();
        for (T item : items) {
            if (filter.include(item)) {
                result.add(item);
            }
        }
        return result;
    }
}

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
import org.junit.Assert;
import org.junit.Test;

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

        Assert.assertArrayEquals("Should include all paths", paths.toArray(), filteredPaths.toArray());
    }

    @Test
    public void shouldIncludePathRegExp() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/META-INF/"), ArchivePaths.create("/WEB-INF/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.include(".*META-INF.*"));

        Assert.assertEquals("Should only contain one", 1, filteredPaths.size());

        Assert.assertEquals("Should only contain metainf", ArchivePaths.create("/META-INF/"), filteredPaths.get(0));
    }

    @Test
    public void shouldExcludePathRegExp() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/META-INF/"), ArchivePaths.create("/WEB-INF/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.exclude(".*META-INF.*"));

        Assert.assertEquals("Should only contain one", 1, filteredPaths.size());

        Assert.assertEquals("Should only contain webinf", ArchivePaths.create("/WEB-INF/"), filteredPaths.get(0));
    }

    @Test
    public void shouldIncludePathsStringArray() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.includePaths("A", "B/"));

        Assert.assertEquals("Should contain two", 2, filteredPaths.size());
        Assert.assertEquals("Should contain A", ArchivePaths.create("/A"), filteredPaths.get(0));
        Assert.assertEquals("Should contain B", ArchivePaths.create("/B"), filteredPaths.get(1));
    }

    @Test
    public void shouldIncludePathsCollection() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.includePaths(Arrays.asList("A", "B/")));

        Assert.assertEquals("Should contain two", 2, filteredPaths.size());
        Assert.assertEquals("Should contain A", ArchivePaths.create("/A"), filteredPaths.get(0));
        Assert.assertEquals("Should contain B", ArchivePaths.create("/B"), filteredPaths.get(1));
    }

    @Test
    public void shouldExcludePathsStringArray() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.excludePaths("/A", "/B/"));

        Assert.assertEquals("Should only contain one", 1, filteredPaths.size());
        Assert.assertEquals("Should only contain C", ArchivePaths.create("/C"), filteredPaths.get(0));
    }

    @Test
    public void shouldExcludePathsCollection() {
        List<ArchivePath> paths = Arrays.asList(ArchivePaths.create("/A"), ArchivePaths.create("/B/"), ArchivePaths.create("/C/"));
        List<ArchivePath> filteredPaths = executeFilter(ArchivePath.class, paths, Filters.excludePaths(Arrays.asList("/A", "/B/")));

        Assert.assertEquals("Should only contain one", 1, filteredPaths.size());
        Assert.assertEquals("Should only contain C", ArchivePaths.create("/C"), filteredPaths.get(0));
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

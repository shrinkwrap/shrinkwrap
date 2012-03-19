/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api.classloader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

/**
 * Filter delegate classloader.
 *
 * @author <a href="mailto:ales.justin@jboss.org">Ales Justin</a>
 */
public class FilteredClassLoader extends ClassLoader {
    private static final Method getPackage;
    private final ClassLoader delegate;
    private final Set<String> excludes;
    private final Set<String> suffixes = new HashSet<String>();

    static {
        try {
            getPackage = ClassLoader.class.getDeclaredMethod("getPackage", String.class);
            getPackage.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Error getting getPackage method.", e);
        }
    }

    public FilteredClassLoader(String... excludes) {
        this(null, excludes);
    }

    public FilteredClassLoader(ClassLoader delegate, String... excludes) {
        if (delegate == null)
            delegate = getSystemClassLoader();
        this.delegate = delegate;
        this.excludes = new HashSet<String>();
        for (String ex : excludes) {
            String path = toPath(ex);
            this.excludes.add(path.endsWith("/") || path.endsWith("*") ? path : (path + "/"));
        }
        // add default suffixes
        addSuffix(".class");
        addSuffix(".xml");
        addSuffix(".properties");
    }

    public void addSuffix(String suffix) {
        suffixes.add(suffix);
    }

    protected static String toPath(String pckg) {
        return pckg.replace(".", "/");
    }

    protected boolean isExcluded(String name) {
        boolean match;
        for (String suffix : suffixes) {
            match = name.endsWith(suffix);
            if (match) {
                name = name.substring(0, name.length() - suffix.length());
                break;
            }
        }
        name = toPath(name);
        name = name.substring(0, name.lastIndexOf("/") + 1); // cut off the name

        for (String exclude : excludes) {
            if (exclude.endsWith("*")) {
                if (name.startsWith(exclude.substring(0, exclude.length() - 1)))
                    return true;
            } else {
                if (name.equals(exclude))
                    return true;
            }
        }

        return false;
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        if (name.startsWith("java.") || isExcluded(name) == false) {
            Class<?> clazz = delegate.loadClass(name);
            if (resolve)
                resolveClass(clazz);
            return clazz;
        }

        throw new ClassNotFoundException("Cannot load excluded class: " + name);
    }

    @Override
    public URL getResource(String name) {
        return isExcluded(name) ? null : delegate.getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return isExcluded(name) ? Collections.enumeration(Collections.<URL>emptySet()) : delegate.getResources(name);
    }

    @Override
    protected Package getPackage(String name) {
        try {
            return isExcluded(name) ? null : (Package) getPackage.invoke(delegate, name);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}

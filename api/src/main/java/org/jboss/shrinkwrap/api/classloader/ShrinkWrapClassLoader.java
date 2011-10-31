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
package org.jboss.shrinkwrap.api.classloader;

import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Extension that will create a ClassLoader based on a Array of Archives. When done, call
 * {@link ShrinkWrapClassLoader#close()} to free resources.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public class ShrinkWrapClassLoader extends URLClassLoader implements Closeable {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ShrinkWrapClassLoader.class.getName());

    /**
     * Empty String
     */
    private static final String EMPTY = "";

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * List of all streams opened, such that they may be closed in {@link ShrinkWrapClassLoader#close()}. Guarded by
     * "this".
     */
    private final List<InputStream> openedStreams = new ArrayList<InputStream>();

    // -------------------------------------------------------------------------------------||
    // Constructors -----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Constructs a new ShrinkWrapClassLoader for the specified {@link Archive}s using the default delegation parent
     * <code>ClassLoader</code>. The {@link Archive}s will be searched in the order specified for classes and resources
     * after first searching in the parent class loader.
     *
     * @param archives
     *            the {@link Archive}s from which to load classes and resources
     */
    public ShrinkWrapClassLoader(final Archive<?>... archives) {
        super(new URL[] {});

        if (archives == null) {
            throw new IllegalArgumentException("Archives must be specified");
        }
        addArchives(archives);
    }

    /**
     * Constructs a new ShrinkWrapClassLoader for the given {@link Archive}s. The {@link Archive}s will be searched in
     * the order specified for classes and resources after first searching in the specified parent class loader.
     *
     * @param parent
     *            the parent class loader for delegation
     * @param archives
     *            the {@link Archive}s from which to load classes and resources
     */
    public ShrinkWrapClassLoader(final ClassLoader parent, final Archive<?>... archives) {
        super(new URL[] {}, parent);

        if (archives == null) {
            throw new IllegalArgumentException("Archives must be specified");
        }
        addArchives(archives);
    }

    private void addArchives(final Archive<?>[] archives) {
        for (final Archive<?> archive : archives) {
            addArchive(archive);
        }
    }

    private void addArchive(final Archive<?> archive) {
        try {
            addURL(new URL(null, "archive:" + archive.getName() + "/", new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(final URL u) throws IOException {
                    return new URLConnection(u) {
                        @Override
                        public void connect() throws IOException {
                        }

                        @Override
                        public InputStream getInputStream() throws IOException {
                            final ArchivePath path = convertToArchivePath(u);
                            final Node node = archive.get(path);

                            // SHRINKWRAP-308
                            if (node == null) {
                                // We've asked for a path that doesn't exist
                                throw new FileNotFoundException("Requested path: " + path + " does not exist in "
                                    + archive.toString());
                            }

                            final Asset asset = node.getAsset();

                            // SHRINKWRAP-306
                            if (asset == null) {
                                // This is a directory, so return null InputStream to denote as such
                                return null;
                            }

                            final InputStream input = asset.openStream();
                            synchronized (this) {
                                openedStreams.add(input);
                            }
                            return input;

                        }

                        private ArchivePath convertToArchivePath(URL url) {
                            String path = url.getPath();
                            path = path.replace(archive.getName(), EMPTY);

                            return ArchivePaths.create(path);
                        }
                    };
                }
            }));
        } catch (Exception e) {
            throw new RuntimeException("Could not create URL for archive: " + archive.getName(), e);
        }
    }

    public void close() throws IOException {
        synchronized (this) {
            for (InputStream stream : openedStreams) {
                try {
                    stream.close();
                } catch (Exception e) {
                    log.warning("Could not close opened inputstream: " + e);
                }
            }
            openedStreams.clear();
        }
    }
}
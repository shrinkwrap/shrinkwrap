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
package org.jboss.shrinkwrap.impl.base.spec;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

/**
 * Base class for AddPackage* tests operating with a custom classloader.
 *
 * @author Falko Modler
 * @author Bob McWhirter
 */
public abstract class AddPackageTestBase {

    private static final Logger LOG = Logger.getLogger(AddPackageTestBase.class.getName());

    protected Domain domain;

    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {

        Archive<?> archive = buildArchive();

        tempFile = File.createTempFile("test", archive instanceof WebArchive ? ".war" : ".jar");
        archive.as(ZipExporter.class).exportTo(tempFile, true);
        URL archiveUrl = tempFile.toURI().toURL();

        URLClassLoader archiveCl = buildArchiveClassLoader(archiveUrl);

        ClassLoader shrinkwrapCl = new FilteringClassLoader(this.getClass().getClassLoader());

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.classLoaders(Arrays.asList(archiveCl, shrinkwrapCl));

        // Create a domain that includes the `donotchange` classes within a .jar/.war
        // and the rest, excluding `donotchange` from the regular app classloader.
        this.domain = ShrinkWrap.createDomain(builder.build());
    }

    protected abstract Archive<?> buildArchive();

    protected URLClassLoader buildArchiveClassLoader(final URL archiveUrl) {
        return new URLClassLoader(new URL[]{archiveUrl}, null);
    }

    @AfterEach
    public void tearDown() {
        // URLClassLoader.close() requires JDK7+

        if (tempFile.isFile() && !tempFile.delete()) {
            LOG.warning("Potential file leak: Could not delete " + tempFile);
        }
    }

    /**
     * Classloader to ensure the donotchange bits are not loaded through the app-classloader,
     * while still enabling the default ShrinkWrap bits to be found through the junit test.
     *
     * @author Bob McWhirter
     * @author Falko Modler
     */
    private static class FilteringClassLoader extends ClassLoader {

        private static final Enumeration<URL> EMPTY_ENUMERATION = new Enumeration<URL>() {

            @Override
            public URL nextElement() {
                throw new NoSuchElementException();
            }

            @Override
            public boolean hasMoreElements() {
                return false;
            }
        };

        public FilteringClassLoader(final ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(final String name) throws ClassNotFoundException {
            if (name.contains("donotchange")) {
                return null;
            }
            return super.loadClass(name);
        }

        @Override
        public Enumeration<URL> getResources(final String name) throws IOException {
            if (name.contains("donotchange")) {
                // Collections.emptyEnumeration() requires JDK7+
                return EMPTY_ENUMERATION;
            }
            return super.getResources(name);
        }
    }
}

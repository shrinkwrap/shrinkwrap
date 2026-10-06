/*
 * JBoss, Home of Professional Open Source
 * Copyright 2026, Red Hat Inc., and individual contributors
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

/**
 * ShrinkWrap Implementation Base module - core implementation.
 */
module org.jboss.shrinkwrap.impl.base {
    // Exported packages
    exports org.jboss.shrinkwrap.impl.base;
    exports org.jboss.shrinkwrap.impl.base.asset;
    exports org.jboss.shrinkwrap.impl.base.container;
    exports org.jboss.shrinkwrap.impl.base.exporter;
    exports org.jboss.shrinkwrap.impl.base.exporter.tar;
    exports org.jboss.shrinkwrap.impl.base.exporter.zip;
    exports org.jboss.shrinkwrap.impl.base.filter;
    exports org.jboss.shrinkwrap.impl.base.importer;
    exports org.jboss.shrinkwrap.impl.base.importer.tar;
    exports org.jboss.shrinkwrap.impl.base.importer.zip;
    exports org.jboss.shrinkwrap.impl.base.io;
    exports org.jboss.shrinkwrap.impl.base.io.tar;
    exports org.jboss.shrinkwrap.impl.base.io.tar.bzip;
    exports org.jboss.shrinkwrap.impl.base.nio2.file;
    exports org.jboss.shrinkwrap.impl.base.path;
    exports org.jboss.shrinkwrap.impl.base.serialization;
    exports org.jboss.shrinkwrap.impl.base.spec;

    // Transitive dependencies - needed by consumers
    requires transitive org.jboss.shrinkwrap.api;
    requires transitive org.jboss.shrinkwrap.spi;

    // Required modules
    requires java.logging;
    requires java.activation;

    // Service provider for NIO.2 FileSystem
    provides java.nio.file.spi.FileSystemProvider
        with org.jboss.shrinkwrap.impl.base.nio2.file.ShrinkWrapFileSystemProvider;
}

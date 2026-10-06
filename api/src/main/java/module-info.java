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
 * ShrinkWrap API module - public API for creating Java archives.
 */
module org.jboss.shrinkwrap.api {
    // Public API packages
    exports org.jboss.shrinkwrap.api;
    exports org.jboss.shrinkwrap.api.asset;
    exports org.jboss.shrinkwrap.api.classloader;
    exports org.jboss.shrinkwrap.api.container;
    exports org.jboss.shrinkwrap.api.exporter;
    exports org.jboss.shrinkwrap.api.formatter;
    exports org.jboss.shrinkwrap.api.importer;
    exports org.jboss.shrinkwrap.api.nio2.file;
    exports org.jboss.shrinkwrap.api.serialization;
    exports org.jboss.shrinkwrap.api.spec;

    // Internal package - only accessible to impl-base
    exports org.jboss.shrinkwrap.api.internal to org.jboss.shrinkwrap.impl.base;

    // Required JDK modules
    requires java.logging;
}

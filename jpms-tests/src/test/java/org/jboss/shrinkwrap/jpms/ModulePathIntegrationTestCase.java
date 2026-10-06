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
package org.jboss.shrinkwrap.jpms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.util.Map;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.nio2.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Exercises ShrinkWrap while it is loaded as named modules on the Java module path - the scenario
 * downstream modular applications rely on. The regular build patches its tests into
 * {@code org.jboss.shrinkwrap.impl.base}, so it never crosses a module boundary; this test does,
 * guarding against JPMS regressions such as split packages, dropped exports, or a missing
 * {@code provides}/{@code uses} directive.
 *
 * @author <a href="mailto:mjusko@redhat.com">Marek Jusko</a>
 */
class ModulePathIntegrationTestCase {

    /**
     * Fails if the test is not actually running on the module path. Without this guard the remaining
     * assertions could pass trivially from the class path, hiding a broken module descriptor.
     */
    @Test
    void runsAsNamedModulesOnTheModulePath() {
        final Module testModule = this.getClass().getModule();
        assertTrue(testModule.isNamed(),
            "Test must run on the module path, but its own module is unnamed");
        assertEquals("org.jboss.shrinkwrap.jpms", testModule.getName());
        assertEquals("org.jboss.shrinkwrap.api", ShrinkWrap.class.getModule().getName(),
            "ShrinkWrap API must be resolved as a named module");
        assertNotNull(testModule.getLayer(), "A named module on the module path must belong to a layer");
        assertTrue(testModule.getLayer().findModule("org.jboss.shrinkwrap.impl.base").isPresent(),
            "ShrinkWrap implementation must be resolved as a named module");
    }

    /**
     * Spec, exporter and importer extensions are all resolved through ShrinkWrap's reflective
     * extension loader, which must work across the api -> impl-base module boundary.
     */
    @Test
    void createsExportsAndReimportsArchive(@TempDir File tempDir) throws Exception {
        final String classPath = "/org/jboss/shrinkwrap/jpms/ModulePathIntegrationTestCase.class";
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClass(ModulePathIntegrationTestCase.class)
            .add(new StringAsset("hello"), "greeting.txt");
        assertTrue(jar.contains(classPath), "Archive should contain the added class");
        assertTrue(jar.contains("/greeting.txt"), "Archive should contain the added asset");

        // WebArchive exercises a second spec extension, including a web-specific container method
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
            .addAsWebResource(new StringAsset("<html/>"), "index.html");
        assertEquals("test.war", war.getName());
        assertTrue(war.contains("/index.html"), "WebArchive should place the web resource at its root");

        // Exporter extension -> real file on disk
        final File exported = new File(tempDir, "exported.jar");
        jar.as(ZipExporter.class).exportTo(exported, true);
        assertTrue(exported.length() > 0, "Exported archive should not be empty");

        // Importer extension -> read the archive back
        final JavaArchive reimported = ShrinkWrap.create(ZipImporter.class, "reimported.jar")
            .importFrom(exported).as(JavaArchive.class);
        assertTrue(reimported.contains(classPath), "Reimported archive should contain the class");
        assertTrue(reimported.contains("/greeting.txt"), "Reimported archive should contain the asset");
    }

    /**
     * The NIO.2 provider is discovered by the JDK through {@link java.util.ServiceLoader}, so it only
     * works on the module path when {@code impl-base} declares the {@code provides} directive.
     */
    @Test
    void resolvesNio2FileSystemProviderViaServiceLoader() throws Exception {
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "fs.jar")
            .add(new StringAsset("hello"), "greeting.txt");
        final Map<String, Object> env = new HashMap<>();
        env.put(ShrinkWrapFileSystems.FS_ENV_KEY_ARCHIVE, jar);
        try (FileSystem fs = FileSystems.newFileSystem(ShrinkWrapFileSystems.getRootUri(jar), env)) {
            assertEquals("shrinkwrap", fs.provider().getScheme());
        }
    }
}

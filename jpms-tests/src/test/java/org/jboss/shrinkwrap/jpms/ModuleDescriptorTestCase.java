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

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleDescriptor.Exports;
import java.lang.module.ModuleDescriptor.Provides;
import java.lang.module.ModuleDescriptor.Requires;
import java.util.Set;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.jupiter.api.Test;

/**
 * Asserts the shape of the ShrinkWrap module descriptors so that an accidental edit to a
 * {@code module-info.java} - a dropped export, a broadened qualified export, a removed
 * {@code requires transitive}, or a lost {@code provides} - fails the build rather than silently
 * breaking downstream module-path consumers. Complements {@link ModulePathIntegrationTestCase},
 * which exercises the runtime behaviour those descriptors enable.
 *
 * @author <a href="mailto:mjusko@redhat.com">Marek Jusko</a>
 */
class ModuleDescriptorTestCase {

    private static final String API = "org.jboss.shrinkwrap.api";
    private static final String SPI = "org.jboss.shrinkwrap.spi";
    private static final String IMPL = "org.jboss.shrinkwrap.impl.base";

    @Test
    void apiExportsPublicPackagesAndHidesInternal() {
        final ModuleDescriptor api = descriptorOf(API);
        assertEquals(API, api.name());

        // Public API packages downstream consumers depend on
        assertUnqualifiedExport(api, "org.jboss.shrinkwrap.api");
        assertUnqualifiedExport(api, "org.jboss.shrinkwrap.api.asset");
        assertUnqualifiedExport(api, "org.jboss.shrinkwrap.api.exporter");
        assertUnqualifiedExport(api, "org.jboss.shrinkwrap.api.importer");
        assertUnqualifiedExport(api, "org.jboss.shrinkwrap.api.spec");

        // The internal utility package must stay hidden from everyone except impl-base
        final Exports internal = exportOf(api, "org.jboss.shrinkwrap.api.internal");
        assertTrue(internal.isQualified(),
            "org.jboss.shrinkwrap.api.internal must be a qualified export, not public API");
        assertEquals(Set.of(IMPL), internal.targets(),
            "org.jboss.shrinkwrap.api.internal must be exported only to impl-base");
    }

    @Test
    void spiRequiresApiTransitively() {
        final ModuleDescriptor spi = descriptorOf(SPI);
        assertEquals(SPI, spi.name());
        assertUnqualifiedExport(spi, "org.jboss.shrinkwrap.spi");
        assertTrue(isTransitiveRequire(spi, API),
            "spi must 'requires transitive api' so its API-typed signatures are usable");
    }

    @Test
    void implBaseRequiresApiAndSpiTransitively() {
        final ModuleDescriptor impl = descriptorOf(IMPL);
        assertEquals(IMPL, impl.name());
        assertTrue(isTransitiveRequire(impl, API), "impl-base must 'requires transitive api'");
        assertTrue(isTransitiveRequire(impl, SPI), "impl-base must 'requires transitive spi'");
    }

    @Test
    void implBaseProvidesFileSystemProvider() {
        final ModuleDescriptor impl = descriptorOf(IMPL);
        final Provides provides = impl.provides().stream()
            .filter(p -> p.service().equals("java.nio.file.spi.FileSystemProvider"))
            .findFirst()
            .orElseThrow(() -> new AssertionError("impl-base must provide a FileSystemProvider"));
        assertTrue(provides.providers()
            .contains("org.jboss.shrinkwrap.impl.base.nio2.file.ShrinkWrapFileSystemProvider"),
            "The NIO.2 provider must be registered via the 'provides' directive");
    }

    private static ModuleDescriptor descriptorOf(final String moduleName) {
        if (API.equals(moduleName)) {
            // Resolved directly from a known API type to avoid depending on layer lookup for the API
            return ShrinkWrap.class.getModule().getDescriptor();
        }
        final ModuleLayer layer = ModuleDescriptorTestCase.class.getModule().getLayer();
        assertNotNull(layer, "Test module must be executed within a module layer");
        return layer.findModule(moduleName)
            .orElseThrow(() -> new AssertionError("Module not resolved on the module path: " + moduleName))
            .getDescriptor();
    }

    private static Exports exportOf(final ModuleDescriptor descriptor, final String packageName) {
        return descriptor.exports().stream()
            .filter(e -> e.source().equals(packageName))
            .findFirst()
            .orElseThrow(() -> new AssertionError(
                descriptor.name() + " must export " + packageName));
    }

    private static void assertUnqualifiedExport(final ModuleDescriptor descriptor, final String packageName) {
        assertTrue(!exportOf(descriptor, packageName).isQualified(),
            descriptor.name() + " must export " + packageName + " to all modules");
    }

    private static boolean isTransitiveRequire(final ModuleDescriptor descriptor, final String moduleName) {
        return descriptor.requires().stream()
            .anyMatch(r -> r.name().equals(moduleName)
                && r.modifiers().contains(Requires.Modifier.TRANSITIVE));
    }
}

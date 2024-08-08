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
import java.util.Collection;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * ServiceExtensionLoaderTestCase
 *
 * Test to ensure the behaviour of ServiceExtensionLoader
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ServiceExtensionLoaderTestCase {

    @Test
    public void shouldBeAbleToLoadExtension() {
        Extension extension = createLoaderUsingTccl().load(Extension.class,
            ShrinkWrap.create(JavaArchive.class, "test.jar"));

        Assertions.assertNotNull(extension);

        Assertions.assertSame(extension.getClass(), ExtensionImpl.class);
    }

    @Test
    public void shouldBeAbleToOverrideExtension() {
        Extension extension = createLoaderUsingTccl().addOverride(Extension.class, ExtensionImpl2.class).load(
            Extension.class, ShrinkWrap.create(JavaArchive.class, "test.jar"));

        Assertions.assertNotNull(extension);

        Assertions.assertSame(extension.getClass(), ExtensionImpl2.class);
    }

    @Test
    public void shouldBePlacedInCacheAfterLoad() {
        ServiceExtensionLoader loader = createLoaderUsingTccl();
        loader.load(Extension.class, ShrinkWrap.create(JavaArchive.class, "test.jar"));

        Assertions.assertTrue(loader.isCached(Extension.class), "Should be placed in cache");
    }

    @Test
    public void shouldThrowExceptionOnMissingExtension() {
        Assertions.assertThrows(RuntimeException.class,
                () -> createLoaderUsingTccl().load(MissingExtension.class, ShrinkWrap.create(JavaArchive.class, "test.jar")));
    }

    @Test
    public void shouldThrowExceptionOnWrongImplType() {
        Assertions.assertThrows(RuntimeException.class,
                () -> createLoaderUsingTccl().load(WrongImplExtension.class, ShrinkWrap.create(JavaArchive.class, "test.jar")));
    }

    public interface WrongImplExtension extends Assignable {

    }

    public interface Extension extends Assignable {

    }

    public static class ExtensionImpl extends AssignableBase<Archive<?>> implements Extension {
        public ExtensionImpl(Archive<?> archive) {
            super(archive);
        }
    }

    public static class ExtensionImpl2 extends AssignableBase<Archive<?>> implements Extension {
        public ExtensionImpl2(Archive<?> archive) {
            super(archive);
        }
    }

    public interface MissingExtension extends Assignable {

    }

    /**
     * Creates a new {@link ServiceExtensionLoader using the TCCL}
     *
     * @return
     */
    private ServiceExtensionLoader createLoaderUsingTccl() {
        final Collection<ClassLoader> cls = new ArrayList<>(1);
        cls.add(TestSecurityActions.getThreadContextClassLoader());
        return new ServiceExtensionLoader(cls);
    }
}

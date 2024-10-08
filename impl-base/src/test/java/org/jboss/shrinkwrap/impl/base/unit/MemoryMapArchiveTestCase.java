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
package org.jboss.shrinkwrap.impl.base.unit;

import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl;
import org.jboss.shrinkwrap.impl.base.test.ArchiveTestBase;
import org.jboss.shrinkwrap.spi.MemoryMapArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MemoryMapArchiveTestCase
 * <p>
 * TestCase to ensure that the MemoryMapArchive works as expected.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class MemoryMapArchiveTestCase extends ArchiveTestBase<MemoryMapArchive> {
    private MemoryMapArchive archive;

    /**
     * Create a new Archive instance per Test.
     *
     */
    @BeforeEach
    public void createArchive() {
        archive = createNewArchive();
        archive.toString(false);
    }

    @Override
    protected MemoryMapArchive createNewArchive() {
        return new MemoryMapArchiveImpl(ShrinkWrap.getDefaultDomain().getConfiguration());
    }

    /**
     * Return the created instance to the super class, so it can perform the common test cases.
     */
    @Override
    protected MemoryMapArchive getArchive() {
        return archive;
    }

    /**
     * Test to ensure MemoryMap archives can be created with a name
     *
     */
    @Test
    public void testConstructorWithName() {
        String name = "test.jar";
        MemoryMapArchive tmp = new MemoryMapArchiveImpl(name, ShrinkWrap.getDefaultDomain().getConfiguration());
        Assertions.assertEquals(name, tmp.getName(), "Should return the same name as constructor arg");
    }

    /**
     * Test to ensure the MemoryMapArchive requires a name
     *
     */
    @Test
    public void testConstructorRequiresName() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MemoryMapArchiveImpl(null));
    }

    /**
     * Test to ensure the MemoryMapArchive requires a name
     *
     */
    @Test
    public void testConstructorRequiresExtensionLoader() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new MemoryMapArchiveImpl("test.jar", null));
    }

    @Override
    protected ArchiveFormat getExpectedArchiveFormat() {
        return ArchiveFormat.UNKNOWN;
    }
}

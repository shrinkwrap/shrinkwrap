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
package org.jboss.shrinkwrap.impl.base.asset;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * AssetUtilTest
 * <p>
 * Test case to ensure the correctness of the AssetUtil.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class AssetUtilTestCase {
    private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/impl/base/asset/Test.properties";

    @Test
    public void shouldBeAbleToGetPathForClassloaderResource() {
        ArchivePath foundResourcePath = AssetUtil.getPathForClassloaderResource(EXISTING_RESOURCE);

        Assertions.assertEquals(new BasicPath("org/jboss/shrinkwrap/impl/base/asset"), foundResourcePath,
                "The classloader resource path should not contain the file name");
    }

    @Test
    public void shouldBeAbleToGetNameForClassloaderResource() {
        String foundResourceName = AssetUtil.getNameForClassloaderResource(EXISTING_RESOURCE);

        Assertions.assertEquals("Test.properties", foundResourceName,
                "The classloader resource name should not contain the path");
    }

    @Test
    public void shouldBeAbleToGetFullPathForClassResoruce() {
        ArchivePath foundClassResourcePath = AssetUtil.getFullPathForClassResource(this.getClass());

        Assertions.assertEquals(new BasicPath("/org/jboss/shrinkwrap/impl/base/asset/AssetUtilTestCase.class"),
                foundClassResourcePath, "The class resource should have a / delimiter and a .class extension");
    }
}

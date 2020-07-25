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
package org.jboss.shrinkwrap.impl.base.test;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.container.ResourceAdapterContainer;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Test;

public abstract class DynamicResourceAdapterContainerTestBase<T extends Archive<T>> extends DynamicContainerTestBase<T> {

    protected abstract ArchivePath getResourceAdapterPath();

    protected abstract ResourceAdapterContainer<T> getResourceAdapterContainer();

    @Test
    @ArchiveType(ResourceAdapterContainer.class)
    public void testSetResourceAdapterXMLResource() throws Exception {
        getResourceAdapterContainer().setResourceAdapterXML(NAME_TEST_PROPERTIES);

        ArchivePath testPath = new BasicPath(getResourceAdapterPath(), "ra.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceAdapterContainer.class)
    public void testSetResourceAdapterXMLResourceInPackage() throws Exception {
        getResourceAdapterContainer().setResourceAdapterXML(AssetUtil.class.getPackage(), "Test.properties");

        ArchivePath testPath = new BasicPath(getResourceAdapterPath(), "ra.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceAdapterContainer.class)
    public void testSetResourceAdapterXMLFile() throws Exception {
        getResourceAdapterContainer().setResourceAdapterXML(getFileForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getResourceAdapterPath(), "ra.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceAdapterContainer.class)
    public void testSetResourceAdapterXMLURL() throws Exception {
        getResourceAdapterContainer().setResourceAdapterXML(getURLForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getResourceAdapterPath(), "ra.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }

    @Test
    @ArchiveType(ResourceAdapterContainer.class)
    public void testSetResourceAdapterXMLAsset() throws Exception {
        getResourceAdapterContainer().setResourceAdapterXML(getAssetForClassResource(NAME_TEST_PROPERTIES));

        ArchivePath testPath = new BasicPath(getResourceAdapterPath(), "ra.xml");
        Assert.assertTrue("Archive should contain " + testPath, getArchive().contains(testPath));
    }
}

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

import java.util.Arrays;
import java.util.Collection;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.TargetArchiveAwareAsset;
import org.jboss.shrinkwrap.impl.base.NodeImpl;
import org.jboss.shrinkwrap.spi.TargetArchiveAwareAssetArranger;

/**
 * Default implementation of {@link TargetArchiveAwareAssetArranger} that knows
 * to arrange the default Java EE descriptors.
 *
 * @author <a href="mailto:robert.panzer@me.com">Robert Panzer</a>
 */
public class DefaultAssetArranger implements TargetArchiveAwareAssetArranger {

    private static final String META_INF = "META-INF";

    private static final String WEB_INF  = "WEB-INF";

    @Override
    public Node arrange(TargetArchiveAwareAsset asset, Archive<?> archive) {

        if (archive.getName().endsWith(".war")) {
            return arrangeWarDescriptors(asset);
        } else if (archive.getName().endsWith(".rar")) {
            return arrangeRarDescriptors(asset);
        } else if (archive.getName().endsWith(".jar")) {
            return arrangeJarDescriptors(asset);
        } else if (archive.getName().endsWith(".ear")) {
            return arrangeEarDescriptors(asset);
        }
        return null;

    }

    private static final Collection<String> WAR_WEB_INF_DESCRIPTORS =
            Arrays.asList(
                "web.xml",
                "ejb-jar.xml",
                "faces-config.xml",
                "beans.xml");

    private static final Collection<String> WAR_META_INF_DESCRIPTORS =
            Arrays.asList(
                "MANIFEST.MF");

    private Node arrangeWarDescriptors(TargetArchiveAwareAsset asset) {
        if (WAR_WEB_INF_DESCRIPTORS.contains(asset.getName())) {
            return new NodeImpl(ArchivePaths.create(WEB_INF, asset.getName()), asset);
        } else if (WAR_META_INF_DESCRIPTORS.contains(asset.getName())) {
            return new NodeImpl(ArchivePaths.create(META_INF, asset.getName()), asset);
        }
        return null;
    }

    private static final Collection<String> RAR_META_INF_DESCRIPTORS =
            Arrays.asList(
                "ra.xml",
                "beans.xml",
                "MANIFEST.MF");

    private Node arrangeRarDescriptors(TargetArchiveAwareAsset asset) {
        if (RAR_META_INF_DESCRIPTORS.contains(asset.getName())) {
            return new NodeImpl(ArchivePaths.create(META_INF, asset.getName()), asset);
        }
        return null;
    }

    private static final Collection<String> JAR_META_INF_DESCRIPTORS =
            Arrays.asList(
                "ejb-jar.xml",
                "ra.xml",
                "web-fragment.xml",
                "faces-config.xml",
                "MANIFEST.MF");

    private Node arrangeJarDescriptors(TargetArchiveAwareAsset asset) {
        if (JAR_META_INF_DESCRIPTORS.contains(asset.getName())) {
            return new NodeImpl(ArchivePaths.create(META_INF, asset.getName()), asset);
        }
        return null;
    }

    private static final Collection<String> EAR_META_INF_DESCRIPTORS =
            Arrays.asList(
                "application.xml",
                "MANIFEST.MF");

    private Node arrangeEarDescriptors(TargetArchiveAwareAsset asset) {
        if (EAR_META_INF_DESCRIPTORS.contains(asset.getName())) {
            return new NodeImpl(ArchivePaths.create(META_INF, asset.getName()), asset);
        }
        return null;
    }

}

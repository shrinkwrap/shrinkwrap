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
package org.jboss.shrinkwrap.spi;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.TargetArchiveAwareAsset;
/**
 * Implementations "know" where TargetArchiveAwareAssets are located in the archive.
 * For example the default implementation knows that a beans.xml is located at WEB-INF
 * in a war file and at META-INF in other jar files.
 *
 * @author <a href="mailto:robert.panzer@me.com">Robert Panzer</a>
 */
public interface TargetArchiveAwareAssetArranger {

    /**
     * Creates a node with the correct path for the specified asset and archive.
     * E.g. for an asset with name beans.xml and an archive with a name ending
     * on .war it should return a {@link Node} with path WEB-INF/beans.xml and
     * the given asset as the nodes asset.
     * @param asset
     * @param archive
     * @return The {@link Node} having the archive path and asset to be added to
     * the archive.
     */
    Node arrange(TargetArchiveAwareAsset asset, Archive<?> archive);

}

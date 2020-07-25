/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api;

import java.util.Set;

import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Represents an entry inside an {@link Archive}. Indicates an empty directory if {@link Node#getAsset()} returns null. May be
 * the parent of child {@link Node}s. Lives inside the {@link Archive} under the context denoted by
 * {@link Node#getPath()}.
 *
 * @author <a href="mailto:german.escobarc@gmail.com">German Escobar</a>
 */
public interface Node {

    /**
     * @return The {@link Asset} this node holds, null if it is an empty directory
     */
    Asset getAsset();

    /**
     * @return The child nodes of this node or, an empty set if it has no children or holds an asset. This method will
     *         never return null. The returned Set will be an immutable view.
     */
    Set<Node> getChildren();

    /**
     * @return The path where this node is placed within the {@link Archive}
     */
    ArchivePath getPath();

}

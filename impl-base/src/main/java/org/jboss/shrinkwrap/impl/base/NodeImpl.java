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
package org.jboss.shrinkwrap.impl.base;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * The default implementation of {@link Node}
 *
 * @author <a href="mailto:german.escobarc@gmail.com">German Escobar</a>
 */
public class NodeImpl implements Node {

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * The path of this node inside the {@link Archive}
     */
    private ArchivePath path;

    /**
     * The asset this node holds.
     */
    private Asset asset;

    /**
     * The children nodes.
     */
    private Set<Node> children = Collections.synchronizedSet(new LinkedHashSet<Node>());

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Constructor
     *
     * This constructor will create a directory Node with the specified path.
     *
     * @param path
     *            The {@link ArchivePath} this Node is placed within the {@link Archive}
     */
    public NodeImpl(ArchivePath path) {
        this(path, null);
    }

    /**
     * Constructor
     *
     * This constructor will create an asset Node with the specified path.
     *
     * @param path
     *            The {@link ArchivePath} this Node is placed within the {@link Archive}
     * @param asset
     *            The {@link Asset} that this Node holds.
     */
    public NodeImpl(ArchivePath path, Asset asset) {
        Validate.notNull(path, "Path was not specified");

        this.path = path;
        this.asset = asset;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.Node#getPath()
     */
    @Override
    public ArchivePath getPath() {
        return path;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.Node#getAsset()
     */
    @Override
    public Asset getAsset() {
        return this.asset;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.Node#getChildren()
     */
    @Override
    public Set<Node> getChildren() {
        return Collections.unmodifiableSet(this.children);
    }

    /**
     * Adds a child to the Set of nodes. If already exists, nothing happens.
     *
     * @param node
     *            The Node that will be added as a child
     */
    public void addChild(Node node) {
        Validate.notNull(node, "No node was specified");

        children.add(node);
    }

    /**
     * Removes a child from the Set of nodes. If it doesn't exists, nothing happens.
     *
     * @param node
     *            The Node that will be removed from the childs
     */
    public void removeChild(Node node) {
        Validate.notNull(node, "No node was specified");

        children.remove(node);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Node) {
            Node node = (Node) obj;
            if (path.equals(node.getPath())) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.path.hashCode();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.path.get();
    }

}

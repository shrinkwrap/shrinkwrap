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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ArchiveEvent;
import org.jboss.shrinkwrap.api.Configuration;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.IllegalArchivePathException;
import org.jboss.shrinkwrap.api.ArchiveEventHandler;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;

/**
 * MemoryMapArchiveBase
 *
 * A base implementation for all MemoryMap archives. Thread-safe.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class MemoryMapArchiveBase<T extends Archive<T>> extends ArchiveBase<T> implements Archive<T> {

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Storage for the {@link Node}s.
     */
    private final Map<ArchivePath, NodeImpl> content = new ConcurrentHashMap<ArchivePath, NodeImpl>();

    /**
     * Storage for the {@link ArchiveAsset}s. Used to help get access to nested archive content.
     */
    private final Map<ArchivePath, ArchiveAsset> nestedArchives = new ConcurrentHashMap<ArchivePath, ArchiveAsset>();

    private List<ArchiveEventHandler> handlers = new ArrayList<ArchiveEventHandler>();

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Constructor
     *
     * This constructor will generate a unique {@link Archive#getName()} per instance.
     *
     * @param configuration
     *            The configuration for this archive
     * @throws IllegalArgumentException
     *             If the configuration is not specified
     */
    public MemoryMapArchiveBase(final Configuration configuration) throws IllegalArgumentException {
        this("Archive-" + UUID.randomUUID().toString() + ".jar", configuration);
    }

    /**
     * Constructor
     *
     * This constructor will generate an {@link Archive} with the provided name.
     *
     * @param archiveName
     * @param configuration
     *            The configuration for this archive
     * @throws IllegalArgumentException
     *             If the name or configuration is not specified
     */
    public MemoryMapArchiveBase(final String archiveName, final Configuration configuration)
        throws IllegalArgumentException {
        super(archiveName, configuration);

        // Add the root node to the content
        final ArchivePath rootPath = new BasicPath("/");
        content.put(rootPath, new NodeImpl(rootPath));
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - Archive -------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset,
     *      org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T add(Asset asset, ArchivePath path) {
        Validate.notNull(asset, "No asset was specified");
        Validate.notNull(path, "No path was specified");

        return addAsset(path, asset);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Archive, java.lang.String, java.lang.Class)
     */
    @Override
    public T add(final Archive<?> archive, final String path, final Class<? extends StreamExporter> exporter) {
        Validate.notNull(archive, "Archive must be specified");
        Validate.notNullOrEmpty(path, "Archive Path must be specified");
        Validate.notNull(exporter, "exporter must be specified");
        return this.add(archive, ArchivePaths.create(path), exporter);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.ArchiveBase#add(org.jboss.shrinkwrap.api.Archive,
     *      org.jboss.shrinkwrap.api.ArchivePath, java.lang.Class)
     */
    @Override
    public T add(final Archive<?> archive, final ArchivePath path, final Class<? extends StreamExporter> exporter) {
        // Add archive asset
        super.add(archive, path, exporter);

        // Expected Archive Path
        final ArchivePath archivePath = new BasicPath(path, archive.getName());

        // Get the Asset that was just added
        final Node node = get(archivePath);

        // Make sure it is an ArchiveAsset
        if (node.getAsset() != null && node.getAsset() instanceof ArchiveAsset) {
            final ArchiveAsset archiveAsset = ArchiveAsset.class.cast(node.getAsset());
            // Add asset to ArchiveAsset Map
            nestedArchives.put(archivePath, archiveAsset);
        }

        return covariantReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectory(org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T addAsDirectory(final ArchivePath path) throws IllegalArgumentException {
        // Precondition check
        Validate.notNull(path, "path must be specified");

        // Adjust the path to remove any trailing slash
        ArchivePath adjustedPath = new BasicPath(PathUtil.optionallyRemoveFollowingSlash(path.get()));
        return addAsset(adjustedPath, null);
    }

    private T addAsset(ArchivePath path, Asset asset) {
       Asset handledAsset = invokeHandlers(path, asset);

       // Check if it exists. If it doesn't, create it and add it.
       if (!contains(path)) {
          // Add the node to the content of the archive
          NodeImpl node = new NodeImpl(path, handledAsset);
          content.put(path, node);

          // Add the new node to the parent as a child
          NodeImpl parentNode = obtainParent(path.getParent());
          if (parentNode != null) {
             parentNode.addChild(node);
          }
       }
       return covariantReturn();
    }

    /**
     * {@inheritDoc}
     * @see org.jboss.shrinkwrap.api.Archive#addListener(org.jboss.shrinkwrap.api.Filter, org.jboss.shrinkwrap.api.ArchiveEventHandler)
     */
    @Override
    public T addHandlers(ArchiveEventHandler... handlers) {
       for (ArchiveEventHandler handler : handlers) {
          this.handlers.add(handler);
       }
       return covariantReturn();
    }

    private Asset invokeHandlers(ArchivePath path, Asset asset) {
       final ArchiveEvent event = new ArchiveEvent(path, asset);
       for (ArchiveEventHandler handler : handlers) {
         handler.handle(event);
       }
       return event.getHandledAsset();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#contains(org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public boolean contains(ArchivePath path) {
        Validate.notNull(path, "No path was specified");

        boolean found = content.containsKey(path);
        if (!found) {
            found = nestedContains(path);
        }
        return found;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#contains(java.lang.String)
     */
    @Override
    public boolean contains(final String path) throws IllegalArgumentException {
        Validate.notNull(path, "Path must be specified");
        final ArchivePath archivePath = ArchivePaths.create(path);
        return this.contains(archivePath);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#delete(org.jboss.declarchive.api.ArchivePath)
     */
    @Override
    public Node delete(ArchivePath path) {
        Validate.notNull(path, "No path was specified");

        final Node node = content.get(path);
        if (node == null) {
            return null;
        }

        final NodeImpl parentNode = content.get(path.getParent());
        if (parentNode != null) {
            parentNode.removeChild(node);
        }

        return content.remove(path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#delete(java.lang.String)
     */
    @Override
    public Node delete(String archivePath) {
        Validate.notNull(archivePath, "No path was specified");
        return delete(ArchivePaths.create(archivePath));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#get(org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public Node get(ArchivePath path) {
        Validate.notNull(path, "No path was specified");
        Node node = content.get(path);
        if (node == null && contains(path)) {
            node = getNestedNode(path);
        }
        return node;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getContent()
     */
    @Override
    public Map<ArchivePath, Node> getContent() {
        Map<ArchivePath, Node> ret = new HashMap<ArchivePath, Node>();
        for (Map.Entry<ArchivePath, NodeImpl> item : content.entrySet()) {
            if (!item.getKey().equals(new BasicPath("/"))) {
                ret.put(item.getKey(), item.getValue());
            }
        }

        return Collections.unmodifiableMap(ret);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getContent(org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public Map<ArchivePath, Node> getContent(Filter<ArchivePath> filter) {
        Validate.notNull(filter, "Filter must be specified");

        Map<ArchivePath, Node> filteredContent = new HashMap<ArchivePath, Node>();
        for (Map.Entry<ArchivePath, NodeImpl> contentEntry : content.entrySet()) {
            if (filter.include(contentEntry.getKey())) {
                if (!contentEntry.getKey().equals(new BasicPath("/"))) {
                    filteredContent.put(contentEntry.getKey(), contentEntry.getValue());
                }
            }
        }
        return filteredContent;
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Check to see if a path is found in a nested archive
     */
    private boolean nestedContains(ArchivePath path) {
        // Iterate through nested archives
        for (Entry<ArchivePath, ArchiveAsset> nestedArchiveEntry : nestedArchives.entrySet()) {
            ArchivePath archivePath = nestedArchiveEntry.getKey();
            ArchiveAsset archiveAsset = nestedArchiveEntry.getValue();

            // Check to see if the requested path starts with the nested archive path
            if (startsWith(path, archivePath)) {
                Archive<?> nestedArchive = archiveAsset.getArchive();

                // Get the asset path from within the nested archive
                ArchivePath nestedAssetPath = getNestedPath(path, archivePath);

                // Recurse the call to the nested archive
                return nestedArchive.contains(nestedAssetPath);
            }
        }
        return false;
    }

    /**
     * Attempt to get the asset from a nested archive.
     *
     * @param path
     * @return
     */
    private Node getNestedNode(ArchivePath path) {
        // Iterate through nested archives
        for (Entry<ArchivePath, ArchiveAsset> nestedArchiveEntry : nestedArchives.entrySet()) {
            ArchivePath archivePath = nestedArchiveEntry.getKey();
            ArchiveAsset archiveAsset = nestedArchiveEntry.getValue();

            // Check to see if the requested path starts with the nested archive path
            if (startsWith(path, archivePath)) {
                Archive<?> nestedArchive = archiveAsset.getArchive();

                // Get the asset path from within the nested archive
                ArchivePath nestedAssetPath = getNestedPath(path, archivePath);

                // Recurse the call to the nested archive
                return nestedArchive.get(nestedAssetPath);
            }
        }
        return null;
    }

    /**
     * Check to see if one path starts with another
     *
     * @param fullPath
     * @param startingPath
     * @return
     */
    private boolean startsWith(ArchivePath fullPath, ArchivePath startingPath) {
        final String context = fullPath.get();
        final String startingContext = startingPath.get();

        return context.startsWith(startingContext);
    }

    /**
     * Given a full path and a base path return a new path containing the full path with the base path removed from the
     * beginning.
     *
     * @param fullPath
     * @param basePath
     * @return
     */
    private ArchivePath getNestedPath(ArchivePath fullPath, ArchivePath basePath) {
        final String context = fullPath.get();
        final String baseContent = basePath.get();

        // Remove the base path from the full path
        String nestedArchiveContext = context.substring(baseContent.length());

        return new BasicPath(nestedArchiveContext);
    }

    /**
     * Used to retrieve a {@link Node} from the content of the {@link Archive}. If the {@link Node} doesn�t exists in
     * the specified location, it is created and added to the {@link Archive}. The same happens to all its non-existing
     * parents. However, if the {@link Node} is an asset, an IllegalArchivePathException is thrown.
     *
     * @param path
     *            The {@link ArchivePath} from which we are obtaining the {@link Node}
     * @return The {@link Node} in the specified path
     * @throws IllegalArchivePathException
     *             if the node is an {@link Asset}
     */
    private NodeImpl obtainParent(ArchivePath path) {
        if (path == null) {
            return null;
        }

        NodeImpl node = content.get(path);

        // If the node exists, just return it
        if (node != null) {
            // if the node is an asset, throw an exception
            if (node.getAsset() != null) {
                throw new IllegalArchivePathException("Could not create node under " + path.getParent()
                    + ". It points to an asset.");
            }

            return node;
        }

        // If the node doesn't exists, create it. Also create all possible non-existing
        // parents
        node = new NodeImpl(path);
        NodeImpl parentNode = obtainParent(path.getParent());

        if (parentNode != null) {
            parentNode.addChild(node);
        }

        // Add the node to the contents of the archive
        content.put(path, node);

        return node;
    }
}

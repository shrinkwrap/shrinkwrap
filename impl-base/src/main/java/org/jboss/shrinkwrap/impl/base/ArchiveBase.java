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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.Configuration;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.IllegalArchivePathException;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.importer.ArchiveImportException;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.spi.ArchiveFormatAssociable;
import org.jboss.shrinkwrap.spi.Configurable;

/**
 * Base implementation of {@link Archive}. Contains support for operations (typically overloaded) that are not specific
 * to any particular storage implementation, and may be delegated to other forms.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public abstract class ArchiveBase<T extends Archive<T>> implements Archive<T>, Configurable, ArchiveFormatAssociable {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ArchiveBase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Name of the archive
     */
    private final String name;

    /**
     * Configuration for this archive
     */
    private final Configuration configuration;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Constructor
     *
     * Creates a new Archive with the specified name
     *
     * @param name
     *            Name of the archive
     * @param configuration
     *            The configuration for this archive
     * @throws IllegalArgumentException
     *             If the name was not specified
     */
    protected ArchiveBase(final String name, final Configuration configuration) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNullOrEmpty(name, "name must be specified");
        Validate.notNull(configuration, "configuration must be specified");

        // Set
        this.name = name;
        this.configuration = configuration;
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.spi.ArchiveFormatAssociable#getArchiveFormat()
     */
    @Override
    public ArchiveFormat getArchiveFormat() {
        return ArchiveFormat.UNKNOWN;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(java.lang.String, org.jboss.shrinkwrap.api.asset.Asset)
     */
    @Override
    public T add(final Asset asset, final String target) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNullOrEmpty(target, "target must be specified");
        Validate.notNull(asset, "asset must be specified");

        // Make a Path from the target
        final ArchivePath path = new BasicPath(target);

        // Delegate
        return this.add(asset, path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public T add(final Asset asset, final String target, final String name) throws IllegalArgumentException {
        Validate.notNull(target, "target must be specified");
        final ArchivePath path = ArchivePaths.create(target);
        return this.add(asset, path, name);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.ArchivePath, java.lang.String,
     *      org.jboss.shrinkwrap.api.asset.Asset)
     */
    @Override
    public T add(final Asset asset, final ArchivePath path, final String name) {
        // Precondition checks
        Validate.notNull(path, "No path was specified");
        Validate.notNullOrEmpty(name, "No target name name was specified");
        Validate.notNull(asset, "No asset was was specified");

        // Make a relative path
        final ArchivePath resolvedPath = new BasicPath(path, name);

        // Delegate
        return this.add(asset, resolvedPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#get(java.lang.String)
     */
    @Override
    public Node get(final String path) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNullOrEmpty(path, "No path was specified");

        // Make a Path
        final ArchivePath realPath = new BasicPath(path);

        // Delegate
        return get(realPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, java.lang.String)
     */
    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, String path) {
        Validate.notNull(path, "Path must be specified");
        return getAsType(type, ArchivePaths.create(path));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public <X extends Archive<X>> Collection<X> getAsType(Class<X> type, Filter<ArchivePath> filter) {
        Validate.notNull(type, "Type must be specified");
        Validate.notNull(filter, "Filter must be specified");

        Collection<X> archives = new ArrayList<X>();

        Map<ArchivePath, Node> matches = getContent(filter);
        for (ArchivePath path : matches.keySet()) {
            archives.add(getAsType(type, path));
        }
        return archives;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, ArchivePath path) {
        Validate.notNull(type, "Type must be specified");
        Validate.notNull(path, "ArchivePath must be specified");

        Node content = get(path);
        if (content == null) {
            return null;
        }
        Asset asset = content.getAsset();
        if (asset == null) {
            return null;
        }

        if (asset instanceof ArchiveAsset) {
            ArchiveAsset archiveAsset = (ArchiveAsset) asset;
            return archiveAsset.getArchive().as(type);
        }

        ArchiveFormat archiveFormat = this.configuration.getExtensionLoader()
            .getArchiveFormatFromExtensionMapping(type);
        return getAsType(type, path, archiveFormat);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, java.lang.String,
     *      org.jboss.shrinkwrap.api.ArchiveFormat)
     */
    @Override
    public <X extends Archive<X>> X getAsType(final Class<X> type, final String path, final ArchiveFormat archiveFormat) {
        Validate.notNull(path, "ArchiveFormat must be specified");

        return getAsType(type, ArchivePaths.create(path), archiveFormat);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.ArchivePath,
     *      org.jboss.shrinkwrap.api.ArchiveFormat)
     */
    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, ArchivePath path, final ArchiveFormat archiveFormat) {
        Validate.notNull(type, "Type must be specified");
        Validate.notNull(path, "ArchivePath must be specified");
        Validate.notNull(archiveFormat, "ArchiveFormat must be specified");

        // Get stream importer/exporter bindings for specified format
        final ArchiveFormatStreamBindings formatBinding = new ArchiveFormatStreamBindings(archiveFormat);

        Node content = get(path);
        if (content == null) {
            return null;
        }

        Asset asset = content.getAsset();
        if (asset == null) {
            return null;
        }

        InputStream stream = null;
        try {
            stream = asset.openStream();
            X archive = ShrinkWrap.create(formatBinding.getImporter(), path.get()).importFrom(stream).as(type);
            delete(path);
            add(new ArchiveAsset(archive, formatBinding.getExporter()), path);

            return archive;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    throw new ArchiveImportException("Stream not closed after import", e);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.Filter,
     *      org.jboss.shrinkwrap.api.ArchiveFormat)
     */
    @Override
    public <X extends Archive<X>> Collection<X> getAsType(Class<X> type, Filter<ArchivePath> filter,
        final ArchiveFormat archiveFormat) {
        Validate.notNull(type, "Type must be specified");
        Validate.notNull(filter, "Filter must be specified");
        Validate.notNull(archiveFormat, "ArchiveFormat must be specified");

        Collection<X> archives = new ArrayList<X>();

        Map<ArchivePath, Node> matches = getContent(filter);
        for (ArchivePath path : matches.keySet()) {
            archives.add(getAsType(type, path, archiveFormat));
        }
        return archives;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.ArchivePath,
     *      java.lang.Class)
     */
    @Override
    public T add(final Archive<?> archive, final ArchivePath path, Class<? extends StreamExporter> exporter) {
        // Precondition checks
        Validate.notNull(path, "No path was specified");
        Validate.notNull(archive, "No archive was specified");
        Validate.notNull(exporter, "No exporter was specified");

        // Make a Path
        final String archiveName = archive.getName();
        final ArchivePath contentPath = new BasicPath(path, archiveName);

        // Create ArchiveAsset
        final ArchiveAsset archiveAsset = new ArchiveAsset(archive, exporter);

        // Delegate
        return add(archiveAsset, contentPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(NamedAsset))
     */
    @Override
    public T add(NamedAsset namedAsset) {

        Validate.notNull(namedAsset, "No named asset was specified");

        return add(namedAsset, namedAsset.getName());

    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectory(java.lang.String)
     */
    @Override
    public T addAsDirectory(final String path) throws IllegalArgumentException {
        // Precondition check
        Validate.notNullOrEmpty(path, "path must be specified");

        // Delegate and return
        return this.addAsDirectory(ArchivePaths.create(path));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectories(org.jboss.shrinkwrap.api.ArchivePath[])
     */
    @Override
    public T addAsDirectories(final ArchivePath... paths) throws IllegalArgumentException {
        // Precondition check
        Validate.notNull(paths, "paths must be specified");

        // Add
        for (final ArchivePath path : paths) {
            this.addAsDirectory(path);
        }

        // Return
        return covariantReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectories(java.lang.String[])
     */
    @Override
    public T addAsDirectories(final String... paths) throws IllegalArgumentException {
        // Precondition check
        Validate.notNull(paths, "paths must be specified");

        // Represent as array of Paths
        final Collection<ArchivePath> pathsCollection = new ArrayList<ArchivePath>(paths.length);
        for (final String path : paths) {
            pathsCollection.add(ArchivePaths.create(path));
        }

        // Delegate and return
        return this.addAsDirectories(pathsCollection.toArray(new ArchivePath[] {}));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getName()
     */
    public final String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive)
     */
    @Override
    public T merge(final Archive<?> source) throws IllegalArgumentException {
        return merge(source, new BasicPath());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public T merge(Archive<?> source, Filter<ArchivePath> filter) throws IllegalArgumentException {
        return merge(source, new BasicPath(), filter);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.ArchivePath,
     *      org.jboss.shrinkwrap.api.Archive)
     */
    @Override
    public T merge(final Archive<?> source, final ArchivePath path) throws IllegalArgumentException {
        Validate.notNull(source, "No source archive was specified");
        Validate.notNull(path, "No path was specified");

        return merge(source, path, Filters.includeAll());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, java.lang.String,
     *      org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public T merge(final Archive<?> source, final String path, final Filter<ArchivePath> filter)
        throws IllegalArgumentException {
        Validate.notNull(path, "path must be specified");
        return this.merge(source, ArchivePaths.create(path), filter);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, java.lang.String)
     */
    @Override
    public T merge(final Archive<?> source, final String path) throws IllegalArgumentException {
        Validate.notNull(path, "path must be specified");
        return this.merge(source, ArchivePaths.create(path));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.Path,
     *      org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public T merge(Archive<?> source, ArchivePath path, Filter<ArchivePath> filter) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNull(source, "No source archive was specified");
        Validate.notNull(path, "No path was specified");
        Validate.notNull(filter, "No filter was specified");

        // Get existing contents from source archive
        final Map<ArchivePath, Node> sourceContent = source.getContent();
        Validate.notNull(sourceContent, "Source archive content can not be null.");

        // Add each asset from the source archive
        for (final Entry<ArchivePath, Node> contentEntry : sourceContent.entrySet()) {
            final Node node = contentEntry.getValue();
            ArchivePath nodePath = new BasicPath(path, contentEntry.getKey());
            if (!filter.include(nodePath)) {
                continue;
            }
            // Delegate
            if (node.getAsset() == null) {
                addAsDirectory(nodePath);
            } else {
                add(node.getAsset(), nodePath);
            }
        }
        return covariantReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#move(org.jboss.shrinkwrap.api.ArchivePath, org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T move(ArchivePath source, ArchivePath target) throws IllegalArgumentException, IllegalArchivePathException {
        Validate.notNull(source, "The source path was not specified");
        Validate.notNull(target, "The target path was not specified");

        final Node nodeToMove = get(source);
        if (null == nodeToMove) {
           throw new IllegalArchivePathException(source.get() + " doesn't specify any node in the archive to move");
        }
        add(nodeToMove.getAsset(), target);
        delete(source);

        return covariantReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#move(java.lang.String, java.lang.String)
     */
    @Override
    public T move(String source, String target) throws IllegalArgumentException, IllegalArchivePathException {
        Validate.notNullOrEmpty(source, "The source path was not specified");
        Validate.notNullOrEmpty(target, "The target path was not specified");

        final ArchivePath sourcePath = new BasicPath(source);
        final ArchivePath targetPath = new BasicPath(target);

        return move(sourcePath, targetPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Assignable#as(java.lang.Class)
     */
    @Override
    public <TYPE extends Assignable> TYPE as(final Class<TYPE> clazz) {
        Validate.notNull(clazz, "Class must be specified");

        return this.configuration.getExtensionLoader().load(clazz, this);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#toString()
     */
    @Override
    public String toString() {
        return this.toString(Formatters.SIMPLE);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#toString(boolean)
     */
    @Override
    public String toString(final boolean verbose) {
        return verbose ? this.toString(Formatters.VERBOSE) : this.toString();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#toString(org.jboss.shrinkwrap.api.formatter.Formatter)
     */
    @Override
    public String toString(final Formatter formatter) throws IllegalArgumentException {
        // Precondition check
        if (formatter == null) {
            throw new IllegalArgumentException("Formatter must be specified");
        }

        // Delegate
        return formatter.format(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(final OutputStream outputStream, final Formatter formatter) throws IllegalArgumentException {
        try {
            IOUtil.bufferedWriteWithFlush(outputStream, toString(formatter).getBytes());
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Could not write Archive contents to specified OutputStream", ioe);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getContent() == null) ? 0 : getContent().hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ArchiveBase)) {
            return false;
        }

        ArchiveBase<?> other = (ArchiveBase<?>) obj;

        if (getContent() == null) {
            if (other.getContent() != null) {
                return false;
            }
        } else if (!getContent().equals(other.getContent())) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.spi.Configurable#getConfiguration()
     */
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Returns the actual typed class for this instance, used in safe casting for covariant return types
     *
     * @return
     */
    protected abstract Class<T> getActualClass();

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Provides typesafe covariant return of this instance
     */
    protected final T covariantReturn() {
        try {
            return this.getActualClass().cast(this);
        } catch (final ClassCastException cce) {
            log.log(Level.SEVERE,
                "The class specified by getActualClass is not a valid assignment target for this instance;"
                    + " developer error");
            throw cce;
        }
    }
}

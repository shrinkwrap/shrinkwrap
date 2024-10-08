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
package org.jboss.shrinkwrap.api;

import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.formatter.Formatters;

/**
 * Represents a collection of resources which may be constructed programmatically. In effect this represents a virtual
 * filesystem.
 * <br />
 * <br />
 * All {@link Archive} types support the addition of {@link Node}s under a designated {@link ArchivePath} (context). The
 * contents of a {@link Node} are either a directory or {@link Asset}.
 * <br />
 * <br />
 * {@link Archive}s are generally created via an {@link ArchiveFactory} or via the default configuration shortcut
 * {@link ShrinkWrap} utility class.
 * <br />
 * <br />
 * Because {@link Archive}s are {@link Assignable}, they may be wrapped in another user "view" used to perform
 * operations like adding JavaEE Spec-specific resources or exporting in ZIP format.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface Archive<T extends Archive<T>> extends Assignable {
    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains the name of this archive (i.e. myLibrary.jar)
     *
     * @return The name of the archive
     */
    String getName();

    /**
     * Obtains a globally-unique identifier for this {@link Archive}
     *
     * @return The globally-unique identifier for the archive
     */
    String getId();

    /**
     * Adds the specified asset under the specified path into the target context
     *
     * @param asset
     *            The asset to add
     * @param target
     *            The context under which to add the asset
     * @return This archive
     * @throws IllegalArgumentException
     *             If no target or asset were specified
     * @throws IllegalArchivePathException
     *             If the target is invalid.
     */
    T add(Asset asset, ArchivePath target) throws IllegalArgumentException;

    /**
     * Adds the specified asset under the specified target (directory) using the specified name. The resultant path will
     * treat the specified path as a prefix namespace, then append the name.
     *
     * @param asset
     *            The asset to add
     * @param target
     *            The context directory under which to add the asset
     * @param name
     *            The name to assign the asset under the target namespace
     * @return This archive
     * @throws IllegalArgumentException
     *             If the target, name, or asset was not specified
     * @throws IllegalArchivePathException
     *             If the target is invalid.
     */
    T add(Asset asset, ArchivePath target, String name) throws IllegalArgumentException;

    /**
     * Adds the specified asset under the specified target (directory) using the specified name. The resultant path will
     * treat the specified path as a prefix namespace, then append the name.
     *
     * @param asset
     *            The asset to add
     * @param target
     *            The context directory under which to add the asset
     * @param name
     *            The name to assign the asset under the target namespace
     * @return This archive
     * @throws IllegalArgumentException
     *             If the target, name, or asset was not specified
     * @throws IllegalArchivePathException
     *             If the target is invalid.
     */
    T add(Asset asset, String target, String name) throws IllegalArgumentException;

    /**
     * Adds the asset encapsulated within the specified {@link NamedAsset} under the encapsulated name and target
     * (directory)
     *
     * @param namedAsset
     *            The named asset to add
     * @return This archive
     * @throws IllegalArgumentException
     *             If either the target or asset is not specified
     * @throws IllegalArchivePathException
     *             If the target is invalid.
     */
    T add(NamedAsset namedAsset) throws IllegalArgumentException;

    /**
     * Adds the specified asset under the context denoted by the specified target
     *
     * @param asset
     *            The asset to add
     * @param target
     *            The context under which to add the asset
     * @return This archive
     * @throws IllegalArgumentException
     *             If either the target or asset is not specified
     * @throws IllegalArchivePathException
     *             If the target is invalid.
     */
    T add(Asset asset, String target) throws IllegalArgumentException;

    /**
     * Adds the specified directory.
     *
     * @param path
     *            The path to add
     * @return This archive
     * @throws IllegalArgumentException
     *             If no path was specified
     * @throws IllegalArchivePathException
     *             If the path is invalid.
     */
    T addAsDirectory(String path) throws IllegalArgumentException;

    /**
     * Adds the specified directories.
     *
     * @param paths
     *            The paths to add
     * @return This archive
     * @throws IllegalArgumentException
     *             If no paths were specified
     * @throws IllegalArchivePathException
     *             If at least one path is invalid.
     */
    T addAsDirectories(String... paths) throws IllegalArgumentException;

    /**
     * Adds the specified directory.
     *
     * @param path
     *            The path to add
     * @return This archive
     * @throws IllegalArgumentException
     *             If no path was specified
     * @throws IllegalArchivePathException
     *             If the path is invalid.
     */
    T addAsDirectory(ArchivePath path) throws IllegalArgumentException;

    /**
     * Adds the specified directories.
     *
     * @param paths
     *            The paths to add
     * @return This archive
     * @throws IllegalArgumentException
     *             If no paths were specified
     * @throws IllegalArchivePathException
     *             If at least one path is invalid.
     */
    T addAsDirectories(ArchivePath... paths) throws IllegalArgumentException;

    /**
     * Adds an array of handlers for callback-based operations.
     *
     * @param handlers
     *            The handlers to be added for callback
     * @return This archive
     */
    T addHandlers(ArchiveEventHandler... handlers);

    /**
     * Obtains the {@link Node} located at the specified path
     *
     * @param path
     *            The path to the node within the archive
     * @return The {@link Node}, or null if nothing is found at the specified path
     * @throws IllegalArgumentException
     *             If the path is not specified
     */
    Node get(ArchivePath path) throws IllegalArgumentException;

    /**
     * Obtains the {@link Node} located at the specified path
     *
     * @param path
     *            The path to the node within the archive
     * @return The {@link Node}, or null if nothing is found at the Path
     * @throws IllegalArgumentException
     *             If the path is not specified
     */
    Node get(String path) throws IllegalArgumentException;

    /**
     * Get a nested {@link Archive} as a specific type.<br/>
     * <br/>
     *
     * The found Archives must have been added as an Archive, no import is performed.
     *
     * @param <X>
     *            The type of archive
     * @param type
     *            The Type to return the Archive as
     * @param path
     *            The location of the Archive
     * @return The found Archive as given type or null if none found at given path
     * @throws IllegalArgumentException
     *             if found {@link Asset} is not pointing to a {@link Archive}
     * @see Archive#getAsType(Class, ArchivePath)
     * @see Archive#add(Archive, ArchivePath, Class)
     * @see Archive#add(Archive, String, Class)
     */
    <X extends Archive<X>> X getAsType(Class<X> type, String path);

    /**
     * Get a nested {@link Archive} as a specific type.<br/>
     * <br/>
     *
     * The found Archives must have been added as an Archive, no import is performed.
     *
     * @param <X>
     *            The type of archive
     * @param type
     *            The Type to return the Archive as
     * @param path
     *            The location of the Archive
     * @return The found Archive as given type or null if none found at given {@link ArchivePath}
     * @throws IllegalArgumentException
     *             if found {@link Asset} is not pointing to a {@link Archive}
     * @see Archive#add(Archive, ArchivePath, Class)
     * @see Archive#add(Archive, String, Class)
     */
    <X extends Archive<X>> X getAsType(Class<X> type, ArchivePath path);

    /**
     * Get all nested {@link Archive} matching the filter as a specific type.<br/>
     * <br/>
     *
     * The found Archives must have been added as an Archive, no import is performed.
     *
     * @param <X>
     *            The type of archive
     * @param type
     *            The Type to return the Archive as
     * @param filter
     *            Filter to match result
     * @return A {@link Collection} of found Archives matching given filter or empty {@link Collection} if non found.
     * @throws IllegalArgumentException
     *             if found {@link Asset} is not pointing to a {@link Archive}
     * @see Archive#getAsType(Class, ArchivePath)
     * @see Archive#add(Archive, ArchivePath, Class)
     * @see Archive#add(Archive, String, Class)
     */
    <X extends Archive<X>> Collection<X> getAsType(Class<X> type, Filter<ArchivePath> filter);

    /**
     * Get a nested {@link Archive} as a specific type using the specified {@code ArchiveFormat}
     *
     * @param type
     *            The Type to return the Archive as
     * @param path
     *            The location of the Archive
     * @param archiveFormat
     *            The archive format
     * @return The found Archive as given type or null if none found at the given path
     * @see Archive#add(Archive, ArchivePath, Class)
     * @see Archive#add(Archive, String, Class)
     */
    <X extends Archive<X>> X getAsType(Class<X> type, String path, ArchiveFormat archiveFormat);

    /**
     * Get a nested {@link Archive} located in a {@code ArchivePath} as a specific type using the specified
     * {@code ArchiveFormat}
     *
     * @param type
     *            The Type to return the Archive as
     * @param path
     *            The location of the Archive
     * @param archiveFormat
     *            The archive format
     * @return The found Archive as given type or null if none found at given {@link ArchivePath}
     * @see Archive#add(Archive, ArchivePath, Class)
     * @see Archive#add(Archive, String, Class)
     */
    <X extends Archive<X>> X getAsType(Class<X> type, ArchivePath path, ArchiveFormat archiveFormat);

    /**
     * Get all nested {@link Archive} matching the filter as a specific type using the specified {@code ArchiveFormat}.
     *
     * @param <X>
     *            The type of archive
     * @param type
     *            The Type to return the Archive as
     * @param filter
     *            Filter to match result
     * @param archiveFormat
     *            The archive format
     * @return A {@link Collection} of found Archives matching given filter or empty {@link Collection} if non found.
     * @see Archive#getAsType(Class, ArchivePath, ArchiveFormat)
     * @see Archive#add(Archive, ArchivePath, Class)
     * @see Archive#add(Archive, String, Class)
     */
    <X extends Archive<X>> Collection<X> getAsType(Class<X> type, Filter<ArchivePath> filter,
                                                   ArchiveFormat archiveFormat);

    /**
     * Denotes whether this archive contains a resource at the specified path
     *
     * @param path
     *            The path to the resource
     * @return true if the archive contains the resource, false otherwise
     * @throws IllegalArgumentException
     *             If the path is not specified
     */
    boolean contains(ArchivePath path) throws IllegalArgumentException;

    /**
     * Denotes whether this archive contains a resource at the specified path
     *
     * @param path
     *            The path to the resource
     * @return true if the archive contains the resource, false otherwise
     * @throws IllegalArgumentException
     *             If the path is not specified
     */
    boolean contains(String path) throws IllegalArgumentException;

    /**
     * Removes the {@link Node} in the {@link Archive} at the specified {@link ArchivePath}. If the path is a directory,
     * recursively removes all contents. If the path does not exist, return null.
     *
     * @param path
     *            The path to the node to be removed
     * @return The Node removed
     * @throws IllegalArgumentException
     *             If the path is not specified
     */
    Node delete(ArchivePath path) throws IllegalArgumentException;

    /**
     * Removes the {@link Node} in the {@link Archive} at the {@link ArchivePath} indicated by the specified String
     * archivePath. If the path is a directory, recursively removes all contents. If the path does not exist, return
     * null.
     *
     * @param archivePath
     *            The path to the node to be removed
     * @return The Node removed
     * @throws IllegalArgumentException
     *             If the path is not specified
     * @see #delete(ArchivePath)
     */
    Node delete(String archivePath) throws IllegalArgumentException;

    /**
     * Obtains all assets in this archive, along with their respective paths. The returned Map will be an immutable
     * view.
     *
     * @return A map of all paths and nodes in the archive
     */
    Map<ArchivePath, Node> getContent();

    /**
     * Obtains all assets matching given filter in this archive, along with its respective Path. The returned Map will
     * be an immutable view.
     *
     * @param filter
     *            Filter to match assets
     * @return A map of the paths and nodes found in the archive matching the filter
     */
    Map<ArchivePath, Node> getContent(Filter<ArchivePath> filter);

    /**
     * Obtains all assets matching given filter in this archive as a new Archive.<br/>
     * <br/>
     * This is an alias for shallowCopy(Filter).
     *
     * @see org.jboss.shrinkwrap.api.Archive#shallowCopy(Filter)
     * @param filter
     *            Filter to match assets
     * @return A new archive containing the filtered assets
     */
    T filter(Filter<ArchivePath> filter);

    /**
     * Add an archive under a specific context and maintain the archive name as context path.
     *
     * @param path
     *            The path to use
     * @param archive
     *            The archive to add
     * @param exporter
     *            Exporter type to use in fulfilling the {@link Asset#openStream()} contract for the added (nested)
     *            archive.
     * @return This archive
     * @throws IllegalArgumentException
     *             If any argument is not specified
     */
    T add(Archive<?> archive, ArchivePath path, Class<? extends StreamExporter> exporter)
        throws IllegalArgumentException;

    /**
     * Add an archive under a specific context and maintain the archive name as context path.
     *
     * @param path
     *            The path to use
     * @param archive
     *            The archive to add
     * @param exporter
     *            Exporter type to use in fulfilling the {@link Asset#openStream()} contract for the added (nested)
     *            archive.
     * @return This archive
     * @throws IllegalArgumentException
     *             If the path or archive are not specified
     */
    T add(Archive<?> archive, String path, Class<? extends StreamExporter> exporter) throws IllegalArgumentException;

    /**
     * Merge the contents from an existing archive without maintaining the archive name in the context path.
     *
     * @param source
     *            Archive to add contents from
     * @return This archive
     * @throws IllegalArgumentException
     *             If the existing archive is not specified
     */
    T merge(Archive<?> source) throws IllegalArgumentException;

    /**
     * Merge the contents from an existing archive without maintaining the archive name in the context path.
     * <p>
     * The filter controls which {@link ArchivePath}s to include from the source {@link Archive}.
     *
     * @param source
     *            Archive to add contents from
     * @param filter
     *            Filter to match paths to be included
     * @return This archive
     * @throws IllegalArgumentException
     *             If the existing archive is not specified
     */
    T merge(Archive<?> source, Filter<ArchivePath> filter) throws IllegalArgumentException;

    /**
     * Merge the contents from an existing archive in a specific path without maintaining the archive name in the
     * context path.
     *
     * @param source
     *            Archive to add contents from
     * @param path
     *            Path to add contents to
     * @return This archive
     * @throws IllegalArgumentException
     *             If the path or existing archive is not specified
     */
    T merge(Archive<?> source, ArchivePath path) throws IllegalArgumentException;

    /**
     * Merge the contents from an existing archive in a specific path without maintaining the archive name in the
     * context path.
     *
     * @param source
     *            Archive to add contents from
     * @param path
     *            Path to add contents to
     * @return This archive
     * @throws IllegalArgumentException
     *             If the path or existing archive is not specified
     */
    T merge(Archive<?> source, String path) throws IllegalArgumentException;

    /**
     * Merge the contents from an existing archive in a specific path without maintaining the archive name in the
     * context path. The filter controls which {@link ArchivePath}s to include from the source {@link Archive}.
     *
     * @param source
     *            Archive to add contents from
     * @param path
     *            Path to add contents to
     * @param filter
     *            Filter to match paths to be included
     * @return This archive
     * @throws IllegalArgumentException
     *             If the path or existing archive is not specified
     */
    T merge(Archive<?> source, ArchivePath path, Filter<ArchivePath> filter) throws IllegalArgumentException;

    /**
     * Merge the contents from an existing archive in a specific path without maintaining the archive name in the
     * context path. The filter controls which {@link ArchivePath}s to include from the source {@link Archive}.
     *
     * @param source
     *            Archive to add contents from
     * @param path
     *            Path to add contents to
     * @param filter
     *            Filter to match paths to be included
     * @return This archive
     * @throws IllegalArgumentException
     *             If the path or existing archive is not specified
     */
    T merge(Archive<?> source, String path, Filter<ArchivePath> filter) throws IllegalArgumentException;

    /**
     * Moves the asset under the source path to the target path.
     *
     * @param source
     *            The context under which to remove the assets
     * @param target
     *            The context under which to add the moved assets
     * @return the resulting archive with the moved assets
     * @throws IllegalArgumentException
     *             If any of the paths is not specified
     * @throws IllegalArchivePathException
     *             If the source path is invalid.
     */
    T move(ArchivePath source, ArchivePath target) throws IllegalArgumentException, IllegalArchivePathException;

    /**
     * Moves the asset under the source path to the target path.
     *
     * @param source
     *            The context under which to remove the assets
     * @param target
     *            The context under which to add the moved assets
     * @return the resulting archive with the moved assets
     * @throws IllegalArgumentException
     *             If any of the paths is not specified
     * @throws IllegalArchivePathException
     *             If the source path is invalid.
     */
    T move(String source, String target) throws IllegalArgumentException, IllegalArchivePathException;

    /**
     * Acts as a shorthand for {@link Archive#toString(Formatter)} where the {@link Formatters#SIMPLE} is leveraged.
     *
     * @return A string representation of the archive using the simple formatter
     */
    @Override
    String toString();

    /**
     * If "true" is specified, acts as a shorthand for {@link Archive#toString(Formatter)} where the
     * {@link Formatters#VERBOSE} is leveraged. Otherwise, the {@link Formatters#SIMPLE} will be used (equivalent to
     * {@link Archive#toString()}).
     *
     * @param verbose
     *            If true, use the verbose formatter; otherwise use the simple formatter
     * @return A string representation of the archive
     */
    String toString(boolean verbose);

    /**
     * Returns a view of this {@link Archive} as returned from the specified {@link Formatter}. Common options may be to
     * use the predefined formatters located in {@link Formatters}
     *
     * @param formatter
     *            The formatter to use
     * @return A string representation of the archive using the specified formatter
     * @throws IllegalArgumentException
     *             If the formatter is not specified
     */
    String toString(Formatter formatter) throws IllegalArgumentException;

    /**
     * Prints the content of this {@link Archive} to the specified {@link OutputStream} on the format defined by the
     * specified {@link Formatter}. The caller is responsible for opening, flushing and eventually closing the stream.
     *
     * @param outputStream
     *            the stream to print the archive contents to
     * @param formatter
     *            the output format
     * @throws IllegalArgumentException
     *             if an exceptions occur when writing the archive contents.
     */
    void writeTo(OutputStream outputStream, Formatter formatter) throws IllegalArgumentException;

    /**
     * Creates a shallow copy of this {@link Archive}. Assets from this archive are made available under the same paths.
     * However, removing old assets or adding new assets on this archive affects does not affect the new archive.
     *
     * @return a new archive with a copy of the pointers to the assets
     */
    Archive<T> shallowCopy();

    /**
     * Creates a shallow copy of this {@link Archive} based on given filter.
     * Assets from this archive are made available
     * under the same paths. However, removing old assets or
     * adding new assets on this archive affects does not affect
     * the new archive. This allows for filtering of both directory-based
     * and {@link Asset}-based paths.
     *
     * @return a new archive with a copy of the pointers to the assets
     */
    Archive<T> shallowCopy(Filter<ArchivePath> filter);
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.nio.file;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.nio.file.MemoryNamedAsset;
import org.jboss.shrinkwrap.api.nio.file.SeekableInMemoryByteChannel;

/**
 * {@link FileSystemProvider} implementation for ShrinkWrap {@link Archive}s.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ShrinkWrapFileSystemProvider extends FileSystemProvider {

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ShrinkWrapFileSystemProvider.class.getName());

    /**
     * Scheme
     */
    private static final String SCHEME = "shrinkwrap";

    /**
     * Environment key for creating a new {@link FileSystem} denoting the archive
     */
    private static final String ENV_KEY_ARCHIVE = "archive";

    /**
     * Open file systems, keyed by the {@link Archive#getId()}
     */
    private final ConcurrentMap<String, ShrinkWrapFileSystem> createdFileSystems = new ConcurrentHashMap<>();

    /**
     * Lock for creation of a new filesystem and other tasks which should block until this op has completed
     */
    private final ReentrantLock createNewFsLock = new ReentrantLock();

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#getScheme()
     */
    @Override
    public String getScheme() {
        return SCHEME;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#newFileSystem(java.net.URI, java.util.Map)
     */
    @Override
    public FileSystem newFileSystem(final URI uri, final Map<String, ?> env) throws IOException {

        // Precondition checks
        if (uri == null) {
            throw new IllegalArgumentException("URI must be specified");
        }
        if (env == null) {
            throw new IllegalArgumentException("Environment must be specified");
        }

        // Scheme is correct?
        final String scheme = uri.getScheme();
        if (!scheme.equals(SCHEME)) {
            throw new IllegalArgumentException(ShrinkWrapFileSystem.class.getSimpleName()
                + " supports URI with scheme " + SCHEME + " only.");
        }

        // Get ID of the archive
        final String id = uri.getHost();

        // Archive is provided?
        Archive<?> archive = null;
        final Object archiveArg = env.get(ENV_KEY_ARCHIVE);
        if (archiveArg != null) {
            try {
                archive = Archive.class.cast(archiveArg);
                // Ensure the name of the archive matches the host specified in the URI
                if (!archive.getId().equals(id)) {
                    throw new IllegalArgumentException("Specified archive " + archive.toString()
                        + " does not have name matching the host of specified URI: " + uri.toString());
                }
                if (log.isLoggable(Level.FINER)) {
                    log.finer("Found archive supplied by environment: " + archive.toString());
                }
            } catch (final ClassCastException cce) {
                // User specified the wrong type, translate and rethrow
                throw new IllegalArgumentException("Unexpected argument passed into environment under key "
                    + ENV_KEY_ARCHIVE + ": " + archiveArg);
            }
        }

        // Lock for compound operations on createdFileSystems
        createNewFsLock.lock();

        // Exists?
        final ShrinkWrapFileSystem existsFs = this.createdFileSystems.get(archive.getId());
        if (existsFs != null && existsFs.isOpen()) {
            throw new FileSystemAlreadyExistsException("File System for URI " + uri.toString() + " already exists: "
                + existsFs.toString());
        } else if (existsFs != null && !existsFs.isOpen()) {
            if (log.isLoggable(Level.FINE)) {
                log.fine("Attempting to create a file system for URI " + uri.toString()
                    + ", and one has been made but is closed; it will be replaced by a new one.");
            }
        }

        // Make a new FileSystem
        final ShrinkWrapFileSystem newFs = new ShrinkWrapFileSystem(this, archive);
        if (log.isLoggable(Level.FINE)) {
            log.fine("Created new filesystem: " + newFs.toString() + " for URI " + uri.toString());
        }
        this.createdFileSystems.put(archive.getId(), newFs);

        // Unlock, compound operations done on createdFileSystems
        createNewFsLock.unlock();

        // Return
        return newFs;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#getFileSystem(java.net.URI)
     */
    @Override
    public FileSystem getFileSystem(final URI uri) {

        // Get open FS
        final FileSystem fs = this.createdFileSystems.get(uri.getHost());

        // If not already created
        if (fs == null) {
            throw new FileSystemNotFoundException("Could not find an open file system with URI: " + uri.toString()
                + "; try creating a new file system?");
        }

        // Return
        return fs;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#getPath(java.net.URI)
     */
    @Override
    public Path getPath(final URI uri) {

        // Precondition checks
        if (uri == null) {
            throw new IllegalArgumentException("URI must be specified");
        }

        // ID exists? We're referencing a previously-opened archive?
        final String id = uri.getHost();
        ShrinkWrapFileSystem fs = null;
        if (id != null && id.length() > 0) {
            fs = this.createdFileSystems.get(id);
        }

        // Check that the file system exists
        if (fs == null) {
            throw new FileSystemNotFoundException("Could not find a previously-created filesystem with URI: "
                + uri.toString());
        }
        // Check FS is open
        if (!fs.isOpen()) {
            throw new FileSystemNotFoundException("File System for URI: " + uri.toString()
                + " is closed; create a new one to re-mount.");
        }

        final String pathComponent = uri.getPath();
        final ArchivePath archivePath = ArchivePaths.create(pathComponent);
        final Path path = new ShrinkWrapPath(archivePath, fs);

        // Return
        return path;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#newByteChannel(java.nio.file.Path, java.util.Set,
     *      java.nio.file.attribute.FileAttribute<?>[])
     */
    @Override
    public SeekableByteChannel newByteChannel(final Path path, final Set<? extends OpenOption> options,
        final FileAttribute<?>... attrs) throws IOException {
        // Precondition checks for null path done by NIO2 Files

        // Ignore FileAttributes

        // Get Archive
        final Archive<?> archive = this.getArchive(path);

        // // If overwriting isn't allowed and this path exists
        final ArchivePath archivePath = ArchivePaths.create(path.toString());

        // Open a new Channel (which is also a NamedAsset)
        final MemoryNamedAsset channel = new MemoryNamedAsset(archivePath);

        // Writing?
        if (options.contains(StandardOpenOption.CREATE) || options.contains(StandardOpenOption.CREATE_NEW)
            || options.contains(StandardOpenOption.WRITE)) {

            // Plug channel as Asset into the archive

            if (archive.contains(channel.getName())) {
                // Appending?
                if (options.contains(StandardOpenOption.APPEND)) {
                    // Read in the existing content
                    channel.position(0);
                    final InputStream in = archive.get(archivePath).getAsset().openStream();
                    this.copy(in, channel);
                    in.close();
                    // Delete the existing asset and associate the channel as the new asset
                    archive.delete(archivePath);
                    archive.add(channel);
                } else {
                    // Exception translate
                    throw new FileAlreadyExistsException(archivePath.get());
                }
            } else {
                archive.add(channel);
            }

            // Return the channel
            return channel;
        }

        // Else we're reading...

        // Get the Node
        final Node node = archive.get(archivePath);
        if (node == null) {
            throw new IllegalArgumentException(
                "No open options have been set, and cannot read a file that does not exist");
        }
        // Directory?
        final Asset asset = node.getAsset();
        if (asset == null) {
            throw new IllegalArgumentException("Cannot open a new channel to a path with existing directory: "
                + archivePath.get());
        }

        // Existing asset is read into the channel
        final InputStream in = asset.openStream();
        final SeekableByteChannel outChannel = new SeekableInMemoryByteChannel();
        this.copy(in, outChannel);
        // Set the position to 0 so it can be read from the beginning
        outChannel.position(0);
        return outChannel;

    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#newDirectoryStream(java.nio.file.Path,
     *      java.nio.file.DirectoryStream.Filter)
     */
    @Override
    public DirectoryStream<Path> newDirectoryStream(final Path dir, final Filter<? super Path> filter)
        throws IOException {
        final FileSystem fs = dir.getFileSystem();
        if (!(fs instanceof ShrinkWrapFileSystem)) {
            throw new IllegalArgumentException("Expected ShrinkWrap File System for Path: " + dir.toString());
        }
        return new ShrinkWrapDirectoryStream(dir, (ShrinkWrapFileSystem) fs, filter);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#createDirectory(java.nio.file.Path,
     *      java.nio.file.attribute.FileAttribute<?>[])
     */
    @Override
    public void createDirectory(final Path dir, final FileAttribute<?>... attrs) throws IOException {
        final Archive<?> archive = this.getArchive(dir);
        final ArchivePath parent = ArchivePaths.create(dir.toString()).getParent();
        if (parent != null && !archive.contains(parent)) {
            // IOException? Despite being a stupid choice for this, it's what the NIO.2 API uses.
            throw new IOException("Cannot recursively create parent directories for: " + dir
                + "; instead invoke \"createDirectories\"");
        }
        archive.addAsDirectories(dir.toString());
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Created directory " + dir.toString() + " on " + archive.toString());
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#delete(java.nio.file.Path)
     */
    @Override
    public void delete(final Path path) throws IOException {

        assert path != null : "Path must be specified";
        final Archive<?> archive = this.getArchive(path);
        final String pathString = path.toString();
        if (!archive.contains(pathString)) {
            throw new NoSuchFileException(path + " does not exist in " + archive);
        }
        final boolean isDirectory = archive.get(pathString).getAsset() == null;

        // Directory?
        if (isDirectory) {
            // Check empty?
            if (!archive.getContent(new org.jboss.shrinkwrap.api.Filter<ArchivePath>() {
                @Override
                public boolean include(final ArchivePath path) {
                    final String filterPathString = path.get();
                    return filterPathString.startsWith(pathString) && !filterPathString.equals(pathString);
                }
            }).isEmpty()) {
                throw new DirectoryNotEmptyException(pathString);
            }
        }
        archive.delete(pathString);
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Deleted " + path.toString() + " from " + archive.toString());
        }
    }

    /**
     * Obtains the underlying archive associated with the specified Path
     *
     * @param path
     * @return
     */
    private Archive<?> getArchive(final Path path) {
        assert path != null : "Path must be specified";
        final FileSystem fs = path.getFileSystem();
        assert fs != null : "File system is null";
        // Could be user error in this case, passing in a Path from another provider
        if (!(fs instanceof ShrinkWrapFileSystem)) {
            throw new IllegalArgumentException("This path is not associated with a "
                + ShrinkWrapFileSystem.class.getSimpleName());
        }
        final ShrinkWrapFileSystem swfs = (ShrinkWrapFileSystem) fs;
        final Archive<?> archive = swfs.getArchive();
        assert archive != null : "No archive associated with file system";
        return archive;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.nio.file.spi.FileSystemProvider#copy(java.nio.file.Path, java.nio.file.Path,
     * java.nio.file.CopyOption[])
     */
    @Override
    public void copy(Path source, Path target, CopyOption... options) throws IOException {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#move(java.nio.file.Path, java.nio.file.Path,
     *      java.nio.file.CopyOption[])
     */
    @Override
    public void move(final Path source, final Path target, final CopyOption... options) throws IOException {

        // Precondition checks
        if (source == null) {
            throw new IllegalArgumentException("source must be specified");
        }
        if (target == null) {
            throw new IllegalArgumentException("target must be specified");
        }

        // Source exists?
        if (!Files.exists(source, new LinkOption[] {})) {
            throw new IllegalArgumentException("Source file doesn't exist: " + source.toString());
        }

        // If equal, NOOP
        if (source == target) {
            return;
        }

        final Archive<?> archive = this.getArchive(target);
        final Node node = archive.get(target.toString());
        // Copying to something already present?
        if (node != null) {
            final Asset asset = node.getAsset();
            // Directory
            if (asset == null) {
                // Not empty
                if (node.getChildren().size() > 0) {
                    throw new DirectoryNotEmptyException("Cannot move to non-empty directory: " + target.toString());
                }
            }
        }

        // Move
        archive.move(source.toString(), target.toString());
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#isSameFile(java.nio.file.Path, java.nio.file.Path)
     */
    @Override
    public boolean isSameFile(final Path path1, final Path path2) throws IOException {

        // Only equal if pointing to same FS
        final FileSystem fs1 = path1.getFileSystem();
        final FileSystem fs2 = path2.getFileSystem();
        if (fs1 != fs2) {
            return false;
        }

        // Same if the normalized form points to the same location
        final String normalized1 = path1.normalize().toString();
        final String normalized2 = path2.normalize().toString();
        return normalized1.equals(normalized2);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#isHidden(java.nio.file.Path)
     */
    @Override
    public boolean isHidden(final Path path) throws IOException {
        // No paths are hidden
        return false;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#getFileStore(java.nio.file.Path)
     */
    @Override
    public FileStore getFileStore(final Path path) throws IOException {
        final FileStore fileStore = path.getFileSystem().getFileStores().iterator().next();
        return fileStore;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#checkAccess(java.nio.file.Path, java.nio.file.AccessMode[])
     */
    @Override
    public void checkAccess(final Path path, final AccessMode... modes) throws IOException {
        // We support READ, WRITE, and EXECUTE on everything, so long as a file exists
        final Archive<?> archive = this.getArchive(path);
        final String desired = path.toString();
        if (!archive.contains(desired)) {
            throw new NoSuchFileException(desired);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#getFileAttributeView(java.nio.file.Path, java.lang.Class,
     *      java.nio.file.LinkOption[])
     */
    @Override
    public <V extends FileAttributeView> V getFileAttributeView(final Path path, final Class<V> type,
        final LinkOption... options) {
        if (path == null) {
            throw new IllegalArgumentException("path must be specified");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must be specified");
        }
        if (!type.isAssignableFrom(ShrinkWrapFileAttributeView.class)) {
            // Nope, we don't support this view
            return null;
        }
        if (!(path instanceof ShrinkWrapPath)) {
            throw new IllegalArgumentException("Only " + ShrinkWrapPath.class.getSimpleName() + " is supported");
        }
        return type.cast(new ShrinkWrapFileAttributeView(new ShrinkWrapFileAttributes((ShrinkWrapPath) path,
                getArchive(path))));
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#readAttributes(java.nio.file.Path, java.lang.Class,
     *      java.nio.file.LinkOption[])
     */
    @Override
    public <A extends BasicFileAttributes> A readAttributes(final Path path, final Class<A> type,
        final LinkOption... options) throws IOException {
        if (path == null) {
            throw new IllegalArgumentException("path must be specified");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must be specified");
        }
        if (!type.isAssignableFrom(ShrinkWrapFileAttributes.class)) {
            throw new UnsupportedOperationException("Only supports " + ShrinkWrapFileAttributes.class.getSimpleName()
                + " type");
        }
        if (!(path instanceof ShrinkWrapPath)) {
            throw new IllegalArgumentException("Only " + ShrinkWrapPath.class.getSimpleName() + " is supported");
        }
        final ShrinkWrapPath swPath = (ShrinkWrapPath) path;
        if (!((ShrinkWrapFileSystem) swPath.getFileSystem()).getArchive().contains(path.toString())) {
            throw new NoSuchFileException(path.toString());
        }
        final A attributes = type.cast(new ShrinkWrapFileAttributes((ShrinkWrapPath) path, getArchive(path)));
        return attributes;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#readAttributes(java.nio.file.Path, java.lang.String,
     *      java.nio.file.LinkOption[])
     */
    @Override
    public Map<String, Object> readAttributes(final Path path, final String attributes, final LinkOption... options)
        throws IOException {
        throw new UnsupportedOperationException("ShrinkWrap File Systems do not support attributes");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.spi.FileSystemProvider#setAttribute(java.nio.file.Path, java.lang.String, java.lang.Object,
     *      java.nio.file.LinkOption[])
     */
    @Override
    public void setAttribute(final Path path, final String attribute, final Object value, final LinkOption... options)
        throws IOException {
        throw new UnsupportedOperationException("ShrinkWrap File Systems do not support attributes");
    }

    /**
     * Writes the contents of the {@link InputStream} to the {@link SeekableByteChannel}
     *
     * @param in
     * @param out
     * @throws IOException
     */
    private void copy(final InputStream in, final SeekableByteChannel out) throws IOException {
        assert in != null : "InStream must be specified";
        assert out != null : "Channel must be specified";

        final byte[] backingBuffer = new byte[1024 * 4];
        final ByteBuffer byteBuffer = ByteBuffer.wrap(backingBuffer);
        int bytesRead = 0;
        while ((bytesRead = in.read(backingBuffer, 0, backingBuffer.length)) > -1) {
            // Limit to the amount we've actually read in, so we don't overflow into old data blocks
            byteBuffer.limit(bytesRead);
            out.write(byteBuffer);
            // Position back to 0
            byteBuffer.clear();
        }
    }

}

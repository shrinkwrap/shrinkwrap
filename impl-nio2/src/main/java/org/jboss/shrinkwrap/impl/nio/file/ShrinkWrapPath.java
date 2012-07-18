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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;

/**
 * NIO.2 {@link Path} implementation adapting to the {@link ArchivePath} construct in a ShrinkWrap {@link Archive}
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class ShrinkWrapPath implements Path {

    private static final Logger log = Logger.getLogger(ShrinkWrapPath.class.getName());

    private static final String DIR_BACK = "..";

    private static final String DIR_THIS = ".";

    /**
     * Internal representation
     */
    private final String path;

    /**
     * Owning {@link ShrinkWrapFileSystem}
     */
    private final ShrinkWrapFileSystem fileSystem;

    /**
     * Constructs a new instance using the specified (required) canonical form and backing {@link ShrinkWrapFileSystem}
     *
     * @param path
     * @param fileSystem
     * @throws IllegalArgumentException
     *             If the path or file system is not specified
     */
    ShrinkWrapPath(final String path, final ShrinkWrapFileSystem fileSystem) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException("path must be specified");
        }
        if (fileSystem == null) {
            throw new IllegalArgumentException("File system must be specified.");
        }
        this.path = path;
        this.fileSystem = fileSystem;
    }

    /**
     * Constructs a new instance using the specified (required) path and backing {@link ShrinkWrapFileSystem}
     *
     * @param path
     *            to be evaluated using {@link ArchivePath#get()}
     * @param fileSystem
     * @throws IllegalArgumentException
     *             If the path or file system is not specified
     * @throws IllegalArgumentException
     *             If the delegate is not specified
     */
    ShrinkWrapPath(final ArchivePath path, final ShrinkWrapFileSystem fileSystem) throws IllegalArgumentException {
        if (path == null) {
            throw new IllegalArgumentException(ArchivePath.class.getSimpleName() + " must be specified");
        }
        if (fileSystem == null) {
            throw new IllegalArgumentException("File system must be specified.");
        }
        this.path = path.get();
        this.fileSystem = fileSystem;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#getFileSystem()
     */
    @Override
    public FileSystem getFileSystem() {
        return fileSystem;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#isAbsolute()
     */
    @Override
    public boolean isAbsolute() {
        return this.path.startsWith(ArchivePath.SEPARATOR_STRING);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#getRoot()
     */
    @Override
    public Path getRoot() {
        return this.isAbsolute() ? new ShrinkWrapPath(ArchivePaths.root(), fileSystem) : null;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#getFileName()
     */
    @Override
    public Path getFileName() {
        // Root and empty String has no file name
        if (path.length() == 0 || path.equals(ArchivePaths.root().get())) {
            return null;
        } else {
            final List<String> tokens = tokenize(this);
            // Furthest out
            final Path fileName = new ShrinkWrapPath(tokens.get(tokens.size() - 1), this.fileSystem);
            return fileName;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#getParent()
     */
    @Override
    public Path getParent() {
        final List<String> tokens = tokenize(this);
        // No parent?
        final int numTokens = tokens.size();
        if (numTokens == 0 || (numTokens == 1 && !this.isAbsolute())) {
            return null;
        }

        // Iterate over all but the last token and build a new path
        final StringBuffer sb = new StringBuffer();
        if (this.isAbsolute()) {
            sb.append(ArchivePath.SEPARATOR);
        }
        for (int i = 0; i < numTokens - 1; i++) {
            if (i >= 1) {
                sb.append(ArchivePath.SEPARATOR);
            }
            sb.append(tokens.get(i));
        }

        final String parentPath = sb.toString();
        return new ShrinkWrapPath(parentPath, fileSystem);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#getNameCount()
     */
    @Override
    public int getNameCount() {
        String context = this.path;
        // Kill trailing slashes
        if (context.endsWith(ArchivePath.SEPARATOR_STRING)) {
            context = context.substring(0, context.length() - 1);
        }
        // Kill preceding slashes
        if (context.startsWith(ArchivePath.SEPARATOR_STRING)) {
            context = context.substring(1);
        }
        // Root
        if (context.length() == 0) {
            return 0;
        }
        // Else count names by using the separator
        final int pathSeparators = this.countOccurrences(context, ArchivePath.SEPARATOR, 0);
        return pathSeparators + 1;
    }

    /**
     * Returns the number of occurrences of the specified character in the specified {@link String}, starting at the
     * specified offset
     *
     * @param string
     * @param c
     * @param offset
     * @return
     */
    private int countOccurrences(final String string, char c, int offset) {
        assert string != null : "String must be specified";
        return ((offset = string.indexOf(c, offset)) == -1) ? 0 : 1 + countOccurrences(string, c, offset + 1);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#getName(int)
     */
    @Override
    public Path getName(final int index) {
        // Precondition checks handled by subpath impl
        final Path subpath = this.subpath(0, index + 1);
        return subpath;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#subpath(int, int)
     */
    @Override
    public Path subpath(final int beginIndex, final int endIndex) {
        if (beginIndex < 0) {
            throw new IllegalArgumentException("Begin index must be greater than 0");
        }
        if (endIndex < 0) {
            throw new IllegalArgumentException("End index must be greater than 0");
        }
        if (endIndex <= beginIndex) {
            throw new IllegalArgumentException("End index must be greater than begin index");
        }
        final List<String> tokens = tokenize(this);
        final int tokenCount = tokens.size();
        if (beginIndex >= tokenCount) {
            throw new IllegalArgumentException("Invalid begin index " + endIndex + " for " + this.toString()
                + "; must be between 0 and " + tokenCount + " exclusive");
        }
        if (endIndex > tokenCount) {
            throw new IllegalArgumentException("Invalid end index " + endIndex + " for " + this.toString()
                + "; must be between 0 and " + tokenCount + " inclusive");
        }
        final StringBuilder newPathBuilder = new StringBuilder();
        for (int i = 0; i < endIndex; i++) {
            newPathBuilder.append(ArchivePath.SEPARATOR);
            newPathBuilder.append(tokens.get(i));
        }
        final Path newPath = this.fromString(newPathBuilder.toString());
        return newPath;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#startsWith(java.nio.file.Path)
     */
    @Override
    public boolean startsWith(final Path other) {
        // Precondition checks
        if (other == null) {
            throw new IllegalArgumentException("other path must be specified");
        }
        // Unequal FS
        if (this.getFileSystem() != other.getFileSystem()) {
            return false;
        }

        // Tokenize each
        final List<String> ourTokens = tokenize(this);
        final List<String> otherTokens = tokenize((ShrinkWrapPath) other);

        // Inequal roots
        if (other.isAbsolute() && !this.isAbsolute()) {
            return false;
        }

        // More names in the other Path than we have
        final int otherTokensSize = otherTokens.size();
        if (otherTokensSize > ourTokens.size()) {
            return false;
        }

        // Ensure each of the other name elements match ours
        for (int i = 0; i < otherTokensSize; i++) {
            if (!otherTokens.get(i).equals(ourTokens.get(i))) {
                return false;
            }
        }

        // All conditions met
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#startsWith(java.lang.String)
     */
    @Override
    public boolean startsWith(final String other) {
        if (other == null) {
            throw new IllegalArgumentException("other path input must be specified");
        }
        final Path otherPath = this.fromString(other);
        return this.startsWith(otherPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#endsWith(java.nio.file.Path)
     */
    @Override
    public boolean endsWith(final Path other) {
        // Precondition checks
        if (other == null) {
            throw new IllegalArgumentException("other path must be specified");
        }
        // Unequal FS? (also ensures that we can safely cast to this type later)
        if (this.getFileSystem() != other.getFileSystem()) {
            return false;
        }
        final List<String> ourTokens = tokenize(this);
        final List<String> otherTokens = tokenize((ShrinkWrapPath) other);

        // Bigger than us, fails
        final int numOtherTokens = otherTokens.size();
        if (numOtherTokens > ourTokens.size()) {
            return false;
        }

        // Difference in component size
        final int differential = ourTokens.size() - numOtherTokens;
        // Given an absolute?
        if (other.isAbsolute()) {
            // We must have the same number of elements
            if (differential != 0) {
                return false;
            }
        }
        // Compare all components
        for (int i = numOtherTokens - 1; i >= 0; i--) {
            if (!ourTokens.get(i + differential).equals(otherTokens.get(i))) {
                // Any tokens don't match, punt
                return false;
            }
        }

        // All conditions met
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#endsWith(java.lang.String)
     */
    @Override
    public boolean endsWith(final String other) {
        if (other == null) {
            throw new IllegalArgumentException("other path input must be specified");
        }
        final Path otherPath = this.fromString(other);
        return this.endsWith(otherPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#normalize()
     */
    @Override
    public Path normalize() {
        final String normalizedString = normalize(tokenize(this), this.isAbsolute());
        final Path normalized = new ShrinkWrapPath(normalizedString, this.fileSystem);
        return normalized;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#resolve(java.nio.file.Path)
     */
    @Override
    public Path resolve(final Path other) {
        if (other == null) {
            throw new IllegalArgumentException("other path must be specified");
        }

        if (other.isAbsolute()) {
            return other;
        }

        if (other.toString().length() == 0) {
            return this;
        }

        // Else join other to this
        final StringBuilder sb = new StringBuilder(this.path);
        if (!this.path.endsWith(ArchivePath.SEPARATOR_STRING)) {
            sb.append(ArchivePath.SEPARATOR);
        }
        sb.append(other.toString());
        final Path newPath = new ShrinkWrapPath(sb.toString(), this.fileSystem);
        return newPath;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#resolve(java.lang.String)
     */
    @Override
    public Path resolve(final String other) {
        // Delegate
        final Path otherPath = this.fromString(other);
        return this.resolve(otherPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#resolveSibling(java.nio.file.Path)
     */
    @Override
    public Path resolveSibling(final Path other) {
        if (other == null) {
            throw new IllegalArgumentException("other path must be specified");
        }
        // All paths are absolute, so just return other
        return other;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#resolveSibling(java.lang.String)
     */
    @Override
    public Path resolveSibling(final String other) {
        // Delegate
        final Path otherPath = this.fromString(other);
        return this.resolveSibling(otherPath);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#relativize(java.nio.file.Path)
     */
    @Override
    public Path relativize(final Path other) {
        if (other == null) {
            throw new IllegalArgumentException("other path must be specified");
        }
        if (!(other instanceof ShrinkWrapPath)) {
            throw new IllegalArgumentException("Can only relativize paths of type "
                + ShrinkWrapPath.class.getSimpleName());
        }

        // Equal paths, return empty Path
        if (this.equals(other)) {
            return new ShrinkWrapPath("", this.fileSystem);
        }

        // Recursive relativization
        final Path newPath = relativizeCommonRoot(this, this, other, other, 0);
        return newPath;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#toUri()
     */
    @Override
    public URI toUri() {
        final URI root = ShrinkWrapFileSystems.getRootUri(this.fileSystem.getArchive());
        // Compose a new URI location, stripping out the extra "/" root
        final String location = root.toString() + this.toString().substring(1);
        final URI uri = URI.create(location);
        return uri;
    }

    /**
     * Resolves relative paths against the root directory, normalizing as well.
     *
     * @see java.nio.file.Path#toAbsolutePath()
     */
    @Override
    public Path toAbsolutePath() {

        // Already absolute?
        if (this.isAbsolute()) {
            return this;
        }

        // Else construct a new absolute path and normalize it
        final Path absolutePath = new ShrinkWrapPath(ArchivePath.SEPARATOR + this.path, this.fileSystem);
        final Path normalized = absolutePath.normalize();
        return normalized;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#toRealPath(java.nio.file.LinkOption[])
     */
    @Override
    public Path toRealPath(final LinkOption... options) throws IOException {
        // All links are "real" (no symlinks) and absolute, so just return this (if exists)
        if (!this.fileSystem.getArchive().contains(this.path)) {
            throw new FileNotFoundException("Path points to a nonexistant file or directory: " + this.toString());
        }
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#toFile()
     */
    @Override
    public File toFile() {
        throw new UnsupportedOperationException(
            "This path is associated with a ShrinkWrap archive, not the default provider");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#register(java.nio.file.WatchService, java.nio.file.WatchEvent.Kind<?>[],
     *      java.nio.file.WatchEvent.Modifier[])
     */
    @Override
    public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers) throws IOException {
        throw new UnsupportedOperationException("ShrinkWrap Paths do not support registration with a watch service.");
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#register(java.nio.file.WatchService, java.nio.file.WatchEvent.Kind<?>[])
     */
    @Override
    public WatchKey register(WatchService watcher, Kind<?>... events) throws IOException {
        return this.register(watcher, events, (Modifier) null);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#iterator()
     */
    @Override
    public Iterator<Path> iterator() {

        final List<String> tokens = tokenize(this);
        final int tokensSize = tokens.size();
        final List<Path> paths = new ArrayList<>(tokensSize);
        for (int i = 0; i < tokensSize; i++) {
            ArchivePath newPath = ArchivePaths.root();
            for (int j = 0; j <= i; j++) {
                newPath = ArchivePaths.create(newPath, tokens.get(j));
            }
            paths.add(new ShrinkWrapPath(newPath, this.fileSystem));
        }

        return paths.iterator();
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#compareTo(java.nio.file.Path)
     */
    @Override
    public int compareTo(final Path other) {
        if (other == null) {
            throw new IllegalArgumentException("other path must be specified");
        }
        // Just defer to alpha ordering since we're absolute
        return this.toString().compareTo(other.toString());
    }

    /**
     * {@inheritDoc}
     *
     * @see java.nio.file.Path#toString()
     */
    @Override
    public String toString() {
        return this.path;
    }

    /**
     * Creates a new {@link ShrinkWrapPath} instance from the specified input {@link String}
     *
     * @param path
     * @return
     */
    private Path fromString(final String path) {
        if (path == null) {
            throw new IllegalArgumentException("path must be specified");
        }
        // Delegate
        return new ShrinkWrapPath(path, fileSystem);
    }

    /**
     * Returns the components of this path in order from root out
     *
     * @return
     */
    private static List<String> tokenize(final ShrinkWrapPath path) {
        final StringTokenizer tokenizer = new StringTokenizer(path.toString(), ArchivePath.SEPARATOR_STRING);
        final List<String> tokens = new ArrayList<>();
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    /**
     * Normalizes the tokenized view of the path
     *
     * @param path
     * @return
     */
    private static String normalize(final List<String> path, boolean absolute) {
        assert path != null : "path must be specified";

        // Remove unnecessary references to this dir
        if (path.contains(DIR_THIS)) {
            path.remove(DIR_THIS);
            normalize(path, absolute);
        }

        // Remove unnecessary references to the back dir, and its parent
        final int indexDirBack = path.indexOf(DIR_BACK);
        if (indexDirBack != -1) {
            if (indexDirBack > 0) {
                path.remove(indexDirBack);
                path.remove(indexDirBack - 1);
                normalize(path, absolute);
            } else {
                throw new IllegalArgumentException("Cannot specify to go back \"../\" past the root");
            }
        }

        // Nothing left to do; reconstruct
        final StringBuilder sb = new StringBuilder();
        if (absolute) {
            sb.append(ArchivePath.SEPARATOR);
        }
        for (int i = 0; i < path.size(); i++) {
            if (i > 0) {
                sb.append(ArchivePath.SEPARATOR);
            }
            sb.append(path.get(i));
        }
        return sb.toString();
    }

    /**
     * Relativizes the paths recursively
     *
     * @param thisOriginal
     * @param thisCurrent
     * @param otherOriginal
     * @param otherCurrent
     * @param backupCount
     * @return
     */
    private static ShrinkWrapPath relativizeCommonRoot(final ShrinkWrapPath thisOriginal, final Path thisCurrent,
        final Path otherOriginal, Path otherCurrent, final int backupCount) {
        // Preconditions
        assert thisOriginal != null;
        assert thisCurrent != null;
        assert otherOriginal != null;
        assert otherCurrent != null;
        assert backupCount >= 0;

        // Do we yet have a common root?
        if (!otherCurrent.startsWith(thisCurrent)) {
            // Back up until we do
            final Path otherParent = otherCurrent.getParent();
            final ShrinkWrapPath thisParent = (ShrinkWrapPath) thisCurrent.getParent();
            if (otherParent != null && thisParent != null) {
                return relativizeCommonRoot(thisOriginal, thisParent, otherOriginal, otherParent, backupCount + 1);
            } else {
                throw new IllegalArgumentException("No common components");
            }
        }

        // Common root. Now relativize that.
        final List<String> thisTokens = tokenize(thisOriginal);
        final List<String> otherTokens = tokenize((ShrinkWrapPath) otherOriginal);
        final int numOtherTokens = otherTokens.size();
        final int numToTake = otherTokens.size() - thisTokens.size();
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < backupCount; i++) {
            sb.append(DIR_BACK);
            sb.append(ArchivePath.SEPARATOR);
        }
        final int startCounter = numOtherTokens - numToTake - backupCount;
        final int stopCounter = numOtherTokens - 1;
        if (log.isLoggable(Level.FINEST)) {
            log.finest("Backup: " + backupCount);
            log.finest("This tokens: " + thisTokens);
            log.finest("Other tokens: " + otherTokens);
            log.finest("Differential: " + numToTake);
            log.finest("Start: " + startCounter);
            log.finest("Stop: " + stopCounter);
        }
        for (int i = startCounter; i <= stopCounter; i++) {
            if (i > startCounter) {
                sb.append(ArchivePath.SEPARATOR);
            }
            sb.append(otherTokens.get(i));
        }

        return new ShrinkWrapPath(sb.toString(), thisOriginal.fileSystem);
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fileSystem == null) ? 0 : fileSystem.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        return result;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ShrinkWrapPath other = (ShrinkWrapPath) obj;
        if (this.fileSystem != other.fileSystem) {
            return false;
        }
        if (path == null) {
            if (other.path != null) {
                return false;
            }
        } else if (!path.equals(other.path)) {
            return false;
        }
        return true;
    }

}

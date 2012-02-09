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
package org.jboss.shrinkwrap.impl.base.container;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ClassLoaderSearchUtilDelegator;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.IllegalArchivePathException;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.ClassAsset;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.NamedAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.impl.base.ArchiveBase;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.URLPackageScanner;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.asset.ServiceProviderAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.spi.ArchiveFormatAssociable;
import org.jboss.shrinkwrap.spi.Configurable;

/**
 * ContainerBase
 *
 * Abstract class that helps implement the Archive, ManifestContainer, ResourceContainer, ClassContainer and
 * LibraryContainer.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class ContainerBase<T extends Archive<T>> extends AssignableBase<Archive<?>> implements Archive<T>,
    ManifestContainer<T>, ServiceProviderContainer<T>, ResourceContainer<T>, ClassContainer<T>, LibraryContainer<T>,
    ArchiveFormatAssociable {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private static final String DEFAULT_MANIFEST = "DefaultManifest.MF";
    private static final String DEFAULT_PACKAGE_NAME = "";

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * The exposed archive type.
     */
    private final Class<T> actualType;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    protected ContainerBase(final Class<T> actualType, final Archive<?> archive) {
        super(archive);

        Validate.notNull(actualType, "ActualType should be specified");

        this.actualType = actualType;
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - Archive Delegation --------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Override
    public ArchiveFormat getArchiveFormat() {
        return getArchive().as(Configurable.class).getConfiguration().getExtensionLoader()
            .getArchiveFormatFromExtensionMapping(actualType);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Archive, java.lang.String, java.lang.Class)
     */
    @Override
    public T add(final Archive<?> archive, final String path, final Class<? extends StreamExporter> exporter) {
        this.getArchive().add(archive, path, exporter);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.ArchivePath,
     *      java.lang.Class)
     */
    @Override
    public T add(final Archive<?> archive, final ArchivePath path, final Class<? extends StreamExporter> exporter) {
        this.getArchive().add(archive, path, exporter);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset,
     *      org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T add(Asset asset, ArchivePath target) throws IllegalArgumentException {
        this.getArchive().add(asset, target);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset,
     *      org.jboss.shrinkwrap.api.ArchivePath, java.lang.String)
     */
    @Override
    public T add(Asset asset, ArchivePath path, String name) {
        this.getArchive().add(asset, path, name);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset, java.lang.String,
     *      java.lang.String)
     */
    @Override
    public T add(final Asset asset, final String target, final String name) throws IllegalArgumentException {
        this.getArchive().add(asset, target, name);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(NamedAsset))
     */
    @Override
    public T add(NamedAsset namedAsset) {
        this.getArchive().add(namedAsset);
        return covarientReturn();

    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectories(org.jboss.shrinkwrap.api.ArchivePath[])
     */
    @Override
    public T addAsDirectories(ArchivePath... paths) throws IllegalArgumentException {
        this.getArchive().addAsDirectories(paths);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectories(java.lang.String[])
     */
    @Override
    public T addAsDirectories(String... paths) throws IllegalArgumentException {
        this.getArchive().addAsDirectories(paths);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectory(org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T addAsDirectory(ArchivePath path) throws IllegalArgumentException {
        this.getArchive().addAsDirectory(path);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#addAsDirectory(java.lang.String)
     */
    @Override
    public T addAsDirectory(String path) throws IllegalArgumentException {
        this.getArchive().addAsDirectory(path);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive)
     */
    @Override
    public T merge(Archive<?> source) throws IllegalArgumentException {
        this.getArchive().merge(source);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public T merge(Archive<?> source, Filter<ArchivePath> filter) throws IllegalArgumentException {
        this.getArchive().merge(source, filter);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive,
     *      org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T merge(Archive<?> source, ArchivePath path) throws IllegalArgumentException {
        this.getArchive().merge(source, path);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive,
     *      org.jboss.shrinkwrap.api.ArchivePath, org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public T merge(Archive<?> source, ArchivePath path, Filter<ArchivePath> filter) throws IllegalArgumentException {
        this.getArchive().merge(source, path, filter);
        return covarientReturn();
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
        this.getArchive().merge(source, path, filter);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive, java.lang.String)
     */
    @Override
    public T merge(final Archive<?> source, final String path) throws IllegalArgumentException {
        this.getArchive().merge(source, path);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#move(org.jboss.shrinkwrap.api.ArchivePath, org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T move(ArchivePath source, ArchivePath target) throws IllegalArgumentException, IllegalArchivePathException {
       this.getArchive().move(source, target);
       return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#move(java.lang.String, java.lang.String)
     */
    @Override
    public T move(String source, String target) throws IllegalArgumentException, IllegalArchivePathException {
       this.getArchive().move(source, target);
       return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.asset.Asset, java.lang.String)
     */
    @Override
    public T add(Asset asset, String name) {
        this.getArchive().add(asset, name);
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#contains(org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public boolean contains(ArchivePath path) {
        return this.getArchive().contains(path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#contains(java.lang.String)
     */
    @Override
    public boolean contains(final String path) throws IllegalArgumentException {
        Validate.notNull(path, "Path must be specified");
        return this.contains(ArchivePaths.create(path));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#delete(org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public Node delete(ArchivePath path) {
        return this.getArchive().delete(path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#delete(java.lang.String)
     */
    @Override
    public Node delete(String archivePath) {
        Validate.notNull(archivePath, "No path was specified");
        return getArchive().delete(archivePath);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#get(org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public Node get(ArchivePath path) {
        return this.getArchive().get(path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#get(java.lang.String)
     */
    @Override
    public Node get(String path) throws IllegalArgumentException {
        return this.getArchive().get(path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, java.lang.String)
     */
    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, String path) {
        return this.getArchive().getAsType(type, path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public <X extends Archive<X>> X getAsType(Class<X> type, ArchivePath path) {
        return this.getArchive().getAsType(type, path);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public <X extends Archive<X>> Collection<X> getAsType(Class<X> type, Filter<ArchivePath> filter) {
        return this.getArchive().getAsType(type, filter);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, java.lang.String,
     *      org.jboss.shrinkwrap.api.ArchiveFormat)
     */
    @Override
    public <X extends Archive<X>> X getAsType(final Class<X> type, final String path,
        final ArchiveFormat archiveCompression) {
        return this.getArchive().getAsType(type, path, archiveCompression);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.ArchivePath,
     *      org.jboss.shrinkwrap.api.ArchiveFormat)
     */
    @Override
    public <X extends Archive<X>> X getAsType(final Class<X> type, final ArchivePath path,
        final ArchiveFormat archiveCompression) {
        return this.getArchive().getAsType(type, path, archiveCompression);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getAsType(java.lang.Class, org.jboss.shrinkwrap.api.Filter,
     *      org.jboss.shrinkwrap.api.ArchiveFormat)
     */
    @Override
    public <X extends Archive<X>> Collection<X> getAsType(final Class<X> type, final Filter<ArchivePath> filter,
        final ArchiveFormat archiveCompression) {
        return this.getArchive().getAsType(type, filter, archiveCompression);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getContent()
     */
    @Override
    public Map<ArchivePath, Node> getContent() {
        return this.getArchive().getContent();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getContent(org.jboss.shrinkwrap.api.Filter)
     */
    @Override
    public Map<ArchivePath, Node> getContent(Filter<ArchivePath> filter) {
        return this.getArchive().getContent(filter);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#getName()
     */
    @Override
    public String getName() {
        return this.getArchive().getName();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.declarchive.api.Archive#toString(boolean)
     */
    @Override
    public String toString() {
        return this.getArchive().toString();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.declarchive.api.Archive#toString(boolean)
     */
    @Override
    public String toString(final boolean verbose) {
        return this.getArchive().toString(verbose);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.Archive#toString(org.jboss.shrinkwrap.api.formatter.Formatter)
     */
    @Override
    public String toString(final Formatter formatter) throws IllegalArgumentException {
        return this.getArchive().toString(formatter);
    }

    @Override
    public int hashCode() {
        return this.getArchive().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ArchiveBase<?>) {
            return this.getArchive().equals(obj);
        }

        if (!(obj instanceof ContainerBase)) {
            return false;
        }

        final ContainerBase<?> other = (ContainerBase<?>) obj;
        return this.getArchive().equals(other.getArchive());
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - ManifestContainer ---------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Should be implemented to set the path for Manifest related resources.
     *
     * @return Base Path for the ManifestContainer resources
     */
    protected abstract ArchivePath getManifestPath();

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ManifestContainer#setManifest(java.lang.String)
     */
    @Override
    public final T setManifest(String resourceName) {
        Validate.notNull(resourceName, "ResourceName should be specified");
        return setManifest(new ClassLoaderAsset(resourceName));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#setManifest(java.io.File)
     */
    @Override
    public T setManifest(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return setManifest(new FileAsset(resource));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#setManifest(java.net.URL)
     */
    @Override
    public T setManifest(URL resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return setManifest(new UrlAsset(resource));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#setManifest(org.jboss.shrinkwrap.api.Asset)
     */
    @Override
    public T setManifest(Asset resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return addAsManifestResource(resource, "MANIFEST.MF");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#setManifestResource(java.lang.Package,
     * java.lang.String)
     */
    @Override
    public T setManifest(Package resourcePackage, String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        return setManifest(classloaderResourceName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ManifestContainer#addManifestResource(java.lang.String)
     */
    @Override
    public final T addAsManifestResource(String resourceName) {
        Validate.notNull(resourceName, "ResourceName should be specified");
        return addAsManifestResource(fileFromResource(resourceName), resourceName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File)
     */
    @Override
    public T addAsManifestResource(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return addAsManifestResource(resource, resource.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String, java.lang.String)
     */
    @Override
    public T addAsManifestResource(String resourceName, String target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsManifestResource(fileFromResource(resourceName), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File, java.lang.String)
     */
    @Override
    public T addAsManifestResource(File resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsManifestResource(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.net.URL, java.lang.String)
     */
    @Override
    public T addAsManifestResource(URL resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsManifestResource(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Asset,
     * java.lang.String)
     */
    @Override
    public T addAsManifestResource(Asset resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsManifestResource(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.String,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsManifestResource(String resourceName, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsManifestResource(fileFromResource(resourceName), target);
    }

    /**
     * Adds the specified {@link File} resource (a nested JAR File form) to the current archive, returning the archive
     * itself
     *
     * @param resource
     * @param target
     * @return
     * @throws IllegalArgumentException
     */
    private T addNestedJarFileResource(final File resource, final ArchivePath target, final ArchivePath base)
        throws IllegalArgumentException {
        final Iterable<ClassLoader> classLoaders = ((Configurable) this.getArchive()).getConfiguration()
            .getClassLoaders();

        for (final ClassLoader classLoader : classLoaders) {
            final InputStream in = classLoader.getResourceAsStream(resourceAdjustedPath(resource));
            if (in != null) {
                final Asset asset = new ByteArrayAsset(in);
                return add(asset, base, target.get());
            }
        }
        throw new IllegalArgumentException(resource.getPath() + " was not found in any available ClassLoaders");
    }

    private String resourceAdjustedPath(final File resource) {
        final String path = resource.getPath();
        final String adjustedPath = path.substring(path.indexOf("!" + File.separator) + 2, path.length());
        return adjustedPath.replace(File.separator, "/");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.io.File,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsManifestResource(File resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        if (resource.isFile()) {
            return addAsManifestResource(new FileAsset(resource), target);
        }

        final File[] files = resource.listFiles();

        // SHRINKWRAP-275, resource URL coming in from a JAR
        if (files == null) {
            return this.addNestedJarFileResource(resource, target, this.getManifestPath());
        }

        if (files.length == 0) {
            return addAsManifestResource(new FileAsset(resource), target);
        }

        for (File file : resource.listFiles()) {
            ArchivePath child = ArchivePaths.create(file.getName());
            addAsManifestResource(file, new BasicPath(target, child));
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.net.URL,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsManifestResource(URL resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        File file = new File(resource.getFile());
        if (file.exists()) {
            return addAsManifestResource(file, target);
        }

        return addAsManifestResource(new UrlAsset(resource), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(org.jboss.shrinkwrap.api.Asset,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsManifestResource(Asset resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        ArchivePath location = new BasicPath(getManifestPath(), target);
        return add(resource, location);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResources(java.lang.Package,
     * java.lang.String[])
     */
    @Override
    public T addAsManifestResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNullAndNoNullValues(resourceNames,
            "ResourceNames must be specified and can not container null values");
        for (String resourceName : resourceNames) {
            addAsManifestResource(resourcePackage, resourceName);
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.Package,
     * java.lang.String)
     */
    @Override
    public T addAsManifestResource(Package resourcePackage, String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        ArchivePath target = ArchivePaths.create(classloaderResourceName);

        return addAsManifestResource(resourcePackage, resourceName, target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.Package,
     * java.lang.String, java.lang.String)
     */
    @Override
    public T addAsManifestResource(Package resourcePackage, String resourceName, String target)
        throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsManifestResource(resourcePackage, resourceName, ArchivePaths.create(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addManifestResource(java.lang.Package,
     * java.lang.String, org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T addAsManifestResource(Package resourcePackage, String resourceName, ArchivePath target)
        throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        Asset resource = new ClassLoaderAsset(classloaderResourceName);

        return addAsManifestResource(resource, target);
    }

    /**
     * {@inheritDoc}
     */
    public T addManifest() throws IllegalArgumentException {
        return addAsManifestResource(DEFAULT_MANIFEST, ManifestContainer.DEFAULT_MANIFEST_NAME);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addServiceProvider(java.lang.Class,
     * java.lang.Class<?>[])
     */
    @Override
    public T addAsServiceProvider(Class<?> serviceInterface, Class<?>... serviceImpls) throws IllegalArgumentException {
        Validate.notNull(serviceInterface, "ServiceInterface must be specified");
        Validate.notNullAndNoNullValues(serviceImpls, "ServiceImpls must be specified and can not contain null values");

        Asset asset = new ServiceProviderAsset(serviceImpls);
        ArchivePath path = new BasicPath("services", serviceInterface.getName());
        return addAsManifestResource(asset, path);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ManifestContainer#addServiceProvider(java.lang.String,
     * java.lang.String[])
     */
    @Override
    public T addAsServiceProvider(String serviceInterface, String... serviceImpls) throws IllegalArgumentException {
        Validate.notNull(serviceInterface, "ServiceInterface must be specified");
        Validate.notNullAndNoNullValues(serviceImpls, "ServiceImpls must be specified and can not contain null values");

        Asset asset = new ServiceProviderAsset(serviceImpls);
        ArchivePath path = new BasicPath("services", serviceInterface);
        return addAsManifestResource(asset, path);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.api.container.ServiceProviderContainerContainer#addServiceProvideraddAsServiceAndClasses
     * (java.lang.Class, java.lang.Class<?>[])
     */
    @Override
    public T addAsServiceProviderAndClasses(Class<?> serviceInterface, Class<?>... serviceImpls)
        throws IllegalArgumentException {
        Validate.notNull(serviceInterface, "ServiceInterface must be specified");
        Validate.notNullAndNoNullValues(serviceImpls, "ServiceImpls must be specified and can not contain null values");

        addAsServiceProvider(serviceInterface, serviceImpls);
        addClass(serviceInterface);
        return addClasses(serviceImpls);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - ResourceContainer ---------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Should be implemented to set the path for Resource related resources.
     *
     * @return Base Path for the ResourceContainer resources
     */
    protected abstract ArchivePath getResourcePath();

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.lang.String)
     */
    @Override
    public final T addAsResource(String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName should be specified");
        return addAsResource(resourceName, resourceName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ResourceContainer#addResource(java.net.URL)
     */
    @Override
    public final T addAsResource(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        return addAsResource(resource, resource.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String, java.lang.String)
     */
    @Override
    public final T addAsResource(String resourceName, String target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsResource(fileFromResource(resourceName), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.io.File, java.lang.String)
     */
    @Override
    public T addAsResource(File resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsResource(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.net.URL, java.lang.String)
     */
    @Override
    public T addAsResource(URL resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsResource(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Asset,
     * java.lang.String)
     */
    @Override
    public T addAsResource(Asset resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        return addAsResource(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsResource(String resourceName, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName should be specified");
        Validate.notNull(target, "Target should be specified");

        File resource = fileFromResource(resourceName);
        return addAsResource(resource, target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.String,
     * org.jboss.shrinkwrap.api.Path, java.lang.ClassLoader)
     */
    @Override
    public T addAsResource(String resourceName, ArchivePath target, ClassLoader classLoader)
        throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName should be specified");
        Validate.notNull(target, "Target should be specified");
        Validate.notNull(classLoader, "ClassLoader should be specified");

        return addAsResource(new ClassLoaderAsset(resourceName, classLoader), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.io.File,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsResource(File resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        if (resource.isFile()) {
            return addAsResource(new FileAsset(resource), target);
        }

        final File[] files = resource.listFiles();

        // SHRINKWRAP-275, resource URL coming in from a JAR
        if (files == null) {
            return this.addNestedJarFileResource(resource, target, this.getResourcePath());
        }

        // SHRINKWRAP-320 Empty Directory Causes FileNotFoundException
        if (files.length == 0) {
            return addAsDirectory(new BasicPath(getResourcePath(), target));
        }

        for (File file : resource.listFiles()) {
            ArchivePath child = ArchivePaths.create(file.getName());
            addAsResource(file, new BasicPath(target, child));
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.net.URL,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsResource(URL resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        File file = new File(resource.getFile());
        if (file.exists()) {
            return addAsResource(file, target);
        }

        return addAsResource(new UrlAsset(resource), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(org.jboss.shrinkwrap.api.Asset,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsResource(Asset resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource should be specified");
        Validate.notNull(target, "Target should be specified");

        ArchivePath location = new BasicPath(getResourcePath(), target);
        return add(resource, location);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResources(java.lang.Package, java.lang.String[])
     */
    @Override
    public T addAsResources(Package resourcePackage, String... resourceNames) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNullAndNoNullValues(resourceNames,
            "ResourceNames must be specified and can not container null values");
        for (String resourceName : resourceNames) {
            addAsResource(resourcePackage, resourceName);
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.Package, java.lang.String)
     */
    @Override
    public T addAsResource(Package resourcePackage, String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        ArchivePath target = ArchivePaths.create(classloaderResourceName);

        return addAsResource(resourcePackage, resourceName, target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.Package, java.lang.String,
     * java.lang.String)
     */
    @Override
    public T addAsResource(Package resourcePackage, String resourceName, String target) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsResource(resourcePackage, resourceName, ArchivePaths.create(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ResourceContainer#addResource(java.lang.Package, java.lang.String,
     * org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T addAsResource(Package resourcePackage, String resourceName, ArchivePath target)
        throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        Asset resource = new ClassLoaderAsset(classloaderResourceName);

        return addAsResource(resource, target);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - ClassContainer ------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Should be implemented to set the path for Class related resources.
     *
     * @return Base Path for the ClassContainer resources
     */
    protected abstract ArchivePath getClassesPath();

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ClassContainer#addClass(java.lang.Class)
     */
    @Override
    public T addClass(Class<?> clazz) throws IllegalArgumentException {
        Validate.notNull(clazz, "Clazz must be specified");

        return addClasses(clazz);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.ClassContainer#addClass(java.lang.String)
     */
    @Override
    public T addClass(String fullyQualifiedClassName) throws IllegalArgumentException {
        Validate.notNullOrEmpty(fullyQualifiedClassName, "Fully-qualified class name must be specified");

        // Get this archive's CLs
        final Archive<?> archive = this.getArchive();
        final Iterable<ClassLoader> cls = ((Configurable) archive).getConfiguration().getClassLoaders();
        assert cls != null : "CLs of this archive is not specified:" + archive;

        // Find the class in the configured CLs
        final Class<?> classToAdd;
        try {
            classToAdd = ClassLoaderSearchUtilDelegator.findClassFromClassLoaders(fullyQualifiedClassName, cls);
        } catch (final ClassNotFoundException cnfe) {
            throw new IllegalArgumentException("Could not find the requested Class " + fullyQualifiedClassName
                + " in any of the configured ClassLoaders for this archive", cnfe);
        }

        // Add
        return addClass(classToAdd);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.ClassContainer#addClass(java.lang.String, java.lang.ClassLoader)
     */
    @Override
    public T addClass(final String fullyQualifiedClassName, final ClassLoader cl) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNullOrEmpty(fullyQualifiedClassName, "Fully-qualified class name must be specified");
        Validate.notNull(cl, "ClassLoader must be specified");

        // Obtain the Class
        final Class<?> clazz;
        try {
            clazz = Class.forName(fullyQualifiedClassName, false, cl);
        } catch (final ClassNotFoundException e) {
            throw new IllegalArgumentException("Could not load class of name " + fullyQualifiedClassName + " with "
                + cl, e);
        }

        // Delegate and return
        return this.addClass(clazz);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ClassContainer#addClasses(java.lang.Class<?>[])
     */
    public T addClasses(Class<?>... classes) throws IllegalArgumentException {
        Validate.notNull(classes, "Classes must be specified");

        for (final Class<?> clazz : classes) {
            Asset resource = new ClassAsset(clazz);
            ArchivePath location = new BasicPath(getClassesPath(), AssetUtil.getFullPathForClassResource(clazz));
            add(resource, location);

            // SHRINKWRAP-335, account for classes loaded from the
            // Bootstrap CL (some JDK impls may return null on class.getClassLoader)
            final ClassLoader loadingCl = clazz.getClassLoader();
            final ClassLoader adjustedCl = loadingCl == null ? ClassLoader.getSystemClassLoader() : loadingCl;

            // Get all inner classes and add them
            addPackages(false, new Filter<ArchivePath>() {
                /**
                 * path = /package/MyClass$Test.class <br/>
                 * clazz = /package/MyClass.class <br/>
                 *
                 *
                 * @param path
                 *            The added classes
                 * @return
                 */
                public boolean include(ArchivePath path) {
                    ArchivePath classArchivePath = AssetUtil.getFullPathForClassResource(clazz);
                    String expression = classArchivePath.get().replace(".class", "\\$.*");
                    return path.get().matches(expression);
                };
            }, adjustedCl,
            // Assumes a null package is a class in the default package
                clazz.getPackage() == null ? DEFAULT_PACKAGE_NAME : clazz.getPackage().getName());
        }
        return covarientReturn();
    };

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ClassContainer#addPackage(java.lang.Package)
     */
    @Override
    public T addPackage(Package pack) throws IllegalArgumentException {
        Validate.notNull(pack, "Pack must be specified");

        return addPackage(pack.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.ClassContainer#addPackages(boolean, java.lang.Package[])
     */
    @Override
    public T addPackages(boolean recursive, Package... packages) throws IllegalArgumentException {
        Validate.notNull(packages, "Packages must be specified");

        return addPackages(recursive, Filters.includeAll(), packages);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackages(boolean, org.jboss.shrinkwrap.api.Filter,
     * java.lang.Package[])
     */
    @Override
    public T addPackages(final boolean recursive, final Filter<ArchivePath> filter, final Package... packages)
        throws IllegalArgumentException {
        return addPackages(recursive, filter, null, packages);
    }

    private T addPackages(final boolean recursive, final Filter<ArchivePath> filter, final ClassLoader cl,
        final Package... packages) throws IllegalArgumentException {
        Validate.notNull(filter, "Filter must be specified");
        Validate.notNull(packages, "Packages must be specified");

        String[] packageNames = new String[packages.length];
        for (int i = 0; i < packages.length; i++) {
            packageNames[i] = packages[i] == null ? null : packages[i].getName();
        }

        if (cl == null) {
            return addPackages(recursive, filter, packageNames);
        }

        return addPackages(recursive, filter, cl, packageNames);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackage(java.lang.String)
     */
    @Override
    public T addPackage(String pack) throws IllegalArgumentException {
        Validate.notNull(pack, "Package must be specified");

        return addPackages(false, pack);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ClassContainer#addDefaultPackage()
     */
    @Override
    public T addDefaultPackage() {
        return addPackages(false, DEFAULT_PACKAGE_NAME);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackages(boolean, java.lang.String[])
     */
    @Override
    public T addPackages(boolean recursive, String... packages) throws IllegalArgumentException {
        Validate.notNullAndNoNullValues(packages, "Pakcages must be specified and can not container null values");

        return addPackages(recursive, Filters.includeAll(), packages);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.ClassContainer#addPackages(boolean, org.jboss.shrinkwrap.api.Filter,
     * java.lang.String[])
     */
    @Override
    public T addPackages(boolean recursive, final Filter<ArchivePath> filter, String... packageNames)
        throws IllegalArgumentException {
        Validate.notNull(filter, "Filter must be specified");
        Validate.notNull(packageNames, "PackageNames must be specified");

        // Get the CLs for this archive
        final Iterable<ClassLoader> classLoaders = ((Configurable) this.getArchive()).getConfiguration()
            .getClassLoaders();

        for (String packageName : packageNames) {
            for (final ClassLoader classLoader : classLoaders) {
                addPackage(recursive, filter, classLoader, packageName);
            }
        }
        return covarientReturn();
    }

    private T addPackages(final boolean recursive, final Filter<ArchivePath> filter, final ClassLoader classLoader,
        String... packageNames) {
        Validate.notNull(filter, "Filter must be specified");
        Validate.notNull(packageNames, "PackageNames must be specified");

        for (String packageName : packageNames) {
            addPackage(recursive, filter, classLoader, packageName);
        }
        return covarientReturn();
    }

    private void addPackage(final boolean recursive, final Filter<ArchivePath> filter, final ClassLoader classLoader,
        String packageName) {
        // precondition checks
        Validate.notNull(packageName, "Package doesn't exist");

        final URLPackageScanner.Callback callback = new URLPackageScanner.Callback() {
            @Override
            public void classFound(String className) {
                ArchivePath classNamePath = AssetUtil.getFullPathForClassResource(className);
                if (!filter.include(classNamePath)) {
                    return;
                }
                Asset asset = new ClassLoaderAsset(classNamePath.get().substring(1), classLoader);
                ArchivePath location = new BasicPath(getClassesPath(), classNamePath);
                add(asset, location);
            }
        };
        final URLPackageScanner scanner = URLPackageScanner.newInstance(recursive, classLoader, callback, packageName);
        scanner.scanPackage();
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - LibraryContainer ----------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Should be implemented to set the path for Library related resources.
     *
     * @return Base Path for the LibraryContainer resources
     */
    protected abstract ArchivePath getLibraryPath();

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(org.jboss.shrinkwrap.api.Archive)
     */
    public T addAsLibrary(final Archive<?> archive) throws IllegalArgumentException {
        Validate.notNull(archive, "Archive must be specified");
        // Libraries are JARs, so add as ZIP
        return add(archive, getLibraryPath(), ZipExporter.class);
    };

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.lang.String)
     */
    @Override
    public T addAsLibrary(String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");
        File file = fileFromResource(resourceName);
        return addAsLibrary(file, new BasicPath(resourceName));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.io.File)
     */
    @Override
    public T addAsLibrary(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        return addAsLibrary(resource, resource.getName());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.lang.String, java.lang.String)
     */
    @Override
    public T addAsLibrary(String resourceName, String target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsLibrary(resourceName, new BasicPath(target));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibrary(java.io.File, java.lang.String)
     */
    @Override
    public T addAsLibrary(File resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsLibrary(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.net.URL, java.lang.String)
     */
    @Override
    public T addAsLibrary(URL resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsLibrary(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Asset,
     * java.lang.String)
     */
    @Override
    public T addAsLibrary(Asset resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsLibrary(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.lang.String,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsLibrary(String resourceName, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsLibrary(fileFromResource(resourceName), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.io.File, org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsLibrary(File resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        if (resource.isFile()) {
            return addAsLibrary(new FileAsset(resource), target);
        }

        if (resource.listFiles().length == 0) {
            return addAsLibrary(new FileAsset(resource), target);
        }

        for (File file : resource.listFiles()) {
            addAsLibrary(file, new BasicPath(target, file.getName()));
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(java.net.URL, org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsLibrary(URL resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        File resourceFile = new File(resource.getFile());
        if (!resourceFile.exists()) {
            return addAsLibrary(new UrlAsset(resource), target);
        }

        if (resourceFile.isFile()) {
            return addAsLibrary(new UrlAsset(resource), target);
        }

        if (resourceFile.listFiles().length == 0) {
            return addAsLibrary(new UrlAsset(resource), target);
        }

        for (File file : resourceFile.listFiles()) {
            addAsLibrary(file, new BasicPath(target, file.getName()));
        }

        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibrary(org.jboss.shrinkwrap.api.Asset,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsLibrary(Asset resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        ArchivePath location = new BasicPath(getLibraryPath(), target);
        return add(resource, location);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(java.lang.String[])
     */
    @Override
    public T addAsLibraries(String... resourceNames) throws IllegalArgumentException {
        Validate.notNull(resourceNames, "ResourceNames must be specified");
        for (String resourceName : resourceNames) {
            addAsLibrary(resourceName);
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(java.io.File[])
     */
    @Override
    public T addAsLibraries(File... resources) throws IllegalArgumentException {
        Validate.notNull(resources, "Resources must be specified");
        for (File resource : resources) {
            addAsLibrary(resource);
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addLibraries(org.jboss.shrinkwrap.api.Archive<?>[])
     */
    @Override
    public T addAsLibraries(Archive<?>... archives) throws IllegalArgumentException {
        Validate.notNull(archives, "Archives must be specified");
        for (final Archive<?> archive : archives) {
            addAsLibrary(archive);
        }
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibraries(java.util.Collection)
     */
    @Override
    public T addAsLibraries(final Collection<? extends Archive<?>> archives) throws IllegalArgumentException {
        Validate.notNull(archives, "Archives must be specified");
        return this.addAsLibraries(archives.toArray(new Archive<?>[archives.size()]));
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.LibraryContainer#addAsLibraries(java.util.Collection)
     */
    @Override
    public T addAsLibraries(final Archive<?>[]... archives) throws IllegalArgumentException {
        Validate.notNullAndNoNullValues(archives, "Archives must be specified");
        for (Archive<?>[] archiveArray : archives) {
            for (Archive<?> archive : archiveArray) {
                this.addAsLibraries(archive);
            }
        }
        return covarientReturn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeTo(final OutputStream outputStream, final Formatter formatter) throws IllegalArgumentException {
        this.getArchive().writeTo(outputStream, formatter);
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    protected T covarientReturn() {
        return getActualClass().cast(this);
    }

    protected Class<T> getActualClass() {
        return this.actualType;
    }

    /**
     * Gets a resource from the TCCL and returns its file path.
     *
     * @param resourceName
     *            is the name of the resource in the classpath
     * @return the file path for resourceName @see {@link java.net.URL#getFile()}
     * @throws IllegalArgumentException
     *             if resourceName doesn't exist in the classpath or privileges are not granted
     */
    private File fileFromResource(final String resourceName) throws IllegalArgumentException {
        final URL resourceUrl = AccessController.doPrivileged(GetTcclAction.INSTANCE).getResource(resourceName);
        Validate.notNull(resourceUrl, resourceName + " doesn't exist or can't be accessed");

        String resourcePath = AccessController.doPrivileged(GetTcclAction.INSTANCE).getResource(resourceName).getFile();
        try {
            // Have to URL decode the string as the ClassLoader.getResource(String) returns an URL encoded URL
            resourcePath = URLDecoder.decode(resourcePath, "UTF-8");
        } catch (UnsupportedEncodingException uee) {
            throw new IllegalArgumentException(uee);
        }
        return new File(resourcePath);
    }

    /**
     * Obtains the {@link Thread} Context {@link ClassLoader}
     *
     * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
     */
    private enum GetTcclAction implements PrivilegedAction<ClassLoader> {
        INSTANCE;

        @Override
        public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
        }

    }
}

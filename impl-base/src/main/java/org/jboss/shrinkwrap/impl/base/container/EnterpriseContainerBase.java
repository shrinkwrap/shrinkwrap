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
import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ClassLoaderAsset;
import org.jboss.shrinkwrap.api.asset.FileAsset;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.container.EnterpriseContainer;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.asset.AssetUtil;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * EnterpriseContainerSupport
 * <p>
 * Abstract class that helps implement the EnterpriseContainer. Used by specs that extends the EnterpriseContainer.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class EnterpriseContainerBase<T extends Archive<T>> extends ContainerBase<T> implements
    EnterpriseContainer<T> {
    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    protected EnterpriseContainerBase(Class<T> actualType, Archive<?> archive) {
        super(actualType, archive);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - EnterpriseContainer - Resources -------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Should be implemented to set the path for Application related resources.
     *
     * @return Base Path for the EnterpriseContainer application resources
     */
    protected abstract ArchivePath getApplicationPath();

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.EnterpriseContainer#setApplicationXML(java.lang.String)
     */
    @Override
    public T setApplicationXML(String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");
        return setApplicationXML(new ClassLoaderAsset(resourceName));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#setApplicationXML(java.io.File)
     */
    @Override
    public T setApplicationXML(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        return setApplicationXML(new FileAsset(resource));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#setApplicationXML(java.net.URL)
     */
    @Override
    public T setApplicationXML(URL resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        return setApplicationXML(new UrlAsset(resource));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#setApplicationXML(org.jboss.shrinkwrap.api.Asset)
     */
    @Override
    public T setApplicationXML(Asset resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        return addAsApplicationResource(resource, "application.xml");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#setApplicationXML(java.lang.Package,
     * java.lang.String)
     */
    @Override
    public T setApplicationXML(Package resourcePackage, String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        return setApplicationXML(new ClassLoaderAsset(classloaderResourceName));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.declarchive.api.container.EnterpriseContainer#addApplicationResource(java.lang.String)
     */
    @Override
    public T addAsApplicationResource(String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");

        return addAsApplicationResource(new ClassLoaderAsset(resourceName), resourceName);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.io.File)
     */
    @Override
    public T addAsApplicationResource(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");

        return addAsApplicationResource(new FileAsset(resource), resource.getName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.String,
     * java.lang.String)
     */
    @Override
    public T addAsApplicationResource(String resourceName, String target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(new ClassLoaderAsset(resourceName), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.io.File,
     * java.lang.String)
     */
    @Override
    public T addAsApplicationResource(File resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(new FileAsset(resource), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.net.URL,
     * java.lang.String)
     */
    @Override
    public T addAsApplicationResource(URL resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(new UrlAsset(resource), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(org.jboss.shrinkwrap.api.Asset,
     * java.lang.String)
     */
    @Override
    public T addAsApplicationResource(Asset resource, String target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(resource, new BasicPath(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.String,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsApplicationResource(String resourceName, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(new ClassLoaderAsset(resourceName), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.io.File,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsApplicationResource(File resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(new FileAsset(resource), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.net.URL,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsApplicationResource(URL resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(new UrlAsset(resource), target);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(org.jboss.shrinkwrap.api.Asset,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsApplicationResource(Asset resource, ArchivePath target) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(target, "Target must be specified");

        ArchivePath location = new BasicPath(getApplicationPath(), target);
        return add(resource, location);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResources(java.lang.Package,
     * java.lang.String[])
     */
    @Override
    public T addAsApplicationResources(Package resourcePackage, String... resourceNames)
        throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNullAndNoNullValues(resourceNames,
            "ResourceNames must be specified and can not container null values");
        for (String resourceName : resourceNames) {
            addAsApplicationResource(resourcePackage, resourceName);
        }
        return covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.Package,
     * java.lang.String)
     */
    @Override
    public T addAsApplicationResource(Package resourcePackage, String resourceName) throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        ArchivePath target = ArchivePaths.create(classloaderResourceName);

        return addAsApplicationResource(resourcePackage, resourceName, target);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.Package,
     * java.lang.String, java.lang.String)
     */
    @Override
    public T addAsApplicationResource(Package resourcePackage, String resourceName, String target)
        throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        return addAsApplicationResource(resourcePackage, resourceName, ArchivePaths.create(target));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addApplicationResource(java.lang.Package,
     * java.lang.String, org.jboss.shrinkwrap.api.ArchivePath)
     */
    @Override
    public T addAsApplicationResource(Package resourcePackage, String resourceName, ArchivePath target)
        throws IllegalArgumentException {
        Validate.notNull(resourcePackage, "ResourcePackage must be specified");
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(target, "Target must be specified");

        String classloaderResourceName = AssetUtil.getClassLoaderResourceName(resourcePackage, resourceName);
        Asset resource = new ClassLoaderAsset(classloaderResourceName);

        return addAsApplicationResource(resource, target);
    }

    // -------------------------------------------------------------------------------------||
    // Required Implementations - EnterpriseContainer - Modules ---------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Should be implemented to set the path for Module related resources.
     *
     * @return Base Path for the EnterpriseContainer module resources
     */
    protected abstract ArchivePath getModulePath();

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addAsModule(org.jboss.shrinkwrap.api.Archive)
     */
    @Override
    public T addAsModule(final Archive<?> archive) throws IllegalArgumentException {
        Validate.notNull(archive, "Archive must be specified");

        // Add as ZIP, as JARs are :)
        return add(archive, getModulePath(), ZipExporter.class);
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addAsModule(java.lang.String)
     */
    @Override
    public T addAsModule(String resourceName) {
        Validate.notNull(resourceName, "ResourceName must be specified");

        ArchivePath location = new BasicPath(AssetUtil.getNameForClassloaderResource(resourceName));
        return addAsModule(resourceName, location);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.io.File)
     */
    @Override
    public T addAsModule(File resource) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");

        return addAsModule(resource, resource.getName());
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addAsModules(org.jboss.shrinkwrap.api.Archive[])
     */
    @Override
    public T addAsModules(final Archive<?>... archives) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNull(archives, "archives must be specified");

        // Add each
        for (final Archive<?> archive : archives) {
            this.addAsModule(archive);
        }

        // Return
        return this.covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addAsModules(java.lang.String[])
     */
    @Override
    public T addAsModules(final String... resourceNames) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNull(resourceNames, "resourceNames must be specified");

        // Add each
        for (final String resourceName : resourceNames) {
            this.addAsModule(resourceName);
        }

        // Return
        return this.covarientReturn();
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addAsModules(java.io.File[])
     */
    @Override
    public T addAsModules(final File... resources) throws IllegalArgumentException {
        // Precondition checks
        Validate.notNull(resources, "resources must be specified");

        // Add each
        for (final File resource : resources) {
            this.addAsModule(resource);
        }

        // Return
        return this.covarientReturn();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.io.File,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsModule(final File resource, final ArchivePath targetPath) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        final Asset asset = new FileAsset(resource);
        return addAsModule(asset, targetPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.lang.String,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsModule(final String resourceName, final ArchivePath targetPath) throws IllegalArgumentException {
        Validate.notNull(resourceName, "ResourceName must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        final Asset asset = new ClassLoaderAsset(resourceName);
        return addAsModule(asset, targetPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.net.URL,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsModule(final URL resource, final ArchivePath targetPath) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        Asset asset = new UrlAsset(resource);
        return addAsModule(asset, targetPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.io.File, java.lang.String)
     */
    @Override
    public T addAsModule(final File resource, final String targetPath) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        return addAsModule(resource, new BasicPath(targetPath));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(org.jboss.shrinkwrap.api.Asset,
     * java.lang.String)
     */
    @Override
    public T addAsModule(Asset resource, String targetPath) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        return addAsModule(resource, new BasicPath(targetPath));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.lang.String, java.lang.String)
     */
    @Override
    public T addAsModule(final String resourceName, final String targetPath) throws IllegalArgumentException {
        Validate.notNull(resourceName, "Resource must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        return addAsModule(resourceName, new BasicPath(targetPath));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(java.net.URL, java.lang.String)
     */
    @Override
    public T addAsModule(final URL resource, final String targetPath) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        return addAsModule(resource, new BasicPath(targetPath));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jboss.shrinkwrap.api.container.EnterpriseContainer#addModule(org.jboss.shrinkwrap.api.Asset,
     * org.jboss.shrinkwrap.api.Path)
     */
    @Override
    public T addAsModule(Asset resource, ArchivePath targetPath) throws IllegalArgumentException {
        Validate.notNull(resource, "Resource must be specified");
        Validate.notNull(targetPath, "Target Path must be specified");

        return add(resource, new BasicPath(getModulePath(), targetPath));
    }
}

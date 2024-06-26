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
package org.jboss.shrinkwrap.api.asset;

import java.io.InputStream;
import java.io.File;
import java.net.URL;

/**
 * A descriptive asset contains descriptors that are available from the file system,
 * class loader or some URL and can be automatically added to an archive at the
 * correct location. So a DescriptiveAsset having the name beans.xml will be
 * added at WEB-INF for a WebArchive and META-INF for a JavaArchive.
 * <p>A DescriptiveAsset wraps an {@link EmptyAsset} a {@link FileAsset}
 * a {@link StringAsset} or a {@link UrlAsset} or some other arbitrary asset.
 * <p>Examples:
 * <ul>
 * <li>Adding an empty beans.xml to a jar so that the jar eventually contains
 * META-INF/beans.xml:
 * <code><pre>
 * ShrinkWrap.create(JavaArchive.class).add(new DescriptiveAsset("beans.xml"));
 * </pre></code>
 * <li>Adding an empty beans.xml to a war so that the war eventually contains
 * WEB-INF/beans.xml:
 * <code><pre>
 * ShrinkWrap.create(WebArchive.class).add(new DescriptiveAsset("beans.xml"));
 * </pre></code>
 * </ul>
 *
 * @author <a href="mailto:robert.panzer@me.com">Robert Panzer</a>
 */
public class DescriptiveAsset implements TargetArchiveAwareAsset {

    private String name;

    private Asset wrappedAsset;

    /**
     * Creates a new asset with an empty content.
     * @param name The name of the asset, e.g. beans.xml
     * @see EmptyAsset
     */
    public DescriptiveAsset(String name) {
        this(name, EmptyAsset.INSTANCE);
    }

    /**
     * Creates a new asset using the content of the specified file.
     * @param name The name of the asset, e.g. beans.xml
     * @param file The file of which the content should be used.
     * @see FileAsset
     */
    public DescriptiveAsset(String name, File file) {
        this(name, new FileAsset(file));
    }

    /**
     * Creates a new asset using the specified plain text content.
     * @param name The name of the asset, e.g. beans.xml
     * @param content The plain text content of this asset
     * @see StringAsset
     */
    public DescriptiveAsset(String name, String content) {
        this(name, new StringAsset(content));
    }

    /**
     * Creates a new asset using the content obtained from the specified URL.
     * @param name The name of the asset, e.g. beans.xml
     * @param url The URL that backs the content of this asset
     * @see UrlAsset
     */
    public DescriptiveAsset(String name, URL url) {
        this(name, new UrlAsset(url));
    }

    /**
     * Creates a new asset using the content of the specified asset.
     * @param name The name of the asset, e.g. beans.xml
     * @param asset The asset that backs the content of this asset.
     * @see UrlAsset
     */
    public DescriptiveAsset(String name, Asset asset) {
        this.name = name;
        this.wrappedAsset = asset;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public InputStream openStream() {
        return wrappedAsset.openStream();
    }

}

/*
 * Copyright 2014 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.shrinkwrap.api.container;

import java.io.File;
import java.net.URL;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Defines the contract for a component capable of storing CDI bean descriptors, i.e. a beans.xml. <br/>
 * <br/>
 * The actual path to the resources within the {@link Archive} is up to the implementations/specifications.
 *
 * @author <a href="mailto:robert.panzer@me.com">Robert Panzer</a>
 * @version $Revision: $
 */
public interface CDIBeanContainer<T extends Archive<T>> {

    /**
     * Adds an empty beans.xml to this {@link Archive}.
     *
     * @return This virtual archive
     */
    T setBeansXML();

    /**
     * Adds a resource to this {@link Archive} as beans.xml. <br/>
     * <br/>
     * The {@link ClassLoader} used to obtain the resource is up to the implementation. <br/>
     * For instance a resourceName of "test/example.xml" could be placed in "/META-INF/beans.xml"
     * or "/WEB-INF/beans.xml".
     *
     * @param resourceName
     *            Name of the {@link ClassLoader} resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resourceName is null
     * @see #setBeansXML(Asset)
     */
    T setBeansXML(String resourceName);

    /**
     * Adds a {@link File} to this {@link Archive} as beans.xml. <br/>
     * <br/>
     * For instance a {@link File} "test/example.xml" could be placed in "/META-INF/beans.xml"
     * or "/WEB-INF/beans.xml".
     *
     * @param resource
     *            {@link File} resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resource is null
     * @see #setBeansXML(Asset)
     */
    T setBeansXML(File resource);

    /**
     * Adds a {@link URL} to this {@link Archive} as beans.xml. <br/>
     * <br/>
     * For instance a {@link URL} "http://my.com/example.xml" could be placed in "/META-INF/beans.xml"
     * or "/WEB-INF/beans.xml"
     *
     * @param resource
     *            {@link URL} resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resource is null
     * @see #setBeansXML(Asset)
     */
    T setBeansXML(URL resource);

    /**
     * Adds a {@link Asset} to this {@link Archive} as beans.xml.
     *
     * @param asset
     *            {@link Asset} resource to add
     * @return This virtual archive
     * @throws IllegalArgumentException
     *             if resource is null
     */
    T setBeansXML(Asset asset);
}

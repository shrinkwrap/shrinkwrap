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
package org.jboss.shrinkwrap.api.container;

import java.io.File;
import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * ResourceAdapterContainer
 * 
 * Defines the contract for a component capable of storing Resource adapter
 * resources. <br/>
 * <br/>
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public interface ResourceAdapterContainer<T extends Archive<T>>
{
   /**
    * Adds the resource as ra.xml to the container, returning the container itself.
    * <br/>
    * The {@link ClassLoader} used to obtain the resource is up to the implementation.  
    * 
    * @param resourceName resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resourceName is null
    * @see #setResourceAdapterXML(Asset)
    */
   T setResourceAdapterXML(String resourceName) throws IllegalArgumentException;
   
   /**
    * Adds the {@link File} as ra.xml to the container, returning the container itself.
    * 
    * @param resource {@link File} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #setResourceAdapterXML(Asset)
    */
   T setResourceAdapterXML(File resource) throws IllegalArgumentException;
   
   /**
    * Adds the {@link URL} as ra.xml to the container, returning the container itself.
    * 
    * @param resource {@link URL} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    * @see #setResourceAdapterXML(Asset)
    */
   T setResourceAdapterXML(URL resource) throws IllegalArgumentException;
   
   /**
    * Adds the {@link Asset} as ra.xml to the container, returning the container itself.
    * 
    * @param resource {@link Asset} resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException if resource is null
    */
   T setResourceAdapterXML(Asset resource) throws IllegalArgumentException;

}

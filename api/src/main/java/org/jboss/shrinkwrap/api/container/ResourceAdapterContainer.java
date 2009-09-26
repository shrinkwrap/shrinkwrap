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

import org.jboss.shrinkwrap.api.Archive;

/**
 * ResourceAdapterContainer
 * 
 * Defines the contract for a component capable of storing Resource adapter
 * resources. <br/>
 * <br/>
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 * @param <T>
 */
public interface ResourceAdapterContainer<T extends Archive<T>>
{
   /**
    * Adds a resource to this Archive as ra.xml. <br/>
    * <br/>
    * The mechanism to obtain the resource is up to the
    * implementation. <br/>
    * For instance a resourceName of "test/ra.xml" could be placed in
    * "/META-INF/ra.xml"
    * 
    * @param resourceName Name of the resource to add
    * @return This virtual archive
    * @throws IllegalArgumentException
    *             if resourceName is null
    */
   T setResourceAdapterXML(String resourceName) throws IllegalArgumentException;

}

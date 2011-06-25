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
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Defines the contract for a component capable of storing 
 * service provider related resources.
 * <br/><br/>
 * The actual path to the  service provider within the Archive 
 * is up to the implementations/specifications.
 *
 * @author Davide D'Alto
 * @version $Revision: $
 * @param <T>
 */
public interface ServiceProviderContainer<T extends Archive<T>> extends ManifestContainer<T>, ClassContainer<T>
{
   /**
    * Adds a META-INF/services/ServiceInterfaceName {@link Asset} and the classes related to the service
    * to the archive.
    * 
    * @param serviceInterface The Service Interface class
    * @param serviceImpls The Service Interface Implementations
    * @return This virtual archive
    * @throws IllegalArgumentException if serviceInterface is null
    * @throws IllegalArgumentException if serviceImpls is null or contain null values
    */
   /*
    * TODO: The interface should have been like this:
    * <X> T addServiceProvider(Class<X> serviceInterface, Class<? extends X>... serviceImpls) throws IllegalArgumentException;
    * But due to how java generic works, this will cause a unsafe warning for the user. 
    */
   T addAsServiceProviderAndClasses(Class<?> serviceInterface, Class<?>... serviceImpls) throws IllegalArgumentException;
   
}

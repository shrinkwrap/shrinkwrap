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

/**
 * Pluggable separation between an Asset and a default name used to add 
 * Assets into an archive without having to explicitly supply the name 
 * (ArchivePath) each time.
 * 
 * This interface is intended to be a pluggable way to specify and 
 * create many instances of Assets that use the same name in a programmatic 
 * manner, mainly to remove duplication of having to specify the same resource 
 * name repeatedly.
 * 
 * @author <a href="mailto:chris.wash@gmail.com">Chris Wash</a>
 *
 */
public interface PluggableNamedAsset
{

   /**
    * Specifies the name (ArchivePath) for the archive
    * @return {@link String} representation of the ArchivePath
    */
   String getName();

   /**
    * Specifies the asset to be represented
    * @return underlying {@link Asset}
    */
   Asset getAsset();

}

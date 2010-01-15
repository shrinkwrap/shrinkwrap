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
package org.jboss.shrinkwrap.impl.base.asset;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.ArchivePath;

/**
 * {@link Asset} implementation used to denote no backing
 * resource, but simply a directory structure.  When placed 
 * into an {@link Archive} under some {@link ArchivePath}, only the 
 * path context will be respected.  Modeled as a singleton
 * as this implementation has no real state or identity (all
 * directory assets are equal).  Calls to {@link DirectoryAsset#openStream()}
 * will always return null.
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public enum DirectoryAsset implements Asset {

   /**
    * Singleton Instance
    */
   INSTANCE;

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Asset#openStream()
    */
   @Override
   public InputStream openStream()
   {
      // To signify that we've got nothing to back us (we're just a directory),
      // we use null.  A stream backed by an empty byte array would be an 
      // empty file, which is different.
      return null;
   }

}

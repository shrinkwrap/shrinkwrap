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
package org.jboss.shrinkwrap.impl.base.spec;

import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.container.WebContainerBase;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * WebArchiveImpl
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class WebArchiveImpl 
   extends WebContainerBase<WebArchive> 
   implements WebArchive
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final Logger log = Logger.getLogger(JavaArchiveImpl.class.getName());

   /**
    * Path to the web inside of the Archive.
    */
   private static final ArchivePath PATH_WEB = new BasicPath("WEB-INF");

   /**
    * Path to the manifests inside of the Archive.
    */
   private static final ArchivePath PATH_MANIFEST = new BasicPath("META-INF");

   /**
    * Path to the resources inside of the Archive.
    */
   private static final ArchivePath PATH_RESOURCE = new BasicPath("/");

   /**
    * Path to the libraries inside of the Archive.
    */
   private static final ArchivePath PATH_LIBRARY = new BasicPath(PATH_WEB, "lib");

   /**
    * Path to the classes inside of the Archive.
    */
   private static final ArchivePath PATH_CLASSES = new BasicPath(PATH_WEB, "classes");

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Create a new WebArchive with any type storage engine as backing.
    * 
    * @param delegate The storage backing.
    */
   public WebArchiveImpl(final Archive<?> delegate)
   {
      super(WebArchive.class, delegate);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.declarchive.impl.base.ContainerBase#getManinfestPath()
    */
   @Override
   protected ArchivePath getManinfestPath()
   {
      return PATH_MANIFEST;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.impl.base.ContainerBase#getClassesPath()
    */
   @Override
   protected ArchivePath getClassesPath()
   {
      return PATH_CLASSES;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.impl.base.ContainerBase#getResourcePath()
    */
   @Override
   protected ArchivePath getResourcePath()
   {
      return PATH_RESOURCE;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.impl.base.ContainerBase#getLibraryPath()
    */
   @Override
   protected ArchivePath getLibraryPath()
   {
      return PATH_LIBRARY;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.declarchive.impl.base.WebContainerBase#getWebPath()
    */
   @Override
   protected ArchivePath getWebPath()
   {
      return PATH_WEB;
   }
   
}

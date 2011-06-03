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

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceAdapterContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.test.ArchiveType;
import org.jboss.shrinkwrap.impl.base.test.DynamicResourceAdapterContainerTestBase;
import org.junit.After;
import org.junit.Before;

/**
 * ResourceAdapterArchiveImplTestCase
 *
 * Test to ensure that ResourceAdapterArchiveImpl follow to java rar spec.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
@ArchiveType(ResourceAdapterArchive.class)
public class ResourceAdapterArchiveImplTestCase extends DynamicResourceAdapterContainerTestBase<ResourceAdapterArchive>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final ArchivePath PATH_RESOURCE = new BasicPath("/");

   private static final ArchivePath PATH_MANIFEST = new BasicPath("META-INF");

   private static final ArchivePath PATH_LIBRARY = new BasicPath("/");

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private ResourceAdapterArchive archive;

   //-------------------------------------------------------------------------------------||
   // Lifecycle Methods ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Before
   public void createResourceAdapterArchive() throws Exception
   {
      archive = createNewArchive();
   }

   @After
   public void ls()
   {
      System.out.println("test@jboss:/$ ls -l " + archive.getName());
      System.out.println(archive.toString(true));
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - ArchiveTestBase ---------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /** 
    * Return the current ResourceAdapterArchive
    */
   @Override
   protected ResourceAdapterArchive getArchive()
   {
      return archive;
   }

   /**
    * Create a new instance of a ResourceAdapterArchive
    */
   @Override
   protected ResourceAdapterArchive createNewArchive()
   {
      return ShrinkWrap.create(ResourceAdapterArchive.class);
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicContainerTestBase -------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ClassContainer<ResourceAdapterArchive> getClassContainer()
   {
      throw new UnsupportedOperationException("ResourceAdapterArchive do not support classes");
   }

   @Override
   protected ArchivePath getClassPath()
   {
      throw new UnsupportedOperationException("ResourceAdapterArchive do not support classes");
   }

   @Override
   protected LibraryContainer<ResourceAdapterArchive> getLibraryContainer()
   {
      return archive;
   }
   
   @Override
   protected ServiceProviderContainer<ResourceAdapterArchive> getServiceProviderContainer()
   {
      throw new UnsupportedOperationException("ResourceAdapterArchive do not support service provider classes");
   }

   @Override
   protected ArchivePath getManifestPath()
   {
      return PATH_MANIFEST;
   }

   @Override
   protected ArchivePath getResourcePath()
   {
      return PATH_RESOURCE;
   }

   @Override
   protected ArchivePath getLibraryPath()
   {
      return PATH_LIBRARY;
   }

   @Override
   protected ManifestContainer<ResourceAdapterArchive> getManifestContainer()
   {
      return getArchive();
   }

   @Override
   protected ResourceContainer<ResourceAdapterArchive> getResourceContainer()
   {
      return getArchive();
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicResourceAdapterContainerTestBase ---------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ArchivePath getResourceAdapterPath()
   {
      return getManifestPath();
   }
   
   @Override
   protected ResourceAdapterContainer<ResourceAdapterArchive> getResourceAdapterContainer()
   {
      return getArchive();
   }
}

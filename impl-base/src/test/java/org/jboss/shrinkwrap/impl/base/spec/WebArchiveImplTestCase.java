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

import java.util.UUID;

import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.WebContainer;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.Archives;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.test.ArchiveType;
import org.jboss.shrinkwrap.impl.base.test.DynamicWebContainerTestBase;
import org.junit.After;
import org.junit.Before;

/**
 * WebArchiveImplTestCase
 * 
 * Test case to ensure that the WebArchive follows the War spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
@ArchiveType(WebArchive.class)
public class WebArchiveImplTestCase extends DynamicWebContainerTestBase<WebArchive>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final Path PATH_WEBINF = new BasicPath("WEB-INF");

   private static final Path PATH_LIBRARY = new BasicPath(PATH_WEBINF, "lib");

   private static final Path PATH_CLASSES = new BasicPath(PATH_WEBINF, "classes");

   private static final Path PATH_MANIFEST = new BasicPath("META-INF");

   private static final Path PATH_RESOURCE = new BasicPath();

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private WebArchive archive;

   //-------------------------------------------------------------------------------------||
   // Lifecycle Methods ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Before
   public void createWebArchive() throws Exception
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

   @Override
   protected WebArchive getArchive()
   {
      return archive;
   }

   /**
    * Create a new instance of a WebArchive
    */
   @Override
   protected WebArchive createNewArchive()
   {
      return Archives.create(UUID.randomUUID().toString() + ".jar", WebArchive.class);
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicContainerTestBase ------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ManifestContainer<WebArchive> getManifestContainer()
   {
      return getArchive();
   }

   @Override
   protected ResourceContainer<WebArchive> getResourceContainer()
   {
      return getArchive();
   }

   @Override
   protected ClassContainer<WebArchive> getClassContainer()
   {
      return archive;
   }

   @Override
   protected LibraryContainer<WebArchive> getLibraryContainer()
   {
      return archive;
   }

   @Override
   protected Path getManifestPath()
   {
      return PATH_MANIFEST;
   }

   @Override
   protected Path getResourcePath()
   {
      return PATH_RESOURCE;
   }

   @Override
   protected Path getClassPath()
   {
      return PATH_CLASSES;
   }

   @Override
   protected Path getLibraryPath()
   {
      return PATH_LIBRARY;
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicWebContainerTestBase ---------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   public WebContainer<WebArchive> getWebContainer()
   {
      return getArchive();
   }
   
   @Override
   public Path getWebPath()
   {
      return PATH_WEBINF;
   }
}

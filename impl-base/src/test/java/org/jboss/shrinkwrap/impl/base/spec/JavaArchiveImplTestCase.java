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
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.test.ArchiveType;
import org.jboss.shrinkwrap.impl.base.test.DynamicContainerTestBase;
import org.junit.After;
import org.junit.Before;

/**
 * JavaArchiveImplTestCase
 * 
 * Test case to ensure that the JavaArchive follows the Jar spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
@ArchiveType(JavaArchive.class)
public class JavaArchiveImplTestCase extends DynamicContainerTestBase<JavaArchive>
{
   private static final ArchivePath PATH_MANIFEST = new BasicPath("META-INF");

   private static final ArchivePath PATH_CLASS = new BasicPath("/");

   private static final ArchivePath PATH_RESOURCE = new BasicPath();

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private JavaArchive archive;

   //-------------------------------------------------------------------------------------||
   // Lifecycle Methods ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Before
   public void createArchive()
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
    * Return the archive to super class
    */
   @Override
   protected JavaArchive getArchive()
   {
      return archive;
   }

   /** 
    * Create a new JavaArchive instance
    */
   @Override
   protected JavaArchive createNewArchive()
   {
      return ShrinkWrap.create(JavaArchive.class);
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - ContainerTestBase ---------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ResourceContainer<JavaArchive> getResourceContainer()
   {
      return getArchive();
   }

   @Override
   protected ClassContainer<JavaArchive> getClassContainer()
   {
      return getArchive();
   }

   @Override
   protected ManifestContainer<JavaArchive> getManifestContainer()
   {
      return getArchive();
   }

   @Override
   protected ServiceProviderContainer<JavaArchive> getServiceProviderContainer()
   {
      return getArchive();
   }
   
   @Override
   protected LibraryContainer<JavaArchive> getLibraryContainer()
   {
      throw new UnsupportedOperationException("JavaArchive does not support libraries");
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
   protected ArchivePath getClassPath()
   {
      return PATH_CLASS;
   }

   @Override
   protected ArchivePath getLibraryPath()
   {
      throw new UnsupportedOperationException("JavaArchive does not support libraries");
   }
}

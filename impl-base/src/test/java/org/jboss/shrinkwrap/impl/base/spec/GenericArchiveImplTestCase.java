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
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.impl.base.GenericArchiveImpl;
import org.jboss.shrinkwrap.impl.base.test.ArchiveType;
import org.jboss.shrinkwrap.impl.base.test.DynamicContainerTestBase;
import org.junit.After;
import org.junit.Before;

/**
 * Test case to ensure that the {@link GenericArchiveImpl}
 * is working as contracted
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
@ArchiveType(GenericArchive.class)
public class GenericArchiveImplTestCase extends DynamicContainerTestBase<GenericArchive>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Unsupported operation
    */
   private static final UnsupportedOperationException UNSUPPORTED = new UnsupportedOperationException(
         GenericArchive.class.getSimpleName() + " does not support container spec paths.");

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private GenericArchive archive;

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
   protected GenericArchive getArchive()
   {
      return archive;
   }

   /** 
    * Create a new JavaArchive instance
    */
   @Override
   protected GenericArchive createNewArchive()
   {
      return ShrinkWrap.create(GenericArchive.class);
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - ContainerTestBase ---------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ResourceContainer<GenericArchive> getResourceContainer()
   {
      throw UNSUPPORTED;
   }

   @Override
   protected ClassContainer<GenericArchive> getClassContainer()
   {
      throw UNSUPPORTED;
   }

   @Override
   protected ManifestContainer<GenericArchive> getManifestContainer()
   {
      throw UNSUPPORTED;
   }

   @Override
   protected LibraryContainer<GenericArchive> getLibraryContainer()
   {
      throw UNSUPPORTED;
   }
   
   @Override
   protected ServiceProviderContainer<GenericArchive> getServiceProviderContainer()
   {
      throw UNSUPPORTED;
   }

   @Override
   protected ArchivePath getManifestPath()
   {
      throw UNSUPPORTED;
   }

   @Override
   protected ArchivePath getResourcePath()
   {
      throw UNSUPPORTED;
   }

   @Override
   protected ArchivePath getClassPath()
   {
      throw UNSUPPORTED;
   }

   @Override
   protected ArchivePath getLibraryPath()
   {
      throw UNSUPPORTED;
   }
}

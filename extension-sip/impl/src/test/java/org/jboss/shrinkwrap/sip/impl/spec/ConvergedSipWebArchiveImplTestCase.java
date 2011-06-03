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
package org.jboss.shrinkwrap.sip.impl.spec;

import java.util.UUID;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.ClassContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.impl.base.test.ArchiveType;
import org.jboss.shrinkwrap.sip.api.container.ConvergedSipWebContainer;
import org.jboss.shrinkwrap.sip.api.spec.ConvergedSipWebArchive;
import org.jboss.shrinkwrap.sip.impl.test.DynamicConvergedSipWebContainerTestBase;
import org.junit.After;
import org.junit.Before;

/**
 * ConvergedSipWebArchiveImplTestCase
 * 
 * Test case to ensure that the ConvergedSipWebArchive follows the Sip Servlets spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author Jean Deruelle
 * @version $Revision: $
 */
@ArchiveType(ConvergedSipWebArchive.class)
public class ConvergedSipWebArchiveImplTestCase extends DynamicConvergedSipWebContainerTestBase<ConvergedSipWebArchive>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final ArchivePath PATH_WEBINF = ArchivePaths.create("WEB-INF");

   private static final ArchivePath PATH_LIBRARY = ArchivePaths.create(PATH_WEBINF, "lib");

   private static final ArchivePath PATH_CLASSES = ArchivePaths.create(PATH_WEBINF, "classes");

   private static final ArchivePath PATH_MANIFEST = ArchivePaths.create(PATH_CLASSES, "META-INF");

   private static final ArchivePath PATH_RESOURCE = ArchivePaths.root();;

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private ConvergedSipWebArchive archive;

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
   protected ConvergedSipWebArchive getArchive()
   {
      return archive;
   }

   /**
    * Create a new instance of a WebArchive
    */
   @Override
   protected ConvergedSipWebArchive createNewArchive()
   {
      return ShrinkWrap.create(ConvergedSipWebArchive.class, UUID.randomUUID().toString() + ".jar");
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicContainerTestBase ------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ManifestContainer<ConvergedSipWebArchive> getManifestContainer()
   {
      return getArchive();
   }

   @Override
   protected ServiceProviderContainer<ConvergedSipWebArchive> getServiceProviderContainer()
   {
      return getArchive();
   }
   
   @Override
   protected ResourceContainer<ConvergedSipWebArchive> getResourceContainer()
   {
      return getArchive();
   }

   @Override
   protected ClassContainer<ConvergedSipWebArchive> getClassContainer()
   {
      return archive;
   }

   @Override
   protected LibraryContainer<ConvergedSipWebArchive> getLibraryContainer()
   {
      return archive;
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
      return PATH_CLASSES;
   }

   @Override
   protected ArchivePath getLibraryPath()
   {
      return PATH_LIBRARY;
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicConvergedSipWebContainerTestBase----------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   public ConvergedSipWebContainer<ConvergedSipWebArchive> getConvergedSipWebContainer()
   {
      return getArchive();
   }
   
   @Override
   public ArchivePath getWebPath()
   {
      return PATH_WEBINF;
   }
}

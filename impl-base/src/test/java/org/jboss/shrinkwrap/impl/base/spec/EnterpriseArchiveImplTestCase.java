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
import org.jboss.shrinkwrap.api.container.EnterpriseContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;
import org.jboss.shrinkwrap.api.container.ServiceProviderContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.test.ArchiveType;
import org.jboss.shrinkwrap.impl.base.test.DynamicEnterpriseContainerTestBase;
import org.junit.After;
import org.junit.Before;

/**
 * EnterpriseArchiveImplTest
 *
 * Test to ensure that EnterpriseArchiveImpl follow to java ear spec.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
@ArchiveType(EnterpriseArchive.class)
public class EnterpriseArchiveImplTestCase extends DynamicEnterpriseContainerTestBase<EnterpriseArchive>
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   private static final ArchivePath PATH_APPLICATION = new BasicPath("META-INF");

   private static final ArchivePath PATH_LIBRARY = new BasicPath("lib");

   private static final ArchivePath PATH_MODULE = new BasicPath("/");

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private EnterpriseArchive archive;

   //-------------------------------------------------------------------------------------||
   // Lifecycle Methods ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Before
   public void createEnterpriseArchive() throws Exception
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
    * Return the current EnterpriseArchive
    */
   @Override
   protected EnterpriseArchive getArchive()
   {
      return archive;
   }

   /**
    * Create a new instance of a EnterpriseArchive
    */
   @Override
   protected EnterpriseArchive createNewArchive()
   {
      return ShrinkWrap.create(EnterpriseArchive.class);
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicContainerTestBase ------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   protected ClassContainer<EnterpriseArchive> getClassContainer()
   {
      throw new UnsupportedOperationException("EnterpriseArchives do not support classes");
   }

   @Override
   protected ArchivePath getClassPath()
   {
      throw new UnsupportedOperationException("EnterpriseArchives do not support classes");
   }

   @Override
   protected ServiceProviderContainer<EnterpriseArchive> getServiceProviderContainer()
   {
      throw new UnsupportedOperationException("EnterpriseArchives do not support service provider classes");
   }
   
   @Override
   protected LibraryContainer<EnterpriseArchive> getLibraryContainer()
   {
      return archive;
   }

   @Override
   protected ArchivePath getManifestPath()
   {
      return PATH_APPLICATION;
   }

   @Override
   protected ArchivePath getResourcePath()
   {
      return PATH_APPLICATION;
   }

   @Override
   protected ArchivePath getLibraryPath()
   {
      return PATH_LIBRARY;
   }

   @Override
   protected ManifestContainer<EnterpriseArchive> getManifestContainer()
   {
      return getArchive();
   }

   @Override
   protected ResourceContainer<EnterpriseArchive> getResourceContainer()
   {
      return getArchive();
   }

   //-------------------------------------------------------------------------------------||
   // Required Impls - DynamicEnterpriseContainerTestBase --------------------------------||
   //-------------------------------------------------------------------------------------||
   
   protected ArchivePath getModulePath() {
      return PATH_MODULE;
   }

   protected ArchivePath getApplicationPath() {
      return PATH_APPLICATION;
   }
   
   @Override
   protected EnterpriseContainer<EnterpriseArchive> getEnterpriseContainer()
   {
      return getArchive();
   }
}

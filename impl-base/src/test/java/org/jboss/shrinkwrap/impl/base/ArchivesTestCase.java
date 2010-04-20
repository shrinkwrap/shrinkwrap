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
package org.jboss.shrinkwrap.impl.base;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;
import org.junit.Test;


/**
 * ArchivesTest
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @deprecated To be moved / replaced by the {@link ShrinkWrapTestCase}
 */
@Deprecated
public class ArchivesTestCase
{
   @Test
   public void shouldBeAbleToCreateANewArchive() throws Exception {
      String archiveName = "test.war";
      JavaArchive archive = Archives.create(archiveName, JavaArchive.class);
      
      Assert.assertNotNull(
            "A archive should have been created", archive);
      
      Assert.assertEquals(
            "Should have the same name as given imput", 
            archiveName, 
            archive.getName());
   }
   
   @Test
   public void shouldBeAbleToAddOverride() throws Exception {
      Archives.addExtensionOverride(JavaArchive.class, MockJavaArchiveImpl.class);
      JavaArchive archive = Archives.create("test.jar", JavaArchive.class);
      
      Assert.assertEquals(
            "Should have overridden normal JavaArchive impl", 
            MockJavaArchiveImpl.class, archive.getClass());
      
   }
   
   public static class MockJavaArchiveImpl extends ContainerBase<JavaArchive> implements JavaArchive {

      public MockJavaArchiveImpl(Archive<?> archive)
      {
         super(JavaArchive.class, archive);
      }

      @Override
      protected ArchivePath getClassesPath()
      {
         return ArchivePaths.root();
      }

      @Override
      protected ArchivePath getLibraryPath()
      {
         return ArchivePaths.root();
      }

      @Override
      protected ArchivePath getManinfestPath()
      {
         return ArchivePaths.root();
      }

      @Override
      protected ArchivePath getResourcePath()
      {
         return ArchivePaths.root();
      }

      @Override
      public String toString(final Formatter formatter) throws IllegalArgumentException
      {
         return formatter.format(this);
      }
   }
}

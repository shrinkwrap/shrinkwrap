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

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.ExtensionLoader;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.Paths;
import org.jboss.shrinkwrap.api.Specializer;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;
import org.jboss.shrinkwrap.impl.base.spec.JavaArchiveImpl;
import org.junit.Test;


/**
 * ArchivesTest
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ArchivesTestCase
{
   @Test
   public void shouldBeAbleToCreateANewArchive() throws Exception {
      String archiveName = "test.war";
      reset();
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
      reset();
      Archives.addExtensionOverride(JavaArchive.class, MockJavaArchiveImpl.class);
      
      JavaArchive archive = Archives.create("test.jar", JavaArchive.class);
      
      Assert.assertEquals(
            "Should have overridden normal JavaArchive impl", 
            MockJavaArchiveImpl.class, archive.getClass());
      
   }

   private static boolean extensionLoaderCalled = false;   
   
   @Test
   public void shouldBeAbleToSetExtensionLoader() throws Exception {
      reset();
      Archives.setExtensionLoader(new ExtensionLoader()
      {
         @Override
         public <T extends Specializer> T load(Class<T> extensionClass,
               Archive<?> baseArchive)
         {
            extensionLoaderCalled = true;
            return (T)new JavaArchiveImpl(baseArchive);
         }
         
         @Override
         public <T extends Specializer> ExtensionLoader addOverride(
               Class<T> extensionClass, Class<? extends T> extensionImplClass)
         {
            return null;
         }
      });
      Archives.create("test.jar", JavaArchive.class);

      Assert.assertTrue(
            "Specified ExtensionLoader should have been used", 
            extensionLoaderCalled);
   }

   @Test(expected = IllegalArgumentException.class)
   public void shouldNotBeAbleToSetExtensionLoaderNull() throws Exception {
      reset();
      Archives.setExtensionLoader(null);
   }

   @Test(expected = IllegalStateException.class)
   public void shouldNotBeAbleToSetExtensionLoaderAfterInitialized() throws Exception {
      reset();
      Archives.create("test.jar", JavaArchive.class);
      Archives.setExtensionLoader(new ExtensionLoader()
      {
         @Override
         public <T extends Specializer> T load(Class<T> extensionClass,
               Archive<?> baseArchive)
         {
            return null;
         }
         
         @Override
         public <T extends Specializer> ExtensionLoader addOverride(
               Class<T> extensionClass, Class<? extends T> extensionImplClass)
         {
            return null;
         }
      });
   }
   
   private void reset() throws Exception {
      
      Method method = AccessController.doPrivileged(new PrivilegedExceptionAction<Method>()
      {
         @Override
         public Method run() throws Exception
         {
            Method method = Archives.class.getDeclaredMethod("resetState", new Class<?>[]{});
            method.setAccessible(true);
            return method;
         }
      });
      method.invoke(null);
   }
   
   public static class MockJavaArchiveImpl extends ContainerBase<JavaArchive> implements JavaArchive {

      public MockJavaArchiveImpl(Archive<?> archive)
      {
         super(JavaArchive.class, archive);
      }

      @Override
      protected Path getClassesPath()
      {
         return Paths.root();
      }

      @Override
      protected Path getLibraryPath()
      {
         return Paths.root();
      }

      @Override
      protected Path getManinfestPath()
      {
         return Paths.root();
      }

      @Override
      protected Path getResourcePath()
      {
         return Paths.root();
      }
   }
}

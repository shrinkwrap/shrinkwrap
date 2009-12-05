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
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;


/**
 * ServiceExtensionLoaderTestCase
 * 
 * Test to ensure the behaviour of ServiceExtensionLoader
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ServiceExtensionLoaderTestCase
{

   @Test
   public void shouldBeAbleToLoadExtension() throws Exception {
      Extension extension = new ServiceExtensionLoader()
               .load(Extension.class, Archives.create("test.jar", JavaArchive.class));

      Assert.assertNotNull(extension);
      
      Assert.assertTrue(extension.getClass() == ExtensionImpl.class);
   }

   @Test
   public void shouldBeAbleToOverrideExtension() throws Exception {
      Extension extension = new ServiceExtensionLoader()
         .addOverride(Extension.class, ExtensionImpl2.class)
         .load(Extension.class, Archives.create("test.jar", JavaArchive.class));

      Assert.assertNotNull(extension);
   
      Assert.assertTrue(extension.getClass() == ExtensionImpl2.class);
   }
   
   @Test
   public void shouldBePlacedInCacheAfterLoad() throws Exception {
      ServiceExtensionLoader loader = new ServiceExtensionLoader();
      loader.load(Extension.class, Archives.create("test.jar", JavaArchive.class));
      
      Assert.assertTrue(
            "Should be placed in cache",
            loader.isCached(Extension.class)
      );      
   }
   
   @Test(expected = RuntimeException.class)
   public void shouldThrowExceptionOnMissingExtension() throws Exception {
      new ServiceExtensionLoader()
         .load(MissingExtension.class, Archives.create("test.jar", JavaArchive.class));
   }

   @Test(expected = RuntimeException.class)
   public void shouldThrowExceptionOnWrongImplType() throws Exception {
      new ServiceExtensionLoader()
         .load(WrongImplExtension.class, Archives.create("test.jar", JavaArchive.class));
   }

   
   public static interface WrongImplExtension extends Assignable {
      
   }
   
   public static interface Extension extends Assignable {
      
   }

   public static class ExtensionImpl extends AssignableBase implements Extension {

      private Archive<?> archive;
      public ExtensionImpl(Archive<?> archive)
      {
         this.archive = archive;
      }
      
      @Override
      protected Archive<?> getArchive()
      {
         return archive;
      }
   }

   public static class ExtensionImpl2 extends AssignableBase implements Extension {

      private Archive<?> archive;
      public ExtensionImpl2(Archive<?> archive)
      {
         this.archive = archive;
      }
      
      @Override
      protected Archive<?> getArchive()
      {
         return archive;
      }
   }
   
   public static interface MissingExtension extends Assignable {
      
   }
}

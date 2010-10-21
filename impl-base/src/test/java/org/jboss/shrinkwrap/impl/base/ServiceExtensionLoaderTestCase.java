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

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.UnknownExtensionTypeException;
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
   public void shouldBeAbleToLoadExtension() throws Exception
   {
      Extension extension = new ServiceExtensionLoader().load(Extension.class,
            ShrinkWrap.create(JavaArchive.class, "test.jar"));

      Assert.assertNotNull(extension);

      Assert.assertTrue(extension.getClass() == ExtensionImpl.class);
   }

   /**
    * SHRINKWRAP-234
    * 
    * Ensures we don't use a cached ClassLoader in loading extension
    * types; instead look to TCCL
    */
   @Test
   public void useTcclInLoading()
   {

      // Initialize a loader
      final ServiceExtensionLoader loader = new ServiceExtensionLoader();
      final JavaArchive archive = ShrinkWrap.create(JavaArchive.class);

      // First ensure we can't load the Mock Archive.  Only our special ClassLoader 
      // will know how.
      boolean gotExpectedError = false;
      try
      {
         loader.load(MockArchive.class, archive);
      }
      catch (final UnknownExtensionTypeException uete)
      {
         gotExpectedError = true;
      }
      TestCase.assertTrue("Test setup is broken, should not be able to load mock archive", gotExpectedError);

      // Make a mock ClassLoader which knows how to handle our stuff
      final ClassLoader cl = new MockArchiveDescriptorNameAdjustingClassLoader();
      final ClassLoader originalCl = SecurityActions.getThreadContextClassLoader();
      // Set the CL as TCCL
      setTccl(cl);
      try
      {
         // Try to load, using our special CL as TCCL, proving that the CL used by the loader is
         // no longer cached
         final Archive<?> loaded = loader.load(MockArchive.class, archive);
         TestCase.assertNotNull("Archive should have been loaded", loaded);
      }
      finally
      {
         setTccl(originalCl);
      }

   }

   @Test
   public void shouldBeAbleToOverrideExtension() throws Exception
   {
      Extension extension = new ServiceExtensionLoader().addOverride(Extension.class, ExtensionImpl2.class).load(
            Extension.class, ShrinkWrap.create(JavaArchive.class, "test.jar"));

      Assert.assertNotNull(extension);

      Assert.assertTrue(extension.getClass() == ExtensionImpl2.class);
   }

   @Test
   public void shouldBePlacedInCacheAfterLoad() throws Exception
   {
      ServiceExtensionLoader loader = new ServiceExtensionLoader();
      loader.load(Extension.class, ShrinkWrap.create(JavaArchive.class, "test.jar"));

      Assert.assertTrue("Should be placed in cache", loader.isCached(Extension.class));
   }

   @Test(expected = RuntimeException.class)
   public void shouldThrowExceptionOnMissingExtension() throws Exception
   {
      new ServiceExtensionLoader().load(MissingExtension.class, ShrinkWrap.create(JavaArchive.class, "test.jar"));
   }

   @Test(expected = RuntimeException.class)
   public void shouldThrowExceptionOnWrongImplType() throws Exception
   {
      new ServiceExtensionLoader().load(WrongImplExtension.class, ShrinkWrap.create(JavaArchive.class, "test.jar"));
   }

   public static interface WrongImplExtension extends Assignable
   {

   }

   public static interface Extension extends Assignable
   {

   }

   public static class ExtensionImpl extends AssignableBase<Archive<?>> implements Extension
   {
      public ExtensionImpl(Archive<?> archive)
      {
         super(archive);
      }
   }

   public static class ExtensionImpl2 extends AssignableBase<Archive<?>> implements Extension
   {
      public ExtensionImpl2(Archive<?> archive)
      {
         super(archive);
      }
   }

   public static interface MissingExtension extends Assignable
   {

   }
   
   /**
    * A {@link ClassLoader} which maps requests for {@link MockArchive} to its hidden, 
    * adjusted name such that only this CL may be used in loading it via the 
    * {@link ServiceExtensionLoader}. 
    *
    */
   private static class MockArchiveDescriptorNameAdjustingClassLoader extends URLClassLoader
   {

      /**
       * Name of the archive descriptor name we'll adjust
       */
      private static final String MOCK_ARCHIVE_DESCRIPTOR_NAME = "META-INF/services/" + MockArchive.class.getName();

      /**
       * Suffix we append to the mock descriptor so that only this CL will see it
       */
      private static final String MOCK_ARCHIVE_DESCRIPTOR_SUFFIX = "-MockClassLoader";

      public MockArchiveDescriptorNameAdjustingClassLoader()
      {
         super(new URL[]
         {}, SecurityActions.getThreadContextClassLoader());
      }

      /**
       * Returns a descriptor if the requested resource is for a 
       * META-INF/services/{@link MockArchive}
       * @see java.net.URLClassLoader#findResource(java.lang.String)
       */
      @Override
      public Enumeration<URL> getResources(String name) throws IOException
      {
         // Determine if we're asking for the mock archive descriptor
         if (name.equals(MOCK_ARCHIVE_DESCRIPTOR_NAME))
         {
            // Add in the suffix and look for that
            name = name + MOCK_ARCHIVE_DESCRIPTOR_SUFFIX;
         }

         // Use the super implementation
         return super.getResources(name);
      }
   }
   
   /**
    * Sets the TCCL to that supplied
    * @param cl
    */
   private static void setTccl(final ClassLoader cl)
   {
      assert cl != null : "CL must be supplied.";
      AccessController.doPrivileged(new PrivilegedAction<Void>()
      {

         @Override
         public Void run()
         {
            Thread.currentThread().setContextClassLoader(cl);

            // Return
            return null;
         }
      });
   }
}

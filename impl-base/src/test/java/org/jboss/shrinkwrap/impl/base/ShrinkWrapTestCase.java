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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.Configuration;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ExtensionLoader;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

/**
 * Tests ensuring that the static entry point {@link ShrinkWrap}
 * is working as contracted.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ShrinkWrapTestCase
{

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures we can create a new archive under the default {@link Domain}
    */
   @Test
   public void createNewArchiveUnderDefaultDomain()
   {
      final String archiveName = "test.war";
      final JavaArchive archive = ShrinkWrap.create(archiveName, JavaArchive.class);

      // Test
      Assert.assertNotNull("A archive should have been created", archive);
      Assert.assertEquals("Should have the same name as given imput", archiveName, archive.getName());
   }

   /**
    * Ensures that we can create isolated {@link Domain}s
    */
   @Test
   public void createIsolatedDomains()
   {
      // Make a couple domains
      final Domain domain1 = ShrinkWrap.createDomain();
      final Domain domain2 = ShrinkWrap.createDomain();

      // Ensure they exist
      TestCase.assertNotNull("Domain should exist", domain1);
      TestCase.assertNotNull("Domain should exist", domain2);

      // Ensure they're not equal
      TestCase.assertNotSame("Creation of domains should return new instances", domain1, domain2);

      // Ensure the underlying configs are not equal
      TestCase.assertNotSame("Creation of domains should have unique / isolated configurations", domain1
            .getConfiguration(), domain2.getConfiguration());
   }

   /**
    * Ensures that we can create a new {@link Domain} with explicit
    * {@link Configuration}
    */
   @Test
   public void createDomainWithExplicitConfiguration()
   {
      // Define configuration properties
      final ExecutorService service = Executors.newSingleThreadExecutor();
      final ExtensionLoader loader = new MockExtensionLoader();

      // Create a new domain using these config props in a config 
      final Domain domain = ShrinkWrap.createDomain(new ConfigurationBuilder().executorService(service)
            .extensionLoader(loader).build());

      // Test
      TestCase.assertEquals(ExecutorService.class.getSimpleName() + " specified was not contained in resultant "
            + Domain.class.getSimpleName(), service, domain.getConfiguration().getExecutorService());
      TestCase.assertEquals(ExtensionLoader.class.getSimpleName() + " specified was not contained in resultant "
            + Domain.class.getSimpleName(), loader, domain.getConfiguration().getExtensionLoader());

   }

   /**
    * Ensures that we can create a new {@link Domain} with explicit
    * {@link ConfigurationBuilder}
    */
   @Test
   public void createDomainWithExplicitConfigurationBuilder()
   {
      // Define configuration properties
      final ExecutorService service = Executors.newSingleThreadExecutor();
      final ExtensionLoader loader = new MockExtensionLoader();

      // Create a new domain using these config props in a builder
      final Domain domain = ShrinkWrap.createDomain(new ConfigurationBuilder().executorService(service)
            .extensionLoader(loader));

      // Test
      TestCase.assertEquals(ExecutorService.class.getSimpleName() + " specified was not contained in resultant "
            + Domain.class.getSimpleName(), service, domain.getConfiguration().getExecutorService());
      TestCase.assertEquals(ExtensionLoader.class.getSimpleName() + " specified was not contained in resultant "
            + Domain.class.getSimpleName(), loader, domain.getConfiguration().getExtensionLoader());

   }

   /**
    * Ensures we cannot create a new {@link Domain} with null 
    * {@link Configuration} specified
    */
   @Test(expected = IllegalArgumentException.class)
   public void newDomainRequiresConfiguration()
   {
      ShrinkWrap.createDomain((Configuration) null);
   }

   /**
    * Ensures we cannot create a new {@link Domain} with null 
    * {@link ConfigurationBuilder} specified
    */
   @Test(expected = IllegalArgumentException.class)
   public void newDomainRequiresConfigurationBuilder()
   {
      ShrinkWrap.createDomain((ConfigurationBuilder) null);
   }

   /**
    * Ensures all calls to get the default domain return the same reference
    */
   @Test
   public void getDefaultDomain()
   {
      // Get the default domain twice
      final Domain domain1 = ShrinkWrap.getDefaultDomain();
      final Domain domain2 = ShrinkWrap.getDefaultDomain();

      // Ensure they exist
      TestCase.assertNotNull("Domain should exist", domain1);
      TestCase.assertNotNull("Domain should exist", domain2);

      // Ensure they're not equal
      TestCase.assertSame("Obtaining the default domain should always return the same instance (idempotent operation)",
            domain1, domain2);

   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Members ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * A Mock {@link ExtensionLoader} used only in testing reference equality
    *
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    * @version $Revision: $
    */
   private static class MockExtensionLoader implements ExtensionLoader
   {
      @Override
      public <T extends Assignable> T load(final Class<T> extensionClass, final Archive<?> baseArchive)
      {
         return null;
      }

      @Override
      public <T extends Assignable> ExtensionLoader addOverride(final Class<T> extensionClass,
            final Class<? extends T> extensionImplClass)
      {
         return null;
      }
   }

}

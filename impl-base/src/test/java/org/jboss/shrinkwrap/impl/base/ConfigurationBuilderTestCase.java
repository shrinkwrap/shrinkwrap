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

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.Configuration;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.ExtensionLoader;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests to ensure the {@link ConfigurationBuilder} is working 
 * as contracted
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ConfigurationBuilderTestCase
{

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The builder under test
    */
   private ConfigurationBuilder builder;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates the {@link ConfigurationBuilder} instance to be tested
    */
   @Before
   public void createDefaultBuilder()
   {
      builder = new ConfigurationBuilder();
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that a null {@link ExecutorService} is not defaulted
    * to any value, and may remain null
    */
   @Test
   public void defaultsExecutorService()
   {
      // Build and default
      builder.build();

      // Get the defaulted service
      final ExecutorService service = builder.getExecutorService();

      // Test
      Assert.assertNull("The builder should not default an " + ExecutorService.class.getSimpleName(), service);
   }

   /**
    * Ensures that the {@link ExtensionLoader} is defaulted
    * as contracted to a new instance.
    */
   @Test
   public void defaultsExtensionLoader()
   {
      // Build and default
      builder.build();

      final ExtensionLoader loader = builder.getExtensionLoader();
      Assert.assertNotNull("The builder should default an " + ExtensionLoader.class.getSimpleName(), loader);
   }

   /**
    * Ensures that building does not override a user-supplied 
    * {@link ExecutorService}
    */
   @Test
   public void allowsUserDefinedExecutorService()
   {
      // Define a custom ES
      final ExecutorService service = Executors.newSingleThreadExecutor();

      // Supply and build
      builder.executorService(service).build();

      // Test
      TestCase.assertEquals("Building should not override the user-supplied " + ExecutorService.class.getSimpleName(),
            service, builder.getExecutorService());
   }

   /**
    * Ensures that building does not override a user-supplied 
    * {@link ExtensionLoader}
    */
   @Test
   public void allowsUserDefinedExtensionLoader()
   {
      // Define a custom EL
      final ExtensionLoader loader = new ExtensionLoader()
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

         @Override
         public <T extends Assignable> String getExtensionFromExtensionMapping(Class<T> type)
         {
            return null;
         }

         @Override
         public <T extends Archive<T>> ArchiveFormat getArchiveFormatFromExtensionMapping(Class<T> extensionClass)
         {
            return null;
         }
      };

      // Supply and build
      builder.extensionLoader(loader).build();

      // Test
      TestCase.assertEquals("Building should not override the user-supplied " + ExtensionLoader.class.getSimpleName(),
            loader, builder.getExtensionLoader());
   }

   /**
    * Ensures that invoking {@link ConfigurationBuilder#build()}
    * creates a {@link Configuration} with the same properties
    * as in the builder
    */
   @Test
   public void createsConfiguration()
   {
      // Create the config
      final Configuration configuration = builder.build();

      // Grab props from the builder
      final ExecutorService service = builder.getExecutorService();
      final ExtensionLoader loader = builder.getExtensionLoader();

      // Test that they match the props in the config
      TestCase.assertEquals(
            ExecutorService.class.getSimpleName() + " in the config does not match that in the builder", service,
            configuration.getExecutorService());
      TestCase.assertEquals(
            ExtensionLoader.class.getSimpleName() + " in the config does not match that in the builder", loader,
            configuration.getExtensionLoader());
   }

}

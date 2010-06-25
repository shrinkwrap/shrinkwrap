/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api;

import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Mutable construction object for new instances of {@link Configuration}.
 * Provides defaults for each property if not specified (null) according to the following:
 * 
 * <ul>
 *   <li><code>executorService</code> - Stay null, none is required and ShrinkWrap will create its own and destroy it when done as needed</li>
 *   <li><code>extensionLoader</code> - A new instance of the service extension loader from shrinkwrap-impl</li>
 * </ul>
 * 
 * Not thread-safe.  When done altering properties here, a new configuration may be
 * constructed by calling upon {@link ConfigurationBuilder#build()}.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public class ConfigurationBuilder
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ConfigurationBuilder.class.getName());

   /**
    * Implementation class name of the default {@link ExtensionLoader} to be used
    */
   private static final String EXTENSION_LOADER_IMPL = "org.jboss.shrinkwrap.impl.base.ServiceExtensionLoader";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Loader mapping archive types to the appropriate underlying implementation
    */
   private ExtensionLoader extensionLoader;

   /**
    * {@link ExecutorService} used for all asynchronous operations 
    */
   private ExecutorService executorService;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new builder initialized to defaults (null) values.  Any properties
    * not explicitly provided by the user will be defaulted during 
    * {@link ConfigurationBuilder#build()}.
    */
   public ConfigurationBuilder()
   {
   }

   //-------------------------------------------------------------------------------------||
   // Functional Methods ----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||   

   /**
    * @return the extensionLoader
    */
   public ExtensionLoader getExtensionLoader()
   {
      return extensionLoader;
   }

   /**
    * @return the executorService
    */
   public ExecutorService getExecutorService()
   {
      return executorService;
   }


   /**
    * Sets the {@link ExtensionLoader} to be used, returning this instance
    * 
    * @param extensionLoader
    * @return
    */
   public ConfigurationBuilder extensionLoader(final ExtensionLoader extensionLoader)
   {
      this.extensionLoader = extensionLoader;
      return this;
   }

   /**
    * Sets the {@link ExecutorService} to be used, returning this instance
    * @param executorService
    * @return
    */
   public ConfigurationBuilder executorService(final ExecutorService executorService)
   {
      this.executorService = executorService;
      return this;
   }

   /**
    * Builds a new {@link Configuration} using the properties contained
    * in this builder.  In the case a property has not been specified, it will be defaulted
    * according to the rules set forth in this {@link ConfigurationBuilder}'s contract.
    * @return
    */
   public Configuration build()
   {
      // First set all defaults if not explicitly provided by the user
      this.setDefaults();

      // Make a new configuration and return it.
      return new Configuration(this);
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||   

   /**
    * Sets properties to their default values if they haven't been explicitly
    * provided by the user
    */
   private void setDefaults()
   {
      // If no extension loader is present, create one
      if (getExtensionLoader() == null)
      {
         final ExtensionLoader loader = createDefaultExtensionLoader();
         if (log.isLoggable(Level.FINER))
         {
            log.finer("User has not defined an explicit " + ExtensionLoader.class.getSimpleName() + "; defaulting to "
                  + loader);
         }
         this.extensionLoader(loader);
      }
   }

   /**
    * Obtains the default {@link ExtensionLoader} to be used if none
    * is specified
    * @return
    */
   ExtensionLoader createDefaultExtensionLoader()
   {
      return SecurityActions.newInstance(EXTENSION_LOADER_IMPL, new Class<?>[]
      {}, new Object[]
      {}, ExtensionLoader.class);
   }

}

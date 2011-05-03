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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
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

   /**
    * {@link ClassLoader}s used for extension loading, adding resources, etc
    */
   private Iterable<ClassLoader> classLoaders;

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
    * @return
    */
   public Iterable<ClassLoader> getClassLoaders()
   {
      return classLoaders;
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
    * Sets the {@link ClassLoader} used in resolving extension implementations
    * by the {@link ExtensionLoader}; other tasks requiring a CL by the 
    * {@link Archive}
    * @param classLoaders
    * @return
    */
   public ConfigurationBuilder classLoaders(final Iterable<ClassLoader> classLoaders)
   {
      this.classLoaders = classLoaders;
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
      // Make a new configuration and return it.
      return new Configuration(this);
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||   

   /**
    * Sets properties to their default values if they haven't been explicitly
    * provided by the user. If no ClassLoaders are specified, use the TCCL.
    */
   void setDefaults()
   {
      // If no ClassLoaders are specified, use the TCCL
      // Ensure we default this BEFORE defaulting the extension loader, as 
      // the loader needs a CL to be created
      if (this.getClassLoaders() == null)
      {
         final ClassLoader tccl = SecurityActions.getThreadContextClassLoader();
         if (log.isLoggable(Level.FINER))
         {
            log.finer("User has not defined an explicit " + ClassLoader.class.getSimpleName()
                  + "; defaulting to the TCCL: " + tccl);
         }
         final Collection<ClassLoader> tcclCollection = new ArrayList<ClassLoader>(1);
         // Adjust for the bootstrap CL, which may be denoted as null in some JVM impls
         if (tccl != null)
         {
            tcclCollection.add(tccl);
         }
         else
         {
            tcclCollection.add(ClassLoader.getSystemClassLoader());
         }
         this.classLoaders = tcclCollection;
      }

      // Adjust the CLs to be sure of no duplicate or null references (which may indicate
      // the bootstrap CL, so get to the system CL)
      final Collection<ClassLoader> adjustedCls = new HashSet<ClassLoader>();
      for (ClassLoader cl : this.classLoaders)
      {

         // Adjust for null references
         if (cl == null)
         {
            cl = ClassLoader.getSystemClassLoader();
         }

         // Add to the set, which will restrict duplicates
         adjustedCls.add(cl);
      }
      this.classLoaders = adjustedCls;

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
      // First find the right Class/ClassLoader
      final Class<?> extensionLoaderImplClass;
      try
      {
         extensionLoaderImplClass = ClassLoaderSearchUtil.findClassFromClassLoaders(EXTENSION_LOADER_IMPL,
               getClassLoaders());;
      }
      catch (final ClassNotFoundException cnfe)
      {
         throw new IllegalStateException(
               "Could not find extension loader impl class in any of the configured ClassLoaders", cnfe);
      }

      // Return
      return SecurityActions.newInstance(extensionLoaderImplClass, new Class<?>[]
      {Iterable.class}, new Object[]
      {this.getClassLoaders()}, ExtensionLoader.class);
   }

}

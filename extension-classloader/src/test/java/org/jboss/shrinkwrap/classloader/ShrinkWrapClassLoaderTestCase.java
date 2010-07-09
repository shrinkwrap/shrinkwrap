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
package org.jboss.shrinkwrap.classloader;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures the {@link ShrinkWrapClassLoader}
 * is working as contracted
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ShrinkWrapClassLoaderTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ShrinkWrapClassLoaderTestCase.class.getName());

   /**
    * Class to be accessed via a ShrinkWrap ClassLoader
    */
   private static final Class<?> applicationClassLoaderClass = LoadedTestClass.class;

   /**
    * Archive to be read via a {@link ShrinkWrapClassLoaderTestCase#shrinkWrapClassLoader}
    */
   private static final JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
         .addClass(applicationClassLoaderClass);

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * ClassLoader used to load {@link ShrinkWrapClassLoaderTestCase#applicationClassLoaderClass}
    */
   private ClassLoader shrinkWrapClassLoader;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates the {@link ShrinkWrapClassLoaderTestCase#shrinkWrapClassLoader}
    * used to load classes from an {@link Archive}.  The {@link ClassLoader}
    * will be isolated from the application classpath by specifying a
    * null parent explicitly.
    */
   @Before
   public void createClassLoader()
   {
      shrinkWrapClassLoader = new ShrinkWrapClassLoader((ClassLoader) null, archive);
   }

   /**
    * Closes resources associated with the 
    * {@link ShrinkWrapClassLoaderTestCase#shrinkWrapClassLoader}
    */
   @After
   public void closeClassLoader()
   {
      if (shrinkWrapClassLoader instanceof Closeable)
      {
         try
         {
            ((Closeable) shrinkWrapClassLoader).close();
         }
         catch (final IOException e)
         {
            log.warning("Could not close the " + shrinkWrapClassLoader + ": " + e);
         }
      }
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures we can load a Class instance from the {@link ShrinkWrapClassLoader}
    */
   @Test
   public void shouldBeAbleToLoadClassFromArchive() throws ClassNotFoundException
   {
      // Load the test class from the CL
      final Class<?> loadedTestClass = Class.forName(
            applicationClassLoaderClass.getName(), 
            false,
            shrinkWrapClassLoader);
      
      final ClassLoader loadedTestClassClassLoader = loadedTestClass.getClassLoader();
      log.info("Got " + loadedTestClass + " from " + loadedTestClassClassLoader);

      // Assertions
      Assert.assertNotNull(
            "Test class could not be found via the ClassLoader", 
            loadedTestClass);
      
      Assert.assertSame(
            "Test class should have been loaded via the archive ClassLoader", 
            shrinkWrapClassLoader,
            loadedTestClassClassLoader);
      
      Assert.assertNotSame(
            "Class Loaded from the CL should not be the same as the one on the appCL", 
            loadedTestClass,
            applicationClassLoaderClass);

   }

   /**
    * Ensures we can load a resource by name from the {@link ShrinkWrapClassLoader}
    */
   @Test
   public void shouldBeAbleToLoadResourceFromArchive()
   {

      // Load the class as a resource
      final URL resource = shrinkWrapClassLoader.getResource(
            getResourceNameOfClass(
                  applicationClassLoaderClass));

      // Assertions
      Assert.assertNotNull(resource);

   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the resource name for a given class
    */
   private static String getResourceNameOfClass(final Class<?> clazz)
   {
      assert clazz != null : "clazz must be specified";
      final StringBuilder sb = new StringBuilder();
      final String className = clazz.getName().replace('.', '/');
      sb.append(className);
      sb.append(".class");
      return sb.toString();
   }
}
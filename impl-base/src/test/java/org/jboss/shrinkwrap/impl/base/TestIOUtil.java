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

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * IOUtil
 * 
 * Inport/export utilities for test classes
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class TestIOUtil
{
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Internal constructor; should not be called
    */
   private TestIOUtil()
   {
      throw new UnsupportedOperationException("No instances should be created; stateless class");
   }

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Recursively deletes a directory and all its contents 
    * @param directory
    */
   public static void deleteDirectory(final File directory)
   {
      if (directory.isDirectory() && directory.exists())
      {
         // For each file in the directory run cleanup
         for (File file : directory.listFiles())
         {
            if (file.isDirectory())
            {
               // A nested directory, recurse 
               deleteDirectory(file);
            }
            else
            {
               // Just a file delete it
               if (!file.delete())
               {
                  throw new RuntimeException("Failed to delete file: " + file);
               }
            }
         }
         // Delete the directory
         if (!directory.delete())
         {
            throw new RuntimeException("Failed to delete directory: " + directory);
         }
      }
      else
      {
         throw new RuntimeException("Unable to delete directory: " + directory
               + ".  It is either not a directory or does not exist.");
      }
   }
   
   /**
    * Given an existing resource location, create and return a File
    * @param existingResourceLocation
    * @return
    * @throws URISyntaxException
    */
   public static File createFileFromResourceName(final String resourceName) throws URISyntaxException
   {
      assert resourceName != null : "Resource name must be specified";
      final URL resourceLocation = TestSecurityActions.getThreadContextClassLoader().getResource(resourceName);
      assert resourceLocation != null : "Resource was not found at specified location: " + resourceName;
      return new File(resourceLocation.toURI());
   }
   
   public static InputStream createInputstreamFromResourceName(final String resourceName) throws URISyntaxException
   {
      assert resourceName != null : "Resource name must be specified";
      final InputStream resourceStream = TestSecurityActions.getThreadContextClassLoader().getResourceAsStream(resourceName);
      assert resourceStream != null : "Resource was not found at specified location: " + resourceName;
      return resourceStream;
   }
}

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
package org.jboss.declarchive.impl.base.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jboss.declarchive.impl.base.Validate;

/**
 * IOUtil
 * 
 * Generic input/output utilities
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public final class IOUtil
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Internal constructor; should not be called
    */
   private IOUtil()
   {
      throw new UnsupportedOperationException("No instances should be created; stateless class");
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the contents of the specified stream
    * as a byte array
    * 
    * @param in
    * @throws IllegalArgumentException If the stream was not specified
    */
   public static byte[] asByteArray(final InputStream in) throws IllegalArgumentException
   {
      // Precondition check
      Validate.notNull(in, "stream must be specified");

      // Get content as an array of bytes
      final ByteArrayOutputStream out = new ByteArrayOutputStream();
      final int len = 1024;
      final byte[] buffer = new byte[len];
      int read = 0;
      try
      {

         while (((read = in.read(buffer)) != -1))
         {
            out.write(buffer, 0, read);
         }
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Error in obtainting bytes from " + in, ioe);
      }
      finally
      {
         try
         {
            in.close();
         }
         catch (final IOException ignore)
         {

         }
         // We don't need to close the outstream, it's a byte array out
      }

      // Represent as byte array
      final byte[] content = out.toByteArray();

      // Return
      return content;
   }

   /**
    * Copies the contents from an InputStream to an OutputStream
    * 
    * @param input
    * @param output
    */
   public static void copy(InputStream input, OutputStream output)
   {
      final byte[] buffer = new byte[1024];
      int read = 0;
      try
      {
         while ((read = input.read(buffer)) != -1)
         {
            output.write(buffer, 0, read);
         }
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Error copying contents from " + input + " to " + output, ioe);
      }
   }

   /**
    * Copies the contents from an InputStream to an OutputStream and closes both streams.
    * 
    * @param input
    * @param output
    */
   public static void copyWithClose(InputStream input, OutputStream output)
   {
      try
      {
         copy(input, output);
      }
      finally
      {
         try
         {
            input.close();
            output.close();
         }
         catch (final IOException ignore)
         {

         }
      }
   }

   /**
    * Recursively deletes a directory and all its contents 
    * @param directory
    */
   public static void deleteDirectory(File directory)
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
}

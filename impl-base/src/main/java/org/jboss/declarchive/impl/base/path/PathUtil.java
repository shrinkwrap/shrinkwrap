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
package org.jboss.declarchive.impl.base.path;

/**
 * PathUtil
 * 
 * A series of internal-only path utilities for
 * adjusting relative forms, removing double-slashes, etc.
 * Used in correcting inputs in the creation of new Paths
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
final class PathUtil
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Slash character
    */
   static final char SLASH = '/';

   /**
    * Empty String
    */
   static final String EMPTY = "";

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private PathUtil()
   {
      throw new UnsupportedOperationException("Constructor should never be invoked; this is a static util class");
   }

   //-------------------------------------------------------------------------------------||
   // Utilities --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Composes an absolute context from a given base and actual context
    * relative to the base, returning the result.  ie. base of
    * "base" and context of "context" will
    * result in form "/base/context".
    */
   static String composeAbsoluteContext(final String base, final String context)
   {
      // Precondition checks
      assertSpecified(base);
      assertSpecified(context);

      // Compose
      final String relative = PathUtil.adjustToAbsoluteDirectoryContext(base);
      final String reformedContext = PathUtil.optionallyRemovePrecedingSlash(context);
      final String actual = relative + reformedContext;

      // Return
      return actual;
   }

   /**
    * Adjusts the specified path to relative form:
    * 
    * 1) Removes, if present, a preceding slash
    * 2) Adds, if not present, a trailing slash
    * 
    * Null arguments are returned as-is
    * 
    * @param path
    */
   static String adjustToRelativeDirectoryContext(final String path)
   {
      // Return nulls
      if (path == null)
      {
         return path;
      }

      // Strip absolute form
      final String removedPrefix = optionallyRemovePrecedingSlash(path);
      // Add end of context slash
      final String addedPostfix = optionallyAppendSlash(removedPrefix);

      // Return
      return addedPostfix;
   }

   /**
    * Adjusts the specified path to absolute form:
    * 
    * 1) Adds, if not present, a preceding slash
    * 2) Adds, if not present, a trailing slash
    * 
    * Null arguments are returned as-is
    * 
    * @param path
    */
   static String adjustToAbsoluteDirectoryContext(String path)
   {
      // Return nulls
      if (path == null)
      {
         return path;
      }

      // Add prefic slash
      final String prefixedPath = optionallyPrependSlash(path);
      // Add end of context slash
      final String addedPostfix = optionallyAppendSlash(prefixedPath);

      // Return
      return addedPostfix;
   }

   /**
    * Removes, if present, the absolute slash preceding
    * the specified path, and returns the adjusted result. 
    * 
    * @param path
    * @return
    */
   static String optionallyRemovePrecedingSlash(final String path)
   {
      // Precondition check
      assertSpecified(path);

      // Is there's a first character of slash
      if (isFirstCharSlash(path))
      {
         // Return everything but first char
         return path.substring(1);
      }

      // Return as-is
      return path;
   }

   /**
    * Adds, if not already present, the absolute slash following
    * the specified path, and returns the adjusted result.  
    * 
    * @param path
    * @return
    */
   static String optionallyAppendSlash(final String path)
   {
      // Precondition check
      assertSpecified(path);

      // If the last character is not a slash
      if (!isLastCharSlash(path))
      {
         // Append
         return path + SLASH;
      }

      // Return as-is
      return path;
   }

   /**
    * Adds, if not already present, the absolute slash preceding
    * the specified path, and returns the adjusted result.  If 
    * the argument is null, adjusts to an empty String 
    * before processing.
    * 
    * @param path
    * @return
    */
   static String optionallyPrependSlash(final String path)
   {
      // Adjust null
      String resolved = path;
      if (resolved == null)
      {
         resolved = EMPTY;
      }

      // If the first character is not a slash
      if (!isFirstCharSlash(resolved))
      {
         // Prepend the slash
         return SLASH + resolved;
      }

      // Return as-is
      return resolved;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Returns whether or not the first character in the specified String is
    * a slash  
    */
   private static boolean isFirstCharSlash(final String path)
   {
      assertSpecified(path);
      if (path.length() == 0)
      {
         return false;
      }
      return path.charAt(0) == SLASH;
   }

   /**
    * Returns whether or not the last character in the specified String is
    * a slash  
    */
   private static boolean isLastCharSlash(final String path)
   {
      assertSpecified(path);
      if (path.length() == 0)
      {
         return false;
      }
      return path.charAt(path.length() - 1) == SLASH;
   }

   /**
    * Ensures the path is specified
    * @param path
    */
   private static void assertSpecified(final String path)
   {
      // Precondition check
      assert path != null : "Path must be specified";
   }

}

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
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class PathUtil
{

   private PathUtil()
   {
   }

   public static String fixRelativePath(String path)
   {
      if (path == null)
      {
         return path;
      }
      String removedPrefix = removePrefix(path);
      String addedPostfix = addPostfix(removedPrefix);

      return addedPostfix;
   }

   public static String fixBasePath(String path)
   {
      if (path == null)
      {
         return path;
      }
      String prefixedPath = addPrefix(path);
      String prePostfixedPath = addPostfix(prefixedPath);

      return prePostfixedPath;
   }

   private static String removePrefix(String path)
   {
      if (path.charAt(0) == '/')
      {
         return path.substring(1);
      }
      return path;
   }

   private static String addPostfix(String path)
   {
      if (path.charAt(path.length() - 1) != '/')
      {
         return path + '/';
      }
      return path;
   }

   private static String addPrefix(String path)
   {
      if (path.charAt(0) != '/')
      {
         return '/' + path;
      }
      return path;
   }

}

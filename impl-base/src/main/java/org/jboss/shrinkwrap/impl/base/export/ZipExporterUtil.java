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
package org.jboss.shrinkwrap.impl.base.export;

import org.jboss.shrinkwrap.api.Path;

/**
 * ZipExporterUtil
 * 
 * Utility used to assist exporting to Zip format. 
 * 
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public class ZipExporterUtil
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Slash character
    */
   static final String SLASH = "/";

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private ZipExporterUtil()
   {
      throw new UnsupportedOperationException("Constructor should never be invoked; this is a static util class");
   }

   //-------------------------------------------------------------------------------------||
   // Utilities --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Converts a Path to a ZipEntry path name
    * 
    *  @param path to convert
    *  @return String representing a ZipEntry path
    */
   public static String toZipEntryPath(Path path)
   {

      String context = path.get();
      if (context.startsWith(SLASH))
      {
         context = context.substring(1);
      }
      return context;
   }
}

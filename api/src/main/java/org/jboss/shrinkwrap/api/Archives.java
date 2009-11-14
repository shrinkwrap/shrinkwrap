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
package org.jboss.shrinkwrap.api;


/**
 * Generic unified factory for archive creation.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class Archives
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final String ARCHIVE_IMPL = "org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl";
   
   /**
    * Create a archive as a specific type.
    * 
    * @param archiveName The name of the archive
    * @return A {@link Specializer} archive base  
    */
   public static <T extends Specializer> T create(String archiveName, Class<T> type) 
   {
      if(archiveName == null) 
      {
         throw new IllegalArgumentException("ArchiveName must be specified");
      }
      if(type == null) 
      {
         throw new IllegalArgumentException("Type must be specified");
      }
      Archive<?> archive = SecurityActions.newInstance(
                                 ARCHIVE_IMPL,
                                 new Class<?>[]{String.class},
                                 new Object[]{archiveName},
                                 Archive.class); 
      return archive.as(type);
   }

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private Archives() {}
}

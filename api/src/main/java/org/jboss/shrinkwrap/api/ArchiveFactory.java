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
 * 
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public final class ArchiveFactory
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Implementation class name backing {@link Archive}s to be created
    */
   private static final String ARCHIVE_IMPL = "org.jboss.shrinkwrap.impl.base.MemoryMapArchiveImpl";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Configuration for all archives created from this factory
    */
   private final Configuration configuration;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link ArchiveFactory} which will use the supplied 
    * {@link Configuration} for each new {@link Archive} it creates.
    * 
    * @param configuration
    */
   ArchiveFactory(final Configuration configuration) throws IllegalArgumentException
   {
      // Precondition checks
      assert configuration != null : "configuration must be supplied";

      // Set
      this.configuration = configuration;
   }

   //-------------------------------------------------------------------------------------||
   // Functional Methods ----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new archive of the specified type.  The archive
    * will be be backed by the {@link Configuration}
    * specific to this {@link ArchiveFactory}.
    * 
    * @param archiveName The name of the archive
    * @return An {@link Assignable} archive base  
    * @throws IllegalArgumentException If either argument is not specified
    */
   public <T extends Assignable> T create(final String archiveName, final Class<T> type)
         throws IllegalArgumentException
   {
      // Precondition checks
      if (archiveName == null)
      {
         throw new IllegalArgumentException("ArchiveName must be specified");
      }
      if (type == null)
      {
         throw new IllegalArgumentException("Type must be specified");
      }

      final Archive<?> archive = SecurityActions.newInstance(ARCHIVE_IMPL, new Class<?>[]
      {String.class, Configuration.class}, new Object[]
      {archiveName, configuration}, Archive.class);
      return archive.as(type);
   }
}

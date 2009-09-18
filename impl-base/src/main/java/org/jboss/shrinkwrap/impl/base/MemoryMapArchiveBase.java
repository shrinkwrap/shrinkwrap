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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;

/**
 * MemoryMapArchiveBase
 * 
 * A base implementation for all MemoryMap archives. Thread-safe.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public abstract class MemoryMapArchiveBase<T extends Archive<T>> extends ArchiveBase<T> implements Archive<T>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(MemoryMapArchiveBase.class.getName());
   
   /**
    * Newline character
    */
   private static final char NEWLINE = '\n';
   
   /**
    * Colon character
    */
   private static final char COLON = ':';

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Storage for the {@link Asset}s.
    */
   private final Map<Path, Asset> content = new ConcurrentHashMap<Path, Asset>();

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    *  
    * This constructor will generate a 
    * unique {@link Archive#getName()} per instance.
    *  
    * @param actualType The {@link Archive} type.
    */
   public MemoryMapArchiveBase()
   {
      this("Archive-" + UUID.randomUUID().toString() + ".jar");
   }

   /**
    * Constructor
    * 
    * This constructor will generate an {@link Archive} with the provided name.
    *  
    * @param archiveName
    */
   public MemoryMapArchiveBase(final String archiveName)
   {
      super(archiveName);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - Archive -------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Asset[])
    */
   @Override
   public T add(Path path, Asset asset)
   {
      Validate.notNull(path, "No path was specified");
      Validate.notNull(asset, "No asset was specified");

      content.put(path, asset);
      return covariantReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#contains(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean contains(Path path)
   {
      Validate.notNull(path, "No path was specified");
      return content.containsKey(path);
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#delete(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean delete(Path path)
   {
      Validate.notNull(path, "No path was specified");
      return content.remove(path) != null;
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#get(org.jboss.declarchive.api.Path)
    */
   @Override
   public Asset get(Path path)
   {
      Validate.notNull(path, "No path was specified");
      return content.get(path);
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#getContent()
    */
   @Override
   public Map<Path, Asset> getContent()
   {
      return Collections.unmodifiableMap(content);
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#toString(boolean)
    */
   @Override
   public String toString(boolean verbose)
   {
      // If we want verbose output
      if (verbose)
      {
         // Make a builder
         StringBuilder sb = new StringBuilder();
         
         // Add the name
         sb.append(this.getName()).append(COLON).append(NEWLINE);
         
         // Sort all paths
         final List<Path> paths = new ArrayList<Path>(content.keySet());
         Collections.sort(paths);

         for (final Path path : paths)
         {
            sb.append(path.get()).append(NEWLINE);
         }
         return sb.toString();
      }
      // Fall back on toString
      return this.toString();
   }

}

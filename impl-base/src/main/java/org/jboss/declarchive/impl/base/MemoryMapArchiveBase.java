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
package org.jboss.declarchive.impl.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.declarchive.api.Archive;
import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.api.AssetNotFoundException;
import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.path.BasicPath;
import org.jboss.declarchive.spi.MemoryMapArchive;

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
public abstract class MemoryMapArchiveBase<T extends MemoryMapArchive> implements Archive<MemoryMapArchive>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(MemoryMapArchiveBase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Storage for the {@link Asset}s.
    */
   private final Map<Path, Asset> content = new ConcurrentHashMap<Path, Asset>();

   /**
    * The file name for this {@link Archive}.
    */
   private final String archiveName;

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
   public MemoryMapArchiveBase(String archiveName)
   {
      super();
      Validate.notNull(archiveName, "Archive name is required");

      this.archiveName = archiveName;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - Archive -------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#getName()
    */
   @Override
   public String getName()
   {
      return archiveName;
   }

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
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public T add(Path path, String name, Asset asset)
   {
      Validate.notNull(path, "No path path was specified");
      Validate.notNull(name, "No asset name was specified");
      Validate.notNull(asset, "No asset was was specified");

      content.put(new BasicPath(path, name), asset);
      return covariantReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#add(java.lang.String, org.jboss.declarchive.api.Asset)
    */
   @Override
   public T add(String name, Asset asset)
   {
      throw new UnsupportedOperationException("Remove when API updated");
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
    * @see org.jboss.declarchive.api.Archive#get(java.lang.String)
    */
   @Override
   public Asset get(String path) throws AssetNotFoundException, IllegalArgumentException
   {
      Validate.notNull(path, "No path was specified");
      return get(new BasicPath(path));
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#getContent()
    */
   @Override
   public Map<Path, Asset> getContent()
   {
      return Collections.unmodifiableMap(content);
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#add(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Archive)
    */
   @Override
   public T add(Path path, Archive<?> archive)
   {
      Validate.notNull(path, "No path was specified");
      Validate.notNull(archive, "No archive was specified");

      final Path contentPath = new BasicPath(path, archive.getName());

      return addContents(contentPath, archive);
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#addContents(org.jboss.declarchive.api.Archive)
    */
   @Override
   public T addContents(Archive<?> source) throws IllegalArgumentException
   {
      return addContents(new BasicPath(""), source);
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#addContents(org.jboss.declarchive.api.Path, org.jboss.declarchive.api.Archive)
    */
   @Override
   public T addContents(Path path, Archive<?> source) throws IllegalArgumentException
   {
      Validate.notNull(path, "No path was specified");
      Validate.notNull(source, "No source archive was specified");

      // Get existing contents from source archive
      final Map<Path, Asset> sourceContent = source.getContent();
      Validate.notNull(sourceContent, "Source archive content can not be null.");

      // Add each asset from the source archive
      for (Entry<Path, Asset> contentEntry : sourceContent.entrySet())
      {
         final Asset asset = contentEntry.getValue();
         Path assetPath = contentEntry.getKey();
         if (path != null)
         {
            assetPath = new BasicPath(path, assetPath);
         }
         add(assetPath, asset);
      }
      return covariantReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.declarchive.api.Archive#toString(boolean)
    */
   @Override
   public String toString(boolean verbose)
   {
      StringBuilder sb = new StringBuilder();
      List<Path> paths = new ArrayList<Path>(content.keySet());
      Collections.sort(paths);

      for (Path path : paths)
      {
         sb.append(path.get()).append('\n');
      }
      return sb.toString();
   }

   /*
    * (non-Javadoc)
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return toString(false);
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Provides typesafe covariant return of this instance
    */
   protected final T covariantReturn()
   {
      try
      {
         return getActualClass().cast(this);
      }
      catch (final ClassCastException cce)
      {
         log.log(Level.SEVERE,
               "The class specified by getActualClass is not a valid assignment target for this instance;"
                     + " developer error");
         throw cce;
      }
   }

   /**
    * 
    * @return actual MemoryMapArchive type
    */
   protected abstract Class<T> getActualClass();
}

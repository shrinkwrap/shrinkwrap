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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.ExtensionLoader;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.impl.base.asset.ArchiveAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

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
   private final Map<ArchivePath, Asset> content = new ConcurrentHashMap<ArchivePath, Asset>();

   /**
    * Storage for the {@link ArchiveAsset}s.  Used to help get access to nested archive content.
    */
   private final Map<ArchivePath, ArchiveAsset> nestedArchives = new ConcurrentHashMap<ArchivePath, ArchiveAsset>();

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    *  
    * This constructor will generate a 
    * unique {@link Archive#getName()} per instance.
    *  
    * @param extensionLoader The extensionLoader to be used
    */
   public MemoryMapArchiveBase(ExtensionLoader extensionLoader)
   {
      this("Archive-" + UUID.randomUUID().toString() + ".jar", extensionLoader);
   }

   /**
    * Constructor
    * 
    * This constructor will generate an {@link Archive} with the provided name.
    *  
    * @param archiveName
    * @param extensionLoader The extensionLoader to be used
    */
   public MemoryMapArchiveBase(final String archiveName, ExtensionLoader extensionLoader)
   {
      super(archiveName, extensionLoader);
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations - Archive -------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Asset, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T add(Asset asset, ArchivePath path)
   {
      Validate.notNull(asset, "No asset was specified");
      Validate.notNull(path, "No path was specified");

      content.put(path, asset);
      return covariantReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.impl.base.ArchiveBase#add(org.jboss.shrinkwrap.api.Archive, org.jboss.shrinkwrap.api.Path)
    */
   @Override
   public T add(Archive<?> archive, ArchivePath path)
   {
      // Add archive asset
      super.add(archive, path);

      // Expected Archive Path
      ArchivePath archivePath = new BasicPath(path, archive.getName());

      // Get the Asset that was just added 
      Asset asset = get(archivePath);

      // Make sure it is an ArchiveAsset
      if (asset instanceof ArchiveAsset)
      {
         ArchiveAsset archiveAsset = ArchiveAsset.class.cast(asset);
         // Add asset to ArchiveAsset Map
         nestedArchives.put(archivePath, archiveAsset);
      }

      return covariantReturn();
   }

   /* {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#contains(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean contains(ArchivePath path)
   {
      Validate.notNull(path, "No path was specified");

      boolean found = content.containsKey(path);
      if (!found)
      {
         found = nestedContains(path);
      }
      return found;
   }

   /* {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#delete(org.jboss.declarchive.api.Path)
    */
   @Override
   public boolean delete(ArchivePath path)
   {
      Validate.notNull(path, "No path was specified");
      return content.remove(path) != null;
   }

   /* {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#get(org.jboss.declarchive.api.Path)
    */
   @Override
   public Asset get(ArchivePath path)
   {
      Validate.notNull(path, "No path was specified");
      Asset asset = content.get(path);
      if (asset == null && contains(path))
      {
         asset = getNestedAsset(path);
      }
      return asset;
   }

   /* {@inheritDoc}
    * @see org.jboss.declarchive.api.Archive#getContent()
    */
   @Override
   public Map<ArchivePath, Asset> getContent()
   {
      return Collections.unmodifiableMap(content);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Archive#getContent(org.jboss.shrinkwrap.api.Filter)
    */
   @Override
   public Map<ArchivePath, Asset> getContent(Filter<ArchivePath> filter)
   {
      Validate.notNull(filter, "Filter must be specified");
      
      Map<ArchivePath, Asset> filteredContent = new HashMap<ArchivePath, Asset>();
      for(Map.Entry<ArchivePath, Asset> contentEntry : content.entrySet())
      {
         if(filter.include(contentEntry.getKey()))
         {
            filteredContent.put(contentEntry.getKey(), contentEntry.getValue());
         }
      }
      return filteredContent;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Check to see if a path is found in a nested archive
    */
   private boolean nestedContains(ArchivePath path)
   {
      // Iterate through nested archives
      for (Entry<ArchivePath, ArchiveAsset> nestedArchiveEntry : nestedArchives.entrySet())
      {
         ArchivePath archivePath = nestedArchiveEntry.getKey();
         ArchiveAsset archiveAsset = nestedArchiveEntry.getValue();

         // Check to see if the requested path starts with the nested archive path
         if (startsWith(path, archivePath))
         {
            Archive<?> nestedArchive = archiveAsset.getArchive();

            // Get the asset path from within the nested archive
            ArchivePath nestedAssetPath = getNestedPath(path, archivePath);

            // Recurse the call to the nested archive
            return nestedArchive.contains(nestedAssetPath);
         }
      }
      return false;
   }

   /** 
    * Attempt to get the asset from a nested archive. 
    * 
    * @param path
    * @return
    */
   private Asset getNestedAsset(ArchivePath path)
   {
      // Iterate through nested archives
      for (Entry<ArchivePath, ArchiveAsset> nestedArchiveEntry : nestedArchives.entrySet())
      {
         ArchivePath archivePath = nestedArchiveEntry.getKey();
         ArchiveAsset archiveAsset = nestedArchiveEntry.getValue();

         // Check to see if the requested path starts with the nested archive path
         if (startsWith(path, archivePath))
         {
            Archive<?> nestedArchive = archiveAsset.getArchive();

            // Get the asset path from within the nested archive
            ArchivePath nestedAssetPath = getNestedPath(path, archivePath);

            // Recurse the call to the nested archive
            return nestedArchive.get(nestedAssetPath);
         }
      }
      return null;
   }

   /**
    * Check to see if one path starts with another
    * 
    * @param fullPath
    * @param startingPath
    * @return
    */
   private boolean startsWith(ArchivePath fullPath, ArchivePath startingPath)
   {
      final String context = fullPath.get();
      final String startingContext = startingPath.get();

      return context.startsWith(startingContext);
   }

   /**
    * Given a full path and a base path return a new path containing the full path with the 
    * base path removed from the beginning.
    * 
    * @param fullPath
    * @param basePath
    * @return
    */
   private ArchivePath getNestedPath(ArchivePath fullPath, ArchivePath basePath)
   {
      final String context = fullPath.get();
      final String baseContent = basePath.get();

      // Remove the base path from the full path
      String nestedArchiveContext = context.substring(baseContent.length());

      return new BasicPath(nestedArchiveContext);
   }
}

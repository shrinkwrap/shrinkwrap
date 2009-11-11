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

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.api.Specializer;
import org.jboss.shrinkwrap.impl.base.asset.ArchiveAsset;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;

/**
 * ArchiveBase
 * 
 * Base implementation of {@link Archive}.  Contains
 * support for operations (typically overloaded) that are 
 * not specific to any particular storage implementation, 
 * and may be delegated to other forms.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public abstract class ArchiveBase<T extends Archive<T>> implements Archive<T>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ArchiveBase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Name of the archive
    */
   private final String name;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    * 
    * Creates a new Archive with the specified name
    * 
    * @param name Name of the archive
    * @throws IllegalArgumentException If the name was not specified
    */
   protected ArchiveBase(final String name) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNullOrEmpty(name, "name must be specified");

      // Set
      this.name = name;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(java.lang.String, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T add(final String target, final Asset asset) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNullOrEmpty(target, "target must be specified");
      Validate.notNull(asset, "asset must be specified");

      // Make a Path from the target
      final Path path = new BasicPath(target);

      // Delegate
      return this.add(path, asset);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Path, java.lang.String, org.jboss.shrinkwrap.api.Asset)
    */
   @Override
   public T add(final Path path, final String name, final Asset asset)
   {
      // Precondition checks
      Validate.notNull(path, "No path was specified");
      Validate.notNullOrEmpty(name, "No target name name was specified");
      Validate.notNull(asset, "No asset was was specified");

      // Make a relative path
      final Path resolvedPath = new BasicPath(path, name);

      // Delegate
      return this.add(resolvedPath, asset);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#get(java.lang.String)
    */
   @Override
   public Asset get(final String path) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNullOrEmpty(path, "No path was specified");

      // Make a Path
      final Path realPath = new BasicPath(path);

      // Delegate
      return get(realPath);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#add(org.jboss.shrinkwrap.api.Path, org.jboss.shrinkwrap.api.Archive)
    */
   @Override
   public T add(final Path path, final Archive<?> archive)
   {
      // Precondition checks
      Validate.notNull(path, "No path was specified");
      Validate.notNull(archive, "No archive was specified");

      // Make a Path
      final String archiveName = archive.getName();
      final Path contentPath = new BasicPath(path, archiveName);

      // Create ArchiveAsset 
      ArchiveAsset archiveAsset = new ArchiveAsset(archive);

      // Delegate
      return add(contentPath, archiveAsset);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#getName()
    */
   public final String getName()
   {
      return name;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Archive)
    */
   @Override
   public T merge(final Archive<?> source) throws IllegalArgumentException
   {
      return merge(source, new BasicPath());
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Archive#merge(org.jboss.shrinkwrap.api.Path, org.jboss.shrinkwrap.api.Archive)
    */
   @Override
   public T merge(final Archive<?> source, final Path path) throws IllegalArgumentException
   {
      // Precondition checks
      Validate.notNull(source, "No source archive was specified");
      Validate.notNull(path, "No path was specified");

      // Get existing contents from source archive
      final Map<Path, Asset> sourceContent = source.getContent();
      Validate.notNull(sourceContent, "Source archive content can not be null.");

      // Add each asset from the source archive
      for (final Entry<Path, Asset> contentEntry : sourceContent.entrySet())
      {
         final Asset asset = contentEntry.getValue();
         Path assetPath = contentEntry.getKey();
         if (path != null)
         {
            assetPath = new BasicPath(path, assetPath);
         }
         // Delegate
         add(assetPath, asset);
      }
      return covariantReturn();
   }

   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Specializer#as(java.lang.Class)
    */
   @Override
   public <TYPE extends Specializer> TYPE as(Class<TYPE> clazz)
   {
      Validate.notNull(clazz, "Class must be specified");

      return new ArchiveExtensionLoader<TYPE>(clazz).load(this);
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Returns the actual typed class for this instance, used in safe casting 
    * for covariant return types
    * 
    * @return
    */
   protected abstract Class<T> getActualClass();

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
         return this.getActualClass().cast(this);
      }
      catch (final ClassCastException cce)
      {
         log.log(Level.SEVERE,
               "The class specified by getActualClass is not a valid assignment target for this instance;"
                     + " developer error");
         throw cce;
      }
   }
}

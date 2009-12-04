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
package org.jboss.shrinkwrap.impl.base.path;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.spi.PathProvider;

/**
 * BasicPath
 * 
 * A Path which may be optionally prefixed with some common
 * namespace context at construction time.  Thread-safe.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class BasicPath implements PathProvider, Comparable<Path>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(BasicPath.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The context which this path represents; immutable so we're
    * thread-safe.
    */
   private final String context;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new Path representing the root context
    */
   public BasicPath()
   {
      this(null);
   }

   /**
    * Creates a new Path with the specified context
    * 
    * @param context The context which this path represents.  Null or 
    * blank represents the root.  Relative paths will be adjusted
    * to absolute form.
    */
   public BasicPath(final String context)
   {
      final String resolvedContext = PathUtil.optionallyPrependSlash(context);
      if (log.isLoggable(Level.FINER))
      {
         log.finer("Resolved \"" + context + "\" to absolute form: " + resolvedContext);
      }      
      this.context = resolvedContext;
   }

   /**
    * Creates a new Path using the specified base 
    * and specified relative context.
    * 
    * @param basePath
    * @param context
    */
   public BasicPath(final Path basePath, final Path context)
   {
      this(basePath, context.get());
   }

   /**
    * Creates a new Path using the specified base 
    * and specified relative context.
    * 
    * @param basePath
    * @param context
    */
   public BasicPath(final Path basePath, final String context)
   {
      this(basePath.get(), context);
   }

   /**
    * Creates a new Path using the specified base 
    * and specified relative context.
    * 
    * @param basePath
    * @param context
    */
   public BasicPath(final String basePath, String context)
   {
      this(PathUtil.composeAbsoluteContext(basePath, context));
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Path#get()
    */
   @Override
   public String get()
   {
      return context;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(final Path path)
   {
      // If a null argument, we're greater
      if (path == null)
      {
         return 1;
      }

      // Just delegate to underlying Strings
      return path.get().compareTo(this.get());
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.spi.PathProvider#parent()
    */
   @Override
   public Path parent()
   {
      // Get the last index of "/"
      final String resolvedContext = PathUtil.optionallyRemoveFollowingSlash(this.context);
      final int lastIndex = resolvedContext.lastIndexOf(PathUtil.SLASH);
      // If it either doesn't occur or is the root
      if (lastIndex == -1 || (lastIndex == 0 && resolvedContext.length() == 1))
      {
         // No parent present, return null
         return null;
      }
      // Get the parent context
      final String sub = resolvedContext.substring(0, lastIndex);
      // Return
      return new BasicPath(sub);
   }

   //-------------------------------------------------------------------------------------||
   // Overridden Implementations ---------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((context == null) ? 0 : context.hashCode());
      return result;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      final BasicPath other = (BasicPath) obj;
      if (context == null)
      {
         if (other.context != null)
            return false;
      }
      else if (!context.equals(other.context))
         return false;
      return true;
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.getClass().getSimpleName() + " [context=" + context + "]";
   }

}

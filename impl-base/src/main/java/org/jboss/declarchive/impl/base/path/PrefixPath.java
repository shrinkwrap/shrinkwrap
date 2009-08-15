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

import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.impl.base.Validate;

/**
 * PrefixPath
 * 
 * A Path which may be optionally prefixed with some common
 * namespace
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
abstract class PrefixPath implements Path, Comparable<Path>
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Empty String
    */
   private static final String EMPTY_STRING = "";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The context which this path represents
    */
   private final String context;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new Path with the specified context
    * 
    * @param prefix The prefix to prepend to every context returned
    * in {@link PrefixPath#get()}.  May be null or blank
    * @param context The context which this path represents.  Null or 
    * blank represents the root.
    */
   PrefixPath(final String context)
   {
      Validate.notNull(context, "Context must be specified");
      this.context = context;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.declarchive.api.Path#get()
    */
   @Override
   public String get()
   {
      // Return the prefix plus the context
      final String prefix = this.getPrefix();
      final String prefixToUse = prefix == null ? EMPTY_STRING : prefix;
      final String resolvedContext = prefixToUse + context;
      return resolvedContext;
   }

   /**
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(final Path path)
   {
      if (path == null)
      {
         return 1;
      }
      else
      {
         // Compare the contexts
         return this.get().compareTo(path.get());
      }
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the prefix to prepend to all path contexts
    */
   abstract String getPrefix();

   //-------------------------------------------------------------------------------------||
   // Overridden Implementations ---------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
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
      final PrefixPath other = (PrefixPath) obj;
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
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return this.getClass().getSimpleName() + " [context=" + context + "]";
   }

}

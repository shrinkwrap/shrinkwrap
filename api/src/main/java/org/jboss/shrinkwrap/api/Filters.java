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

import java.util.regex.Pattern;

import javax.annotation.processing.Filer;

/**
 * Factory class for the creation of new {@link Filter}
 * instances
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class Filters
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final String INCLUDE_ALL_PATHS = "org.jboss.shrinkwrap.impl.base.filter.IncludeAllPaths";
   
   private static final String INCLUDE_REGEXP_PATHS = "org.jboss.shrinkwrap.impl.base.filter.IncludeRegExpPaths";
   
   private static final String EXCLUDE_REGEXP_PATHS = "org.jboss.shrinkwrap.impl.base.filter.ExcludeRegExpPaths";
   
   /**
    * {@link Filter} that includes all {@link ArchivePath}s.
    * 
    * Only meant to be used internally.
    * 
    * @return A {@link Filter} that always return true
    */
   @SuppressWarnings("unchecked")
   public static Filter<ArchivePath> includeAll() 
   {
      return SecurityActions.newInstance(
            INCLUDE_ALL_PATHS, 
            new Class<?>[]{}, 
            new Object[]{}, 
            Filter.class);
   }
   
   /**
    * {@link Filer} that include all {@link ArchivePath}s that match the given Regular Expression {@link Pattern}.
    * 
    * @param regexp The expression to include
    * @return A Regular Expression based include {@link Filter}
    */
   @SuppressWarnings("unchecked")
   public static Filter<ArchivePath> include(String regexp) 
   {
      return SecurityActions.newInstance(
            INCLUDE_REGEXP_PATHS, 
            new Class<?>[]{String.class}, 
            new Object[]{regexp}, 
            Filter.class);
   }

   /**
    * {@link Filter} that exclude all {@link ArchivePath}s that match a given Regular Expression {@link Pattern}.
    * 
    * @param regexp The expression to exclude
    * @return A Regular Expression based exclude {@link Filter}
    */
   @SuppressWarnings("unchecked")
   public static Filter<ArchivePath> exclude(String regexp) 
   {
      return SecurityActions.newInstance(
            EXCLUDE_REGEXP_PATHS, 
            new Class<?>[]{String.class}, 
            new Object[]{regexp}, 
            Filter.class);
   }
   
   /**
    * {@link Filter} that includes listed {@link Class}.
    * 
    * @param classes To be included
    * @return 
    */
   public static Filter<ArchivePath> include(Package... packages)
   {
      StringBuilder classExpression = new StringBuilder();
      for (Package pack : packages)
      {
         classExpression.append("|");
         classExpression.append("(.*" + pack.getName().replaceAll("\\.", "\\.") + ".*)");
      }
      classExpression.deleteCharAt(0);

      return SecurityActions.newInstance(
            INCLUDE_REGEXP_PATHS
            , new Class<?>[]{String.class}
            , new Object[]{classExpression.toString()}
            , Filter.class);
   }
   
   /**
    * {@link Filter} that includes listed {@link Class}.
    * 
    * @param classes To be included
    * @return 
    */
   public static Filter<ArchivePath> include(Class<?>... classes)
   {
      return createFilterThat(INCLUDE_REGEXP_PATHS, classes);
   }
   
   /**
    * {@link Filter} that excludes listed {@link Class}.
    * 
    * @param classes To be excluded
    * @return 
    */
   public static Filter<ArchivePath> exclude(Class<?>... classes)
   {
      return createFilterThat(EXCLUDE_REGEXP_PATHS, classes);
   }

   @SuppressWarnings("unchecked")
   private static Filter<ArchivePath> createFilterThat(String regExpPath, Class<?>... classes)
   {
      StringBuilder classExpression = new StringBuilder();
      for (Class<?> clazz : classes)
      {
         classExpression.append("|");
         classExpression.append("(.*" + clazz.getName().replaceAll("\\.", "\\.") + "\\.class)");
      }
      classExpression.deleteCharAt(0);

      return SecurityActions.newInstance(
            regExpPath
            , new Class<?>[]{String.class}
            , new Object[]{classExpression.toString()}
            , Filter.class);
   }
   
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private Filters() {}
}

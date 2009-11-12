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
 * A Factory for Path creation.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public final class Paths
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static final String PATH_IMPL = "org.jboss.shrinkwrap.impl.base.path.BasicPath";
   
   /**
    * Creates a new Path representing the root path (/).
    * 
    * @return a new root Path  
    */
   public static Path root() 
   {
      return create(null);
   }
   
   /**
    * Creates a new Path with the specified context
    * 
    * @param context The context which this path represents.  Null or 
    * blank represents the root.  Relative paths will be adjusted
    * to absolute form.
    * @return a new Path 
    */
   public static Path create(String context) 
   {
      return createInstance(new Class<?>[] {String.class}, new Object[]{context});
   }
   
   /**
    * Creates a new Path using the specified base 
    * and specified relative context.
    * 
    * @param basePath A absolute path
    * @param context A relative path to basePath
    * @return a new Path
    */
   public static Path create(String basePath, String context) 
   {
      return createInstance(new Class<?>[] {String.class, String.class}, new Object[]{basePath, context});
   }

   /**
    * Creates a new Path using the specified base 
    * and specified relative context.
    * 
    * @param basePath A absolute path
    * @param context A relative path to basePath
    * @return a new Path
    */
   public static Path create(Path basePath, String context) 
   {
      return createInstance(new Class<?>[] {Path.class, String.class}, new Object[]{basePath, context});
   }

   /**
    * Creates a new Path using the specified base 
    * and specified relative context.
    * 
    * @param basePath A absolute path
    * @param context A relative path to basePath
    * @return a new Path
    */
   public static Path create(Path basePath, Path context) 
   {
      return createInstance(new Class<?>[] {Path.class, Path.class}, new Object[]{basePath, context});
   }
   
   //-------------------------------------------------------------------------------------||
   // Class Members - Internal Helpers ---------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static Path createInstance(Class<?>[] argumentTypes, Object[] arguments) 
   {
      return (Path) ReflectionUtil.createInstance(PATH_IMPL, argumentTypes, arguments);
   }

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * No instantiation
    */
   private Paths() {}
   
}

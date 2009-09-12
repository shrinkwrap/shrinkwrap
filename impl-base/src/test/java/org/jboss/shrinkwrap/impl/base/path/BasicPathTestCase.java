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

import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Path;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * BasicPathTestCase
 *
 * Tests to ensure that the {@link BasicPath}
 * implementation creates Paths as expected 
 * from various specified contexts 
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class BasicPathTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(BasicPathTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that a null context results in 
    * a root Path
    */
   @Test
   public void testNullDefaultsToRoot()
   {
      // Log
      log.info("testNullDefaultsToRoot");

      // Create a path with null context
      final Path path = new BasicPath(null);

      // Ensure expected
      final String resolved = path.get();
      Assert.assertEquals("Null context should resolve to root path", String.valueOf(PathUtil.SLASH), resolved);
      log.info("null argument resolves to: " + path);
   }

   /**
    * Ensures that a relative path resolves 
    * to absoulte form in the single-arg ctor
    */
   @Test
   public void testRelativeResolvedToAbsolute()
   {
      // Log
      log.info("testRelativeResolvedToAbsolute");

      // Create a relative path
      final String relative = "relative";
      final Path path = new BasicPath(relative);

      // Ensure expected
      final String resolved = path.get();
      final String expected = PathUtil.SLASH + relative;
      Assert.assertEquals("Relative paths should resolve to absolute", expected, resolved);
      log.info("\"" + relative + "\" resolves to: " + path);
   }

   /**
    * Ensures that an absolute directory path
    * is preserved as-is
    */
   @Test
   public void testAbsoluteDirectoryContextPreserved()
   {
      // Log
      log.info("testAbsoluteDirectoryContextPreserved");

      // Create an absolute dir path
      final String absoluteDir = "/absoluteDir/";
      final Path path = new BasicPath(absoluteDir);

      // Ensure expected
      final String resolved = path.get();
      final String expected = absoluteDir;
      Assert.assertEquals("Absolute directory contexts should be preserved", expected, resolved);
      log.info("\"" + absoluteDir + "\" resolves to: " + path);
   }

   /**
    * Ensures that a new path may be created from a 
    * context under a specified base path
    */
   @Test
   public void testBasePathAndRelativeContext()
   {
      // Log
      log.info("testBasePathAndRelativeContext");

      // Create a base path
      final String base = "base";
      final Path basePath = new BasicPath(base);

      // Create a new path using a relative context to the base
      final String context = "context";
      final Path contextPath = new BasicPath(context);
      final Path path = new BasicPath(basePath, contextPath);

      // Ensure expected
      final String resolved = path.get();
      final String expected = PathUtil.SLASH + base + PathUtil.SLASH + context;
      Assert.assertEquals("Context under base should resolve to relative", expected, resolved);
      log.info("\"" + context + "\" under base " + basePath + " resolves to: " + path);
   }

   /**
    * Ensures that a new path may be created from a 
    * context (as String) under a specified base path
    */
   @Test
   public void testBasePathAndRelativeContextAsString()
   {
      // Log
      log.info("testBasePathAndRelativeContextAsString");

      // Create a base path
      final String base = "base";
      final Path basePath = new BasicPath(base);

      // Create a new path using a relative context to the base
      final String context = "context";
      final Path path = new BasicPath(basePath, context);

      // Ensure expected
      final String resolved = path.get();
      final String expected = PathUtil.SLASH + base + PathUtil.SLASH + context;
      Assert.assertEquals("Context under base should resolve to relative", expected, resolved);
      log.info("\"" + context + "\" under base " + basePath + " resolves to: " + path);
   }

   /**
    * Ensures that a new path may be created from a 
    * context (as String) under a specified base path (which is represented
    * as a String)
    */
   @Test
   public void testBasePathAsStringAndRelativeContextAsString()
   {
      // Log
      log.info("testBasePathAsStringAndRelativeContextAsString");

      // Create a base path
      final String base = "base";

      // Create a new path using a relative context to the base
      final String context = "context";
      final Path path = new BasicPath(base, context);

      // Ensure expected
      final String resolved = path.get();
      final String expected = PathUtil.SLASH + base + PathUtil.SLASH + context;
      Assert.assertEquals("Context under base should resolve to relative", expected, resolved);
      log.info("\"" + context + "\" under base \"" + base + "\" resolves to: " + path);
   }

   /**
    * Ensures that Paths with equal
    * contexts have equal hash codes
    */
   @Test
   public void testHashCode()
   {
      // Log
      log.info("testHashCode");

      // Create new paths
      final String context = "context";
      final Path path1 = new BasicPath(context);
      final Path path2 = new BasicPath(context);

      // Obtain hash 
      final int hash1 = path1.hashCode();
      final int hash2 = path2.hashCode();

      // Ensure expected
      Assert.assertEquals("Paths with the same context should have equal hash codes", hash1, hash2);
      log.info("Both " + path1 + " and " + path2 + " have hashCode: " + hash1);
   }

   /**
    * Ensures that Paths with equal contexts 
    * are equal by value
    */
   @Test
   public void testEquals()
   {
      // Log
      log.info("testEquals");

      // Create new paths
      final String context = "context";
      final Path path1 = new BasicPath(context);
      final Path path2 = new BasicPath(context);

      // Ensure expected
      Assert.assertEquals("Paths with same context should be equal by value", path1, path2);
      log.info(path1 + " equal by value to " + path2);
   }

   /**
    * Ensures that Paths with inequal contexts 
    * are equal by value
    */
   @Test
   public void testNotEqual()
   {
      // Log
      log.info("testEquals");

      // Create new paths
      final String context1 = "context1";
      final String context2 = "context2";
      final Path path1 = new BasicPath(context1);
      final Path path2 = new BasicPath(context2);

      // Ensure expected
      Assert.assertTrue("Paths with different contexts should not be equal by value", !path1.equals(path2));
      log.info(path1 + " not equal by value to " + path2);
   }
}

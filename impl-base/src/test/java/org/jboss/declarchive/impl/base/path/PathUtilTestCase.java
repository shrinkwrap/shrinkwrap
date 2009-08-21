package org.jboss.declarchive.impl.base.path;

import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Test;

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

/**
 * PathUtilTestCase
 * 
 * Test cases to ensure the path utilities are working as expected.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class PathUtilTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(PathUtilTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that the preceding slash is removed as requested
    */
   @Test
   public void testRemovePrecedingSlash()
   {
      log.info("testRemovePrecedingSlash");
      final String precedingSlash = "/test/something";
      final String expected = precedingSlash.substring(1);
      final String result = PathUtil.optionallyRemovePrecedingSlash(precedingSlash);
      Assert.assertEquals("Call to remove preceding slash should return everything in input except the first slash",
            expected, result);
   }

   /**
    * Ensures that the preceding slash is removed as requested
    */
   @Test
   public void testRemovePrecedingSlashWithNoPrecedingSlashEqualToInput()
   {
      log.info("testRemovePrecedingSlash");
      final String noPrecedingSlash = "test/something";
      final String result = PathUtil.optionallyRemovePrecedingSlash(noPrecedingSlash);
      Assert.assertEquals(
            "Call to remove preceding slash on input with no preceding slash should return equal by value to input",
            noPrecedingSlash, result);
   }

   /**
    * Ensures that a slash may be prepended to a given String
    */
   @Test
   public void testPrependSlash()
   {
      log.info("testRemovePrecedingSlash");
      final String noPrecedingSlash = "test/something";
      final String expected = PathUtil.SLASH + noPrecedingSlash;
      final String result = PathUtil.optionallyPrependSlash(noPrecedingSlash);
      Assert.assertEquals("Call to prepend a slash failed", expected, result);
   }

   /**
    * Ensures that optionally prepending a slash upon
    * a String already prefixed with one returns a no-op
    */
   @Test
   public void testNoOpPrependSlash()
   {
      log.info("testRemovePrecedingSlash");
      final String precedingSlash = "/test/something";
      final String result = PathUtil.optionallyPrependSlash(precedingSlash);
      Assert.assertEquals("Call to optionally prepend a slash upon input with slash prefix should return no-op",
            precedingSlash, result);
   }

   /**
    * Ensures that a slash may be appended to a given String
    */
   @Test
   public void testAppendSlash()
   {
      log.info("testRemovePrecedingSlash");
      final String noFollowingSlash = "test/something";
      final String expected = noFollowingSlash + PathUtil.SLASH;
      final String result = PathUtil.optionallyAppendSlash(noFollowingSlash);
      Assert.assertEquals("Call to append a slash failed", expected, result);
   }

   /**
    * Ensures that optionally appending a slash upon
    * a String already suffixed with one returns a no-op
    */
   @Test
   public void testNoOpAppendSlash()
   {
      log.info("testRemovePrecedingSlash");
      final String followingSlash = "/test/something/";
      final String result = PathUtil.optionallyAppendSlash(followingSlash);
      Assert.assertEquals("Call to optionally append a slash upon input with slash suffix should return no-op",
            followingSlash, result);
   }

   /**
    * Ensures that an absolute path may be converted to 
    * full relative directory form
    */
   @Test
   public void testAdjustToRelativeDirectoryContext()
   {
      log.info("testRemovePrecedingSlash");
      final String absoulteWithoutTrailingSlash = "/test/something";
      final String expected = absoulteWithoutTrailingSlash.substring(1) + PathUtil.SLASH;
      final String result = PathUtil.adjustToRelativeDirectoryContext(absoulteWithoutTrailingSlash);
      Assert.assertEquals("Adjusting to relative form should strip preceding slash and append a trailing one",
            expected, result);
   }

   /**
    * Ensures that a relative path may be converted to 
    * full absolute directory form
    */
   @Test
   public void testAdjustToAbsoluteDirectoryContext()
   {
      log.info("testRemovePrecedingSlash");
      final String relativeWithoutTrailingSlash = "test/something";
      final String expected = PathUtil.SLASH + relativeWithoutTrailingSlash + PathUtil.SLASH;
      final String result = PathUtil.adjustToAbsoluteDirectoryContext(relativeWithoutTrailingSlash);
      Assert.assertEquals("Adjusting to absolute form should prepend preceding slash and append a trailing one",
            expected, result);
   }

   /**
    * Ensures that an absolute form may be composed
    * from a relative context and base
    */
   @Test
   public void testComposeAbsoulteContext()
   {
      log.info("testComposeAbsoulteContext");
      final String base = "something";
      final String context = "somethingunder";
      final String expected = PathUtil.SLASH + base + PathUtil.SLASH + context;
      final String result = PathUtil.composeAbsoluteContext(base, context);
      Assert.assertEquals("Composing an absolute context from base and context did not succeed", expected, result);
   }

}

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
package org.jboss.declarchive.impl.base.resource;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.impl.base.asset.FileAsset;
import org.junit.Test;

/**
 * Test to ensure that we can use a File as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class FileResourceTestCase
{
   private static final String BASE_PATH = "src/test/resources/org/jboss/declarchive/impl/base/resource/";

   private static final String EXISTING_FILE = BASE_PATH + "Test.properties";

   private static final String NON_EXISTING_FILE = BASE_PATH + "NoFileShouldBePlacedHere.properties";

   @Test
   public void shouldBeAbleToReadFile() throws Exception
   {
      Asset resource = new FileAsset(new File(EXISTING_FILE));
      InputStream io = resource.getStream();

      Assert.assertNotNull(io);
      Assert.assertEquals("Should be able to read the content of the resource", "declarch=true", 
            TestUtils.convertToString(io));
   }

   @Test
   public void shouldBeAbleToReadDefaultName() throws Exception
   {
      Asset resource = new FileAsset(new File(EXISTING_FILE));
      Assert.assertEquals("A File resource should use the file name as default name, not absolute path",
            "Test.properties", resource.getDefaultName());
   }

   @Test
   public void shouldBeAbleToReadDefaultPath() throws Exception
   {
      Asset resource = new FileAsset(new File(EXISTING_FILE));
      Assert.assertEquals("A File resource should use the file parent name as default path",
            "/src/test/resources/org/jboss/declarchive/impl/base/resource/", resource.getDefaultPath().get());
   }

   @Test
   public void shouldThrowExceptionOnNullFile() throws Exception
   {
      try
      {
         new FileAsset(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (Exception e)
      {
         Assert.assertEquals("A null file argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void shouldThrowExceptionOnMissingFile() throws Exception
   {
      try
      {
         new FileAsset(new File(NON_EXISTING_FILE));
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (Exception e)
      {
         Assert.assertEquals("A non existing file should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }
}

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

import java.io.InputStream;

import junit.framework.Assert;

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.impl.base.asset.UrlAsset;
import org.junit.Test;

/**
 * Test to ensure that we can use a URL as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class URLResourceTestCase
{
   private static final String EXISTING_RESOURCE = "org/jboss/declarchive/impl/base/resource/Test.properties";

   @Test
   public void shouldBeAbleToReadURL() throws Exception
   {
      Asset resource = new UrlAsset(
            Thread.currentThread().getContextClassLoader().getResource(EXISTING_RESOURCE));

      InputStream io = resource.getStream();

      Assert.assertNotNull(io);
      Assert.assertEquals("Should be able to read the content of the resource", "declarch=true", TestUtils
            .convertToString(io));
   }

   @Test
   public void shouldBeAbleToReadDefaultName() throws Exception
   {
      Asset resource = new UrlAsset(
            Thread.currentThread().getContextClassLoader().getResource(EXISTING_RESOURCE));

      Assert.assertEquals("A URL resource should use the file name as default name, not absolute path",
            "Test.properties", resource.getDefaultName());
   }

   @Test
   public void shouldBeAbleToReadDefaultPath() throws Exception
   {
      Asset resource = new UrlAsset(
            Thread.currentThread().getContextClassLoader().getResource(EXISTING_RESOURCE));

      Assert.assertEquals("A URL resource should use / as default path",
            "/", resource.getDefaultPath().get());
   }

   @Test
   public void shouldThrowExceptionOnNullURL() throws Exception
   {
      try
      {
         new UrlAsset(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (Exception e)
      {
         Assert.assertEquals("A null url argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }
}

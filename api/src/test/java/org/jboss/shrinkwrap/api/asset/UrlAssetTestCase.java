/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api.asset;

import java.io.InputStream;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Test to ensure that we can use a URL as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class UrlAssetTestCase
{
   private static final String EXISTING_RESOURCE = "org/jboss/shrinkwrap/api/asset/Test.properties";

   @Test
   public void shouldBeAbleToReadURL() throws Exception
   {
      Asset asset = new UrlAsset(SecurityActions.getThreadContextClassLoader().getResource(EXISTING_RESOURCE));

      InputStream io = asset.openStream();

      Assert.assertNotNull(io);
      Assert.assertEquals("Should be able to read the content of the resource", "shrinkwrap=true", ApiTestUtils
            .convertToString(io));
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

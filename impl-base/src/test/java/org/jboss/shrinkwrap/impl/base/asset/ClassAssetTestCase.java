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
package org.jboss.shrinkwrap.impl.base.asset;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Asset;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test to ensure that we are able to use Classes as Resources.
 *
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class ClassAssetTestCase
{

   @Test
   public void shouldBeAbleToReadThisClass() throws Exception
   {
      Class<?> clazz = ClassAssetTestCase.class;
      Asset asset = new ClassAsset(clazz);
      InputStream io = asset.getStream();

      Assert.assertNotNull(io);
      Assert.assertEquals(
            "Loaded class should have the same size", 
            TestUtils.findLengthOfStream(io), 
            TestUtils.findLengthOfClass(clazz));
   }

   /**
    * https://jira.jboss.org/jira/browse/TMPARCH-19
    * <br/><br/>
    * A {@link Class} loaded by the Bootstrap ClassLoader will return a null {@link ClassLoader}, 
    * should use {@link Thread} current context {@link ClassLoader} instead.
    * 
    * @throws Exception
    */
   @Test
   public void shouldBeAbleAddBootstrapClass() throws Exception 
   {
      Class<?> bootstrapClass = Class.class;
      Asset asset = new ClassAsset(bootstrapClass);
      InputStream io = asset.getStream();

      Assert.assertNotNull(io);
      Assert.assertEquals(
            "Loaded class should have the same size",
            TestUtils.findLengthOfStream(io),
            TestUtils.findLengthOfClass(bootstrapClass));
   }

   @Test
   public void shouldThrowExceptionOnNullClass() throws Exception
   {
      try
      {
         new ClassAsset(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (Exception e)
      {
         Assert.assertEquals(
               "A null clazz argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, 
               e.getClass());
      }
   }
}

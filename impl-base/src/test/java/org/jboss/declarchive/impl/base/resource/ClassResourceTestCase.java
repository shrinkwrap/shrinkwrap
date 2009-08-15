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

import org.jboss.declarchive.api.Asset;
import org.jboss.declarchive.impl.base.asset.ClassAsset;
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
public class ClassResourceTestCase
{

   @Test
   public void shouldBeAbleToReadThisClass() throws Exception
   {
      Asset resource = new ClassAsset(ClassResourceTestCase.class);
      InputStream io = resource.getStream();

      Assert.assertNotNull(io);
   }

   // TODO: add test to byte compare expected result?

   @Test
   public void shouldBeAbleToReadDefaultName() throws Exception
   {
      Asset resource = new ClassAsset(ClassResourceTestCase.class);
      Assert.assertEquals("A Class resource should use class simple name + '.class' as default name",
            "ClassResourceTestCase.class", resource.getDefaultName());
   }

   @Test
   public void shouldBeAbleToReadPathName() throws Exception
   {
      Asset resource = new ClassAsset(ClassResourceTestCase.class);
      Assert.assertEquals("A Class resource should use class package name as default path",
            "/org/jboss/declarchive/impl/base/resource/", resource.getDefaultPath().get());
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
         Assert.assertEquals("A null clazz argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }
}

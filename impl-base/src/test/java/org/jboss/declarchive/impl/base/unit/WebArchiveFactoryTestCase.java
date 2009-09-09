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
package org.jboss.declarchive.impl.base.unit;

import junit.framework.Assert;

import org.jboss.declarchive.api.WebArchiveFactory;
import org.jboss.declarchive.api.spec.WebArchive;
import org.junit.Test;

/**
 * WebArchiveFactoryTestCase
 * 
 * TestCase to ensure the WebArchiveFactory functions correctly.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public class WebArchiveFactoryTestCase
{

   /**
    * Test to ensure WebArchiveFactory returns a valid archive
    * @throws Exception
    */
   @Test
   public void testCreateArchive() throws Exception
   {
      String name = "test.war";
      WebArchive webArchive = WebArchiveFactory.create(name);
      Assert.assertNotNull("Should return a valid web archive", webArchive);
      Assert.assertEquals("Should return the same name as factory arg", name, webArchive.getName());
   }

   /**
    * Test to ensure WebArchiveFactory.create requires an archive name
    * @throws Exception
    */
   @Test
   public void testCreateArchiveRequiresName() throws Exception
   {
      try
      {
         WebArchiveFactory.create(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      }
      catch (IllegalArgumentException expected)
      {
      }

   }

}

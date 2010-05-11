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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Asset;
import org.junit.Test;

/**
 * Test Cases for the {@link EmptyAsset}
 *
 * @author <a href="mailto:dan.j.allen@gmail.com">Dan Allen</a>
 * @version $Revision: $
 */
public class EmptyAssetTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(EmptyAssetTestCase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that the contents of the asset is empty.
    */
   @Test
   public void testRoundtrip() throws Exception
   {
      // Log
      log.info("testRoundtrip");

      // Make Asset
      final Asset asset = EmptyAsset.INSTANCE;

      // Get the contents back out of the asset
      final InputStream stream = asset.openStream();
      final ByteArrayOutputStream out = new ByteArrayOutputStream(0);
      int read;
      while ((read = stream.read()) != -1)
      {
         out.write(read);
      }

      Assert.assertEquals("Roundtrip did not produce empty contents", 0, out.toByteArray().length);
   }
}

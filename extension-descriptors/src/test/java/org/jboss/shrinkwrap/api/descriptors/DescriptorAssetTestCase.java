/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api.descriptors;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.spec.servlet.web.WebAppDescriptor;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Ensures the {@link DescriptorAsset} is working as
 * contracted
 * 
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class DescriptorAssetTestCase
{

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that the contents of a {@link Descriptor} remain
    * intact when expressed as a {@link DescriptorAsset}
    */
   @Test
   public void descriptorAssetRoundtrip()
   {
      // Make the descriptor
      final WebAppDescriptor descriptor = Descriptors.create(WebAppDescriptor.class).displayName("Test");

      // Represent as an Asset
      final Asset asset = new DescriptorAsset(descriptor);

      // Get the contents of the Asset
      final String roundtrip = new String(IOUtil.asByteArray(asset.openStream()));

      // Ensure equal to expected
      final String original = descriptor.exportAsString();
      Assert.assertEquals("Contents of descriptor asset were not as expected", original, roundtrip);
   }

   /**
    * Ensures we must supply a descriptor when creating a new {@link DescriptorAsset}
    */
   @Test(expected = IllegalArgumentException.class)
   public void requiresDescriptor()
   {
      new DescriptorAsset(null);
   }
}

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
package org.jboss.shrinkwrap.impl.base;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Specializer;
import org.junit.Test;


/**
 * ArchiveExtensionLoaderTest to ensure extension loading correctness
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ArchiveExtensionLoaderTest
{

   @Test
   public void shouldBeAbleToLoadExtension() throws Exception {
      Extension extension = new ArchiveExtensionLoader<Extension>(Extension.class)
               .load(new MemoryMapArchiveImpl());

      Assert.assertNotNull(extension);
      
      Assert.assertTrue(extension.getClass() == ExtensionImpl.class);
   }

   @Test
   public void shouldBeAbleToOverrideExtension() throws Exception {
      Extension extension = new ArchiveExtensionLoader<Extension>(Extension.class)
               .addExtesionOverride(Extension.class, ExtensionImpl2.class)
               .load(new MemoryMapArchiveImpl());

      Assert.assertNotNull(extension);
   
      Assert.assertTrue(extension.getClass() == ExtensionImpl2.class);
   }
   
   @Test(expected = RuntimeException.class)
   public void shouldThrowExceptionOnMissingExtension() throws Exception {
      new ArchiveExtensionLoader<ArchiveExtensionLoaderTest>(ArchiveExtensionLoaderTest.class)
         .load(new MemoryMapArchiveImpl());
   }

   @Test(expected = RuntimeException.class)
   public void shouldThrowExceptionOnWrongImplType() throws Exception {
      new ArchiveExtensionLoader<WrongImplExtension>(WrongImplExtension.class)
         .load(new MemoryMapArchiveImpl());
   }

   public static interface WrongImplExtension extends Specializer {
      
   }
   
   public static interface Extension extends Specializer {
      
   }

   public static class ExtensionImpl extends SpecializedBase implements Extension {

      private Archive<?> archive;
      public ExtensionImpl(Archive<?> archive)
      {
         this.archive = archive;
      }
      
      @Override
      protected Archive<?> getArchive()
      {
         return archive;
      }
   }

   public static class ExtensionImpl2 extends SpecializedBase implements Extension {

      private Archive<?> archive;
      public ExtensionImpl2(Archive<?> archive)
      {
         this.archive = archive;
      }
      
      @Override
      protected Archive<?> getArchive()
      {
         return archive;
      }
   }
}

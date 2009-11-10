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

import org.jboss.shrinkwrap.api.Specializer;
import org.jboss.shrinkwrap.spi.MemoryMapArchive;
import org.junit.Test;


/**
 * ArchivesTest
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ArchivesTestCase
{

   @Test
   public void shouldBeAbleToCreateANewArchive() throws Exception {
      String archiveName = "test.war";
      
      Specializer archive = Archives.create(archiveName);
      
      Assert.assertNotNull(
            "A archive should have been created", archive);
      
      Assert.assertTrue(
            "A MemoryMapArchive should have been created", 
            archive instanceof MemoryMapArchive);
      
      Assert.assertEquals(
            "Should have the same name as given imput", 
            archiveName, 
            ((MemoryMapArchive)archive).getName());
   }
}

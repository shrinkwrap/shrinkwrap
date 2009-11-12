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
package org.jboss.shrinkwrap.impl.base.importer;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.importer.ExplodedImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.path.BasicPath;
import org.junit.Assert;
import org.junit.Test;

/**
 * TestCase to ensure the correctness of the ExplodedImporter
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ExplodedImporterTestCase
{

   private static final String EXISTING_DIRECTORY_RESOURCE = "exploded_import_test";
   
   private static final String EXISTING_FILE_RESOURCE = "exploded_import_test/Test.properties";
   
   @Test
   public void shouldBeAbleToImportADriectory() throws Exception {
      
      Archive<?> archive = Archives.create("test.jar", ExplodedImporter.class)
                              .importDirectory(
                                    Thread.currentThread().getContextClassLoader()
                                       .getResource(EXISTING_DIRECTORY_RESOURCE).toURI().getPath()
                              )
                              .as(JavaArchive.class);
      
      Assert.assertTrue(
            "Root files should be imported",
            archive.contains(new BasicPath("/Test.properties")));      
      
      Assert.assertTrue(
            "Nested files should be imported",
            archive.contains(new BasicPath("/META-INF/MANIFEST.FM")));      

      Assert.assertTrue(
            "Nested files should be imported",
            archive.contains(new BasicPath("/org/jboss/Test.properties")));  
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void shouldThrowExceptionIfImportingAFile() throws Exception {
    
      Archives.create("test.jar", ExplodedImporter.class)
                  .importDirectory(
                        Thread.currentThread().getContextClassLoader()
                           .getResource(EXISTING_FILE_RESOURCE).toURI().getPath()
                  );
   }
}

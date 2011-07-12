import java.util.Set;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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

/**
 * Ensures that we can add classes in the default package to an archive.
 * This test is also in the default package due to compiler restrictions on
 * importing from default package.
 * 
 * SHIRNKWRAP-143
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class DefaultPackageAddTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(DefaultPackageAddTestCase.class.getName());
   
   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   private ArchivePath classInDefaultPackagePath;
   private ArchivePath innerClassInDefaultPackagePath;
   
   //-------------------------------------------------------------------------------------||
   // Fixtures ---------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   @Before
   public void setupPaths() {
      classInDefaultPackagePath = ArchivePaths.create("/ClassInDefaultPackage.class");
      innerClassInDefaultPackagePath = ArchivePaths.create("/ClassInDefaultPackage$InnerClassInDefaultPackage.class");
   }
   
   @After
   public void cleanupPaths() {
      classInDefaultPackagePath = null;
      innerClassInDefaultPackagePath = null;
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||   

   /**
    * Ensures that classes from the default package may be added
    * 
    * SHRINKWRAP-143
    */
   @Test
   public void canAddClassFromDefaultPackage() throws Exception
   {
      // Create an archive with Classes from the default package
      final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .addClass(ClassInDefaultPackage.class);
      log.info(archive.toString(true));

      assertClassesWereAdded(archive);
   }
   
   /**
    * Makes sure classes in the default package, and only in the default package, are added.
    * 
    * SHRINKWRAP-233, SHRINKWRAP-302
    */
   @Test
   public void testAddDefaultPackage() {
      JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
      archive.addDefaultPackage();
      
      assertClassesWereAdded(archive);
      
      /*
       * It should have added only the following three classes:
       * 1. /DefaultPackageAddTestCase.class
       * 2. /ClassInDefaultPackage.class
       * 3. /ClassInDefaultPackage$InnerClassInDefaultPackage.class
       */ 
      final int expectedSize = 3;
      final int size = archive.getContent().size();
      Assert.assertEquals("Not the expected number of assets added to the archive", expectedSize, size);
   }

   private void assertClassesWereAdded(JavaArchive archive) {
      Assert.assertTrue("Class in default package was not added to archive", archive
            .contains(classInDefaultPackagePath));
      Assert.assertTrue("Inner class in default package was not added to archive", archive
            .contains(innerClassInDefaultPackagePath));
   }
   
}

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
package org.jboss.shrinkwrap.impl.base.export;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.Assert;

/**
 * ExportTestBase
 * 
 * Base support for the exporter test cases 
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
class ExportTestBase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ExportTestBase.class.getName());

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /** 
    * Returns a temp directory for a test.  Needs the test
    */
   protected File createTempDirectory(String testName) throws Exception
   {
      // Qualify the temp directory by test case
      File tempDirectoryParent = new File(this.getTarget(), this.getClass().getSimpleName());
      // Qualify the temp directory by test name
      File tempDirectory = new File(tempDirectoryParent, testName);
      log.info("Temp Directory: " + tempDirectory.getCanonicalPath());
      if (tempDirectory.exists())
      {
         IOUtil.deleteDirectory(tempDirectory);
      }
      Assert.assertTrue("Temp directory should be clear before start", !tempDirectory.exists());
      tempDirectory.mkdirs();
      return tempDirectory;
   }

   /**
    * Returns the target directory 
    */
   protected File getTarget()
   {
      try
      {
         return new File(new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()), "../");
      }
      catch (final URISyntaxException urise)
      {
         throw new RuntimeException("Could not obtain the target URI", urise);
      }
   }

}

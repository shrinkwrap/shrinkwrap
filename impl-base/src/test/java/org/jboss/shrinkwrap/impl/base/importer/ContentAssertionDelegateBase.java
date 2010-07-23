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
package org.jboss.shrinkwrap.impl.base.importer;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.jboss.shrinkwrap.api.Archive;

/**
 * Base delegate class for asserting that contents in some
 * formated may be imported as expected
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public abstract class ContentAssertionDelegateBase
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   /**
    * Name of the expected empty directory
    */
   protected static final String EXPECTED_EMPTY_DIR = "empty_dir/";

   /**
    * Name of the expected nested directory
    */
   protected static final String EXPECTED_NESTED_EMPTY_DIR = "parent/empty_dir/";
   
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the name of the 
    */
   protected abstract String getExistingResourceName();

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the existing resource to be imported/tested
    */
   public final File getExistingResource() throws URISyntaxException
   {
      final String existingResourceName = this.getExistingResourceName();
      assert existingResourceName != null : "Existing resource name must be specified by implementors";
      final URL existingResourceLocation = SecurityActions.getThreadContextClassLoader().getResource(
            existingResourceName);
      assert existingResourceLocation != null : "Existing resource was not found at specified location: "
            + existingResourceName;
      return new File(existingResourceLocation.toURI());
   }

   /**
    * Compare the content of the original file and what was imported.
    * 
    * @param importedArchive The archive used for import
    * @param originalSource The original classpath resource file
    */
   public abstract void assertContent(final Archive<?> importedArchive, final File originalSource) throws Exception;

}

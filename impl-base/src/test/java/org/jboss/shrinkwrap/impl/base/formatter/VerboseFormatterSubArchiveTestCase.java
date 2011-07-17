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
package org.jboss.shrinkwrap.impl.base.formatter;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.formatter.Formatters;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.test.ArchiveTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Ensures that the {@link Formatters.VERBOSE} is functioning
 * as expected with sub-archives
 * 
 * @author <a href="mailto:gerhard.poul@gmail.com">Gerhard Poul</a>
 * @version $Revision: $
 */
@Ignore
public class VerboseFormatterSubArchiveTestCase {

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(VerboseFormatterSubArchiveTestCase.class.getName());

   /**
    * Name of the test archive
    */
   static final String NAME_ARCHIVE = "testArchive.war";
   
   /**
    * Expected output of the formatter
    */
   private static final String EXPECTED_OUTPUT = NAME_ARCHIVE
         + ":\n/WEB-INF/\n"
         + "/WEB-INF/lib/\n"
         + "/WEB-INF/lib/library.jar/\n"
         + "/WEB-INF/lib/library.jar/org/\n"
         + "/WEB-INF/lib/library.jar/org/jboss/\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/impl/\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/impl/base/\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/impl/base/formatter/\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/impl/base/formatter/FormatterTestBase.class\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/impl/base/test/\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/impl/base/test/ArchiveTestBase.class\n"
         + "/WEB-INF/lib/library.jar/org/jboss/shrinkwrap/impl/base/test/ArchiveTestBase$1.class";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Archive used in testing
    */
   private Archive<?> archive;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates the archive used in the test
    */
   @Before
   public void createArchive()
   {
      JavaArchive libraryJar = ShrinkWrap.create(JavaArchive.class, "library.jar").addClasses(FormatterTestBase.class,
            ArchiveTestBase.class);
      
      archive = ShrinkWrap.create(WebArchive.class, NAME_ARCHIVE)
            .addAsLibrary(libraryJar);
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that the {@link Formatter} is functioning as 
    * contracted given a test archive
    */
   @Test
   public void testFormatter()
   {
      // Format
      final String formatted = archive.toString(Formatters.VERBOSE);

      // Log out, just so we can see
      log.info(formatted);

      // Ensure expected form
      TestCase.assertEquals("Formatter output did not match that expected", EXPECTED_OUTPUT, formatted);
   }

}

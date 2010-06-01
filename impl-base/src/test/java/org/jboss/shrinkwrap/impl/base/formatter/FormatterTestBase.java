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
package org.jboss.shrinkwrap.impl.base.formatter;

import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.test.ArchiveTestBase;
import org.junit.Before;
import org.junit.Test;

/**
 * Base support for implementations of {@link Formatter}
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public abstract class FormatterTestBase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(FormatterTestBase.class.getName());

   /**
    * Name of the test archive
    */
   static final String NAME_ARCHIVE = "testArchive.jar";

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
      archive = ShrinkWrap.create(JavaArchive.class, NAME_ARCHIVE).addClasses(FormatterTestBase.class,
            ArchiveTestBase.class);
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
      final String formatted = archive.toString(getFormatter());

      // Log out, just so we can see
      log.info(formatted);

      // Ensure expected form
      TestCase.assertEquals("Formatter output did not match that expected", this.getExpectedOutput(), formatted);
   }

   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Obtains the {@link Formatter} instance to be used for this test
    */
   abstract Formatter getFormatter();

   /**
    * Obtains the output expected of the {@link Formatter} instance returned by {@link FormatterTestBase#getFormatter()}
    * @return
    */
   abstract String getExpectedOutput();
}

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
package org.jboss.shrinkwrap.dependencies;

import java.io.File;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.dependencies.Dependencies;
import org.junit.Test;

/**
 * Tests to ensure the {@link ArchiveFileSystem} is working as contracted
 * 
 * @author <a href="jbailey@redhat.com">John Bailey</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class DependenciesUnitTestCase
{

   // -------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(DependenciesUnitTestCase.class.getName());
   
   // -------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   // -------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   /**
    * Adds some files/directories to an {@link Archive}, then reads via the {@link ArchiveFileSystem}
    */
   @Test
   public void simpleResolution() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war")
            .addLibraries(Dependencies.artifact("org.apache.maven.plugins:maven-compiler-plugin:2.3.2").resolve().toArray(new Archive<?>[0]));

      log.info("Created archive: " + war.toString(true));

      war.as(ZipExporter.class).exportTo(new File("target/test.war"));
   }
}

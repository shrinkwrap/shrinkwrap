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

import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.junit.Assert;
import org.junit.Test;

/**
 * Tests to ensure Dependencies resolve dependencies correctly
 * 
 * @author <a href="kpiwko@redhat.com">Karel Piwko</a>
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
   // Tests ------------------------------------------------------------------------------||
   // -------------------------------------------------------------------------------------||

   // @Test
   public void testSimpleResolutionWrongArtifact() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "testSimpleResolutionWrongArtifact.war")
            .addLibraries(Dependencies.artifact("org.apache.maven.plugins:maven-compiler-plugin:2.3.2").resolve());

      log.info("Created archive: " + war.toString(true));

      Assert.assertTrue("Archive containes maven-compiler-plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-compiler-plugin-2.3.2-jar")));

      war.as(ZipExporter.class).exportTo(new File("target/testSimpleResolutionWrongArtifact.war"));
   }

   @Test
   public void testSimpleResolution() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "testSimpleResolution.war")
            .addLibraries(Dependencies.artifact("org.apache.maven.plugins:maven-help-plugin:2.1.1")
                                      .resolve());

      log.info("Created archive: " + war.toString(true));

      Assert.assertTrue("Archive contains maven help plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-help-plugin-2.1.1.jar")));
      Assert.assertTrue("Archive contains maven core",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-core-2.0.6.jar")));

      // war.as(ZipExporter.class).exportTo(new File("target/testSimpleResolution.war"));
   }

   @Test
   public void testMultipleResolution() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "testMultipleResolution.war")
            .addLibraries(Dependencies.artifact("org.apache.maven.plugins:maven-help-plugin:2.1.1")
                                      .artifact("org.apache.maven.plugins:maven-patch-plugin:1.1.1")
                                      .resolve());

      log.info("Created archive: " + war.toString(true));

      Assert.assertTrue("Archive contains maven help plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-help-plugin-2.1.1.jar")));
      Assert.assertTrue("Archive contains maven patch plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-patch-plugin-1.1.1.jar")));
      Assert.assertTrue("Archive contains maven core",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-core-2.0.6.jar")));

      // war.as(ZipExporter.class).exportTo(new File("target/testMultipleResolution.war"));
   }

   @Test
   public void testCustomDependencies() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "testCustomDependencies.war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                                      .artifact("org.apache.maven.plugins:maven-help-plugin:2.1.1")
                                      .artifact("org.apache.maven.plugins:maven-patch-plugin:1.1.1")
                                      .resolve());

      log.info("Created archive: " + war.toString(true));

      Assert.assertTrue("Archive contains maven help plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-help-plugin-2.1.1.jar")));
      Assert.assertTrue("Archive contains maven patch plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-patch-plugin-1.1.1.jar")));
      Assert.assertTrue("Archive contains maven core",
            war.contains(ArchivePaths.create("WEB-INF/lib", "maven-core-2.0.6.jar")));

      // war.as(ZipExporter.class).exportTo(new File("target/testCustomDependencies.war"));
   }

   @Test
   public void testParentPomRepositories() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "testParentPomRepositories.war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("src/test/resources/child/pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .resolve());

      log.info("Created archive: " + war.toString(true));

      Assert.assertTrue("Archive contains maven help plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "arquillian-junit-1.0.0.Alpha4.jar")));

      // war.as(ZipExporter.class).exportTo(new File("target/testParentPomRepositories.war"));
   }

   @Test
   public void testPomBasedDependencies() throws Exception
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "testPomBasedDependencies.war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .resolveFrom("src/test/resources/dependency/pom.xml"));

      log.info("Created archive: " + war.toString(true));

      Assert.assertTrue("Archive contains maven help plugin",
            war.contains(ArchivePaths.create("WEB-INF/lib", "selenium-2.0a5.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/testPomBasedDependencies.war"));
   }
}

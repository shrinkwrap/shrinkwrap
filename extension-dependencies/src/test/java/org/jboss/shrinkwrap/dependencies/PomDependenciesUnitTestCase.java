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

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class PomDependenciesUnitTestCase
{
   /**
    * Tests loading of a POM file with parent not available on local file system
    * @throws DependencyException
    */
   @Test
   public void testParentPomRepositories() throws DependencyException
   {
      String name = "parentPomRepositories";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   /**
    * Tests loading of a POM file with parent available on local file system
    * @throws DependencyException
    */
   @Test
   public void testParentPomRemoteRepositories() throws DependencyException
   {
      String name = "parentPomRemoteRepositories";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("src/test/resources/child/pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }
   
   /**
    * Tests loading of a POM file with parent available on local file system
    * Uses POM to get artifact version
    * @throws DependencyException
    */
   @Test
   public void testArtifactVersionRetrievalFromPom() throws DependencyException
   {
      String name = "artifactVersionRetrievalFromPom";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("src/test/resources/dependency/pom.xml")
                           .artifact("org.seleniumhq.selenium:selenium")
                              .exclusions("org.seleniumhq.selenium:selenium-firefox-driver", "org.seleniumhq.selenium:selenium-chrome-driver", "org.seleniumhq.selenium:selenium-ie-driver")
                           .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }
   
   /**
    * Tests loading of a POM file with parent available on local file system.
    * However, the artifact version is not used from there, but specified manually
    * @throws DependencyException
    */
   @Test
   public void testArtifactVersionRetrievalFromPomOverride() throws DependencyException
   {
      String name = "artifactVersionRetrievalFromPomOverride";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("src/test/resources/dependency/pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .resolve());

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }


   /**
    * Tests resolution of dependencies for a POM file with parent on local file system
    * @throws DependencyException
    */
   @Test
   public void testPomBasedDependencies() throws DependencyException
   {
      String name = "pomBasedDependencies";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .resolveFrom("src/test/resources/dependency/pom.xml"));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }

   /**
    * Tests resolution of dependencies for a POM file without parent on local file system
    * @throws DependencyException
    */
   @Test
   public void testPomRemoteBasedDependencies() throws DependencyException
   {
      String name = "pomRemoteBasedDependencies";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .resolveFrom("pom.xml"));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"));
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);

   }
}

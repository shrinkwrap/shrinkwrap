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
import java.util.Map;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.jboss.shrinkwrap.dependencies.impl.filter.CombinedFilter;
import org.jboss.shrinkwrap.dependencies.impl.filter.ScopeFilter;
import org.jboss.shrinkwrap.dependencies.impl.filter.StrictFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class DependencyFilterUnitTestCase
{
   /**
    * Tests that only directly defined artifacts are added to dependencies
    * @throws DependencyException
    */
   @Test
   public void testStrictFilter() throws DependencyException
   {
      String name = "strictFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .resolve(new StrictFilter()));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is only one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged arquillian-junit:1.0.0.Alpha4",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-junit-1.0.0.Alpha4.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));

   }

   /**
    * Tests that only directly defined artifacts are added to dependencies,
    * the artifact version is taken from a POM file
    * @throws DependencyException
    */
   @Test
   public void testStrictFilterInferredVersion() throws DependencyException
   {
      String name = "strictFilterInferredVersion";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                  .loadPom("src/test/resources/dependency/pom.xml")
                  .artifact("org.jboss.arquillian:arquillian-junit")
                  .resolve(new StrictFilter()));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is only one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged arquillian-junit:1.0.0-SNAPSHOT",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-junit-1.0.0-SNAPSHOT.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));

   }

   /**
    * Tests loading of a POM file with parent not available on local file system
    * @throws DependencyException
    */
   @Test
   public void testDefaultScopeFilter() throws DependencyException
   {
      String name = "defaultScopeFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .resolve(new ScopeFilter()));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is only one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged arquillian-junit:1.0.0.Alpha4",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-junit-1.0.0.Alpha4.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));
   }

   /**
    * Tests limiting of the scope
    * @throws DependencyException
    */
   @Test
   public void testTestScopeFilter() throws DependencyException
   {
      String name = "testScopeFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .scope("test")
                           .resolve(new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/artifactVersionRetrievalFromPomOverride.tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));
   }

   /**
    * Tests limiting of the scope and strict artifacts
    * @throws DependencyException
    */
   @Test
   public void testCombinedScopeFilter() throws DependencyException
   {
      String name = "testCombinedScopeFilter";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .scope("test")
                           .artifact("org.jboss.arquillian:arquillian-testng:1.0.0.Alpha4")
                           .resolve(new CombinedFilter(new ScopeFilter("", "test"), new StrictFilter())));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There are two jars in the package", 2, map.size());
      Assert.assertTrue("The artifact is packaged arquillian-junit:1.0.0.Alpha4",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-junit-1.0.0.Alpha4.jar")));
      Assert.assertTrue("The artifact is packaged arquillian-testng:1.0.0.Alpha4",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-testng-1.0.0.Alpha4.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));
   }

   /**
    * Tests limiting of the scope and strict artifacts. Uses artifacts() method
    * @throws DependencyException
    */
   @Test
   public void testCombinedScopeFilter2() throws DependencyException
   {
      String name = "testCombinedScopeFilter2";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifacts("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4", "org.jboss.arquillian:arquillian-testng:1.0.0.Alpha4")
                           .scope("test")
                           .resolve(new CombinedFilter(new ScopeFilter("test"), new StrictFilter())));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There are two jars in the package", 2, map.size());
      Assert.assertTrue("The artifact is packaged arquillian-junit:1.0.0.Alpha4",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-junit-1.0.0.Alpha4.jar")));
      Assert.assertTrue("The artifact is packaged arquillian-testng:1.0.0.Alpha4",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-testng-1.0.0.Alpha4.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));
   }

   /**
    * Tests limiting of the scope and strict artifacts
    * @throws DependencyException
    */
   @Test
   public void testCombinedScopeFilter3() throws DependencyException
   {
      String name = "testCombinedScopeFilter3";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .scope("test")
                           .artifact("org.jboss.arquillian:arquillian-testng:1.0.0.Alpha4")
                           .scope("provided")
                           .resolve(new CombinedFilter(new ScopeFilter("provided"), new StrictFilter())));

      Map<ArchivePath, Node> map = war.getContent(JAR_FILTER);

      Assert.assertEquals("There is one jar in the package", 1, map.size());
      Assert.assertTrue("The artifact is packaged arquillian-testng:1.0.0.Alpha4",
            map.containsKey(ArchivePaths.create("WEB-INF/lib/arquillian-testng-1.0.0.Alpha4.jar")));

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));
   }

   /**
    * Tests resolution of dependencies for a POM file with parent on local file system
    * @throws DependencyException
    */
   @Test
   public void testPomBasedDependenciesWithScope() throws DependencyException
   {
      String name = "pomBasedDependenciesWithScope";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .resolveFrom("src/test/resources/dependency/pom.xml", new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/pomBasedDependencies.tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"));

   }

   // filter to retrieve jar files only
   private static final Filter<ArchivePath> JAR_FILTER = new Filter<ArchivePath>()
   {
      public boolean include(ArchivePath object)
         {
            if (object.get().endsWith(".jar"))
            {
               return true;
            }

            return false;
         }
   };

}

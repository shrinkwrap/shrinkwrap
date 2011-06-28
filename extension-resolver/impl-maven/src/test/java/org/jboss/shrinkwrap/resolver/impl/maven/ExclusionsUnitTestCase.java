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
package org.jboss.shrinkwrap.resolver.impl.maven;

import java.io.File;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.ResolutionException;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;
import org.jboss.shrinkwrap.resolver.api.maven.filter.ScopeFilter;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class ExclusionsUnitTestCase
{
   @BeforeClass
   public static void setRemoteRepository()
   {
      System.setProperty(MavenRepositorySettings.ALT_LOCAL_REPOSITORY_LOCATION, "target/the-other-repository");
   }

   /**
    * Tests exclusion of the artifacts
    * 
    * @throws ResolutionException
    */
   @Test
   public void testExclusion() throws ResolutionException
   {
      String name = "exclusion";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("target/poms/test-parent.xml")
                  .artifact("org.jboss.shrinkwrap.test:test-dependency-test:jar:1.0.0").scope("test")
                  .exclusion("org.jboss.shrinkwrap.test:test-deps-f")
                  .resolveAs(GenericArchive.class, new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/"
            + name + ".tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests exclusion of the artifacts
    * 
    * @throws ResolutionException
    */
   @Test
   @Deprecated
   public void testExclusionDeprecated() throws ResolutionException
   {
      String name = "exclusion";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class).loadReposFromPom("target/poms/test-parent.xml")
                  .artifact("org.jboss.shrinkwrap.test:test-dependency-test:jar:1.0.0").scope("test")
                  .exclusion("org.jboss.shrinkwrap.test:test-deps-f")
                  .resolveAs(GenericArchive.class, new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/"
            + name + ".tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests exclusion of the artifacts
    * 
    * @throws ResolutionException
    */
   @Test
   public void testExclusions() throws ResolutionException
   {
      String name = "exclusions";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class).loadMetadataFromPom("target/poms/test-parent.xml")
                  .artifact("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0").scope("test")
                  .exclusions("org.jboss.shrinkwrap.test:test-deps-f", "org.jboss.shrinkwrap.test:test-deps-g")
                  .resolveAs(GenericArchive.class, new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/"
            + name + ".tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests exclusion of the artifacts
    * 
    * @throws ResolutionException
    */
   @Test
   @Deprecated
   public void testExclusionsDeprecated() throws ResolutionException
   {
      String name = "exclusions";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war").addAsLibraries(
            DependencyResolvers.use(MavenDependencyResolver.class).loadReposFromPom("target/poms/test-parent.xml")
                  .artifact("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0").scope("test")
                  .exclusions("org.jboss.shrinkwrap.test:test-deps-f", "org.jboss.shrinkwrap.test:test-deps-g")
                  .resolveAs(GenericArchive.class, new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/"
            + name + ".tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests exclusion of all transitive artifacts
    */
   @Test
   public void testUniversalExclusion()
   {

      File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadMetadataFromPom("target/poms/test-parent.xml")
            .artifact("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0").scope("test").exclusion("*")
            .resolveAsFiles(new ScopeFilter("test"));

      Assert.assertEquals("There is only one jar in the package", 1, files.length);
      Assert.assertEquals("The file is packaged as test-dependency-test-1.0.0.jar", "test-dependency-test-1.0.0.jar",
            files[0].getName());
   }

   /**
    * Tests exclusion of all transitive artifacts
    */
   @Test
   @Deprecated
   public void testUniversalExclusionDeprecated()
   {

      File[] files = DependencyResolvers.use(MavenDependencyResolver.class)
            .loadReposFromPom("target/poms/test-parent.xml")
            .artifact("org.jboss.shrinkwrap.test:test-dependency-test:1.0.0").scope("test").exclusion("*")
            .resolveAsFiles(new ScopeFilter("test"));

      Assert.assertEquals("There is only one jar in the package", 1, files.length);
      Assert.assertEquals("The file is packaged as test-dependency-test-1.0.0.jar", "test-dependency-test-1.0.0.jar",
            files[0].getName());
   }

}

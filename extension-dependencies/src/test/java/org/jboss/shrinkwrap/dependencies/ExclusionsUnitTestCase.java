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
import org.jboss.shrinkwrap.dependencies.impl.filter.ScopeFilter;
import org.junit.Test;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class ExclusionsUnitTestCase
{

   /**
    * Tests exclusion of the artifacts
    * @throws DependencyException
    */
   @Test
   public void testExclusion() throws DependencyException
   {
      String name = "exclusion";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .scope("test")
                           .exclusion("org.jboss.arquillian:arquillian-api")
                           .resolve(new ScopeFilter("test")));

      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"), "test");
      desc.validateArchive(war).results();

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }

   /**
    * Tests exclusion of the artifacts
    * @throws DependencyException
    */
   @Test
   public void testExclusions() throws DependencyException
   {
      String name = "exclusions";

      WebArchive war = ShrinkWrap.create(WebArchive.class, name + ".war")
            .addLibraries(Dependencies.use(MavenDependencies.class)
                           .loadPom("pom.xml")
                           .artifact("org.jboss.arquillian:arquillian-junit:1.0.0.Alpha4")
                           .scope("test")
                           .exclusions("org.jboss.arquillian:arquillian-api", "org.jboss.arquillian:arquillian-spi")
                           .resolve(new ScopeFilter("test")));

      
      DependencyTreeDescription desc = new DependencyTreeDescription(new File("src/test/resources/dependency-trees/" + name + ".tree"), "test");
      desc.validateArchive(war).results();
      

      war.as(ZipExporter.class).exportTo(new File("target/" + name + ".war"), true);
   }
   
   

}

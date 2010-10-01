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
package org.jboss.shrinkwrap.dependencies.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipFile;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.dependencies.DependencyBuilder;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * A default implementation of dependency builder based on Maven
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenDependencies implements DependencyBuilder
{
   private static final Archive<?>[] ARCHIVE_CAST = new Archive<?>[0];

   private MavenRepositorySettings settings;

   private MavenDependencyRepository repository;

   private List<Dependency> dependencies;

   public MavenDependencies()
   {
      this.settings = new MavenRepositorySettings();
      this.repository = new MavenDependencyRepository();
      this.dependencies = new ArrayList<Dependency>();
   }

   public MavenDependencies configureFrom(String path)
   {
      return this;
   }

   public MavenDependencies loadPom(String path) throws Exception
   {
      Validate.notNullOrEmpty(path, "Path to the pom.xml file must be defined");

      File pom = new File(path);
      settings.setRemoteRepositories(settings.createModelFromPom(pom));
      return this;
   }

   public Archive<?>[] resolveFrom(String path) throws Exception
   {
      Validate.notNullOrEmpty(path, "Path to the pom.xml file must be defined");
      File pom = new File(path);
      Model model = settings.createModelFromPom(pom);
      settings.setRemoteRepositories(model);

      // wrap from Maven to Aether
      for (org.apache.maven.model.Dependency d : model.getDependencies())
      {

         List<Exclusion> exclusions = new ArrayList<Exclusion>();
         for (org.apache.maven.model.Exclusion e : d.getExclusions())
         {
            Exclusion exclusion = new Exclusion(e.getGroupId(), e.getArtifactId(), null, null);
            exclusions.add(exclusion);
         }

         // TODO does Maven type always map to Aether extension ?
         Artifact artifact = new DefaultArtifact(d.getGroupId(), d.getArtifactId(), d.getClassifier(), d.getType(), d.getVersion());
         boolean optional = Boolean.valueOf(d.getOptional());

         dependencies.add(new Dependency(artifact, d.getScope(), optional, exclusions));
      }
      return new MavenArtifactBuilder().resolution();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang.String)
    */
   public MavenArtifactBuilder artifact(String coordinates)
   {
      Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");
      return new MavenArtifactBuilder(coordinates);
   }

   public class MavenArtifactBuilder implements DependencyBuilder.ArtifactBuilder
   {
      private Artifact artifact;

      private List<Exclusion> exclusions = new ArrayList<Exclusion>();

      private String scope;

      private boolean optional;

      public MavenArtifactBuilder(String coordinates)
      {
         this.artifact = new DefaultArtifact(coordinates);
      }

      // used for resolution from pom.xml only
      private MavenArtifactBuilder()
      {
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#exclusion(org.sonatype.aether.graph.Exclusion)
       */
      public MavenArtifactBuilder exclusion(Exclusion exclusion)
      {
         this.exclusions.add(exclusion);
         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#exclusions(org.sonatype.aether.graph.Exclusion[])
       */
      public MavenArtifactBuilder exclusions(Exclusion... exclusions)
      {
         this.exclusions.addAll(Arrays.asList(exclusions));
         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#exclusions(java.util.Collection)
       */
      public MavenArtifactBuilder exclusions(Collection<Exclusion> exclusions)
      {
         this.exclusions.addAll(exclusions);
         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#optional(boolean)
       */
      public MavenArtifactBuilder optional(boolean optional)
      {
         this.optional = optional;
         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#scope(java.lang.String)
       */
      public MavenArtifactBuilder scope(String scope)
      {
         this.scope = scope;
         return this;
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder.ArtifactBuilder#resolve()
       */
      public Archive<?>[] resolve() throws Exception
      {
         Dependency dependency = new Dependency(artifact, scope, optional, exclusions);
         dependencies.add(dependency);

         return resolution();
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang.String)
       */
      public MavenArtifactBuilder artifact(String coordinates)
      {
         Validate.notNullOrEmpty(coordinates, "Artifact coordinates must not be null or empty");

         Dependency dependency = new Dependency(artifact, scope, optional, exclusions);
         dependencies.add(dependency);

         return new MavenArtifactBuilder(coordinates);
      }

      private Archive<?>[] resolution() throws Exception
      {
         Validate.notEmpty(dependencies, "No dependencies were set or found");

         RepositorySystem system = repository.getRepositorySystem();

         CollectRequest request = new CollectRequest(dependencies, null, settings.getRemoteRepositories());

         // wrap artifact files to archives
         List<ArtifactResult> artifacts = system.resolveDependencies(repository.getSession(system, settings), request, null);
         Collection<Archive<?>> archives = new ArrayList<Archive<?>>(artifacts.size());
         for (ArtifactResult artifact : artifacts)
         {
            File file = artifact.getArtifact().getFile();
            Archive<?> archive = ShrinkWrap.create(JavaArchive.class, file.getName()).as(ZipImporter.class).importFrom(new ZipFile(file)).as(JavaArchive.class);

            archives.add(archive);
         }

         return archives.toArray(ARCHIVE_CAST);
      }
   }
}

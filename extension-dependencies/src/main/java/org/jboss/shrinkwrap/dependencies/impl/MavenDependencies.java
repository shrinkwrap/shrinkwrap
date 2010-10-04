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
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * A default implementation of dependency builder based on Maven.
 * 
 * Apart from contract, it allows to load Maven settings from an
 * XML file, configure remote repositories from an POM file and retrieve
 * dependencies defined in a POM file, including ones in POM parents.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenDependencies implements DependencyBuilder
{
   private static final Archive<?>[] ARCHIVE_CAST = new Archive<?>[0];

   private MavenRepositorySystem system;

   private RepositorySystemSession session;

   private List<Dependency> dependencies;

   /**
    * Constructs new instance of MavenDependencies
    */
   public MavenDependencies()
   {
      this.system = new MavenRepositorySystem(new MavenRepositorySettings());
      this.dependencies = new ArrayList<Dependency>();
      this.session = system.getSession();
   }

   /**
    * Configures Maven from a settings.xml file
    * @param path A path to a settings.xml configuration file
    * @return A dependency builder with a configuration from given file
    */
   public MavenDependencies configureFrom(String path)
   {
      Validate.notNullOrEmpty(path, "Path to the settings.xml must be defined");
      File settings = new File(path);
      system.loadSettings(settings, session);
      return this;
   }

   /**
    * Loads remote repositories for a POM file. If repositories are
    * defined in the parent of the POM file and there are accessible
    * via local file system, they are set as well.
    * 
    * These remote repositories are used to resolve the
    * artifacts during dependency resolution.
    * 
    * 
    * @param path A path to the POM file, must not be {@code null} or empty
    * @return A dependency builder with remote repositories set according
    *         to the content of POM file.
    * @throws Exception
    */
   public MavenDependencies loadPom(String path) throws Exception
   {
      Validate.notNullOrEmpty(path, "Path to the pom.xml file must be defined");

      File pom = new File(path);
      system.loadPom(pom, session);
      return this;
   }

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws Exception
    */
   public Archive<?>[] resolveFrom(String path) throws Exception
   {
      Validate.notNullOrEmpty(path, "Path to the pom.xml file must be defined");
      File pom = new File(path);
      Model model = system.loadPom(pom, session);

      ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

      // wrap from Maven to Aether
      for (org.apache.maven.model.Dependency dependency : model.getDependencies())
      {
         dependencies.add(MavenConverter.convert(dependency, stereotypes));
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
         Validate.notEmpty(dependencies, "No dependencies were set for resolution");

         CollectRequest request = new CollectRequest(dependencies, null, system.getRemoteRepositories());

         // wrap artifact files to archives
         Collection<ArtifactResult> artifacts = system.resolveDependencies(session, request, null);
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

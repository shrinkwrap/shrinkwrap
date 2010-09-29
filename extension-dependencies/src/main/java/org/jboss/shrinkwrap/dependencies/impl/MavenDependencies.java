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

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.dependencies.DependencyBuilder;
import org.jboss.shrinkwrap.dependencies.DependencyRepository;
import org.jboss.shrinkwrap.dependencies.RepositorySettings;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.resolution.ArtifactResult;
import org.sonatype.aether.util.artifact.DefaultArtifact;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenDependencies implements DependencyBuilder
{

   private RepositorySettings settings;

   private DependencyRepository repository;

   private Artifact artifact;

   private List<Exclusion> exclusions;

   private String scope;

   private boolean optional;

   public MavenDependencies()
   {
      this.settings = new MavenRepositorySettings();
      this.repository = new MavenDependencyRepository();
      this.exclusions = new ArrayList<Exclusion>();
      this.scope = "";
      this.optional = false;
   }

   public DependencyBuilder configureFrom(String path) {
      return this;
   }
   
   public DependencyBuilder loadPom(String path) {
      return this;
   }
   
   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#artifact(java.lang.String)
    */
   public DependencyBuilder artifact(String coordinates)
   {
      this.artifact = new DefaultArtifact(coordinates);
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#exclusion(org.sonatype.aether.graph.Exclusion)
    */
   public DependencyBuilder exclusion(Exclusion exclusion)
   {
      this.exclusions.add(exclusion);
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#exclusions(org.sonatype.aether.graph.Exclusion[])
    */
   public DependencyBuilder exclusions(Exclusion... exclusions)
   {
      this.exclusions.addAll(Arrays.asList(exclusions));
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#exclusions(java.util.Collection)
    */
   public DependencyBuilder exclusions(Collection<Exclusion> exclusions)
   {
      this.exclusions.addAll(exclusions);
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#optional(boolean)
    */
   public DependencyBuilder optional(boolean optional)
   {
      this.optional = optional;
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#scope(java.lang.String)
    */
   public DependencyBuilder scope(String scope)
   {
      this.scope = scope;
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyBuilder#resolve()
    */
   public Collection<Archive<?>> resolve() throws Exception
   {
      Validate.notNull(artifact, "No artifact was set to be resolved");

      RepositorySystem system = repository.getRepositorySystem();
      RepositorySystemSession session = repository.getSession(system, settings);

      Dependency dependency = new Dependency(artifact, scope, optional, exclusions);

      CollectRequest request = new CollectRequest(dependency, settings.getRemoteRepositories());

      List<ArtifactResult> artifacts = system.resolveDependencies(session, request, null);

      Collection<Archive<?>> archives = new ArrayList<Archive<?>>(artifacts.size());

      for (ArtifactResult artifact : artifacts)
      {
         File file = artifact.getArtifact().getFile();
         Archive<?> archive = ShrinkWrap.create(JavaArchive.class).as(ZipImporter.class).importFrom(new ZipFile(file)).as(JavaArchive.class);

         archives.add(archive);
      }

      return archives;

   }

}

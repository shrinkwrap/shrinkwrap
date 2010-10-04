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
import java.util.Collection;
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelBuildingResult;
import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.PlexusContainerException;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.DependencyFilter;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.ArtifactRequest;
import org.sonatype.aether.resolution.ArtifactResolutionException;
import org.sonatype.aether.resolution.ArtifactResult;

/**
 * Abstraction of the repository system for purposes of dependency
 * resolution used by Maven
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenRepositorySystem
{
   private final MavenRepositorySettings settings;

   private final RepositorySystem system;

   public MavenRepositorySystem(MavenRepositorySettings settings)
   {
      this.settings = settings;
      this.system = getRepositorySystem();
   }

   /**
    * Spawns a working session from the repository system
    * @param system A repository system
    * @param settings A configuration of current session, such as local or remote repositories and listeners
    * @return The working session for dependency resolution
    */
   public RepositorySystemSession getSession()
   {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      session.setLocalRepositoryManager(system.newLocalRepositoryManager(settings.getLocalRepository()));
      session.setTransferListener(settings.getTransferListener());
      session.setRepositoryListener(settings.getRepositoryListener());
      return session;
   }

   public Model loadPom(File pom, RepositorySystemSession session) throws ModelBuildingException
   {
      ModelBuildingRequest request = new DefaultModelBuildingRequest();
      request.setPomFile(pom);
      request.setModelResolver(new MavenModelResolver(system, session, getRemoteRepositories()));

      ModelBuilder builder = new DefaultModelBuilderFactory().newInstance();
      ModelBuildingResult result = builder.build(request);

      Model model = result.getEffectiveModel();
      settings.setRemoteRepositories(model);
      return model;
   }

   public void loadSettings(File file, RepositorySystemSession session)
   {
      if (!(session instanceof MavenRepositorySystemSession))
      {
         throw new IllegalArgumentException("Cannot set local repository path for a Maven repository, expecting instance of " + MavenRepositorySystemSession.class.getName() + ", but got " + session.getClass().getName());
      }

      SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
      request.setUserSettingsFile(file);
      settings.buildSettings(request);
      ((MavenRepositorySystemSession) session).setLocalRepositoryManager(system.newLocalRepositoryManager(settings.getLocalRepository()));
   }

   public List<RemoteRepository> getRemoteRepositories()
   {
      return settings.getRemoteRepositories();
   }

   public Collection<ArtifactResult> resolveDependencies(RepositorySystemSession session, CollectRequest request, DependencyFilter filter) throws DependencyCollectionException, ArtifactResolutionException
   {
      return system.resolveDependencies(session, request, filter);
   }

   public ArtifactResult resolveArtifact(RepositorySystemSession session, ArtifactRequest request) throws ArtifactResolutionException
   {
      return system.resolveArtifact(session, request);
   }

   /**
    * Finds a current implementation of repository system.
    * A {@link RepositorySystem} is an entry point to dependency resolution
    * @return A repository system
    */
   private RepositorySystem getRepositorySystem()
   {
      try
      {
         return new DefaultPlexusContainer().lookup(RepositorySystem.class);
      }
      catch (ComponentLookupException e)
      {
         throw new RuntimeException("Unable to lookup component RepositorySystem, cannot establish Aether dependency resolver.", e);
      }
      catch (PlexusContainerException e)
      {
         throw new RuntimeException("Unable to load RepositorySystem component by Plexus, cannot establish Aether dependency resolver.", e);
      }
   }

}

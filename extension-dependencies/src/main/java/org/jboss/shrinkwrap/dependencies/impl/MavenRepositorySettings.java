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
import java.util.List;

import org.apache.maven.model.Model;
import org.apache.maven.model.Repository;
import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.sonatype.aether.RepositoryListener;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.transfer.TransferListener;

/**
 * An encapsulation of settings required to be handle Maven dependency resolution.
 * 
 * It holds links to local and remote repositories
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenRepositorySettings
{
   private static final String DEFAULT_USER_SETTINGS_PATH = SecurityActions.getProperty("user.home").concat("/.m2/settings.xml");
   private static final String DEFAULT_REPOSITORY_PATH = SecurityActions.getProperty("user.home").concat("/.m2/repository");

   private List<RemoteRepository> repositories;

   // settings object
   private Settings settings;

   /**
    * Creates a new Maven settings using default user settings,
    * that is the one located in ${user.home}/.m2/settings.xml.
    * 
    * Appends Maven Central repository to available remote repositories.
    * 
    * The file is used to track local Maven repository.
    */
   public MavenRepositorySettings()
   {
      SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
      request.setUserSettingsFile(new File(DEFAULT_USER_SETTINGS_PATH));

      this.repositories = new ArrayList<RemoteRepository>();
      this.repositories.add(centralRepository());

      buildSettings(request);
   }

   /**
    * Sets a list of remote repositories using a POM model.
    * Maven Central repository is always added, even if it is not included in the model.
    * @param model the POM model
    */
   public void setRemoteRepositories(Model model)
   {
      List<RemoteRepository> newRepositories = new ArrayList<RemoteRepository>();
      newRepositories.add(centralRepository());
      for (Repository repository : model.getRepositories())
      {
         newRepositories.add(MavenConverter.convert(repository));
      }

      this.repositories = newRepositories;
   }

   /**
    * Returns a list of available remote repositories
    * @return The list of remote repositories
    */
   public List<RemoteRepository> getRemoteRepositories()
   {
      return repositories;
   }

   /**
    * Returns a local repository determined from settings.xml or the
    * default repository located
    * @return The local repository
    */
   public LocalRepository getLocalRepository()
   {
      return new LocalRepository(settings.getLocalRepository());
   }

   /**
    * Returns a listener which captures repository based events, such as
    * an attempt to download from a repository and similar events.
    * @return The {@link RepositoryListener} implementation
    */
   public RepositoryListener getRepositoryListener()
   {
      return new LogRepositoryListener();
   }

   /**
    * Returns a listener which captures transfer based events, such
    * as a download progress and similar events.
    * @return The {@link TransferListener} implementation
    */
   public TransferListener getTransferListener()
   {
      return new LogTransferListerer();
   }

   // creates a link to Maven Central Repository
   private RemoteRepository centralRepository()
   {
      return new RemoteRepository("central", "default", "http://repo1.maven.org/maven2");
   }

   /**
    * Replaces currents settings with ones retrieved from request.
    * 
    * The list of remote repositories is not affected.
    * 
    * @param request The request for new settings
    */
   public void buildSettings(SettingsBuildingRequest request)
   {

      SettingsBuildingResult result;
      try
      {
         SettingsBuilder builder = new DefaultSettingsBuilderFactory().newInstance();
         result = builder.build(request);
      }
      catch (SettingsBuildingException e)
      {
         e.printStackTrace();
         throw new RuntimeException("Unable to parse Maven configuration", e);
      }

      Settings settings = result.getEffectiveSettings();

      if (settings.getLocalRepository() == null)
      {
         settings.setLocalRepository(DEFAULT_REPOSITORY_PATH);
      }
      this.settings = settings;
   }
}

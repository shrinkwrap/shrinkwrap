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

import org.apache.maven.settings.Settings;
import org.apache.maven.settings.building.DefaultSettingsBuilderFactory;
import org.apache.maven.settings.building.DefaultSettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuilder;
import org.apache.maven.settings.building.SettingsBuildingException;
import org.apache.maven.settings.building.SettingsBuildingRequest;
import org.apache.maven.settings.building.SettingsBuildingResult;
import org.jboss.shrinkwrap.dependencies.RepositorySettings;
import org.sonatype.aether.repository.LocalRepository;
import org.sonatype.aether.repository.RemoteRepository;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenRepositorySettings implements RepositorySettings
{
   /**
    * 
    */
   private static final long serialVersionUID = 1L;

   private static final String DEFAULT_GLOBAL_SETTINGS_PATH = System.getProperty("user.home").concat("/.m2/settings.xml");
   private static final String DEFAULT_REPOSITORY_PATH = System.getProperty("user.home").concat("/.m2/repository");

   private Settings settings;
  
   
   public MavenRepositorySettings()
   {
      SettingsBuilder builder = createBuilder();
      SettingsBuildingRequest request = new DefaultSettingsBuildingRequest();
      request.setGlobalSettingsFile(new File(DEFAULT_GLOBAL_SETTINGS_PATH));

      this.settings = buildSettings(builder, request);
   }

   public List<RemoteRepository> getRemoteRepositories()
   {
      List<RemoteRepository> repositories = new ArrayList<RemoteRepository>();
      repositories.add(new RemoteRepository("central", "default", "http://repo1.maven.org/maven2"));

      return repositories;
   }

   public LocalRepository getLocalRepository()
   {
      return new LocalRepository(settings.getLocalRepository());
   }

   private Settings buildSettings(SettingsBuilder builder, SettingsBuildingRequest request)
   {

      SettingsBuildingResult result;
      try
      {
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
      return settings;
   }

   private SettingsBuilder createBuilder()
   {
      return new DefaultSettingsBuilderFactory().newInstance();
   }

}

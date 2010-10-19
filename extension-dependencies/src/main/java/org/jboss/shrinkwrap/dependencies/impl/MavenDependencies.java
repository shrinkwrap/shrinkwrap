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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.model.Model;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.dependencies.DependencyBuilder;
import org.jboss.shrinkwrap.dependencies.DependencyException;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.collection.DependencyCollectionException;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.resolution.ArtifactResolutionException;
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
   private static final Logger log = Logger.getLogger(MavenDependencies.class.getName());

   private static final Archive<?>[] ARCHIVE_CAST = new Archive<?>[0];

   private static final Pattern COORDINATES_PATTERN = Pattern.compile("([^: ]+):([^: ]+)(:([^: ]*)(:([^: ]+))?)?(:([^: ]+))?");

   private static final int COORDINATES_GROUP_ID = 1;
   private static final int COORDINATES_ARTIFACT_ID = 2;
   private static final int COORDINATES_TYPE_ID = 4;
   private static final int COORDINATES_CLASSIFIER_ID = 6;
   private static final int COORDINATES_VERSION_ID = 8;

   private MavenRepositorySystem system;

   private RepositorySystemSession session;

   private List<Dependency> dependencies;

   private Map<ArtifactAsKey, Dependency> pomInternalDependencyManagement;

   /**
    * Constructs new instance of MavenDependencies
    */
   public MavenDependencies()
   {
      this.system = new MavenRepositorySystem(new MavenRepositorySettings());
      this.dependencies = new ArrayList<Dependency>();
      this.pomInternalDependencyManagement = new HashMap<ArtifactAsKey, Dependency>();
      this.session = system.getSession();
   }

   /**
    * Configures Maven from a settings.xml file
    * @param path A path to a settings.xml configuration file
    * @return A dependency builder with a configuration from given file
    */
   public MavenDependencies configureFrom(String path)
   {
      Validate.readable(path, "Path to the settings.xml must be defined and accessible");
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
    * Additionally, it loads dependencies defined in the POM file model
    * in an internal cache, which can be later used to resolve an artifact
    * without explicitly specifying its version.
    * 
    * @param path A path to the POM file, must not be {@code null} or empty
    * @return A dependency builder with remote repositories set according
    *         to the content of POM file.
    * @throws Exception
    */
   public MavenDependencies loadPom(String path) throws DependencyException
   {
      Validate.readable(path, "Path to the pom.xml file must be defined and accessible");

      File pom = new File(path);
      Model model = system.loadPom(pom, session);

      ArtifactTypeRegistry stereotypes = session.getArtifactTypeRegistry();

      // store all dependency information to be able to retrieve versions later
      for (org.apache.maven.model.Dependency dependency : model.getDependencies())
      {
         Dependency d = MavenConverter.convert(dependency, stereotypes);
         pomInternalDependencyManagement.put(new ArtifactAsKey(d.getArtifact()), d);
      }

      return this;
   }

   /**
    * Uses dependencies and remote repositories defined in a POM file to and
    * tries to resolve them
    * @param path A path to the POM file
    * @return An array of ShrinkWrap archives
    * @throws DependencyException If dependencies could not be resolved or the
    *         POM processing failed
    */
   public Archive<?>[] resolveFrom(String path) throws DependencyException
   {
      Validate.readable(path, "Path to the pom.xml file must be defined and accessible");
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
   public MavenArtifactBuilder artifact(String coordinates) throws DependencyException
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

      public MavenArtifactBuilder(String coordinates) throws DependencyException
      {
         try
         {
            coordinates = resolveArtifactVersion(coordinates);
            this.artifact = new DefaultArtifact(coordinates);
         }
         catch (IllegalArgumentException e)
         {
            throw new DependencyException("Unable to create artifact from coordinates " + coordinates + ", " +
                  "they are either invalid or version information was not specified in loaded POM file (maybe the POM file wasn't load at all)", e);
         }
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
      public Archive<?>[] resolve() throws DependencyException
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

      private Archive<?>[] resolution() throws DependencyException
      {
         Validate.notEmpty(dependencies, "No dependencies were set for resolution");

         CollectRequest request = new CollectRequest(dependencies, null, system.getRemoteRepositories());

         // wrap artifact files to archives
         Collection<ArtifactResult> artifacts;
         try
         {
            artifacts = system.resolveDependencies(session, request, null);
         }
         catch (DependencyCollectionException e)
         {
            throw new DependencyException("Unable to collect dependeny tree for a resolution", e);
         }
         catch (ArtifactResolutionException e)
         {
            throw new DependencyException("Unable to resolve an artifact", e);
         }
         Collection<Archive<?>> archives = new ArrayList<Archive<?>>(artifacts.size());
         for (ArtifactResult artifact : artifacts)
         {
            Artifact a = artifact.getArtifact();
            // skip all non-jar artifacts
            if (!"jar".equals(a.getExtension()))
            {
               log.info("Removed non-JAR artifact " + a.toString() + " from archive, it's dependencies were fetched");
               continue;
            }

            File file = a.getFile();
            Archive<?> archive = ShrinkWrap.create(JavaArchive.class, file.getName()).as(ZipImporter.class).importFrom(convert(file)).as(JavaArchive.class);
            archives.add(archive);
         }

         return archives.toArray(ARCHIVE_CAST);
      }

      // converts a file to a ZIP file
      private ZipFile convert(File file) throws DependencyException
      {
         try
         {
            return new ZipFile(file);
         }
         catch (ZipException e)
         {
            throw new DependencyException("Unable to treat dependecy artifact \"" + file.getAbsolutePath() + "\" as a ZIP file", e);
         }
         catch (IOException e)
         {
            throw new DependencyException("Unable to access artifact file at \"" + file.getAbsolutePath() + "\".");
         }
      }

      /**
       * Tries to resolve artifact version from internal dependencies from a fetched POM file.
       * If no version is found, it simply returns original coordinates
       * @param coordinates The coordinates excluding the {@code version} part
       * @return Either coordinates with appended {@code version} or original coordinates
       */
      private String resolveArtifactVersion(String coordinates)
      {
         Matcher m = COORDINATES_PATTERN.matcher(coordinates);
         if (!m.matches())
         {
            throw new DependencyException("Bad artifact coordinates"
                  + ", expected format is <groupId>:<artifactId>[:<extension>[:<classifier>]][:<version>]");
         }

         ArtifactAsKey key = new ArtifactAsKey(m.group(COORDINATES_GROUP_ID), m.group(COORDINATES_ARTIFACT_ID),
               m.group(COORDINATES_TYPE_ID), m.group(COORDINATES_CLASSIFIER_ID));

         if (m.group(COORDINATES_VERSION_ID) == null && pomInternalDependencyManagement.containsKey(key))
         {
            String version = pomInternalDependencyManagement.get(key).getArtifact().getVersion();
            log.fine("Resolved version " + version + " from the POM file for the artifact: " + coordinates);
            coordinates = coordinates + ":" + version;
         }

         return coordinates;
      }

   }

}

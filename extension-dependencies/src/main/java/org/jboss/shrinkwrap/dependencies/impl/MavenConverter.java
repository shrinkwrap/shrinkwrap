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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.artifact.ArtifactType;
import org.sonatype.aether.artifact.ArtifactTypeRegistry;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.Exclusion;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.repository.RepositoryPolicy;
import org.sonatype.aether.util.artifact.ArtifactProperties;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.DefaultArtifactType;

/**
 * An utility class which provides conversion between Maven and Aether objects
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenConverter
{
   // disable instantiation
   private MavenConverter()
   {
      throw new AssertionError("Utility class MavenConverter cannot be instantiated.");
   }

   /**
    * Converts Maven {@link Repository} to Aether {@link RemoteRepository}
    * @param repository the Maven repository to be converted
    * @return Equivalent remote repository
    */
   public static RemoteRepository convert(org.apache.maven.model.Repository repository)
   {

      return new RemoteRepository()
            .setId(repository.getId())
            .setContentType(repository.getLayout())
            .setUrl(repository.getUrl())
            .setPolicy(true, convertPolicy(repository.getSnapshots()))
            .setPolicy(false, convertPolicy(repository.getReleases()));
   }

   /**
    * Converts Maven {@link org.apache.maven.model.Dependency} to Aether {@link org.sonatype.aether.graph.Dependency}
    * @param dependency the Maven dependency to be converted
    * @param registry the Artifact type catalog to determine common artifact properties
    * @return Equivalent Aether dependency
    */
   public static Dependency convert(org.apache.maven.model.Dependency dependency, ArtifactTypeRegistry registry)
   {
      ArtifactType stereotype = registry.get(dependency.getType());
      if (stereotype == null)
      {
         stereotype = new DefaultArtifactType(dependency.getType());
      }

      boolean system = dependency.getSystemPath() != null && dependency.getSystemPath().length() > 0;

      Map<String, String> props = null;
      if (system)
      {
         props = Collections.singletonMap(ArtifactProperties.LOCAL_PATH, dependency.getSystemPath());
      }

      Artifact artifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getClassifier(), null, dependency.getVersion(), props, stereotype);

      List<Exclusion> exclusions = new ArrayList<Exclusion>();
      for (org.apache.maven.model.Exclusion e : dependency.getExclusions())
      {
         Exclusion exclusion = new Exclusion(e.getGroupId(), e.getArtifactId(), "*", "*");
         exclusions.add(exclusion);
      }

      Dependency result = new Dependency(artifact, dependency.getScope(), dependency.isOptional(), exclusions);

      return result;
   }

   // converts repository policy
   private static RepositoryPolicy convertPolicy(org.apache.maven.model.RepositoryPolicy policy)
   {
      boolean enabled = true;
      String checksums = RepositoryPolicy.CHECKSUM_POLICY_WARN;
      String updates = RepositoryPolicy.UPDATE_POLICY_DAILY;

      if (policy != null)
      {
         enabled = policy.isEnabled();
         if (policy.getUpdatePolicy() != null)
         {
            updates = policy.getUpdatePolicy();
         }
         if (policy.getChecksumPolicy() != null)
         {
            checksums = policy.getChecksumPolicy();
         }
      }

      return new RepositoryPolicy(enabled, updates, checksums);
   }

}

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

import org.apache.maven.repository.internal.MavenRepositorySystemSession;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.jboss.shrinkwrap.dependencies.DependencyRepository;
import org.jboss.shrinkwrap.dependencies.RepositorySettings;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class MavenDependencyRepository implements DependencyRepository
{

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyRepository#getRepositorySystem()
    */
   public RepositorySystem getRepositorySystem() throws Exception
   {
      return new DefaultPlexusContainer().lookup(RepositorySystem.class);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyRepository#getSession(org.sonatype.aether.RepositorySystem, org.jboss.shrinkwrap.dependencies.MavenRepositorySettings)
    */
   public RepositorySystemSession getSession(RepositorySystem system, RepositorySettings settings)
   {
      MavenRepositorySystemSession session = new MavenRepositorySystemSession();
      session.setLocalRepositoryManager(system.newLocalRepositoryManager(settings.getLocalRepository()));
      session.setTransferListener(new ConsoleTransferListener(System.out));
      session.setRepositoryListener(new ConsoleRepositoryListener(System.out));

      return session;
   }

}

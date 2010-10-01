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

import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.sonatype.aether.RepositoryEvent;
import org.sonatype.aether.util.listener.AbstractRepositoryListener;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class LogRepositoryListener extends AbstractRepositoryListener
{
   private static final Logger log = Logger.getLogger(LogRepositoryListener.class.getPackage().getName() + ".Listener");

   // set up new logger with output directed to standard out
   static
   {
      Handler h = new StreamHandler(System.out, new ListenerFormatter());
      log.addHandler(h);
      log.setUseParentHandlers(false);
   }

   public void artifactDeployed(RepositoryEvent event)
   {
      log.info("Deployed " + event.getArtifact() + " to " + event.getRepository());
   }

   public void artifactDeploying(RepositoryEvent event)
   {
      log.fine("Deploying " + event.getArtifact() + " to " + event.getRepository());
   }

   public void artifactDescriptorInvalid(RepositoryEvent event)
   {
      log.warning("Invalid artifact descriptor for " + event.getArtifact() + ": "
            + event.getException().getMessage());
   }

   public void artifactDescriptorMissing(RepositoryEvent event)
   {
      log.warning("Missing artifact descriptor for " + event.getArtifact());
   }

   public void artifactInstalled(RepositoryEvent event)
   {
      log.info("Installed " + event.getArtifact() + " to " + event.getFile());
   }

   public void artifactInstalling(RepositoryEvent event)
   {
      log.fine("Installing " + event.getArtifact() + " to " + event.getFile());
   }

   public void artifactResolved(RepositoryEvent event)
   {
      log.info("Resolved artifact " + event.getArtifact() + " from " + event.getRepository());
   }

   public void artifactResolving(RepositoryEvent event)
   {
      log.fine("Resolving artifact " + event.getArtifact());
   }

   public void metadataDeployed(RepositoryEvent event)
   {
      log.info("Deployed " + event.getMetadata() + " to " + event.getRepository());
   }

   public void metadataDeploying(RepositoryEvent event)
   {
      log.fine("Deploying " + event.getMetadata() + " to " + event.getRepository());
   }

   public void metadataInstalled(RepositoryEvent event)
   {
      log.info("Installed " + event.getMetadata() + " to " + event.getFile());
   }

   public void metadataInstalling(RepositoryEvent event)
   {
      log.fine("Installing " + event.getMetadata() + " to " + event.getFile());
   }

   public void metadataInvalid(RepositoryEvent event)
   {
      log.warning("Invalid metadata " + event.getMetadata());
   }

   public void metadataResolved(RepositoryEvent event)
   {
      log.info("Resolved metadata " + event.getMetadata() + " from " + event.getRepository());
   }

   public void metadataResolving(RepositoryEvent event)
   {
      log.fine("Resolving metadata " + event.getMetadata() + " from " + event.getRepository());
   }
}

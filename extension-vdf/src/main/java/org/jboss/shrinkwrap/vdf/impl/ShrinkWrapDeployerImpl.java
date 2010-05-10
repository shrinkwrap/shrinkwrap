/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
  *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.shrinkwrap.vdf.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.jboss.beans.metadata.api.annotations.Inject;
import org.jboss.deployers.client.spi.Deployment;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.spi.DeploymentException;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.logging.Logger;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.vdf.api.ShrinkWrapDeployer;
import org.jboss.shrinkwrap.vfs3.ArchiveFileSystem;
import org.jboss.vfs.TempFileProvider;

/**
 * Base implementation of a {@link ShrinkWrapDeployer}.  Handles
 * mounting/unmounting of {@link Archive}s into VFS using 
 * the {@link ArchiveFileSystem}.  {@link Archive}s are then represented
 * as {@link VFSDeployment}s and passed into the {@link MainDeployer}.
 * Thread-safe; deploy and undeploy will block and service one client at a time.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public final class ShrinkWrapDeployerImpl implements ShrinkWrapDeployer
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ShrinkWrapDeployerImpl.class);

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Delegate to handle the real deployment.
    */
   private final MainDeployer deployer;

   /**
    * Temporary file provider with which archives will be mounted
    * before deployment
    */
   private final TempFileProvider tempFileProvider;

   /**
    * Mapping of all archives deployed via this {@link ShrinkWrapDeployer} instance.  Must
    * be guarded by "this" alongside deploy/undeploy operations.
    */
   private final Map<Archive<?>, ArchiveDeployment> deployments = new HashMap<Archive<?>, ArchiveDeployment>();

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link ShrinkWrapDeployerImpl} instance.
    * 
    * @param deployer The delegate deployer to handle 
    */
   public ShrinkWrapDeployerImpl(@Inject final MainDeployer deployer, @Inject final TempFileProvider tempFileProvider)
   {
      // Precondition checks
      if (deployer == null)
      {
         throw new IllegalArgumentException("deployer must be specified");
      }
      if (tempFileProvider == null)
      {
         throw new IllegalArgumentException("tempFileProvider must be specified");
      }

      // Set
      this.deployer = deployer;
      this.tempFileProvider = tempFileProvider;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.reloaded.api.ShrinkWrapDeployer#deploy(org.jboss.shrinkwrap.api.Archive<?>[])
    */
   @Override
   public void deploy(final Archive<?>... archives) throws IllegalArgumentException, DeploymentException
   {
      // Precondition checks
      if (archives == null)
      {
         throw new IllegalArgumentException("Archives must be specified (even if empty)");
      }

      // Create ArchiveDeployments from the Archives
      final Collection<ArchiveDeployment> newDeployments = new ArrayList<ArchiveDeployment>();
      for (final Archive<?> archive : archives)
      {
         newDeployments.add(new ArchiveDeployment(archive, tempFileProvider));
      }

      // Deployment must be atomic
      synchronized (this)
      {
         // Get the MainDeployer
         final MainDeployer deployer = this.deployer;

         // Init the deployments to add
         final Collection<ArchiveDeployment> deploymentsToAdd = new ArrayList<ArchiveDeployment>(archives.length);

         // For all new deployments
         for (final ArchiveDeployment deployment : newDeployments)
         {
            // Check that we haven't deployed this archive already
            final Archive<?> currentArchive = deployment.getArchive();
            if (deployments.containsKey(currentArchive))
            {
               // Log a warning and unmount this sucker
               log.warnf("Ignoring request to deploy already-deployed archive: %s", currentArchive);
               try
               {
                  deployment.getHandle().close();
               }
               catch (final IOException e)
               {
                  log.warnf(e, "Could not close handle for mounted archive %s", currentArchive);
               }
            }

            // Add to the MainDeployer
            try
            {
               deployer.addDeployment(deployment);
            }
            catch (final DeploymentException de)
            {
               // Some error occurred while adding; revert out all the previously-processed deployments and report the error
               log.warnf(de, "Error in adding deployment for archive %s; reverting out the previous deployments",
                     currentArchive);
               removeDeployments(deploymentsToAdd);
               throw de;
            }
            if (log.isTraceEnabled())
            {
               log.tracef("Adding archive for deployment: %s", currentArchive);
            }
            deploymentsToAdd.add(deployment);

         }

         // Process and check the MainDeployer
         deployer.process();
         try
         {
            deployer.checkComplete();
         }
         catch (final DeploymentException de)
         {
            // Some error occurred while processing and checking; revert out all the previously-processed deployments and report the error
            log.warn("Error in processing deployments; reverting all archives requested for deployment", de);
            removeDeployments(deploymentsToAdd);
            throw de;
         }

         // Success; now formally mark all deployments as deployed
         for (final ArchiveDeployment deploymentAdded : deploymentsToAdd)
         {
            this.deployments.put(deploymentAdded.getArchive(), deploymentAdded);
         }
         if (log.isDebugEnabled())
         {
            log.debugf("Deployed: %s", deploymentsToAdd);
         }
      }

   }

   /**
    * {@inheritDoc}
    * @see org.jboss.reloaded.api.ShrinkWrapDeployer#undeploy(org.jboss.shrinkwrap.api.Archive<?>[])
    */
   @Override
   public void undeploy(final Archive<?>... archives) throws IllegalArgumentException, DeploymentException
   {
      // Precondition checks
      if (archives == null)
      {
         throw new IllegalArgumentException("Archives must be specified (even if empty)");
      }

      // Undeployment must be atomic
      synchronized (this)
      {
         // Init a Collection of deployments to remove
         final Collection<ArchiveDeployment> deploymentsToRemove = new ArrayList<ArchiveDeployment>(archives.length);

         // Get the underlying deployments as keys from the archive
         for (final Archive<?> archive : archives)
         {

            final ArchiveDeployment deployment = this.deployments.get(archive);
            if (deployment == null)
            {
               log.warnf("No deployment has been made for archive %s; ignoring", archive.toString());
            }
            else
            {
               deploymentsToRemove.add(deployment);
            }
         }

         // Remove all
         this.removeDeployments(deploymentsToRemove);
      }

   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Makes a best-effort at removing all specified {@link Deployment}s from
    * the {@link MainDeployer}, throwing a {@link DeploymentException} if one is 
    * encountered during final check of the deployer.  Unconditionally attempts to 
    * close all associated {@link ArchiveDeployment#getHandle()}s.  MUST be called from within
    * a synchronized block using "this" as monitor.
    * @param deployments
    */
   private void removeDeployments(final Collection<ArchiveDeployment> deployments) throws DeploymentException
   {
      // Precondition checks
      assert deployments != null : "deployments must not be null";

      // Get the MainDeployer
      final MainDeployer deployer = this.deployer;

      // For all deployments
      for (final ArchiveDeployment deployment : deployments)
      {
         try
         {
            // Remove from the deployer
            deployer.removeDeployment(deployment);
         }
         catch (final DeploymentException de)
         {
            // Just log this removal failure
            log.warnf(de, "Could not remove deployment %s", deployment);
         }
      }

      // Mark a reference to cache an exception if we get one (to be rethrown)
      DeploymentException cachedException = null;

      // Process and check the deployer
      deployer.process();
      try
      {
         deployer.checkComplete();
      }
      catch (final DeploymentException de)
      {

         log.warn("Problem in Main Deployer while removing pending deployments", de);
         // We can overwrite the exception cached above because this 
         // is likely to be more important
         cachedException = de;
      }

      // For all deployments
      for (final ArchiveDeployment deployment : deployments)
      {
         try
         {
            // Close up
            deployment.getHandle().close();
         }
         catch (final IOException e)
         {
            // Don't bother WARNing this, but make a record in DEBUG
            if (log.isDebugEnabled())
            {
               log.debugf(e, "Could not close the mounted %s", deployment.getArchive());
            }
         }

         // Mark removed
         this.deployments.remove(deployment.getArchive());
      }

      // Throw a DeploymentException if we got one
      if (cachedException != null)
      {
         throw cachedException;
      }

   }

}

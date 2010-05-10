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
package org.jboss.shrinkwrap.vdf.api;

import org.jboss.deployers.spi.DeploymentException;
import org.jboss.shrinkwrap.api.Archive;

/**
 * Deployer for ShrinkWrap {@link Archive} types.  End-user
 * view to adapt archives directly into the Virtual Deployment
 * Framework.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface ShrinkWrapDeployer
{
   //-------------------------------------------------------------------------------------||
   // Contracts --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Deploys the specified archives into the Virtual Deployment Framework
    * as an atomic operation.  
    * 
    * @param archives The archives to deploy
    * @throws IllegalArgumentException If the archives are not specified (null)
    * @throws DeploymentException If an error occurred in deployment
    */
   void deploy(Archive<?>... archives) throws IllegalArgumentException, DeploymentException;

   /**
    * Undeploys the specified archives from the Virtual Deployment Framework.  Each
    * archive must have been previously deployed in via this {@link ShrinkWrapDeployer}
    * instance, else it will be ignored and logged as a warning.
    * 
    * @param archives The archives to undeploy
    * @throws IllegalArgumentException If the archives are not specified
    * @throws DeploymentException If an error occurred during undeployment
    */
   void undeploy(Archive<?>... archives) throws IllegalArgumentException, DeploymentException;

}

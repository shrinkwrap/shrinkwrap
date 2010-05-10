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

import java.io.Closeable;
import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import org.jboss.deployers.client.spi.Deployment;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.spi.attachments.Attachments;
import org.jboss.deployers.vfs.spi.client.VFSDeployment;
import org.jboss.deployers.vfs.spi.client.VFSDeploymentFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.vfs3.ArchiveFileSystem;
import org.jboss.vfs.TempDir;
import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

/**
 * {@link Deployment} implementation allowing an {@link Archive}
 * to be handed off directly to the Virtual Deployment Framework.
 * Clients are responsible, after undeployment has completed, to call 
 * {@link Closeable#close()} upon the return value of
 * {@link ArchiveDeployment#getHandle()}.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
class ArchiveDeployment implements VFSDeployment
{
   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * serialVersionUID
    */
   private static final long serialVersionUID = 1L;

   /**
    * The archive to be deployed
    */
   private final Archive<?> archive;

   /**
    * The VDF deployment view to be passed to {@link MainDeployer} 
    */
   private final Deployment deployment;

   /**
    * The VFS handle which may be closed on undeployment
    */
   private final Closeable handle;

   /**
    * VFS view of the {@link Archive}
    */
   private final VirtualFile file;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link ArchiveDeployment} by mounting the specified {@link Archive} into
    * an {@link ArchiveFileSystem} backed by the specified {@link TempFileProvider} 
    * @param archive The archive to be represented as a {@link Deployment}
    * @param tempFileProvider The backing temp file provider for the archive to be flushed 
    *   to real disk if necessary
    */
   ArchiveDeployment(final Archive<?> archive, final TempFileProvider tempFileProvider)
   {
      // Precondition checks
      assert archive != null : "archive must be specified";
      assert tempFileProvider != null : "provider must be specified";

      // Create a VFS VirtualFile and mount it
      final String archiveName = archive.getName();
      final TempDir tempDir;
      try
      {
         tempDir = tempFileProvider.createTempDir(archiveName);
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not create temp directory to back " + archive.toString(), ioe);
      }
      final VirtualFile virtualFile = VFS.getChild(UUID.randomUUID().toString()).getChild(archiveName);

      // Set
      this.file = virtualFile;
      this.archive = archive;
      try
      {
         this.handle = VFS.mount(virtualFile, new ArchiveFileSystem(archive, tempDir));
      }
      catch (final IOException ioe)
      {
         throw new RuntimeException("Could not mount for deployment: " + archive.toString(), ioe);
      }
      this.deployment = VFSDeploymentFactory.getInstance().createVFSDeployment(virtualFile);

   }

   //-------------------------------------------------------------------------------------||
   // Accessors --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @return the archive
    */
   Archive<?> getArchive()
   {
      return archive;
   }

   /**
    * @return the deployment
    */
   Deployment getDeployment()
   {
      return deployment;
   }

   /**
    * @return the handle
    */
   Closeable getHandle()
   {
      return handle;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /*
    * Everything below this line delegates to the real deployment
    */

   /**
    * {@inheritDoc}
    * @see org.jboss.deployers.client.spi.Deployment#getSimpleName()
    */
   @Override
   public String getSimpleName()
   {
      return this.getDeployment().getSimpleName();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.deployers.client.spi.Deployment#getName()
    */
   @Override
   public String getName()
   {
      return this.getDeployment().getName();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.deployers.client.spi.Deployment#getTypes()
    */
   @Override
   @SuppressWarnings("deprecation")
   public Set<String> getTypes()
   {
      return this.getDeployment().getTypes();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.deployers.client.spi.Deployment#setTypes(java.util.Set)
    */
   @Override
   @SuppressWarnings("deprecation")
   public void setTypes(final Set<String> types)
   {
      this.getDeployment().setTypes(types);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.deployers.spi.attachments.PredeterminedManagedObjectAttachments#getPredeterminedManagedObjects()
    */
   @Override
   public Attachments getPredeterminedManagedObjects()
   {
      return this.getDeployment().getPredeterminedManagedObjects();
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.deployers.spi.attachments.PredeterminedManagedObjectAttachments#setPredeterminedManagedObjects(org.jboss.deployers.spi.attachments.Attachments)
    */
   @Override
   public void setPredeterminedManagedObjects(final Attachments predetermined)
   {
      this.getDeployment().setPredeterminedManagedObjects(predetermined);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.deployers.vfs.spi.client.VFSDeployment#getRoot()
    */
   @Override
   public VirtualFile getRoot()
   {
      return file;
   }

   //-------------------------------------------------------------------------------------||
   // Overridden Implementations ---------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "ArchiveDeployment [archive=" + archive + ", " + VirtualFile.class.getSimpleName() + "=" + file.toString()
            + "]";
   }
}

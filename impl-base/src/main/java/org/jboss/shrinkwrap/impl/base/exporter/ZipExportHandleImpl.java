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
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.jboss.shrinkwrap.api.exporter.ArchiveExportException;
import org.jboss.shrinkwrap.api.exporter.ZipExportHandle;

/**
 * Implementation of a {@link ZipExportHandle}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
class ZipExportHandleImpl implements ZipExportHandle
{
   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Contents of the export process
    */
   private final IsReadReportingInputStream content;

   /**
    * Underlying job carrying out the encoding process; we never expose
    * this because if the user blocks on {@link Future#get()} than this could
    * deadlock the process (the writer Thread would be waiting for the reader Thread
    * to pull off the buffer).  So just provide a mechanism for the caller to see 
    * if we're done, and if there was an exception raised via {@link ZipExportHandle#isDone()}
    */
   private final Future<Void> job;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new handle with the specified content and pointer to the encoding
    * job.  Both arguments must be specified. 
    */
   ZipExportHandleImpl(final IsReadReportingInputStream content, final Future<Void> job)
   {
      // Precondition checks
      assert content != null : "Contents must be specified";
      assert job != null : "job must be specified";

      // Set
      this.content = content;
      this.job = job;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.shrinkwrap.api.exporter.ZipExportHandle#getContent()
    */
   @Override
   public InputStream getContent()
   {
      return content;
   }

   /**
    * @see org.jboss.shrinkwrap.api.exporter.ZipExportHandle#checkComplete()
    */
   @Override
   public void checkComplete() throws ArchiveExportException, IllegalStateException
   {
      // Ensure we can be called; the Stream must have been fully-read
      if (!this.content.isRead())
      {
         throw new IllegalStateException(
               "Cannot invoke until the stream has been fully-read; otherwise we might lead to deadlock");
      }

      // See if we're done
      final boolean done = job.isDone();

      // If done (either completed or exception)
      if (done)
      {
         try
         {
            // Block until the streams have been closed in the underlying job
            job.get();
         }
         catch (final InterruptedException e)
         {
            Thread.interrupted();
         }
         // Some error
         catch (final ExecutionException ee)
         {
            // Unwrap and rethrow
            final Throwable cause = ee.getCause();
            if (cause == null)
            {
               throw new IllegalStateException("Cause of execution failure not specified: ", ee);
            }
            // Wrap as our exception type and rethrow
            throw new ArchiveExportException(cause);
         }
      }

      // Return
      return;
   }
}

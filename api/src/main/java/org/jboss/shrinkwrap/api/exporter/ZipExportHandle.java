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
package org.jboss.shrinkwrap.api.exporter;

import java.io.InputStream;

/**
 * Handle returned to callers from a request to export via
 * the {@link ZipExporter}.  As the encoding process is an asynchronous
 * operation, here we provide the user access to read the 
 * content as well as check for completeness and integrity.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
public interface ZipExportHandle
{
   /**
    * Obtains an {@link InputStream} from which the encoded
    * content may be read.
    * 
    * @return
    */
   InputStream getContent();

   /**
    * Blocking operation which will wait until the encoding process's internal
    * streams have been closed and verified for integrity.  Do not call this method
    * until all bytes have been read from {@link ZipExportHandle#getContent()}; otherwise 
    * this may introduce a deadlock.  Any problems with the encoding process will be reported
    * by throwing {@link ArchiveExportException}.
    * @return
    * @throws ArchiveExportException If an error occurred during export
    * @throws IllegalStateException If invoked before {@link ZipExportHandle#getContent()} has been
    * fully-read
    */
   void checkComplete() throws ArchiveExportException, IllegalStateException;
}

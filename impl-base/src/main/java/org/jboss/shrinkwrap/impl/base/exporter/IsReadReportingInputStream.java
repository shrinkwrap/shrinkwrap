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
package org.jboss.shrinkwrap.impl.base.exporter;

import java.io.IOException;
import java.io.PipedInputStream;

/**
 * {@link PipedInputStream} which may report whether or not
 * it has been fully read.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
class IsReadReportingInputStream extends PipedInputStream
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Number of bytes read signaling the end has been reached
    */
   private static final int EOF = -1;

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Flag showing whether or not we've been fully-read
    */
   private boolean isRead;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new Stream
    */
   public IsReadReportingInputStream()
   {
      super();
   }

   /**
    * {@inheritDoc}
    * @see java.io.PipedInputStream#read()
    */
   @Override
   public synchronized int read() throws IOException
   {
      final int bytesRead = super.read();
      this.markReadOnStreamEnd(bytesRead);
      return bytesRead;
   }

   /**
    * {@inheritDoc}
    * @see java.io.PipedInputStream#read(byte[], int, int)
    */
   @Override
   public synchronized int read(byte[] b, int off, int len) throws IOException
   {
      final int bytesRead = super.read(b, off, len);
      this.markReadOnStreamEnd(bytesRead);
      return bytesRead;
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Marks this stream as read
    * if the number of bytes specified is equal to {@link IsReadReportingInputStream#EOF} 
    */
   private void markReadOnStreamEnd(final int bytesRead)
   {
      if (bytesRead == EOF)
      {
         try
         {
            isRead = true;
         }
         catch (final Exception e)
         {
            throw new RuntimeException("Encountered exception in callback", e);
         }
      }
   }

   /**
    * Returns whether or not this stream has been fully read
    * @return
    */
   boolean isRead()
   {
      return isRead;
   }
}

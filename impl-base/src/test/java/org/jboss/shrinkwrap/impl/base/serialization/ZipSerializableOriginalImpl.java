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
package org.jboss.shrinkwrap.impl.base.serialization;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.serialization.ZipSerializableView;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.AssignableBase;
import org.jboss.shrinkwrap.impl.base.Validate;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;

/**
 * Copy of {@link ZipSerializableViewImpl} so we may test for 
 * backwards-compatibility of the wire protocol.  This class is 
 * *never to change* once we publish/lock the wire protocol.  It is 
 * used to mock different ShrinkWrap versions communicating
 * with one another.
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ZipSerializableOriginalImpl extends AssignableBase implements ZipSerializableView
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * serialVersionUID
    */
   private static final long serialVersionUID = 1L;

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipSerializableViewImpl.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Name of the archive; to be serialized
    */
   private final String name;

   /**
    * Underlying archive.  Won't be directly serialized; 
    * instead we'll encode it as ZIP and send that
    */
   private transient Archive<?> archive;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new instance, wrapping the specified {@link Archive}
    */
   public ZipSerializableOriginalImpl(final Archive<?> archive)
   {
      Validate.notNull(archive, "Archive must be specified");
      final String name = archive.getName();
      Validate.notNullOrEmpty(name, "Name of archive must be specified");
      this.archive = archive;
      this.name = name;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.AssignableBase#getArchive()
    */
   @Override
   protected Archive<?> getArchive()
   {
      assert archive != null : "Underlying archive may never be null; illegal state";
      return archive;
   }

   //-------------------------------------------------------------------------------------||
   // Serialization ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Serializes the invocation with a custom form
    * 
    * @serialData After all non-transient fields are written, we
    * send the {@link Archive} contents encoded as ZIP.
    */
   private void writeObject(final ObjectOutputStream out) throws IOException
   {
      // Default write of non-transient fields
      out.defaultWriteObject();

      // Write as ZIP 
      final InputStream in = archive.as(ZipExporter.class).exportAsInputStream();
      try
      {
         IOUtil.copy(in, out); // Don't close the outstream
      }
      finally
      {
         // In case we get an InputStream type that supports closing
         in.close();
      }

      // Log
      if (log.isLoggable(Level.FINER))
      {
         log.finer("Wrote archive: " + archive.toString());
      }
   }

   /**
    * Deserializes according to the custom form 
    * defined by {@link ZipSerializableViewImpl#writeObject(ObjectOutputStream)}
    */
   private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      // Get default form
      in.defaultReadObject();

      // Create new Archive
      final String name = this.name;
      final ZipImporter archive = ShrinkWrap.create(ZipImporter.class, name);

      // Read in archive
      final ZipInputStream remains = new ZipInputStream(new DoNotDelegateCloseInputStream(in));

      try
      {
         // Read in
         archive.importZip(remains);

         // Set
         this.archive = archive.as(JavaArchive.class);
      }
      finally
      {
         // Close up our stream, but not the underlying ObjectInputStream
         remains.close();
      }

      // Log
      if (log.isLoggable(Level.FINER))
      {
         log.finer("Read in archive: " + archive.toString());
      }

      /*
       * Leave this bit here.
       * 
       * After reading in the ZIP stream contents, we need to also 
       * get to the EOF marker (which is not read in by the ZIP import process
       * because it's the ZIP header, not part of the true contents.  Putting
       * this loop here ensures we reach the marker, which is *not* the true 
       * end of the stream.  Object data may be read again after here
       * via something like:
       * 
       * in.readObject();
       * 
       * Without this loop we'll get an OptionalDataException when trying to
       * read more objects in from the stream.  In the future we may add state
       * which needs to be part of the serialization protocol, and things 
       * need to stay in order, so they'll be added *after* the archive ZIP contents.
       * Thus we must be able to read them.
       */
      while (in.read() != -1);

   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Classes ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * A {@link InputStream} which does not delegate the {@link InputStream#close()}
    * operation to the wrapped delegate; we cannot close the {@link ObjectInputStream}
    * passed into {@link ZipSerializableViewImpl#readObject(ObjectInputStream)}.
    * 
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    * @version $Revision: $
    */
   private static class DoNotDelegateCloseInputStream extends FilterInputStream
   {

      protected DoNotDelegateCloseInputStream(final InputStream in)
      {
         super(in);
      }

      /**
       * {@inheritDoc}
       * @see java.io.FilterInputStream#close()
       */
      @Override
      public void close() throws IOException
      {
         // NOOP
      }

   }

}

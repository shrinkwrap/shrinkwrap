/*
 * JBoss, Home of Professional Open Source  
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.tar.impl.exporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.jboss.javatar.TarEntry;
import org.jboss.javatar.TarInputStream;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.exporter.StreamExporter;
import org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.jboss.shrinkwrap.impl.base.path.PathUtil;
import org.jboss.shrinkwrap.tar.api.exporter.TarGzExporter;
import org.junit.Assert;

/**
 * TestCase to ensure that the {@link TarGzExporter} correctly exports
 * archives to TAR.GZ format.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public final class TarGzExporterTestCase extends StreamExporterTestBase
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Extension for archives
    */
   private static final String EXTENSION = ".tar.gz";

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.ExportTestBase#getStreamExporter()
    */
   @Override
   protected Class<? extends StreamExporter> getExporterClass()
   {
      return TarGzExporter.class;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.ExportTestBase#getArchiveExtension()
    */
   @Override
   protected String getArchiveExtension()
   {
      return EXTENSION;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#ensureInExpectedForm(java.io.File)
    */
   @Override
   protected void ensureInExpectedForm(final File file) throws IOException
   {
      // Validate entries were written out
      assertAssetInTarGz(file, PATH_ONE, ASSET_ONE);
      assertAssetInTarGz(file, PATH_TWO, ASSET_TWO);

      // Validate all paths were written
      // SHRINKWRAP-94
      getEntryFromTarGz(file, NESTED_PATH);

      // Ensure we don't write the root Path
      // SHRINKWRAP-96
      InputStream rootEntry = this.getEntryFromTarGz(file, ArchivePaths.root());
      Assert.assertNull("TAR.GZ should not have explicit root path written (SHRINKWRAP-96)", rootEntry);
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.impl.base.exporter.StreamExporterTestBase#getContentsFromExportedFile(java.io.File, org.jboss.shrinkwrap.api.ArchivePath)
    */
   @Override
   protected InputStream getContentsFromExportedFile(final File file, final ArchivePath path) throws IOException
   {
      // Precondition checks
      assert file != null : "file must be specified";
      assert path != null : "path must be specified";

      // Get as TAR.GZ
      return this.getEntryFromTarGz(file, path);
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   // Inherited

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Assert an asset is actually in the TAR.GZ file
    * @throws IOException 
    * @throws IllegalArgumentException 
    */
   private void assertAssetInTarGz(final File archive, final ArchivePath path, final Asset asset)
         throws IllegalArgumentException, IOException
   {
      final InputStream in = this.getEntryFromTarGz(archive, path);
      byte[] expectedContents = IOUtil.asByteArray(asset.openStream());
      byte[] actualContents = IOUtil.asByteArray(in);
      Assert.assertArrayEquals(expectedContents, actualContents);
   }

   /**
    * Obtains an {@link InputStream} to an entry of specified name from the specified TAR.GZ file, 
    * or null if not found.  We have to iterate through all entries for a matching name, as the 
    * instream does not support random access.
    * @param expectedZip
    * @param path
    * @return
    * @throws IllegalArgumentException
    * @throws IOException
    */
   private InputStream getEntryFromTarGz(final File archive, final ArchivePath path) throws IllegalArgumentException,
         IOException
   {
      String entryPath = PathUtil.optionallyRemovePrecedingSlash(path.get());
      final TarInputStream in = new TarInputStream(new GZIPInputStream(new FileInputStream(archive)));
      TarEntry currentEntry = null;
      while ((currentEntry = in.getNextEntry()) != null)
      {
         final String entryName = currentEntry.getName();
         if (currentEntry.isDirectory())
         {
            entryPath = PathUtil.optionallyAppendSlash(entryPath);
         }
         else
         {
            entryPath = PathUtil.optionallyRemoveFollowingSlash(entryPath);
         }
         if (entryName.equals(entryPath))
         {
            return in;
         }
      }
      // Not found
      return null;
   }
}

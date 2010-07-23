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
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.logging.Logger;

import junit.framework.TestCase;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.Test;

/**
 * Stress test to ensure that archives exported as ZIPs which 
 * have size larger than available RAM can be processed, proving 
 * that we're buffering the encoding process. 
 * 
 * SHRINKWRAP-116
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ZipExporterStressTest
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ZipExporterStressTest.class.getName());

   /**
    * 2^20
    */
   private static BigDecimal MEGA = new BigDecimal(1024 * 1024);

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that we can export archives of large sizes without
    * leading to {@link OutOfMemoryError}
    * 
    * SHRINKWRAP-116
    */
   @Test
   public void exportHugeArchive() throws IOException
   {
      // Log
      log.info("exportHugeArchive");
      log.info("This test may take awhile as it's intended to fill memory");

      // Get an archive instance
      final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "hugeArchive.jar");

      // Approximate the free memory to start
      final Runtime runtime = Runtime.getRuntime();
      final long startFreeMemBytes = totalFreeMemory(runtime);
      long beforeExportFreeMemBytes = startFreeMemBytes;
      int counter = 0;
      // Loop through and add a MB Asset
      final String pathPrefix = "path";

      // Fill up the archive until we've got only 30% of memory left
      while (beforeExportFreeMemBytes > (startFreeMemBytes * .3))
      {
         archive.add(MegaByteAsset.newInstance(), pathPrefix + counter++);
         System.gc(); // Signal to the VM to try to clean up a bit, not the most reliable, but makes this OK on my machine
         beforeExportFreeMemBytes = totalFreeMemory(runtime);
         log.info("Current Free Memory (MB): " + this.megaBytesFromBytes(beforeExportFreeMemBytes));
      }
      log.info("Wrote: " + archive.toString());
      log.info("Started w/ free memory (MB): " + this.megaBytesFromBytes(startFreeMemBytes));
      log.info("Free memory before export (MB): " + this.megaBytesFromBytes(beforeExportFreeMemBytes));

      // Export; at this point we have less than 50% available memory so 
      // we can't carry the whole archive in RAM twice; this
      // should ensure the ZIP impl uses an internal buffer
      final InputStream in = archive.as(ZipExporter.class).exportAsInputStream();
      final CountingOutputStream out = new CountingOutputStream();

      // Copy, counting the final size of the exported ZIP
      IOUtil.copyWithClose(in, out);

      // Ensure we've just exported a ZIP larger than our available memory (proving we've buffered the encoding process)
      TestCase.assertTrue("Test setup failed; we should be writing out more bytes than we have free memory",
            out.bytesWritten > beforeExportFreeMemBytes);
      log.info("Final ZIP export was: " + this.megaBytesFromBytes(out.bytesWritten) + " MB");
      final long afterExportFreeMemBytes = totalFreeMemory(runtime);
      log.info("Free memory after export (MB): " + this.megaBytesFromBytes(afterExportFreeMemBytes));
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Returns the number of MB the specified number of bytes represents
    * @param bytes
    * @return
    */
   private BigDecimal megaBytesFromBytes(final long bytes)
   {
      return new BigDecimal(bytes).divide(MEGA).setScale(2, RoundingMode.HALF_UP);
   }

   /**
    * Obtains an estimate of the total amount of free memory available to the JVM
    * @param runtime
    * @return
    */
   private static long totalFreeMemory(final Runtime runtime)
   {
      return runtime.maxMemory() - runtime.totalMemory() + runtime.freeMemory();
   }

   /**
    * An {@link Asset} which contains a megabyte of dummy data
    *
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    */
   private static class MegaByteAsset extends ByteArrayAsset implements Asset
   {
      /**
       * Dummy megabyte
       */
      private static int MEGA = 1024 * 1024;

      private static final Random random = new Random();

      private MegaByteAsset(final byte[] content)
      {
         super(content);
      }

      static MegaByteAsset newInstance()
      {
         /**
          * Bytes must be random/distributed so that compressing these in ZIP
          * isn't too efficient
          */
         final byte[] content = new byte[MEGA];
         random.nextBytes(content);
         return new MegaByteAsset(content);
      }
   }

   /**
    * {@link OutputStream} which does nothing but count the bytes written
    * 
    * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
    * @version $Revision: $
    */
   private static class CountingOutputStream extends OutputStream
   {
      long bytesWritten = 0;

      @Override
      public void write(int b) throws IOException
      {
         bytesWritten++;
      }
   }

}

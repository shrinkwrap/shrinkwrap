/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.shrinkwrap.impl.base.exporter.zip;

import org.jboss.shrinkwrap.api.ArchiveFactory;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertArrayEquals;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ShrinkWrap269TestCase
{
    @Test
    public void testExecutor() throws InterruptedException, IOException {
        ConfigurationBuilder builder = new ConfigurationBuilder()
                .executorService(Executors.newSingleThreadScheduledExecutor());
        ArchiveFactory factory = ShrinkWrap.createDomain(builder).getArchiveFactory();
        // blow the pipe
        InputStream in = factory.create(JavaArchive.class, "test.jar")
            .add(MegaByteAsset.newInstance(), "dummy")
            .as(ZipExporter.class)
            .exportAsInputStream();
        // I want the pipe full
        Thread.sleep(1000);
        // oopsy, I lost the InputStream :-)
        in = null;
        // go away, go away
        for(int i = 0; i < 3; i++)
        {
            System.gc();
            Runtime.getRuntime().runFinalization();
        }
        // oh well, next!
        InputStream in2 = factory.create(JavaArchive.class, "test2.jar")
            .add(MegaByteAsset.newInstance(), "dummy")
            .as(ZipExporter.class)
            .exportAsInputStream();
        in2.read();
    }

    @Test
    public void testNoClose() throws IOException, InterruptedException {
        final Thread tarray[] = new Thread[100];
        final int numThreads = Thread.enumerate(tarray);
        // blow the pipe
        InputStream in = ShrinkWrap.create(JavaArchive.class, "test.jar")
            .add(MegaByteAsset.newInstance(), "dummy")
            .as(ZipExporter.class)
            .exportAsInputStream();
        // I want the pipe full
        Thread.sleep(1000);
        // oopsy, I lost the InputStream :-)
        in = null;
        // go away, go away
        for(int i = 0; i < 3; i++)
        {
            System.gc();
            Runtime.getRuntime().runFinalization();
        }
        final Thread tarray2[] = new Thread[100];
        final int numThreads2 = Thread.enumerate(tarray2);
//        assertEquals(numThreads, numThreads2);
        assertArrayEquals(tarray, tarray2);
    }

    /**
     * An {@link org.jboss.shrinkwrap.api.asset.Asset} which contains a megabyte of dummy data
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
}

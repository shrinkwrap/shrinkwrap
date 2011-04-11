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
package org.jboss.shrinkwrap.openejb.config;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.UUID;

import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.config.ConfigurationFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;

/**
 * {@link ConfigurationFactory} extension which is capable of creating
 * {@link AppInfo} metadata from a ShrinkWrap {@link Archive}.
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ShrinkWrapConfigurationFactory extends ConfigurationFactory
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * System property denoting the location of the temp dir
    */
   private static final String SYS_PROP_TMP_DIR = "java.io.tmpdir";

   //-------------------------------------------------------------------------------------||
   // Functional Methods -----------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link AppInfo} from the specified {@link Archive}
    * 
    * @param archive
    * @return
    * @throws OpenEJBException
    * @throws IllegalArgumentException If the archive was not specified
    */
   public AppInfo configureApplication(final Archive<?> archive) throws OpenEJBException, IllegalArgumentException
   {
      // Precondition checks
      if (archive == null)
      {
         throw new IllegalArgumentException("archive must be specified");
      }

      /*
       * Make a temp file based on the ZIP output of the specified archive
       */

      // Get location for the new temp dir, and validate
      final String tempDirLocation = AccessController.doPrivileged(GetTempDirAction.INSTANCE);
      final File tmpDir = new File(tempDirLocation);
      if (!tmpDir.exists())
      {
         throw new IllegalStateException("Could not obtain valid temp directory: " + tmpDir.getAbsolutePath());
      }
      if (!tmpDir.isDirectory())
      {
         throw new IllegalStateException("Temp location must be a directory: " + tmpDir.getAbsolutePath());
      }

      // Put each archive in a uniquely-namespaced directory
      final UUID uuid = UUID.randomUUID();
      final File namespace = new File(tmpDir, uuid.toString());
      if (!namespace.mkdir())
      {
         throw new IllegalStateException("Could not create a unique namespace into which we'll deploy "
               + archive.getName() + ": " + namespace.getAbsolutePath());
      }

      // Make the new temp file, set to delete on VM exit
      final String name = archive.getName();
      final File tmpFile = new File(namespace, name);
      tmpFile.deleteOnExit();

      // Write the ZIP to the temp file
      archive.as(ZipExporter.class).exportTo(tmpFile);

      // Delegate to the File-based configuration impl
      return configureApplication(tmpFile);
   }

   //-------------------------------------------------------------------------------------||
   // Inner Classes ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Privileged action to obtain the temp directory
    */
   private static enum GetTempDirAction implements PrivilegedAction<String> {
      INSTANCE;
      @Override
      public String run()
      {
         return System.getProperty(SYS_PROP_TMP_DIR);
      }
   }
}

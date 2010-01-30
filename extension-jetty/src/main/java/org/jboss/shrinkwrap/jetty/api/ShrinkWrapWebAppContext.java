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
package org.jboss.shrinkwrap.jetty.api;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

/**
 * Jetty's {@link WebAppContext} backed by a ShrinkWrap
 * {@link Archive}; capable of being deployed into 
 * the Embedded Jetty {@link Server}
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ShrinkWrapWebAppContext extends WebAppContext implements Assignable
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ShrinkWrapWebAppContext.class.getName());

   /**
    * System property denoting the name of the temp directory
    */
   private static final String SYSPROP_KEY_TMP_DIR = "java.io.tmpdir";

   /**
    * Temporary directory into which we'll extract the {@link WebArchive}s
    */
   private static final File TMP_DIR;
   static
   {
      TMP_DIR = new File(AccessController.doPrivileged(new PrivilegedAction<String>()
      {

         @Override
         public String run()
         {
            return System.getProperty(SYSPROP_KEY_TMP_DIR);
         }

      }));
      // If the temp location doesn't exist or isn't a directory
      if (!TMP_DIR.exists() || !TMP_DIR.isDirectory())
      {
         throw new IllegalStateException("Could not obtain temp directory \"" + TMP_DIR.getAbsolutePath() + "\"");
      }
   }

   /**
    *  /
    */
   private static final char ROOT = '/';

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Underlying delegate
    */
   private final Archive<?> archive;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link ShrinkWrapWebAppContext} using the 
    * specified underlying archive
    * 
    * @throws IllegalArgumentException If the archive is not specified
    */
   public ShrinkWrapWebAppContext(final Archive<?> archive) throws IllegalArgumentException
   {
      // Invoke super
      super();

      // Precondition checks
      if (archive == null)
      {
         throw new IllegalArgumentException("archive must be specified");
      }

      // Flush to file
      final String name = archive.getName();
      final File exported = new File(TMP_DIR, name);
      archive.as(ZipExporter.class).exportZip(exported);

      // Mark to delete when we come down
      exported.deleteOnExit();

      // Add the context
      final URL url;
      try
      {
         url = exported.toURI().toURL();
      }
      catch (final MalformedURLException e)
      {
         throw new RuntimeException("Could not obtain URL of File " + exported.getAbsolutePath(), e);
      }
      log.info("Webapp location: " + url);

      // Set properties regarding the webbapp
      this.setWar(url.toExternalForm());
      this.setContextPath(ROOT + name);

      // Remember the archive from which we're created
      this.archive = archive;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * {@inheritDoc}
    * @see org.jboss.shrinkwrap.api.Assignable#as(java.lang.Class)
    */
   @Override
   public <TYPE extends Assignable> TYPE as(final Class<TYPE> clazz)
   {
      return archive.as(clazz);
   }
}

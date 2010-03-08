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
package org.jboss.shrinkwrap.openejb.test;

import java.util.Properties;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.SecurityServiceInfo;
import org.apache.openejb.assembler.classic.TransactionServiceInfo;
import org.apache.openejb.client.LocalInitialContextFactory;
import org.apache.openejb.config.ConfigurationFactory;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.openejb.config.ShrinkWrapConfigurationFactory;
import org.jboss.shrinkwrap.openejb.ejb.EchoBean;
import org.jboss.shrinkwrap.openejb.ejb.EchoLocalBusiness;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Ensures that {@link Archive} types may be deployed into 
 * the container via an {@link AppInfo} descriptor created
 * by the {@link ConfigurationFactory}
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ShrinkWrapArchiveDeploymentTest
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ShrinkWrapArchiveDeploymentTest.class.getName());

   /**
    * OpenEJB server view
    */
   private static Assembler server;

   /**
    * OpenEJB Configuration
    */
   private static ShrinkWrapConfigurationFactory config;

   /**
    * JNDI Context
    */
   private static Context namingContext;

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * OpenEJB Deployment
    */
   private AppInfo deployment;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Starts the OpenEJB Server
    */
   @BeforeClass
   public static void startServer() throws Exception
   {
      // These two objects pretty much encompass all the EJB Container
      final ShrinkWrapConfigurationFactory configuration = new ShrinkWrapConfigurationFactory();
      config = configuration;
      final Assembler assembler = new Assembler();
      assembler.createTransactionManager(configuration.configureService(TransactionServiceInfo.class));
      assembler.createSecurityService(configuration.configureService(SecurityServiceInfo.class));
      server = assembler;

      // Get a JNDI Context
      final Properties properties = new Properties();
      properties.put(Context.INITIAL_CONTEXT_FACTORY, LocalInitialContextFactory.class.getName());
      final Context ctx = new InitialContext(properties);
      namingContext = ctx;
   }

   /**
    * Deploys the test archive
    * @throws Exception
    */
   @Before
   public void deploy() throws Exception
   {

      // Create archive to hold our test EJB
      final String name = "echo.jar";
      final JavaArchive archive = Archives.create(name, JavaArchive.class).addClasses(EchoBean.class,
            EchoLocalBusiness.class);
      log.info("Created archive: " + archive.toString(true));

      // Deploy as an archive
      final AppInfo appInfo = config.configureApplication(archive);
      server.createApplication(appInfo);
      deployment = appInfo;
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that an archive can be deployed, accessed, and undeployed
    * @throws Exception
    */
   @Test
   public void shrinkWrapDeployment() throws Exception
   {

      // Lookup
      log.info("here");
      final EchoLocalBusiness bean = (EchoLocalBusiness) namingContext.lookup(EchoBean.class.getSimpleName() + "Local");

      // Invoke and test
      final String request = "Word up";
      final String response = bean.echo(request);
      log.info("Sent: \"" + request + "\"; got: \"" + response + "\"");
      TestCase.assertEquals("Response from EJB invocation not expected", request, response);
      TestCase.assertTrue("Response from local EJB invocation is equal by value but not by reference",
            request == response);

      // Undeploy the archive
      server.destroyApplication(deployment.jarPath);

      // Try and execute the bean after it's been undeployed -- should fail
      try
      {
         bean.echo(request);
         Assert.fail("Proxy should no longer be valid");
      }
      catch (final Exception e)
      {
         // this should happen
      }

      try
      {
         namingContext.lookup(EchoBean.class.getSimpleName());
         Assert.fail("JNDI References should have been cleaned up");
      }
      catch (NamingException e)
      {
         // this also should happen
      }
   }
}

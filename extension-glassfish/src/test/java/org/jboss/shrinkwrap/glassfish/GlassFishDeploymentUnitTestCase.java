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
package org.jboss.shrinkwrap.glassfish;

import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.embedded.ContainerBuilder;
import org.glassfish.api.embedded.EmbeddedContainer;
import org.glassfish.api.embedded.EmbeddedDeployer;
import org.glassfish.api.embedded.EmbeddedFileSystem;
import org.glassfish.api.embedded.LifecycleException;
import org.glassfish.api.embedded.Server;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.glassfish.api.ShrinkwrapReadableArchive;
import org.jboss.shrinkwrap.glassfish.ejb.EchoBean;
import org.jboss.shrinkwrap.glassfish.ejb.EchoLocalBusiness;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Integration test ensuring that Deployment of a
 * ShrinkWrap {@link Archive} into GlassFish 
 * works properly
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class GlassFishDeploymentUnitTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(GlassFishDeploymentUnitTestCase.class.getName());

   /**
    * Embedded GFv3 Server
    */
   private static Server server;

   /**
    * Deployer to accept {@link Archive}s
    */
   private static EmbeddedDeployer deployer;

   /**
    * JNDI Context
    */
   private static Context namingContext;

   /**
    * Name of the deployment
    */
   private static final String NAME_DEPLOYMENT = "slsb.jar";

   /**
    * Name in JNDI under which the test EJB will be registered 
    */
   private static final String NAME_JNDI = "java:global/" + NAME_DEPLOYMENT + "/" + EchoBean.class.getSimpleName()
         + "!" + EchoLocalBusiness.class.getName();

   /**
    * EJB Archive to be deployed
    */
   private static final ShrinkwrapReadableArchive archive;
   static
   {
      archive = Archives.create(NAME_DEPLOYMENT, JavaArchive.class).addClasses(EchoLocalBusiness.class, EchoBean.class)
            .as(ShrinkwrapReadableArchive.class);
   }

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Configures and starts the Embedded GFv3 server
    */
   @BeforeClass
   public static void startServer() throws LifecycleException, NamingException
   {
      // Create a builder for the Server
      final Server.Builder builder = new Server.Builder(null);

      // Build the server from an Embedded FS
      final EmbeddedFileSystem.Builder embeddedFsBuilder = new EmbeddedFileSystem.Builder();
      final EmbeddedFileSystem embeddedFs = embeddedFsBuilder.build();
      builder.embeddedFileSystem(embeddedFs);
      server = builder.build();

      // Add an EJB Container
      final ContainerBuilder<EmbeddedContainer> containerBuilder = server.createConfig(ContainerBuilder.Type.ejb);
      server.addContainer(containerBuilder);

      // Set the deployer
      deployer = server.getDeployer();

      // Set the naming context
      namingContext = new InitialContext();

   }

   @AfterClass
   public static void stopServer() throws LifecycleException
   {
      server.stop();
   }

   /**
    * Deploys the EJB into the server
    * @throws Exception
    */
   @Before
   public void deploy()
   {

      final DeployCommandParameters params = new DeployCommandParameters();
      params.name = NAME_DEPLOYMENT;
      deployer.deploy(archive, params);
   }

   /**
    * Undeploys the EJB from the server
    * @throws Exception
    */
   @After
   public void undeploy()
   {
      deployer.undeploy(NAME_DEPLOYMENT, null);
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures the EJB can be looked up in JNDI and invoked upon, proving
    * the deployment was a success
    */
   @Test
   public void testSlsb() throws NamingException
   {
      // Get the proxy
      final EchoLocalBusiness bean = (EchoLocalBusiness) namingContext.lookup(NAME_JNDI);

      // Define the expected return value
      final String expected = "ShrinkWrap>GlassFish (booyeah)";

      // Invoke
      final String received = bean.echo(expected);

      // Test
      log.info("Got: " + received);
      TestCase.assertEquals("Result was not as expected", expected, received);
   }
}

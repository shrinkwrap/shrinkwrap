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
import org.glassfish.api.embedded.EmbeddedDeployer;
import org.glassfish.api.embedded.EmbeddedFileSystem;
import org.glassfish.api.embedded.LifecycleException;
import org.glassfish.api.embedded.Server;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Archives;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
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
    * Name of the deployments
    */
   private static final String NAME_DEPLOYMENT_EAR = "slsb.ear";

   private static final String NAME_DEPLOYMENT_JAR = "slsb.jar";

   /**
    * Names in JNDI under which the test EJB will be registered for the JAR/EAR
    */
   private static final String NAME_JNDI_JAR = "java:global/" + NAME_DEPLOYMENT_JAR + "/"
         + EchoBean.class.getSimpleName() + "!" + EchoLocalBusiness.class.getName();

   private static final String NAME_JNDI_EAR = "java:global/" + NAME_DEPLOYMENT_EAR + "/"
         + NAME_DEPLOYMENT_JAR.substring(0, NAME_DEPLOYMENT_JAR.indexOf('.')) + "/" + EchoBean.class.getSimpleName()
         + "!" + EchoLocalBusiness.class.getName();

   /**
    * EJB Archives to be deployed
    */
   private static final ShrinkwrapReadableArchive enterpriseArchive;

   private static final ShrinkwrapReadableArchive javaArchive;
   static
   {

      // Create the packaging
      javaArchive = Archives.create(NAME_DEPLOYMENT_JAR, JavaArchive.class).addClasses(EchoLocalBusiness.class,
            EchoBean.class).as(ShrinkwrapReadableArchive.class);
      enterpriseArchive = Archives.create(NAME_DEPLOYMENT_EAR, EnterpriseArchive.class).addModule(
            javaArchive.as(JavaArchive.class)).as(ShrinkwrapReadableArchive.class);

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
      server.addContainer(ContainerBuilder.Type.all);

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

      final DeployCommandParameters paramsEar = new DeployCommandParameters();
      paramsEar.name = NAME_DEPLOYMENT_EAR;
      final DeployCommandParameters paramsJar = new DeployCommandParameters();
      paramsJar.name = NAME_DEPLOYMENT_JAR;
      deployer.deploy(enterpriseArchive, paramsEar);
      deployer.deploy(javaArchive, paramsJar);
   }

   /**
    * Undeploys the EJB from the server
    * @throws Exception
    */
   @After
   public void undeploy()
   {
      deployer.undeploy(NAME_DEPLOYMENT_EAR, null);
      deployer.undeploy(NAME_DEPLOYMENT_JAR, null);
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures the EJB from a JAR deployment can be looked up in JNDI and 
    * invoked upon, proving the deployment was a success
    */
   @Test
   public void testSlsbFromJarDeployment() throws NamingException
   {
      this.testSlsb(NAME_JNDI_JAR);
   }

   /**
    * Ensures the EJB from a EAR deployment can be looked up in JNDI and 
    * invoked upon, proving the deployment was a success
    * 
    * SHRINKWRAP-126
    */
   @Test
   public void testSlsbFromEarDeployment() throws NamingException
   {
      this.testSlsb(NAME_JNDI_EAR);
   }

   //-------------------------------------------------------------------------------------||
   // Internal Helper Methods ------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures the EJB from a JAR deployment can be looked up in JNDI and 
    * invoked upon, proving the deployment was a success
    */
   private void testSlsb(final String jndiName) throws NamingException
   {
      assert jndiName != null : "JNDI Name must be specified";

      // Get the proxy
      final EchoLocalBusiness bean = (EchoLocalBusiness) namingContext.lookup(jndiName);

      // Define the expected return value
      final String expected = "ShrinkWrap>GlassFish (booyeah)";

      // Invoke
      final String received = bean.echo(expected);

      // Test
      log.info("Got: " + received);
      TestCase.assertEquals("Result was not as expected", expected, received);
   }
}

package org.jboss.shrinkwrap.vdf;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.jboss.bootstrap.api.descriptor.BootstrapDescriptor;
import org.jboss.bootstrap.api.lifecycle.LifecycleState;
import org.jboss.bootstrap.api.mc.server.MCServer;
import org.jboss.bootstrap.api.mc.server.MCServerFactory;
import org.jboss.deployers.client.spi.Deployment;
import org.jboss.deployers.client.spi.main.MainDeployer;
import org.jboss.deployers.vfs.spi.client.VFSDeploymentFactory;
import org.jboss.kernel.spi.dependency.KernelController;
import org.jboss.logging.Logger;
import org.jboss.reloaded.api.ReloadedDescriptors;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.vdf.api.ShrinkWrapDeployer;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Ensures that the {@link ShrinkWrapDeployer} supports deployment
 * and undeployment of ShrinkWrap {@link Archive}s.
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ShrinkWrapDeployerTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ShrinkWrapDeployerTestCase.class);

   /**
    * MC bean name of the {@link ShrinkWrapDeployer}
    */
   private static final String NAME_MC_SHRINKWRAP_DEPLOYER = "ShrinkWrapDeployer";

   /**
    * Name of a ShrinkWrap {@link Archive} we'll deploy
    */
   private static final String NAME_ARCHIVE = "testDeployment.jar";

   /**
    * Name of the system property signaling JBossXB to ignore order
    */
   private static final String NAME_SYSPROP_JBOSSXB_IGNORE_ORDER = "xb.builder.useUnorderedSequence";

   /**
    * Value to set for JBossXB ordering
    */
   private static final String VALUE_SYSPROP_JBOSSXB_IGNORE_ORDER = "true";

   /**
    * Name of the Deployment XML to install the ShrinkWrapDeployer
    */
   private static final String FILENAME_SHRINKWRAP_DEPLOYER_XML = "../classes/shrinkwrap-deployer-jboss-beans.xml";

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * The Server
    */
   private MCServer server;

   /**
    * The Deployment where we install ShrinkWrapDeployer
    */
   private Deployment shrinkWrapDeployerDeployment;

   /**
    * MainDeployer used to install the ShrinkWrapDeployer
    */
   private MainDeployer mainDeployer;

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Setup JBossXB
    * TODO @see comments below so that this step is not necessary
    */
   @BeforeClass
   public static void setupJBossXb()
   {
      AccessController.doPrivileged(new PrivilegedAction<Void>()
      {
         public Void run()
         {
            // Must use unordered sequence else JBossXB will explode
            //TODO Define a proper vfs.xml which is properly ordered
            System.setProperty(NAME_SYSPROP_JBOSSXB_IGNORE_ORDER, VALUE_SYSPROP_JBOSSXB_IGNORE_ORDER);
            return null;
         }
      });
   }

   /**
    * Starts the server before each test
    * @throws Exception
    */
   @Before
   public void startServerAndInstallCachingDeployer() throws Throwable
   {
      // Create a server
      final MCServer mcServer = MCServerFactory.createServer();
      this.server = mcServer;

      // Configure it
      final List<BootstrapDescriptor> descriptors = server.getConfiguration().getBootstrapDescriptors();
      descriptors.add(ReloadedDescriptors.getClassLoadingDescriptor());
      descriptors.add(ReloadedDescriptors.getVdfDescriptor());

      // Start
      long before = System.currentTimeMillis();
      server.start();
      long after = System.currentTimeMillis();
      long total = after - before;
      log.info("Boot took: " + total + "ms");

      // Install the ShrinkWrapDeployer
      final URL base = this.getClass().getProtectionDomain().getCodeSource().getLocation();
      final URL shrinkWrapDeployerXml = new URL(base, FILENAME_SHRINKWRAP_DEPLOYER_XML);
      log.info(shrinkWrapDeployerXml);
      final VirtualFile shrinkWrapDeployerFile = VFS.getChild(shrinkWrapDeployerXml);
      final Deployment deployment = VFSDeploymentFactory.getInstance().createVFSDeployment(shrinkWrapDeployerFile);
      this.shrinkWrapDeployerDeployment = deployment;
      final MainDeployer mainDeployer = (MainDeployer) server.getKernel().getController().getContextByClass(
            MainDeployer.class).getTarget();
      mainDeployer.addDeployment(deployment);
      mainDeployer.process();
      mainDeployer.checkComplete();
      this.mainDeployer = mainDeployer;
   }

   /**
    * Stops the server after each test
    * @throws Exception
    */
   @After
   public void stopServer() throws Exception
   {
      if (server != null && server.getState().equals(LifecycleState.STARTED))
      {
         // Remove the SW deployer
         mainDeployer.removeDeployment(shrinkWrapDeployerDeployment);
         mainDeployer.process();
         mainDeployer.checkComplete();

         // Stop the server
         server.stop();
      }
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Ensures that a ShrinkWrap {@link Archive} may be directly deployed into
    * the {@link ShrinkWrapDeployer}
    */
   @Test
   public void testShrinkWrapDeployment() throws Throwable
   {
      // Get the KernelController
      final KernelController controller = server.getKernel().getController();

      // Get the ShrinkWrapDeployer (should have been installed via the lifecycle)
      final ShrinkWrapDeployer shrinkwrapDeployer = (ShrinkWrapDeployer) controller.getInstalledContext(
            NAME_MC_SHRINKWRAP_DEPLOYER).getTarget();
      TestCase.assertNotNull(ShrinkWrapDeployer.class.getName() + " instance was not installed into MC",
            shrinkwrapDeployer);

      // Ensure preconditions (state STOPPED)
      LifecyclePojo.State currentState = LifecyclePojo.state;
      log.info("Current State of Lifecycle POJO: " + currentState);
      Assert.assertEquals("Lifecycle POJO state should be stopped before deployment", LifecyclePojo.State.STOPPED,
            currentState);

      // Construct a test JAR to install the Lifecycle POJO
      final Asset deploymentXmlAsset = new Asset()
      {

         @Override
         public InputStream openStream()
         {
            return new ByteArrayInputStream(new String(
                  "<deployment xmlns=\"urn:jboss:bean-deployer:2.0\"><bean name=\"LifecyclePojo\" class=\""
                        + LifecyclePojo.class.getName() + "\" /></deployment>").getBytes());
         }
      };
      final JavaArchive testJar = ShrinkWrap.create(NAME_ARCHIVE, JavaArchive.class).addClass(LifecyclePojo.class).add(
            deploymentXmlAsset, ArchivePaths.create("pojo-jboss-beans.xml"));

      // Deploy the test JAR
      shrinkwrapDeployer.deploy(testJar);

      // Ensure deployed
      currentState = LifecyclePojo.state;
      log.info("Current State of Lifecycle POJO: " + currentState);
      Assert.assertEquals("Lifecycle POJO state should be started after deployment", LifecyclePojo.State.STARTED,
            currentState);

      // Undeploy
      shrinkwrapDeployer.undeploy(testJar);

      // Ensure undeployed
      currentState = LifecyclePojo.state;
      log.info("Current State of Lifecycle POJO: " + currentState);
      Assert.assertEquals("Lifecycle POJO state should be stopped after undeployment", LifecyclePojo.State.STOPPED,
            LifecyclePojo.state);
   }
}

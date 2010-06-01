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

package org.jboss.shrinkwrap.jetty.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.jetty.api.ShrinkWrapWebAppContext;
import org.jboss.shrinkwrap.jetty.servlet.JspForwardingServlet;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;

/**
 * Ensures that deployment on {@link ShrinkWrapWebAppContext} into
 * the Jetty {@link Server} works as expected
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class JettyDeploymentIntegrationUnitTestCase
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(JettyDeploymentIntegrationUnitTestCase.class.getName());

   /**
    * Port to which the HTTP server should bind (above 1024 for *nix non-root environments)
    */
   private static final int HTTP_BIND_PORT = 12345;

   /**
    * Path, relative to the resources base, of the directory containing web.xml descriptor for tests
    */
   private static final String PATH_RESOURCE_WEB_XML = "webxml/";

   /**
    * Path, relative to the resources base, of a test web.xml
    */
   private static final String PATH_ACTUAL_WEB_XML = PATH_RESOURCE_WEB_XML + "servletForwardingToJsp.xml";

   /**
    * Path, relative to the resources base, of a test JSP
    */
   private static final String PATH_JSP = "jsp/requestParamEcho.jsp";

   /**
    * URI Separator
    */
   private static final char SEPARATOR = '/';

   /**
    * Jetty server
    */
   private static Server server;

   /**
    * Servlet Class under test
    */
   private static final Class<?> servletClass = JspForwardingServlet.class;

   /**
    * Name of the web application
    */
   private static final String NAME_WEBAPP = "testServletJsp";

   /**
    * Name to assign to the WAR
    */
   private static final String NAME_WAR = NAME_WEBAPP + ".war";

   //-------------------------------------------------------------------------------------||
   // Lifecycle --------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Configures and starts the Embedded Jetty Server
    */
   @BeforeClass
   public static void createServerAndDeployWebapp() throws Exception
   {
      // Create the new server
      server = new Server(HTTP_BIND_PORT);

      final WebArchive archive = ShrinkWrap.create(WebArchive.class, NAME_WAR);
      final ArchivePath targetPathWebXml = ArchivePaths.create("web.xml");
      archive.addWebResource(PATH_ACTUAL_WEB_XML, targetPathWebXml).addResource(PATH_JSP).addClass(servletClass);
      log.info(archive.toString(true));

      // Deploy
      final Context context = archive.as(ShrinkWrapWebAppContext.class);
      server.addHandler(context);

      // Start the service
      server.start();
   }

   /**
    * Stops the Jetty Server
    * @throws Exception
    */
   @AfterClass
   public static void stopServer() throws Exception
   {
      server.stop();
   }

   //-------------------------------------------------------------------------------------||
   // Tests ------------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Doesn't really test anything now; shows end-user view only
    */
   @Test
   public void requestWebapp() throws Exception
   {

      // Get an HTTP Client
      final HttpClient client = new DefaultHttpClient();

      // Make an HTTP Request, adding in a custom parameter which should be echoed back to us
      final String echoValue = "ShrinkWrap>Jetty Integration";
      final List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("jsp", PATH_JSP));
      params.add(new BasicNameValuePair("echo", echoValue));
      final URI uri = URIUtils.createURI("http", "localhost", HTTP_BIND_PORT, NAME_WAR + SEPARATOR
            + servletClass.getSimpleName(), URLEncodedUtils.format(params, "UTF-8"), null);
      final HttpGet request = new HttpGet(uri);

      // Execute the request
      log.info("Executing request to: " + request.getURI());
      final HttpResponse response = client.execute(request);
      final HttpEntity entity = response.getEntity();
      if (entity == null)
      {
         Assert.fail("Request returned no entity");
      }

      // Read the result, ensure it's what we're expecting (should be the value of request param "echo")
      final BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
      final String line = reader.readLine();
      Assert.assertEquals("Unexpected response from Servlet", echoValue, line);

   }

}

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
package org.jboss.shrinkwrap.jetty_6.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JspForwardingServlet
 * 
 * Servlet which forwards to a JSP as requested
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class JspForwardingServlet extends HttpServlet
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(JspForwardingServlet.class.getName());

   /**
    * serialVersionUID
    */
   private static final long serialVersionUID = 1L;

   /**
    * Name of the request parameter denoting which JSP to forward
    */
   public static final String REQ_PARAM_JSP = "jsp";

   /**
    * Context root
    */
   private static final char ROOT = '/';

   /**
    * Content type to use in forwarding
    */
   private static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";

   //-------------------------------------------------------------------------------------||
   // Overridden Implementations ---------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Forwards the request to a JSP denoted by the request parameter "jsp", 
    * returning a status of 400/Bad Request if not specified 
    * 
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException,
         IOException
   {
      // Log
      log.info("Request: " + request);

      // Get the target JSP page
      final String jsp = request.getParameter(REQ_PARAM_JSP);

      // Handle unspecified
      if (jsp == null)
      {
         // HTTP 400 and return
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         return;
      }

      // Set the content-type to text
      response.setContentType(CONTENT_TYPE_TEXT_PLAIN);

      // Forward
      final String resolvedLocation = ROOT + jsp;
      log.info("Forwarding to: " + resolvedLocation);
      final RequestDispatcher dispatcher = request.getRequestDispatcher(resolvedLocation);
      dispatcher.forward(request, response);
   }

}

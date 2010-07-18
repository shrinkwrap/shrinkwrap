package org.jboss.shrinkwrap.jetty_7.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Dan Allen
 */
public class RequestParamEchoServlet extends HttpServlet
{
   /**
    * serialVersionUID
    */
   private static final long serialVersionUID = 1L;

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
      resp.getWriter().append(req.getParameter("echo"));
   }

}

package org.jboss.shrinkwrap.mobicents.servlet.sip.servlet;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.sip.ConvergedHttpSession;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;

/**
 * Casting the session to ConvergedHttpSession and getting the app name make sure
 * the application is deployed as a Converged Sip Servlets application
 * 
 * @author Dan Allen
 * @author Jean Deruelle
 */
public class RequestParamEchoServlet extends HttpServlet
{ 
   @Resource
   SipFactory sipFactory;
   
   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
   {
	   ConvergedHttpSession convergedHttpSession = ((ConvergedHttpSession)req.getSession());
	   SipApplicationSession sipApplicationSession = convergedHttpSession.getApplicationSession();
      resp.getWriter().append(req.getParameter("echo") + sipApplicationSession.getApplicationName());
   }

}

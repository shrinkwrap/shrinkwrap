/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
  *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.shrinkwrap.vdf;

import org.jboss.beans.metadata.api.annotations.Start;
import org.jboss.beans.metadata.api.annotations.Stop;

/**
 * Simple POJO with lifecycle operations to set state
 * in globally-accessible fashion for testing
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class LifecyclePojo
{

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Notes the state of this POJO
    */
   enum State {
      STARTED, STOPPED;
   }

   /**
    * State flag so the test can see that we've been deployed
    */
   static State state = State.STOPPED;

   //-------------------------------------------------------------------------------------||
   // Lifecycle Methods ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Lifecycle start callback
    * @throws Exception
    */
   @Start
   public void start() throws Exception
   {
      state = State.STARTED;
   }

   /**
    * Lifecycle stop callback 
    * @throws Exception
    */
   @Stop
   public void stop() throws Exception
   {
      state = State.STOPPED;
   }
}

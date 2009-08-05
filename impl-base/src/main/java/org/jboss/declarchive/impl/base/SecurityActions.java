package org.jboss.declarchive.impl.base;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * SecurityActions
 * 
 * A set of privileged actions that are not to leak out
 * of this package 
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
class SecurityActions
{

   //-------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------||
   //-------------------------------------------------------------------------------||

   /**
    * No external instanciation
    */
   private SecurityActions()
   {

   }

   //-------------------------------------------------------------------------------||
   // Utility Methods --------------------------------------------------------------||
   //-------------------------------------------------------------------------------||

   /**
    * Obtains the Thread Context ClassLoader
    */
   static ClassLoader getThreadContextClassLoader()
   {
      return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>()
      {
         public ClassLoader run()
         {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }
}

package org.jboss.declarchive.spi;

import java.io.InputStream;

/**
 * Generic interface for resource loading. 
 * 
 * Used to move the resource loading logic out of the archive backends.  
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public interface Resource
{

   /**
    * Get the default name for this resource, can be overriden by the user.
    * 
    * @return A name for this Resource
    */
   String getDefaultName();
   
   /**
    * Get a open stream for the resource content.
    * The caller is responsible for closing the stream. 
    * 
    * @return A new open inputstream for each call.
    */
   InputStream getStream();
}

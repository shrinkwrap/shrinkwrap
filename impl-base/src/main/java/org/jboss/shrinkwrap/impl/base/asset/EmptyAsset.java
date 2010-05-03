package org.jboss.shrinkwrap.impl.base.asset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jboss.shrinkwrap.api.Asset;

/**
 * Implementation of a {@link Asset} having empty content.
 *
 * @author <a href="mailto:dan.j.allen@gmail.com">Dan Allen</a>
 * @version $Revision: $
 */
public enum EmptyAsset implements Asset {
   INSTANCE;

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.shrinkwrap.api.Asset#openStream()
    */
   @Override
   public InputStream openStream()
   {
      return new ByteArrayInputStream(new byte[0]);
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "EmptyAsset";
   }

}
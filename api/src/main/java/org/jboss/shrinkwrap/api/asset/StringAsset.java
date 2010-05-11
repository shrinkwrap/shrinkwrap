package org.jboss.shrinkwrap.api.asset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Asset;

/**
 * Implementation of an {@link Asset} backed by a String
 *
 * @author <a href="mailto:dan.j.allen@gmail.com">Dan Allen</a>
 * @version $Revision: $
 */
public class StringAsset implements Asset
{
   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(StringAsset.class.getName());

   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Underlying content.
    */
   private final String content;

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new {@link Asset} instance backed by the specified String
    * 
    * @param content The content represented as a String
    * @throws IllegalArgumentException If the contents were not specified
    */
   public StringAsset(final String content)
   {
      // Precondition check
      if (content == null)
      {
         throw new IllegalArgumentException("content must be specified");
      }
      // don't need to copy since String is immutable
      this.content = content;
      if (log.isLoggable(Level.FINER))
      {
         log.finer("Created " + this + " with backing String of size " + content.length() + "b");
      }
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * @see org.jboss.shrinkwrap.api.Asset#openStream()
    */

   @Override
   public InputStream openStream()
   {
      return new ByteArrayInputStream(content.getBytes());
   }

   /**
    * {@inheritDoc}
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "StringAsset [content size=" + content.length() + " bytes]";
   }

}

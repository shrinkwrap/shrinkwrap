package org.jboss.declarchive.impl.base.resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
class TestUtils
{
   private TestUtils() {}

   /**
    * Convert a inputstream to a UTF-8 string. 
    * 
    * Helper for testing the content of loaded resources.
    * 
    * @param in Open inputstream
    * @return The inputstream as a String
    * @throws Exception
    */
   static String convertToString(InputStream in) throws Exception
   {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      int b;
      while ((b = in.read()) != -1)
      {
         out.write(b);
      }
      out.close();
      in.close();
      return new String(out.toByteArray(), "UTF-8");
   }
}
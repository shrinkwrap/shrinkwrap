/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.declarchive.impl.base.resource;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
class TestUtils
{
   private TestUtils()
   {
   }

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
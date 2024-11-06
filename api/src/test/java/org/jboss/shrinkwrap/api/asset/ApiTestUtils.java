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

package org.jboss.shrinkwrap.api.asset;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * ApiTestUtils
 *
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
class ApiTestUtils {
    /**
     * Convert a {@link InputStream} to a UTF-8 string. <br/>
     * Helper for testing the content of loaded resources. <br/>
     * This method will close the stream when done.
     *
     * @param in Open InputStream
     * @return The InputStream as a String
     * @throws Exception
     */
    static String convertToString(InputStream in) throws Exception {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            int b;
            while ((b = in.read()) != -1) {
                out.write(b);
            }
            in.close();
            return new String(out.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    /**
     * Read the byte size of a {@link InputStream}. <br/>
     * This method will close the stream when done.
     *
     * @param in Stream to get the size of.
     * @return The byte size of the stream
     * @throws Exception
     */
    static int findLengthOfStream(InputStream in) throws Exception {
        int length = 0;
        while (in.read() != -1) {
            length++;
        }
        in.close();
        return length;
    }

    /**
     * Read the byte size of a {@link Class}.
     *
     * @param clazz The class
     * @return The byte size of the given {@link Class}
     * @throws Exception
     */
    static int findLengthOfClass(Class<?> clazz) throws Exception {
        String classResourceName = getResourceNameForClass(clazz);
        try (InputStream in = SecurityActions.getThreadContextClassLoader().getResourceAsStream(classResourceName)) {
            assert in != null;
            return findLengthOfStream(in);
        }
    }

    /**
     * Get a resourceName for a {@link Class} so that it can be found in the {@link ClassLoader}. <br/>
     * class.getName.replace( . -> / ) + ".class"
     *
     * @param clazz The class to lookup
     * @return The resource name for the class
     */
    static String getResourceNameForClass(Class<?> clazz) {
        String classResourceDelimiter = clazz.getName().replaceAll("\\.", "/");
        return classResourceDelimiter + ".class";
    }
}

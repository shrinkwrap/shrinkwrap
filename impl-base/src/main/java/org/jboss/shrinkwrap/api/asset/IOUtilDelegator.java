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

import java.io.InputStream;

/**
 * IOUtilDelegator
 *
 * Class that helps expose package private {@link ByteArrayIOUtil}
 *
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public class IOUtilDelegator {
    /**
     * Delegates to {@link ByteArrayIOUtil#asByteArray(java.io.InputStream)}
     *
     * @param in
     * @throws IllegalArgumentException
     *             If the stream was not specified
     * @return the byte[] for the given InputStream
     */
    public static byte[] asByteArray(final InputStream in) throws IllegalArgumentException {
        return ByteArrayIOUtil.asByteArray(in);
    }

}

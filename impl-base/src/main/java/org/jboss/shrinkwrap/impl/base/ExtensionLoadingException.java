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
package org.jboss.shrinkwrap.impl.base;

/**
 * ExtensionLoadingException This Unchecked Exception Class is thrown from the
 * {@link org.jboss.shrinkwrap.impl.base.ServiceExtensionLoader} when something wrong has happened that we can not
 * recover from.
 *
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
public class ExtensionLoadingException extends RuntimeException {
    private static final long serialVersionUID = -4895083865917512623L;

    /**
     * @param message
     *            user-friendly description of why message is thrown
     */
    public ExtensionLoadingException(String message) {
        super(message);
    }

    /**
     * @param message
     *            user-friendly description of why message is thrown
     * @param cause
     *            Underlying cause, add this when using Exception Translation / rethrowing
     */
    public ExtensionLoadingException(String message, Throwable cause) {
        super(message, cause);
    }
}

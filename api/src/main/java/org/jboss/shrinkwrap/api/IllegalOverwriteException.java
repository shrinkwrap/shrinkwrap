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
package org.jboss.shrinkwrap.api;

/**
 * Exception thrown when trying to add an {@code Asset} into an archive under an {@code ArchivePath} which is already
 * taken by a directory.
 *
 * @author <a href="mailto:alr@jboss.org">Andrew Lee Rubinger</a>
 */
public class IllegalOverwriteException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @param message
     *            The detail message.
     */
    public IllegalOverwriteException(final String message) {
        super(message);
    }

    /**
     * @param cause
     *            The cause of the exception.
     */
    public IllegalOverwriteException(final Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     *            The detail message.
     * @param cause
     *            The cause of the exception.
     */
    public IllegalOverwriteException(final String message, final Throwable cause) {
        super(message, cause);
    }

}

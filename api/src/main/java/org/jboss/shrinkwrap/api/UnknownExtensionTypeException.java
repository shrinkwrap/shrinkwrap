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
 * Indicates that a default name cannot be generated for a given type because no extension mapping has been configured
 * via {@link ExtensionLoader#getExtensionFromExtensionMapping(Class)}.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class UnknownExtensionTypeException extends RuntimeException {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------------------------------------||
    // Constructor ------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new instance with message indicating the missing type
     */
    private <T extends Assignable> UnknownExtensionTypeException(final Class<T> type) {
        super("The current configuration has no mapping for type " + type.getCanonicalName()
            + ", unable to determine extension. You should provide extension in the services descriptor file");
    }

    // -------------------------------------------------------------------------------------||
    // Factory ----------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates a new {@link UnknownExtensionTypeException} for the specified type
     *
     * @throws IllegalArgumentException
     *             If the type is not specified
     */
    static <T extends Assignable> UnknownExtensionTypeException newInstance(final Class<T> type)
        throws IllegalArgumentException {
        // Precondition checks
        if (type == null) {
            throw new IllegalArgumentException("type must be specified");
        }

        // Create new
        return new UnknownExtensionTypeException(type);
    }
}

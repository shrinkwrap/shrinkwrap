/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.spi;

import org.jboss.shrinkwrap.api.Assignable;

/**
 * {@link Assignable} view representing an entity with an ID.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface Identifiable extends Assignable {

    /**
     * Obtains a globally-unique identifier
     */
    String getId();

    /**
     * Sets the globally-unique identifier
     *
     * @param id The ID to be set
     * @throws IllegalArgumentException
     *             If the ID is not specified
     */
    void setId(String id) throws IllegalArgumentException;
}

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
package org.jboss.shrinkwrap.api;

/**
 * General interface for representing entities such as {@link Archive}s, importers and exporters as different extension
 * types. The {@link Assignable} type is typically the end-user view. In effect all {@link Assignable} types achieve
 * some limited form of multiple inheritance via a wrapping mechanism.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface Assignable {
    /**
     * Wraps an Archive in a different 'view'.
     *
     * @see org.jboss.shrinkwrap.api.ExtensionLoader
     *
     * @param <TYPE>
     *            The type of the view to obtain.
     * @param clazz
     *            Extension interface to load
     * @return The Archive wrapped as TYPE
     */
    <TYPE extends Assignable> TYPE as(Class<TYPE> clazz);
}

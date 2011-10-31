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
package org.jboss.shrinkwrap.impl.base.io;

import java.io.Closeable;

/**
 * A I/O operation to be executed against a I/O stream in the context of a StreamTemplate
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 */
public interface StreamTask<S extends Closeable> {

    // -------------------------------------------------------------------------------------||
    // Contracts --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * This method execute's this StreamTask with the provided stream.
     *
     * @param stream
     *            This parameter holds the stream that this StreamTask operates on to execute its task.
     * @throws Exception
     *             This method will throw an instance of Exception if an unrecoverable error is encountered while
     *             performing its task.
     */
    void execute(S stream) throws Exception;

}

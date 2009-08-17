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
package org.jboss.declarchive.spi;

import java.io.InputStream;

/**
 * Generic interface for resource loading. 
 * 
 * Used to move the resource loading logic out of the archive backends.  
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public interface Resource
{

   /**
    * Get the default name for this resource, can be overriden by the user.
    * 
    * @return A name for this Resource
    */
   String getDefaultName();

   /**
    * Get a open stream for the resource content.
    * The caller is responsible for closing the stream. 
    * 
    * @return A new open inputstream for each call.
    */
   InputStream getStream();
}

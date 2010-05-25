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

import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * IllegalPathException
 * 
 * Exception thrown when trying to add a {@link Node} on and invalid path 
 * within the {@link Archive} (i.e. you are trying to add an asset to 
 * "/test.txt/somethingelse.txt" where test.txt is an {@link Asset}) 
 * 
 * @author <a href="mailto:german.escobarc@gmail.com">German Escobar</a>
 * @version $Revision: $
 */
public class IllegalArchivePathException extends RuntimeException
{

   private static final long serialVersionUID = 1L;

   /**
    * @param message
    */
   public IllegalArchivePathException(String message)
   {
      super(message);
   }

   /**
    * @param cause
    */
   public IllegalArchivePathException(Throwable cause)
   {
      super(cause);
   }

   /**
    * @param message
    * @param cause
    */
   public IllegalArchivePathException(String message, Throwable cause)
   {
      super(message, cause);
   }

}

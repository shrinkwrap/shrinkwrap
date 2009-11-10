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
package org.jboss.shrinkwrap.impl.base;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Specializer;

/**
 * A generic implementation of {@link Specializer} that delegates down to the Archive
 * extensions inner archive. Used by Archive extensions to simplify handling the generic extension
 * mechanism. 
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public abstract class SpecializedBase implements Specializer
{
   /**
    * Used by the Generic {@link Specializer} implementation to 
    * get the extension wrapped inner {@link Archive}.
    * 
    * @return The wrapped {@link Archive}  
    */
   protected abstract Archive<?> getArchive();
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Specializer#as(java.lang.Class)
    */
   @Override
   public <TYPE extends Specializer> TYPE as(Class<TYPE> clazz)
   {
      return getArchive().as(clazz);
   }
}

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
import org.jboss.shrinkwrap.spi.MemoryMapArchive;

/**
 * MemoryMapArchiveImpl
 * 
 * A default implementation for all MemoryMap archives. Thread-safe.
 *
 * @author <a href="mailto:baileyje@gmail.com">John Bailey</a>
 * @version $Revision: $
 * @param <T>
 */
public class MemoryMapArchiveImpl extends MemoryMapArchiveBase<MemoryMapArchive> implements MemoryMapArchive
{
   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Constructor
    *  
    * This constructor will generate a 
    * unique {@link Archive#getName()} per instance.
    *  
    * @param actualType The {@link Archive} type.
    */
   public MemoryMapArchiveImpl()
   {
      super();
   }

   /**
    * Constructor
    * 
    * This constructor will generate an {@link Archive} with the provided name.
    *  
    * @param archiveName
    */
   public MemoryMapArchiveImpl(String archiveName)
   {
      super(archiveName);
   }

   /*
    * (non-Javadoc)
    * @see org.jboss.declarchive.impl.base.MemoryMapArchiveBase#getActualClass()
    */
   @Override
   protected Class<MemoryMapArchive> getActualClass()
   {
      return MemoryMapArchive.class;
   }

}

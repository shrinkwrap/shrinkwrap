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
package org.jboss.declarchive.impl.base.jar;

import org.jboss.declarchive.impl.base.GenericArchive;

/**
 * MockArchive
 * 
 * Simple archive which provides no functionality beyond the
 * internal Map of content maintained by the base.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 * @param <T>
 */
public class MockArchive extends GenericArchive<TestArchive> implements TestArchive
{

   //-------------------------------------------------------------------------------------||
   // Constructor ------------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new archive with the specified name
    */
   public MockArchive(final String name) throws IllegalArgumentException
   {
      super(name, TestArchive.class);
   }
}

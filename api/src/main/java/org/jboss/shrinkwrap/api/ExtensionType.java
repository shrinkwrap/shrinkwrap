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
 * Class to help provide correct extension 
 * in {@link ArchiveFactory#create(Class)} 
 *
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
class ExtensionType
{
   static final ExtensionType WAR = new ExtensionType("war");
   static final ExtensionType JAR = new ExtensionType("jar");
   static final ExtensionType EAR = new ExtensionType("ear");
   static final ExtensionType RAR = new ExtensionType("rar");
   private static final String DOT_DELIMITER = ".";
   private final String extension;

   private ExtensionType(String extension)
   {
      this.extension = extension;
   }

   @Override
   public String toString()
   {
      return DOT_DELIMITER + extension;
   }
}

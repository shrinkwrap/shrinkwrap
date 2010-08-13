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

package org.jboss.shrinkwrap.impl.base;

import java.util.Map;

import org.jboss.shrinkwrap.api.Assignable;

/**
 * ExtensionWrapper
 * Wrapper used in the extensionMapping of {@link org.jboss.shrinkwrap.api.ExtensionLoader}
 * Initialized in {@link org.jboss.shrinkwrap.impl.base.ServiceExtensionLoader#loadExtensionMapping(Class)}
 *
 * @author <a href="mailto:ken@glxn.net">Ken Gullaksen</a>
 * @version $Revision: $
 */
class ExtensionWrapper
{

   final Map<String, String> properties;

   final String implementingClassName;

   final Class<? extends Assignable> extension;

   public ExtensionWrapper(String implementingClassName, Map<String, String> properties, Class<? extends Assignable> extension)
   {
      this.properties = properties;
      this.implementingClassName = implementingClassName;
      this.extension = extension;
   }

   /**
    * Gets the value for the given key in the properties map.
    * If the property is not found, an exception is thrown.
    *
    * @param key the key to look up value for
    * @return value of the property
    * @throws RuntimeException if the property is not found
    */
   public String getProperty(String key)
   {
      String value = properties.get(key);
      if(value == null)
      {
         throw new RuntimeException("No property value found for key " + key);
      }
      return value;
   }

}

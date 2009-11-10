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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;

/**
 * ArchiveExtensionLoader responsible for loading the Archive extensions based on ShrinkWrap SPI.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 * @param <T>
 */
public class ArchiveExtensionLoader<T>
{
   private Class<T> extensionInterface;
   
   // TODO: generic clash, should be moved out, or only support overloading of Class<T> ?
   private Map<Class<?>, Class<?>> extensionOverrides = new HashMap<Class<?>, Class<?>>();
   
   private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
   
   public ArchiveExtensionLoader(Class<T> extensionInterface) 
   {
      Validate.notNull(extensionInterface, "ExtensionInterface must be specified");
      this.extensionInterface = extensionInterface;
   }

   public <X> ArchiveExtensionLoader<T> addExtesionOverride(
         Class<X> extensionInterfaceClas, Class<? extends X> extensionImplClass) 
   {
      extensionOverrides.put(extensionInterfaceClas, extensionImplClass);
      return this;
   }
   
   public ArchiveExtensionLoader<T> setClassLoader(ClassLoader classLoader)
   {
      Validate.notNull(classLoader, "ClassLoader must be specified");
      this.classLoader = classLoader;
      return this;
   }

   public final ClassLoader getClassLoader()
   {
      return classLoader;
   }
   
   public T load(Archive<?> archive) 
   {
      Validate.notNull(archive, "Archive must be specified");
      try 
      {
         Class<T> extensionImplClass = null;
         
         if(hasExtensionOverride()) 
         {
            extensionImplClass = (Class<T>)getExtensionOverride();
         } 
         else 
         {
            List<URL> urls = findExtensions(extensionInterface);
            if(urls.size() == 0) 
            {
               throw new RuntimeException(
                     "No extension implementation found for " + extensionInterface.getName() + 
                     ", please verify classpath or add a extensionOverride");
            }
            if(urls.size() > 1) 
            {
               throw new RuntimeException(
                     "Multiple extension implementations found for " + extensionInterface.getName() + 
                     ", please verify classpath or add a extensionOverride");
            }

            String extensionClassName = loadExtensionName(urls.get(0));
            extensionImplClass = loadExtensionClass(extensionClassName);          
         }

         if(!extensionInterface.isAssignableFrom(extensionImplClass)) {
               throw new RuntimeException(
                     "Found extension implementation is not of same type " + extensionImplClass.getName() + 
                     " as " + extensionInterface.getName());
         }
   
         Constructor<T> extensionImplConstructor = findConstructor(extensionImplClass);
         Class<?> constructorArg = extensionImplConstructor.getParameterTypes()[0];
         if(constructorArg.isInstance(archive)) 
         {
            return extensionImplConstructor.newInstance(archive);   
         } 
         else 
         {
            return extensionImplConstructor.newInstance(
                  new ArchiveExtensionLoader(constructorArg).load(archive)
            );
         }
      } 
      catch (Exception e) 
      {
         throw new RuntimeException("Could not load extension for " + extensionInterface.getName(), e);
      }
   }
   
   private boolean hasExtensionOverride() {
      return extensionOverrides.containsKey(extensionInterface);
   }
   
   private Class<?> getExtensionOverride() {
      return extensionOverrides.get(extensionInterface);
   }
   
   private List<URL> findExtensions(Class<T> extensionClass) throws IOException 
   {
      Enumeration<URL> urls  = getClassLoader().getResources(
            "META-INF/services/" + extensionClass.getName());

      return Collections.list(urls);
   }
   
   private String loadExtensionName(URL extensionURL) throws IOException 
   {
      return new String(IOUtil.asByteArray(extensionURL.openStream()));
   }

   private Class<T> loadExtensionClass(String extensionClassName) throws ClassNotFoundException 
   {
      return (Class<T>)getClassLoader().loadClass(extensionClassName);      
   }
   
   private Constructor<T> findConstructor(Class<T> implClass) 
   {
      Constructor<T>[] constructors = (Constructor<T>[])implClass.getConstructors(); 
      for(Constructor<T> constructor : constructors) {
         Class<?>[] parameters = constructor.getParameterTypes();
         if(parameters.length != 1) 
         {
            continue;
         }
         Class<?> parameter = parameters[0];
         if(Archive.class.isAssignableFrom(parameter)) 
         {
            return constructor;
         }
      }
      throw new RuntimeException(
            "No constructor with a single argument of type " + 
            Archive.class.getName() + " could be found");
   }
}

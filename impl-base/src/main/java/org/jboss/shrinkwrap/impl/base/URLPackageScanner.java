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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Implementation of scanner which can scan a {@link URLClassLoader}
 *
 * @author Thomas Heute
 * @author Gavin King
 * @author Norman Richards
 * @author Pete Muir
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 */
public class URLPackageScanner
{

   private static final Logger log = Logger.getLogger(URLPackageScanner.class.getName());

   private final String packageName;

   private final String packageNamePath;

   private final boolean addRecursively;

   private final ClassLoader classLoader;
   
   private final Set<Class<?>> classes = new HashSet<Class<?>>();

   public URLPackageScanner(Package pkg, boolean addRecursively, ClassLoader classLoader)
   {
      this(pkg.getName(), addRecursively, classLoader);
   }

   public URLPackageScanner(String packageName, boolean addRecursively, ClassLoader classLoader)
   {
      Validate.notNull(packageName, "PackageName must be specified");
      Validate.notNull(addRecursively, "AddRecursively must be specified");
      Validate.notNull(classLoader, "ClassLoader must be specified");
      
      this.packageName = packageName;
      this.packageNamePath = packageName.replace(".", "/");
      this.addRecursively = addRecursively;
      this.classLoader = classLoader;
   }

   private void scanPackage()
   {
      try
      {
         Set<String> paths = new HashSet<String>();

         for (URL url : loadResources(packageNamePath))
         {
            String urlPath = url.getFile();
            urlPath = URLDecoder.decode(urlPath, "UTF-8");
            if (urlPath.startsWith("file:"))
            {
               urlPath = urlPath.substring(5);
            }
            if (urlPath.indexOf('!') > 0)
            {
               urlPath = urlPath.substring(0, urlPath.indexOf('!'));
            }
            paths.add(urlPath);
         }
         handle(paths);
      }
      catch (IOException ioe)
      {
         log.log(Level.WARNING, "could not read: " + packageName, ioe);
      }
      catch (ClassNotFoundException ioe)
      {
         log.log(Level.WARNING, "Class coud not be loaded in package: " + packageName, ioe);
      }
   }

   private void handleArchiveByFile(File file) throws IOException, ClassNotFoundException
   {
      try
      {
         log.fine("archive: " + file);
         ZipFile zip = new ZipFile(file);
         Enumeration<? extends ZipEntry> entries = zip.entries();
         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.startsWith(packageNamePath) && name.endsWith(".class")
                  && (addRecursively || !name.substring(packageNamePath.length() + 1).contains("/")))
            {
               String className = name.replace("/", ".").replace(".class", "");
               classes.add(classLoader.loadClass(className));
            }
         }
      }
      catch (ZipException e)
      {
         throw new RuntimeException("Error handling file " + file, e);
      }
   }

   private void handle(Set<String> paths) throws IOException, ClassNotFoundException
   {
      for (String urlPath : paths)
      {
         log.fine("scanning: " + urlPath);
         File file = new File(urlPath);
         if (file.isDirectory())
         {
            handle(file, packageName);
         }
         else
         {
            handleArchiveByFile(file);
         }
      }
   }

   private void handle(File file, String packageName) throws ClassNotFoundException
   {
      for (File child : file.listFiles())
      {
         if (!child.isDirectory() && child.getName().endsWith(".class"))
         {
            classes.add(classLoader.loadClass(packageName + "." + child.getName().substring(0, child.getName().lastIndexOf(".class"))));
         }
         else if (child.isDirectory() && addRecursively)
         {
            handle(child, packageName + "." + child.getName());
         }
      }
   }

   public List<URL> loadResources(String name) throws IOException
   {
      return Collections.list(classLoader.getResources(name));
   }

   public Set<Class<?>> getClasses()
   {
      scanPackage();
      return classes;
   }
}
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
package org.jboss.shrinkwrap.classloader;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.vfs3.ArchiveFileSystem;
import org.jboss.vfs.TempDir;
import org.jboss.vfs.TempFileProvider;
import org.jboss.vfs.VFS;
import org.jboss.vfs.VirtualFile;

/**
 * Extension that will create a ClassLoader based on a Array of Archives
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ShrinkWrapClassLoader extends URLClassLoader implements Closeable
{
   private Set<Closeable> vfsHandlers = new HashSet<Closeable>();
   
   private Set<ExecutorService> executorServices = new HashSet<ExecutorService>();
   
   /**
    * @param archives
    */
   public ShrinkWrapClassLoader(final Archive<?>... archives)
   {
      super(new URL[]{});
      
      if(archives == null) 
      {
         throw new IllegalArgumentException("Archives must be specified");   
      }
      addArchives(archives);
   }
   
   /**
    * @param parent
    * @param archives
    */
   public ShrinkWrapClassLoader(ClassLoader parent, final Archive<?>... archives)
   {
      super(new URL[]{});
      
      if(archives == null) 
      {
         throw new IllegalArgumentException("Archives must be specified");   
      }
      addArchives(archives);
   }
   
   protected void addArchives(Archive<?>[] archives) 
   {
      for(Archive<?> archive : archives)
      {
         addArchive(archive);
      }
   }
   
   protected void addArchive(Archive<?> archive) 
   {
      // TODO: Wrap a ExecutorService in a ScheduledExecutorService 
      //Configuration configuration = archive.as(Configurable.class).getConfiguration();
      ScheduledExecutorService executorService = null; //configuration.getExecutorService();
      if(executorService == null)
      {
         executorService = Executors.newScheduledThreadPool(2);

         // TODO: only add to 'managed' executor services if it was created here..
         
         // add to list of resources to cleanup during close()
         executorServices.add(executorService); 
      }
      
      try
      {
         TempFileProvider tempFileProvider = TempFileProvider.create("shrinkwrap-classloader", executorService);
         
         final TempDir tempDir = tempFileProvider.createTempDir(archive.getName());
         final VirtualFile virtualFile = VFS.getChild(UUID.randomUUID().toString()).getChild(archive.getName());
         
         Closeable handle = VFS.mount(virtualFile, new ArchiveFileSystem(archive, tempDir));
         
         // add to list of resources to cleanup during close()
         vfsHandlers.add(handle); 
         
         addURL(virtualFile.toURL());
         
      }
      catch (Exception e) 
      {
         throw new RuntimeException("Could not create ClassLoader from archive: " + archive.getName(), e);
      }
   }

   /* (non-Javadoc)
    * @see java.io.Closeable#close()
    */
   public void close() throws IOException
   {
      // Unmount all VFS3 mount points
      for(Closeable handle : vfsHandlers)
      {
         try
         {
            handle.close();
         } 
         catch (Exception e) 
         {
            e.printStackTrace(); // TODO: handle exception
         }
      }
      
      // Shutdown all created Executor Services.
      for(ExecutorService executorService : executorServices)
      {
         executorService.shutdownNow();
      }
   }
}

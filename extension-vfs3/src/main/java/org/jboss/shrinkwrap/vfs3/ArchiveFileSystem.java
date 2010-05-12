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
package org.jboss.shrinkwrap.vfs3;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Asset;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.vfs.TempDir;
import org.jboss.vfs.VFSUtils;
import org.jboss.vfs.VirtualFile;
import org.jboss.vfs.spi.FileSystem;
import org.jboss.vfs.util.PathTokenizer;

/**
 * VFS {@link FileSystem} which uses an {@link Archive} as a backing.
 * 
 * @author <a href="jbailey@redhat.com">John Bailey</a>
 */
public class ArchiveFileSystem implements FileSystem
{
   /**
    * The Archive used for backing
    */
   private final Archive<?> archive;

   /**
    * A temp directory handle
    */
   private final TempDir tempDirectory;

   /**
    * The filesystem root file for cached assets
    */
   private final File fsRoot;

   /**
    * Creation time to be used for last modified checks for non-cached files  
    */
   private final long creationTime = System.currentTimeMillis();

   /**
    * Constructs an Archive filesystem
    * 
    * @param archive the archive used for backing
    * @param tempDirectory the temp directory used for caching files
    * @throws IOException if any problem occurs
    */
   public ArchiveFileSystem(Archive<?> archive, TempDir tempDirectory) throws IOException
   {
      this.archive = archive;
      this.tempDirectory = tempDirectory;
      this.fsRoot = tempDirectory.getRoot();
   }

   /** {@inheritDoc} */
   public void close() throws IOException
   {
      VFSUtils.safeClose(tempDirectory);
   }

   /** {@inheritDoc} */
   public boolean delete(VirtualFile mountPoint, VirtualFile target)
   {
      boolean deleted = true;
      final Node node = getNode(mountPoint, target);
      if (node != null)
      {
         final File cachedFile = getCachedFile(node);
         if (cachedFile.exists())
         {
            deleted = cachedFile.delete();
         }
         deleted = deleted && archive.delete(node.getPath());
      }
      return deleted;
   }

   /** {@inheritDoc} */
   public boolean exists(VirtualFile mountPoint, VirtualFile target)
   {
      return getNode(mountPoint, target) != null;
   }

   /** {@inheritDoc} */
   public CodeSigner[] getCodeSigners(VirtualFile mountPoint, VirtualFile target)
   {
      return null;
   }

   /** {@inheritDoc} */
   public List<String> getDirectoryEntries(VirtualFile mountPoint, VirtualFile target)
   {
      List<String> entries = new ArrayList<String>();
      final Node node = getNode(mountPoint, target);
      if (node != null)
      {
         final Set<Node> children = node.getChildren();
         for (Node childNode : children)
         {
            final String path = childNode.getPath().get();
            final String entryName = path.substring(path.lastIndexOf("/") + 1);
            entries.add(entryName);
         }
      }
      return entries;
   }

   /** 
    * {@inheritDoc} 
    * <br />
    * Note: This is called by VirtualFile:getPhysicalFile.  This should be avoided if possible to maintain the in-memory nature of the archive.    
    * */
   public File getFile(VirtualFile mountPoint, VirtualFile target) throws IOException
   {
      final Node node = getNode(mountPoint, target);
      if (node == null)
      {
         return null;
      }

      File cachedFile = getCachedFile(node);

      if (fsRoot.equals(cachedFile))
      {
         return fsRoot;
      }

      if (cachedFile.exists())
      {
         return cachedFile;
      }
      synchronized (node)
      {
         final Asset asset = node.getAsset();
         cachedFile = buildFile(node.getPath().get());
         if (asset == null)
         {
            cachedFile.mkdir();
         }
         else
         {
            VFSUtils.copyStreamAndClose(asset.openStream(), new BufferedOutputStream(new FileOutputStream(cachedFile)));
         }
         return cachedFile;
      }
   }

   /** {@inheritDoc} */
   public long getLastModified(VirtualFile mountPoint, VirtualFile target)
   {
      final Node node = getNode(mountPoint, target);
      final File cachedFile = getCachedFile(node);
      if (cachedFile.exists())
      {
         return cachedFile.lastModified();
      }
      return creationTime;
   }

   /** {@inheritDoc} */
   public long getSize(VirtualFile mountPoint, VirtualFile target)
   {
      final Node node = getNode(mountPoint, target);
      final File cachedFile = getCachedFile(node);
      if (cachedFile.exists())
      {
         return cachedFile.length();
      }
      else if (node.getAsset() != null)
      {
         // This sucks, but is the only way to get at it.
         try
         {
            return getFile(mountPoint, target).length();
         }
         catch (IOException e)
         {
            throw new RuntimeException("Failed to get File", e);
         }
      }
      return 0L;
   }

   /** {@inheritDoc} */
   public boolean isDirectory(VirtualFile mountPoint, VirtualFile target)
   {
      // Only if this exists can it be a directory
      if (this.exists(mountPoint, target))
      {
         final Node node = getNode(mountPoint, target);
         final Asset asset = node.getAsset();
         // Null assets under a node indicate a directory
         return asset == null;
      }
      // Doesn't exist, return false
      return false;

   }

   /** {@inheritDoc} */
   public boolean isFile(VirtualFile mountPoint, VirtualFile target)
   {
      final Node node = getNode(mountPoint, target);
      return node != null && node.getAsset() != null;
   }

   /** {@inheritDoc} */
   public boolean isReadOnly()
   {
      return false;
   }

   /** {@inheritDoc} */
   public InputStream openInputStream(VirtualFile mountPoint, VirtualFile target) throws IOException
   {
      InputStream stream = null;
      final Node node = getNode(mountPoint, target);
      if (node != null)
      {
         final File cachedFile = getCachedFile(node);
         if (cachedFile.exists())
         {
            stream = new FileInputStream(cachedFile);
         }
         else if (node.getAsset() != null)
         {
            stream = node.getAsset().openStream();
         }
      }
      return stream;
   }

   /**
    * Get the cached file for a Node
    *
    * @param node The node to get the cached file for
    * @return Returns the cached file location
    */
   private File getCachedFile(Node node)
   {
      return new File(fsRoot, node.getPath().get());
   }

   /**
    * Get the node for the provided target
    * 
    * @param mountPoint the filesystem mount point
    * @param target the target file 
    * @return the node or null if the node is not found
    */
   private Node getNode(VirtualFile mountPoint, VirtualFile target)
   {
      String archivePath = getRelativePath(mountPoint, target);
      if ("".equals(archivePath))
         archivePath = "/";
      return archive.get(archivePath);
   }

   /**
    * Get the relative path between the mountpoint and the target
    * 
    * @param mountPoint the filesystem mount point
    * @param target the target file 
    * @return the relative path
    */
   private String getRelativePath(VirtualFile mountPoint, VirtualFile target)
   {
      if (mountPoint.equals(target))
         return "";
      return target.getPathNameRelativeTo(mountPoint);
   }

   /**
    * Build the cache file and create any parent directories that do not exist.
    *
    * @param path The path to build the file for
    * @return The built file
    */
   private File buildFile(String path)
   {
      final List<String> tokens = PathTokenizer.getTokens(path);
      File currentFile = fsRoot;
      for (String token : tokens)
      {
         currentFile = new File(currentFile, token);
      }
      currentFile.getParentFile().mkdirs();
      return currentFile;
   }

   /**
    * {@inheritDoc}
    * @see org.jboss.vfs.spi.FileSystem#getMountSource()
    */
   public File getMountSource()
   {
      return fsRoot;
   }

}
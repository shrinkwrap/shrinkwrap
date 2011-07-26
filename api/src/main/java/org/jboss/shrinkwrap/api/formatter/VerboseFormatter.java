/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api.formatter;

import java.io.InputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.importer.ZipImporter;

/**
 * {@link Formatter} implementation to provide an "ls -l"-esque
 * output for an {@link Archive}, listing all internal contents
 * in sorted order
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:gerhard.poul@gmail.com">Gerhard Poul</a>
 * @version $Revision: $
 */
enum VerboseFormatter implements Formatter {
   INSTANCE;

   //-------------------------------------------------------------------------------------||
   // Class Members ----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||
   
   /**
    * Root character "/"
    */
   private static final String ROOT = "/";
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   @Override
   public String format(final Archive<?> archive) throws IllegalArgumentException
   {
      // Precondition checks
      if (archive == null)
      {
         throw new IllegalArgumentException("archive must be specified");
      }

      // Start the output with the name of the archive
      StringBuilder sb = new StringBuilder(archive.getName()).append(FormattingConstants.COLON)
         .append(FormattingConstants.NEWLINE);

      // format recursively, except the parent 
      Node rootNode = archive.get(ROOT);
      for (Node child : rootNode.getChildren()) 
      {
         format(sb, child, "");
      }
      
      // remove the last NEWLINE
      sb.deleteCharAt(sb.length() - 1);
      
      return sb.toString();
   }
   
   private void format(StringBuilder sb, Node node, final String subArchiveContext) 
   {
      String nodePath = node.getPath().get();
      Asset nodeAsset = node.getAsset();

      // Check whether this is a non-null asset
      if (nodeAsset != null) {
         String lcNodePath = nodePath.toLowerCase();
         // Is this a sub-archive? (i.e. is this asset of type ArchiveAsset or does it have a known extension?)
         if (nodeAsset.getClass().getName().equals("org.jboss.shrinkwrap.impl.base.asset.ArchiveAsset") || 
               lcNodePath.endsWith(".jar") ||
               lcNodePath.endsWith(".war") ||
               lcNodePath.endsWith(".rar") ||
               lcNodePath.endsWith(".sar")) {
            InputStream nodeInputStream = nodeAsset.openStream();
            // If a valid InputStream is returned, list its contents
            if (nodeInputStream != null) {
               GenericArchive nodeArchive = ShrinkWrap.create(GenericArchive.class).as(ZipImporter.class).importFrom(nodeInputStream).as(GenericArchive.class);
               format(sb, nodeArchive.get(ROOT), subArchiveContext + nodePath);
               // remove the last newline
               sb.deleteCharAt(sb.length() - 1);
               // InputStream is not closed on purpose, as that might fail a subsequent export
            } else {
               // If there is no valid InputStream, only output the path
               sb.append(subArchiveContext);
               sb.append(nodePath);
            }
         } else {
            // If this is not a sub-archive, print the node path
            sb.append(subArchiveContext);
            sb.append(nodePath);
         }
      } else {
         // If this is a null-asset, print the node path
         sb.append(subArchiveContext);
         sb.append(nodePath);
         // Only print a trailing slash if this is not a root node
         if (!nodePath.equals(ROOT))
            sb.append(FormattingConstants.SLASH);
      }
      
      sb.append(FormattingConstants.NEWLINE);
      
      for (Node child : node.getChildren()) 
      {
         format(sb, child, subArchiveContext);
      }
   }

}

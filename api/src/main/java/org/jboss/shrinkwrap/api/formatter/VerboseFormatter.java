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
package org.jboss.shrinkwrap.api.formatter;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Node;

/**
 * {@link Formatter} implementation to provide an "ls -l"-esque
 * output for an {@link Archive}, listing all internal contents
 * in sorted order
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
enum VerboseFormatter implements Formatter {
   INSTANCE;

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
      Node rootNode = archive.get("/");
      for (Node child : rootNode.getChildren()) 
      {
         format(sb, child);
      }
      
      // remove the last NEWLINE
      sb.deleteCharAt(sb.length() - 1);
      
      return sb.toString();
   }
   
   private void format(StringBuilder sb, Node node) 
   {
      sb.append(node.getPath().get());
      if (node.getAsset() == null) 
      {
         sb.append(FormattingConstants.SLASH);
      }
      
      sb.append(FormattingConstants.NEWLINE);
      
      for (Node child : node.getChildren()) 
      {
         format(sb, child);
      }
   }

}

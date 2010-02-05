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

package org.jboss.shrinkwrap.api.formatter;

import java.util.SortedSet;
import java.util.TreeSet;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;

/**
 * {@link Formatter} implementation to provide the full path (including parents) of
 * all items within the {@link Archive}.
 */
enum FullFormatter implements Formatter
{
   INSTANCE;

   @Override
   public String format(final Archive<?> archive) throws IllegalArgumentException
   {
      // Start the output with the name of the archive
      StringBuilder sb = new StringBuilder(archive.getName()).append(FormattingConstants.COLON)
         .append(FormattingConstants.NEWLINE);
      SortedSet<String> archiveContents = new TreeSet<String>();

      // I know it's ugly, but we have to do two iterations per entry so we get everything
      for (ArchivePath path : archive.getContent().keySet())
      {
         archiveContents.add(path.get());
         ArchivePath parentPath = path.getParent();

         while (parentPath != null)
         {
            archiveContents.add(parentPath.get());
            parentPath = parentPath.getParent();
         }
      }

      // spit out the correct format now
      for (String pathEntry : archiveContents)
      {
         sb.append(pathEntry).append(FormattingConstants.NEWLINE);
      }
      int firstLeadingSlash = sb.indexOf(String.valueOf(FormattingConstants.SLASH));
      sb.delete(firstLeadingSlash, firstLeadingSlash + 2);
      sb.deleteCharAt(sb.length() - 1);
      
      return sb.toString();
   }
}

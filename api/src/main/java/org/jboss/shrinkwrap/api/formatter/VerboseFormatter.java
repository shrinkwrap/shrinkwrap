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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Path;

/**
 * {@link Formatter} implementation to provide an "ls -l"-esque
 * output for an {@link Archive}, listing all internal contents
 * in sorted order
 * 
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public enum VerboseFormatter implements Formatter {
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

      // Make a builder
      final StringBuilder sb = new StringBuilder();

      // Add the name
      sb.append(archive.getName()).append(FormattingConstants.COLON).append(FormattingConstants.NEWLINE);

      // Sort all paths
      final List<Path> paths = new ArrayList<Path>(archive.getContent().keySet());
      Collections.sort(paths);
      final int numPaths = paths.size();
      int count = 0;
      for (final Path path : paths)
      {
         count++;
         sb.append(path.get());
         if (count != numPaths)
         {
            sb.append(FormattingConstants.NEWLINE);
         }
      }

      // Return
      return sb.toString();
   }

}

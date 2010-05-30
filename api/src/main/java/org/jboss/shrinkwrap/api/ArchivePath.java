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
package org.jboss.shrinkwrap.api;

/**
 * Represents a target context within an {@link Archive} under
 * which an {@link Node} may be found.  All {@link ArchivePath}
 * contexts are absolute (ie. prepended with the '/' character).
 * {@link ArchivePath}s may have parent contexts, unless the path
 * is at the root.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface ArchivePath extends Comparable<ArchivePath>
{
   /**
    * Obtains the context which this {@link ArchivePath} represents
    * 
    * @return
    */
   String get();

   /**
    * Obtains the parent of this Path, if exists, else null.
    * For instance if the Path is "/my/path", the parent
    * will be "/my".  Each call will result in a new object reference,
    * though subsequent calls upon the same Path will be equal by value.
    * @return
    */
   ArchivePath getParent();
}

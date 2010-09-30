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
package org.jboss.shrinkwrap.dependencies;

import java.util.Collection;

import org.jboss.shrinkwrap.dependencies.impl.MavenDependencies;
import org.sonatype.aether.graph.Exclusion;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class Dependencies
{

   public static <T extends DependencyBuilder> T use(Class<T> clazz)
   {
      return SecurityActions.newInstance(clazz.getName(), new Class<?>[0], new Object[0], clazz);
   }

   public static DependencyBuilder artifact(String coordinates)
   {
      return new MavenDependencies().artifact(coordinates);
   }

   public static DependencyBuilder scope(String scope)
   {
      return new MavenDependencies().scope(scope);
   }

   public static DependencyBuilder optional(boolean optional)
   {
      return new MavenDependencies().optional(optional);
   }

   public static DependencyBuilder exclusion(Exclusion exclusion)
   {
      return new MavenDependencies().exclusion(exclusion);
   }

   public static DependencyBuilder exclusions(Exclusion... exclusions)
   {
      return new MavenDependencies().exclusions(exclusions);
   }

   public static DependencyBuilder exclusions(Collection<Exclusion> exclusions)
   {
      return new MavenDependencies().exclusions(exclusions);
   }

}

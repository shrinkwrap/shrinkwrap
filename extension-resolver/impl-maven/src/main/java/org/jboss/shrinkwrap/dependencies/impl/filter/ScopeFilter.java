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
package org.jboss.shrinkwrap.dependencies.impl.filter;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.shrinkwrap.dependencies.DependencyFilter;
import org.jboss.shrinkwrap.dependencies.impl.MavenBuilderImpl;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;

/**
 * A filter which limits scope of the artifacts. Only the artifacts within
 * specified scopes are included in resolution.
 * 
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class ScopeFilter implements DependencyFilter<MavenBuilderImpl>
{
   private Set<String> allowedScopes;

   /**
    * Creates a filter which accepts all artifacts with no scope defined,
    * that is their scope is an empty string.
    */
   public ScopeFilter()
   {
      this("");
   }

   /**
    * Creates a filter which accepts all artifacts that their scope is one
    * of the specified.
    * @param scopes The enumeration of allowed scopes
    */
   public ScopeFilter(String... scopes)
   {
      this.allowedScopes = new HashSet<String>();
      if (scopes.length != 0)
      {
         allowedScopes.addAll(Arrays.asList(scopes));
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.jboss.shrinkwrap.dependencies.DependencyFilter#configure(org.jboss.shrinkwrap.dependencies.DependencyBuilder)
    */
   public ScopeFilter configure(MavenBuilderImpl dependencyBuilder)
   {
      return this;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.sonatype.aether.graph.DependencyFilter#accept(org.sonatype.aether.graph.DependencyNode, java.util.List)
    */
   public boolean accept(DependencyNode node, List<DependencyNode> parents)
   {
      Dependency dependency = node.getDependency();

      if (dependency == null)
      {
         return false;
      }

      if (allowedScopes.contains(dependency.getScope()))
      {
         return true;
      }

      return false;
   }
}

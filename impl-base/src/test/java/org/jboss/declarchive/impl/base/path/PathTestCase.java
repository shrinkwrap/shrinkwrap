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
package org.jboss.declarchive.impl.base.path;

import junit.framework.Assert;

import org.jboss.declarchive.api.Path;
import org.junit.Test;


/**
 * PathTestCase
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class PathTestCase
{

   @Test
   public void shouldResolveBasePathWithNoPrefixPostfix() throws Exception 
   {
      testBasePath(new BasePath("WEB-INF"));
   }

   @Test
   public void shouldResolveBasePathWithNoPrefix() throws Exception 
   {
      testBasePath(new BasePath("WEB-INF/"));
   }

   @Test
   public void shouldResolveBasePathWithNoPostfix() throws Exception 
   {
      testBasePath(new BasePath("/WEB-INF"));
   }

   @Test
   public void shouldResolveBasePathWithPrefixPostfix() throws Exception 
   {
      testBasePath(new BasePath("/WEB-INF/"));
   }
   
   @Test
   public void shouldResolveRelativePathWithNoPrefixPostfix() throws Exception 
   {
      testRelativePath(
            new RelativePath(
                  new BasePath("WEB-INF"), 
                  "classes"));
   }

   @Test
   public void shouldResolveRelativePathWithNoPrefix() throws Exception 
   {
      testRelativePath(
            new RelativePath(
                  new BasePath("WEB-INF"), 
                  "classes/"));
   }
   
   @Test
   public void shouldResolveRelativePathWithNoPostfix() throws Exception 
   {
      testRelativePath(
            new RelativePath(
                  new BasePath("WEB-INF"), 
                  "/classes"));
   }

   @Test
   public void shouldResolveRelativePathWithPrefixPostfix() throws Exception 
   {
      testRelativePath(
            new RelativePath(
                  new BasePath("WEB-INF"), 
                  "/classes/"));
   }
   
   private void testRelativePath(Path path) throws Exception {
      Assert.assertEquals(
            "Should resolve realtive path",
           "/WEB-INF/classes/", path.get());
     
   }
   
   private void testBasePath(Path path) 
   {
      Assert.assertEquals(
            "Should resolve a absolute path to root",
            "/WEB-INF/", path.get());
   }
}

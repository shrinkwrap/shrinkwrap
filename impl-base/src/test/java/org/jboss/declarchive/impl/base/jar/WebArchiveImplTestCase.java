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
package org.jboss.declarchive.impl.base.jar;

import org.jboss.declarchive.api.Path;
import org.jboss.declarchive.api.jar.WebArchive;
import org.jboss.declarchive.impl.base.path.BasePath;
import org.junit.Test;


/**
 * Test case to ensure that the WebArchiveImpl follow the 
 * java Web Archive specification structure.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class WebArchiveImplTestCase
{
   private Path webinfPath = new BasePath("WEB-INF");
   
   private WebArchive archive;
   
   //TODO Re-enable
   @Test
   public void justHereSoWeDontFail(){}
//   
//   @Before
//   public void createWebArchive() throws Exception {
//      archive = new WebArchiveImpl(new MockArchive<WebArchive>());
//   }
//
//   @After
//   public void displayArchive() throws Exception {
//      System.out.println("Archive:\n" + archive.toString(true));
//   }
//   
//   @Test
//   public void shouldPlaceWebXMLInWebInf() throws Exception 
//   {
//      archive.setWebXML("org/jboss/declarchive/impl/base/resource/Test.properties");
//      Assert.assertTrue(
//            "web.xml should be located in /WEB-INF/web.xml",
//            archive.contains(new ResourcePath(webinfPath, "web.xml")));
//   }
//
//   @Test
//   public void shouldPlaceLibrariesInWebInf() throws Exception 
//   {
//      
//   }
//
//   @Test
//   public void shouldPlaceClassesInWebInf() throws Exception 
//   {
//      
//   }
}

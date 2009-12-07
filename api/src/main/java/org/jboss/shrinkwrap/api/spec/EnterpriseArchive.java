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
package org.jboss.shrinkwrap.api.spec;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.container.DirectoryContainer;
import org.jboss.shrinkwrap.api.container.EnterpriseContainer;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.container.ManifestContainer;
import org.jboss.shrinkwrap.api.container.ResourceContainer;

/**
 * EnterpriseArchive
 *
 * Traditional EAR (Java Enterprise Archive) structure. Used in 
 * construction of enterprise applications.
 *
 * @see TODO: find ear spec doc url
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public interface EnterpriseArchive
      extends
         Archive<EnterpriseArchive>,
         ResourceContainer<EnterpriseArchive>,
         ManifestContainer<EnterpriseArchive>,
         LibraryContainer<EnterpriseArchive>,
         EnterpriseContainer<EnterpriseArchive>,
         DirectoryContainer<EnterpriseArchive>
{

}

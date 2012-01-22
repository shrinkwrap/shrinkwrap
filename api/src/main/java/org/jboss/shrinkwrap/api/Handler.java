/*
 * JBoss, Home of Professional Open Source
 * Copyright ${year}, Red Hat Middleware LLC, and individual contributors
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

import org.jboss.shrinkwrap.api.asset.Asset;

public interface Handler {

    /**
     * CallBack when a {@link Asset} is added to a specific {@link ArchivePath}.
     *
     * @param archivePath Where in the {@link Archive} it is being added.
     * @param asset What is being added
     * @return the Asset to insert into the {@link Archive}, null to not add.
     */
    Asset handle(ArchiveEvent event);

}
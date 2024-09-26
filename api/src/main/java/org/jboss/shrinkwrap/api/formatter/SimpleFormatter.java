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

import java.util.Map;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;

/**
 * {@link Formatter} implementation to provide a simple, one-line description of an {@link Archive}, including its name
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
enum SimpleFormatter implements Formatter {
    INSTANCE;

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * String used to denote assets in the formatted message
     */
    private static final String ASSETS = "assets";

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Override
    public String format(final Archive<?> archive) throws IllegalArgumentException {
        // Precondition checks
        if (archive == null) {
            throw new IllegalArgumentException("archive must be specified");
        }

        // Format: "name: X assets"
        return archive.getName() + FormattingConstants.COLON +
                FormattingConstants.SPACE + this.getNumAssets(archive) + FormattingConstants.SPACE +
                ASSETS;
    }

    /**
     * Returns the number of assets on an {@link Archive}.
     *
     * @param archive
     *            the Archive from which we are going to obtain the number of assets.
     * @return the number of assets inside the archive
     */
    private int getNumAssets(final Archive<?> archive) {
        int assets = 0;

        Map<ArchivePath, Node> content = archive.getContent();
        for (Map.Entry<ArchivePath, Node> entry : content.entrySet()) {
            if (entry.getValue().getAsset() != null) {
                assets++;
            }
        }

        return assets;
    }

}

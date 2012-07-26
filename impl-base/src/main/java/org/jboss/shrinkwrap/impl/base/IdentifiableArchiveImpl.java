/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.impl.base;

import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.spi.Identifiable;

/**
 * {@link Assignable} implementation view of an {@link Identifiable}.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class IdentifiableArchiveImpl extends AssignableBase<ArchiveBase<?>> implements Identifiable {

    /**
     * Logger
     */
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(IdentifiableArchiveImpl.class.getName());

    public IdentifiableArchiveImpl(final ArchiveBase<?> archive) {
        super(archive);
    }

    @Override
    public String getId() {
        return this.getArchive().getId();
    }

    @Override
    public void setId(final String id) throws IllegalArgumentException {
        this.getArchive().setId(id);

    }

}

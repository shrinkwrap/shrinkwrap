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
package org.jboss.shrinkwrap.impl.base.exporter.tar;

import java.io.IOException;
import java.io.OutputStream;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.impl.base.exporter.AbstractOnDemandInputStream;
import org.jboss.shrinkwrap.impl.base.io.tar.TarBzOutputStream;
import org.jboss.shrinkwrap.impl.base.io.tar.TarEntry;

/**
 * TarBz on demand input stream.
 *
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 * @author <a href="mailto:ts@bee.kz">Tair Sabirgaliev</a>
 */
class TarBzOnDemandInputStream extends AbstractOnDemandInputStream<TarBzOutputStream> {

    /**
     * Creates stream directly from archive.
     *
     * @param archive
     */
    TarBzOnDemandInputStream(Archive<?> archive) {
        super(archive);
    }

    @Override
    protected TarBzOutputStream createOutputStream(OutputStream outputStream)
            throws IOException {
        return new TarBzOutputStream(outputStream);
    }

    @Override
    protected void closeEntry(TarBzOutputStream outputStream) throws IOException {
        outputStream.closeEntry();
    }

    @Override
    protected void putNextEntry(TarBzOutputStream outputStream, String context)
            throws IOException {
        final TarEntry entry = new TarEntry(context);
        outputStream.putNextEntry(entry);
    }
}

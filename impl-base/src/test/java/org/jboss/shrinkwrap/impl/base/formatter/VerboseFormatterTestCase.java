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
package org.jboss.shrinkwrap.impl.base.formatter;

import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.formatter.Formatters;

/**
 * Ensures that the {@link Formatters#VERBOSE} is functioning as expected
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class VerboseFormatterTestCase extends FormatterTestBase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private static final String EXPECTED_OUTPUT = NAME_ARCHIVE
        + ":\n/org/\n/org/jboss/\n/org/jboss/shrinkwrap/\n/org/jboss/shrinkwrap/impl/\n/org/jboss/shrinkwrap/impl/base/\n"
        + "/org/jboss/shrinkwrap/impl/base/formatter/\n/org/jboss/shrinkwrap/impl/base/formatter/FormatterTestBase.class\n"
        + "/org/jboss/shrinkwrap/impl/base/test/\n/org/jboss/shrinkwrap/impl/base/test/ArchiveTestBase.class\n"
        /* ArchiveTestBase contains an anonymous inner class which must be accounted for. */
        + "/org/jboss/shrinkwrap/impl/base/test/ArchiveTestBase$1.class";

    // -------------------------------------------------------------------------------------||
    // Required Implementations -----------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.formatter.FormatterTestBase#getFormatter()
     */
    @Override
    Formatter getFormatter() {
        return Formatters.VERBOSE;
    }

    /**
     * {@inheritDoc}
     *
     * @see org.jboss.shrinkwrap.impl.base.formatter.FormatterTestBase#getExpectedOutput()
     */
    @Override
    String getExpectedOutput() {
        return EXPECTED_OUTPUT;
    }
}

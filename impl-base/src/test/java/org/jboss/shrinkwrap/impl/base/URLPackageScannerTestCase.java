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
package org.jboss.shrinkwrap.impl.base;

import java.io.IOException;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.junit.Test;

/**
 * URLPackageScannerTestCase for SHRINKWRAP-90.
 *
 * Asserts parameter validation for more user friendly errors on invalid parameters.
 *
 * @author <a href="mailto:lightguard.jp@gmail.com">Jason Porter</a>
 * @version $Revision$
 */
public class URLPackageScannerTestCase {
    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionNullPackage() {
        URLPackageScanner.newInstance(true, URLPackageScannerTestCase.class.getClassLoader(),
            new URLPackageScanner.Callback() {
                @Override
                public void classFound(String className, Asset asset) {
                }
            }, null);
    }
}

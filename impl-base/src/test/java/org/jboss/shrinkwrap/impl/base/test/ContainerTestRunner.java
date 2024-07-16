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
package org.jboss.shrinkwrap.impl.base.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * A JUnit running for only running the *Container tests relevant to the Archive Type under test.
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ContainerTestRunner extends BlockJUnit4ClassRunner {

    public ContainerTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        return new FilterSupportedArchiveTypes(getTestClass(), super.computeTestMethods()).filter();
    }

    private class FilterSupportedArchiveTypes {

        private final List<FrameworkMethod> frameworkMethods;
        private final TestClass testClass;

        public FilterSupportedArchiveTypes(TestClass testClass, List<FrameworkMethod> frameworkMethods) {
            this.testClass = testClass;
            this.frameworkMethods = frameworkMethods;
        }

        /**
         * Filter Test methods based on matching ArchiveType inheritance.
         *
         * @return The Filtered List
         */
        public List<FrameworkMethod> filter() {
            ArchiveType archiveUnderTest = testClass.getJavaClass().getAnnotation(ArchiveType.class);
            if (archiveUnderTest == null) {
                throw new RuntimeException("TestClass[" + testClass.getJavaClass().getName() + "] is missing "
                    + ArchiveType.class.getName() + " annotation. " + "This describes the Type being tested.");
            }

            List<FrameworkMethod> testMethods = new ArrayList<>();
            for (FrameworkMethod testMethod : frameworkMethods) {
                ArchiveType archiveType = testMethod.getAnnotation(ArchiveType.class);
                if (archiveType != null) {
                    if (supportsArchiveType(archiveType.value(), archiveUnderTest.value())) {
                        testMethods.add(testMethod);
                    }
                } else {
                    testMethods.add(testMethod);
                }
            }
            return testMethods;
        }
    }

    private boolean supportsArchiveType(Class<?> archiveType, Class<?> testCaseArchiveType) {
        Class<?>[] supportedInterfaces = testCaseArchiveType.getInterfaces();
        for (Class<?> supportedInterface : supportedInterfaces) {
            if (archiveType.isAssignableFrom(supportedInterface)) {
                return true;
            }
        }
        return false;
    }
}

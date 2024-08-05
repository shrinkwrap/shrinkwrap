/*
 * JBoss, Home of Professional Open Source
 * Copyright 2024, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import java.util.Optional;

/**
 * @author Katarina Hermanova (c) 2024 Red Hat, Inc.
 */
public class ContainerTestExtension implements ExecutionCondition {

    /**
     * Allow the test to run if the method annotation is null or matches the class annotation.
     *
     * @param context the current extension context; never {@code null}
     * @return ConditionEvaluationResult - enable or disable test in question based on the condition
     */
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        Optional<ArchiveType> classAnnotation = context.getTestClass().map(cls -> cls.getAnnotation(ArchiveType.class));
        Optional<ArchiveType> methodAnnotation = context.getTestMethod().map(mth -> mth.getAnnotation(ArchiveType.class));

        if (methodAnnotation.isPresent()) {
            if (classAnnotation.isPresent() && supportsArchiveType(methodAnnotation.get().value(), classAnnotation.get().value())) {
                return ConditionEvaluationResult.enabled("Method annotation matches class annotation.");
            } else {
                return ConditionEvaluationResult.disabled("Method annotation does not match class annotation.");
            }
        }
        return ConditionEvaluationResult.enabled("No method annotation present.");
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
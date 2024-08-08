import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

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

/**
 * Ensures that we can add classes in the default package to an archive. This test is also in the default package due to
 * compiler restrictions on importing from default package.
 *
 * SHIRNKWRAP-143
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class DefaultPackageAddTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(DefaultPackageAddTestCase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    private ArchivePath classInDefaultPackagePath;
    private ArchivePath innerClassInDefaultPackagePath;

    // -------------------------------------------------------------------------------------||
    // Fixtures ---------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @BeforeEach
    public void setupPaths() {
        classInDefaultPackagePath = ArchivePaths.create("/ClassInDefaultPackage.class");
        innerClassInDefaultPackagePath = ArchivePaths.create("/ClassInDefaultPackage$InnerClassInDefaultPackage.class");
    }

    @AfterEach
    public void cleanupPaths() {
        classInDefaultPackagePath = null;
        innerClassInDefaultPackagePath = null;
    }

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that classes from the default package may be added
     *
     * SHRINKWRAP-143
     */
    @Test
    public void canAddClassFromDefaultPackage() {
        // Create an archive with Classes from the default package
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "test.jar").addClass(
            ClassInDefaultPackage.class);
        log.info(archive.toString(true));

        assertClassesWereAdded(archive);
    }

    /**
     * Makes sure classes in the default package, and only in the default package, are added.
     *
     * SHRINKWRAP-233, SHRINKWRAP-302
     *
     * DISABLED (SHRINKWRAP-543) - After replacing JUnit 4 with JUnit 5 the test is failing - size of archive should
     * be 3 but is 4 (module-info.class was added)
     */
    @Disabled
    @Test
    public void testAddDefaultPackage() {
        JavaArchive archive = ShrinkWrap.create(JavaArchive.class);
        archive.addDefaultPackage();

        assertClassesWereAdded(archive);

        /*
         * It should have added only the following three classes: 1. /DefaultPackageAddTestCase.class 2.
         * /ClassInDefaultPackage.class 3. /ClassInDefaultPackage$InnerClassInDefaultPackage.class
         */
        Assertions.assertEquals(3, archive.getContent().size(), "Not the expected number of assets added to the archive" + ", CONTENT: " + archive.getContent());
    }

    private void assertClassesWereAdded(JavaArchive archive) {
        Assertions.assertTrue(archive.contains(classInDefaultPackagePath),
                "Class in default package was not added to archive");
        Assertions.assertTrue(archive.contains(innerClassInDefaultPackagePath),
                "Inner class in default package was not added to archive");
    }

}

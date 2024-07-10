package org.jboss.shrinkwrap.impl.base.path;

import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

/**
 * PathUtilTestCase
 * <p>
 * Test cases to ensure the path utilities are working as expected.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class PathUtilTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(PathUtilTestCase.class.getName());

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures that the preceding slash is removed as requested
     */
    @Test
    public void testRemovePrecedingSlash() {
        log.info("testRemovePrecedingSlash");
        final String precedingSlash = "/test/something";
        final String expected = precedingSlash.substring(1);
        final String result = PathUtil.optionallyRemovePrecedingSlash(precedingSlash);
        Assertions.assertEquals(expected, result,
                "Call to remove preceding slash should return everything in input except the first slash");
    }

    /**
     * Ensures that the preceding slash is removed as requested
     */
    @Test
    public void testRemovePrecedingSlashWithNoPrecedingSlashEqualToInput() {
        log.info("testRemovePrecedingSlash");
        final String noPrecedingSlash = "test/something";
        final String result = PathUtil.optionallyRemovePrecedingSlash(noPrecedingSlash);
        Assertions.assertEquals(noPrecedingSlash, result,
                "Call to remove preceding slash on input with no preceding slash should return equal by value to input");
    }

    /**
     * Ensures that a slash may be prepended to a given String
     */
    @Test
    public void testPrependSlash() {
        log.info("testRemovePrecedingSlash");
        final String noPrecedingSlash = "test/something";
        final String expected = ArchivePath.SEPARATOR + noPrecedingSlash;
        final String result = PathUtil.optionallyPrependSlash(noPrecedingSlash);
        Assertions.assertEquals(expected, result, "Call to prepend a slash failed");
    }

    /**
     * Ensures that optionally prepending a slash upon a String already prefixed with one returns a no-op
     */
    @Test
    public void testNoOpPrependSlash() {
        log.info("testRemovePrecedingSlash");
        final String precedingSlash = "/test/something";
        final String result = PathUtil.optionallyPrependSlash(precedingSlash);
        Assertions.assertEquals(precedingSlash, result, "Call to optionally prepend a slash upon input with slash prefix should return no-op");
    }

    /**
     * Ensures that a slash may be appended to a given String
     */
    @Test
    public void testAppendSlash() {
        log.info("testRemovePrecedingSlash");
        final String noFollowingSlash = "test/something";
        final String expected = noFollowingSlash + ArchivePath.SEPARATOR;
        final String result = PathUtil.optionallyAppendSlash(noFollowingSlash);
        Assertions.assertEquals(expected, result, "Call to append a slash failed");
    }

    /**
     * Ensures that optionally appending a slash upon a String already suffixed with one returns a no-op
     */
    @Test
    public void testNoOpAppendSlash() {
        log.info("testRemovePrecedingSlash");
        final String followingSlash = "/test/something/";
        final String result = PathUtil.optionallyAppendSlash(followingSlash);
        Assertions.assertEquals(followingSlash, result,
                "Call to optionally append a slash upon input with slash suffix should return no-op");
    }

    /**
     * Ensures that an absolute path may be converted to full relative directory form
     */
    @Test
    public void testAdjustToRelativeDirectoryContext() {
        log.info("testRemovePrecedingSlash");
        final String absoulteWithoutTrailingSlash = "/test/something";
        final String expected = absoulteWithoutTrailingSlash.substring(1) + ArchivePath.SEPARATOR;
        final String result = PathUtil.adjustToRelativeDirectoryContext(absoulteWithoutTrailingSlash);
        Assertions.assertEquals(expected, result,
                "Adjusting to relative form should strip preceding slash and append a trailing one");
    }

    /**
     * Ensures that a relative path may be converted to full absolute directory form
     */
    @Test
    public void testAdjustToAbsoluteDirectoryContext() {
        log.info("testRemovePrecedingSlash");
        final String relativeWithoutTrailingSlash = "test/something";
        final String expected = ArchivePath.SEPARATOR + relativeWithoutTrailingSlash + ArchivePath.SEPARATOR;
        final String result = PathUtil.adjustToAbsoluteDirectoryContext(relativeWithoutTrailingSlash);
        Assertions.assertEquals(expected, result,
                "Adjusting to absolute form should prepend preceding slash and append a trailing one");
    }

    /**
     * Ensures that an absolute form may be composed from a relative context and base
     */
    @Test
    public void testComposeAbsoulteContext() {
        log.info("testComposeAbsoulteContext");
        final String base = "something";
        final String context = "somethingunder";
        final String expected = ArchivePath.SEPARATOR + base + ArchivePath.SEPARATOR + context;
        final String result = PathUtil.composeAbsoluteContext(base, context);
        Assertions.assertEquals(expected, result, "Composing an absolute context from base and context did not succeed");
    }

    /**
     * Ensures the contract of {@link PathUtil#getParent(ArchivePath)} is intact
     */
    @Test
    public void testParent() {
        // Log
        log.info("testParent");

        // Create new paths
        final String rootString = "/";
        final String subpathString = "subpath/";
        final String contextString = "context";
        final String contextWithFollowingSlashString = "context/";
        final ArchivePath root = ArchivePaths.create(rootString);
        final ArchivePath subpath = ArchivePaths.create(subpathString);
        final ArchivePath context = ArchivePaths.create(subpath, contextString);
        final ArchivePath contextWithFollowingSlash = new BasicPath(subpath, contextWithFollowingSlashString);

        // Test
        Assertions.assertEquals(subpath, PathUtil.getParent(context),
                "The parent of the context path should be equal to the initial subpath");
        Assertions.assertEquals(subpath, PathUtil.getParent(contextWithFollowingSlash),
                "The parent of the context path with a following slash should be equal to the initial subpath");
        Assertions.assertEquals(root, PathUtil.getParent(subpath), "The parent of the subpath should be the root");
        Assertions.assertNull(PathUtil.getParent(root), "The parent of the root should be null");
    }

}

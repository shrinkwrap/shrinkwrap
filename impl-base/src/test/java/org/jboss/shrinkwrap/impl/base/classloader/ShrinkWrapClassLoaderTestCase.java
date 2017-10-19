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
package org.jboss.shrinkwrap.impl.base.classloader;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.GenericArchive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.classloader.ShrinkWrapClassLoader;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.io.IOUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures the {@link ShrinkWrapClassLoader} is working as contracted
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public class ShrinkWrapClassLoaderTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Logger
     */
    private static final Logger log = Logger.getLogger(ShrinkWrapClassLoaderTestCase.class.getName());

    /**
     * Class to be accessed via a ShrinkWrap ClassLoader
     */
    private static final Class<?> applicationClassLoaderClass = LoadedTestClass.class;

    /**
     * Archive to be read via a {@link ShrinkWrapClassLoaderTestCase#shrinkWrapClassLoader}
     */
    private static final JavaArchive archive = ShrinkWrap.create(JavaArchive.class).addClass(
        applicationClassLoaderClass);

    // -------------------------------------------------------------------------------------||
    // Instance Members -------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * ClassLoader used to load {@link ShrinkWrapClassLoaderTestCase#applicationClassLoaderClass}
     */
    private ClassLoader shrinkWrapClassLoader;

    // -------------------------------------------------------------------------------------||
    // Lifecycle --------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Creates the {@link ShrinkWrapClassLoaderTestCase#shrinkWrapClassLoader} used to load classes from an
     * {@link Archive}. The {@link ClassLoader} will be isolated from the application classpath by specifying a null
     * parent explicitly.
     */
    @Before
    public void createClassLoader() {
        shrinkWrapClassLoader = new ShrinkWrapClassLoader((ClassLoader) null, archive);
    }

    /**
     * Closes resources associated with the {@link ShrinkWrapClassLoaderTestCase#shrinkWrapClassLoader}
     */
    @After
    public void closeClassLoader() {
        if (shrinkWrapClassLoader instanceof Closeable) {
            try {
                ((Closeable) shrinkWrapClassLoader).close();
            } catch (final IOException e) {
                log.warning("Could not close the " + shrinkWrapClassLoader + ": " + e);
            }
        }
    }

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures we can load a Class instance from the {@link ShrinkWrapClassLoader}
     */
    @Test
    public void shouldBeAbleToLoadClassFromArchive() throws ClassNotFoundException {
        // Load the test class from the CL
        final Class<?> loadedTestClass = Class.forName(applicationClassLoaderClass.getName(), false,
            shrinkWrapClassLoader);

        final ClassLoader loadedTestClassClassLoader = loadedTestClass.getClassLoader();
        log.info("Got " + loadedTestClass + " from " + loadedTestClassClassLoader);

        // Assertions
        Assert.assertNotNull("Test class could not be found via the ClassLoader", loadedTestClass);

        Assert.assertSame("Test class should have been loaded via the archive ClassLoader", shrinkWrapClassLoader,
            loadedTestClassClassLoader);

        Assert.assertNotSame("Class Loaded from the CL should not be the same as the one on the appCL",
            loadedTestClass, applicationClassLoaderClass);

    }

    /**
     * Ensures that we can open up directory content as obtained via a {@link URL} from the
     * {@link ShrinkWrapClassLoader} (ie. should return null, not throw an exception)
     *
     * SHRINKWRAP-306
     */
    @Test
    public void shouldBeAbleToOpenStreamOnDirectoryUrl() throws IOException {
        // Make a new Archive with some content in a directory
        final String nestedResourceName = "nested/test";
        final Asset testAsset = new StringAsset("testContent");
        final GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(testAsset, nestedResourceName);

        // Make a CL to load the content
        final ClassLoader swCl = new ShrinkWrapClassLoader(archive);

        // Get the URL to the parent directory
        final URL nestedResourceUrl = swCl.getResource(nestedResourceName);
        final URL nestedResourceUpALevelUrl = new URL(nestedResourceUrl, "../");

        // openStream on the URL to the parent directory; should return null, not throw an exception
        final InputStream in = nestedResourceUpALevelUrl.openStream();
        Assert.assertNull("URLs pointing to a directory should openStream as null", in);
    }

    /**
     * Ensures that we can open up an asset that doesn't exist via a {@link URL} from the {@link ShrinkWrapClassLoader}
     * (ie. should throw {@link FileNotFoundException}
     *
     * SHRINKWRAP-308
     */
    @Test(expected = FileNotFoundException.class)
    public void shouldNotBeAbleToOpenStreamOnNonexistantAsset() throws IOException {
        // Make a new Archive with some content in a directory
        final String nestedResourceName = "nested/test";
        final Asset testAsset = new StringAsset("testContent");
        final GenericArchive archive = ShrinkWrap.create(GenericArchive.class).add(testAsset, nestedResourceName);

        // Make a CL to load the content
        final ClassLoader swCl = new ShrinkWrapClassLoader(archive);

        // Get the URL to something that doesn't exist
        final URL nestedResourceUrl = swCl.getResource(nestedResourceName);
        final URL nestedResourceThatDoesntExistUrl = new URL(nestedResourceUrl, "../fake");

        // openStream on the URL that doesn't exist should throw FNFE
        nestedResourceThatDoesntExistUrl.openStream();
    }

    /**
     * Ensures we can load a resource by name from the {@link ShrinkWrapClassLoader}
     */
    @Test
    public void shouldBeAbleToLoadResourceFromArchive() {

        // Load the class as a resource
        final URL resource = shrinkWrapClassLoader.getResource(getResourceNameOfClass(applicationClassLoaderClass));

        // Assertions
        Assert.assertNotNull(resource);

    }

    /**
     * SHRINKWRAP-237: Reading the same resource multiple times cause IOException
     */
    @Test
    public void shouldBeAbleToLoadAResourceFromArchiveMultipleTimes() throws Exception {
        String resourceName = getResourceNameOfClass(applicationClassLoaderClass);

        // Load the class as a resource
        URL resource = shrinkWrapClassLoader.getResource(resourceName);

        // Assertions
        Assert.assertNotNull(resource);

        // Read the stream until EOF
        IOUtil.copyWithClose(resource.openStream(), new ByteArrayOutputStream());

        // Load the class as a resource for the second time
        resource = shrinkWrapClassLoader.getResource(resourceName);

        // Assertions
        Assert.assertNotNull(resource);

        // SHRINKWRAP-237: This throws IOException: Stream closed
        IOUtil.copyWithClose(resource.openStream(), new ByteArrayOutputStream());
    }

    /**
     * SHRINKWRAP-369 ShrinkWrapClassLoader does not find service provider in WAR
     */
    @Test
    public void shouldBeAbleToFindServiceProviderInWAR() throws Exception {
       final WebArchive archive = ShrinkWrap.create(WebArchive.class).addAsServiceProvider(Cloneable.class, String.class);
       final ShrinkWrapClassLoader cl = new ShrinkWrapClassLoader((ClassLoader) null, archive);
       final Enumeration<URL> found = cl.findResources("META-INF/services/java.lang.Cloneable");

       Assert.assertTrue("Service provider not found in WAR", found.hasMoreElements());
     }

    @Test
    public void shouldBeAbleToFindServiceProviderInWARWithSlash() throws Exception {
       final WebArchive archive = ShrinkWrap.create(WebArchive.class).addAsServiceProvider(Cloneable.class, String.class);
       final ShrinkWrapClassLoader cl = new ShrinkWrapClassLoader((ClassLoader) null, archive);
       final Enumeration<URL> found = cl.findResources("/META-INF/services/java.lang.Cloneable");

       Assert.assertTrue("Service provider not found in WAR", found.hasMoreElements());
     }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Methods ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Obtains the resource name for a given class
     */
    private static String getResourceNameOfClass(final Class<?> clazz) {
        assert clazz != null : "clazz must be specified";
        final StringBuilder sb = new StringBuilder();
        final String className = clazz.getName().replace('.', '/');
        sb.append(className);
        sb.append(".class");
        return sb.toString();
    }
}
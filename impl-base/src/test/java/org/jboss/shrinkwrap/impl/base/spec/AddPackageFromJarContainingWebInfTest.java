package org.jboss.shrinkwrap.impl.base.spec;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import junit.framework.Assert;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassA;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Ken Finnigan
 */
public class AddPackageFromJarContainingWebInfTest {

    private Domain domain;

    private Path temp;

    @Before
    public void setUp() throws IOException {

        ConfigurationBuilder builder = new ConfigurationBuilder();
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "my.jar");
        jar.addClass(DummyClassA.class);
        jar.add(new StringAsset("stuff"), "WEB-INF/classes/stuff.txt");

        this.temp = Files.createTempFile("test", ".jar");
        jar.as(ZipExporter.class).exportTo(temp.toFile(), true);
        URL jarUrl = temp.toUri().toURL();

        List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();

        URLClassLoader jarCl = new URLClassLoader(new URL[]{jarUrl}, null);
        classLoaders.add(jarCl);

        ClassLoader shrinkwrapCl = new FilteringClassLoader(this.getClass().getClassLoader());
        classLoaders.add(shrinkwrapCl);

        builder.classLoaders(classLoaders);

        // Create a domain that includes the `donotchange` classes within a .war
        // and the rest, excluding `donotchange` from the regular app classloader.
        this.domain = ShrinkWrap.createDomain(builder.build());
    }

    @After
    public void tearDown() throws IOException {
        Files.delete( this.temp );
    }

    @Test
    public void testAddPackage() {
        // This archive's classloaders should be the app classloader (without `donotchange`)
        // and an .war-based classloader including the `donotchange` within WEB-INF/classes/...

        WebArchive war = this.domain.getArchiveFactory().create(WebArchive.class);
        war.addPackage(DummyClassA.class.getPackage());
        Assert.assertNotNull( war.get( "/WEB-INF/classes/" + DummyClassA.class.getName().replace( '.', '/' ) + ".class" ) );
    }

    /** Classloader to ensure the donotchange bits are not loaded through
     * the app-classloader, while still enabling the default ShrinkWrap
     * bits to be found through the junit test.
     */
    private static class FilteringClassLoader extends ClassLoader {

        public FilteringClassLoader(ClassLoader parent) {
            super(parent);
        }

        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.contains("donotchange")) {
                return null;
            }
            return super.loadClass(name);
        }

        @Override
        public Enumeration<URL> getResources(String name) throws IOException {
            if (name.contains("donotchange")) {
                return Collections.emptyEnumeration();
            }
            return super.getResources(name);
        }

    }
}

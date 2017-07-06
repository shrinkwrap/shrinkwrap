package org.jboss.shrinkwrap.impl.base.spec;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassA;
import org.junit.Test;

/**
 * @author Ken Finnigan
 */
public class AddPackageFromWarWithNonRootClassloaderTestCase extends AddPackageTestBase {

    @Test
    public void testAddPackage() {
        // This archive's classloaders should be the app classloader (without `donotchange`)
        // and an .war-based classloader including the `donotchange` within WEB-INF/classes/...

        JavaArchive jar = this.domain.getArchiveFactory().create(JavaArchive.class);
        jar.addPackage("org.jboss.shrinkwrap.impl.base.spec.donotchange" );
        Assert.assertNotNull( jar.get( "/" + DummyClassA.class.getName().replace( '.', '/' ) + ".class" ) );
    }

    @Override
    protected Archive<?> buildArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "my.war");
        war.addClass(DummyClassA.class);
        return war;
    }

    @Override
    protected URLClassLoader buildArchiveClassLoader(URL archiveUrl) {
        return new NonRootURLClassloader(new URL[]{archiveUrl}, null);
    }

    private static class NonRootURLClassloader extends URLClassLoader {
        
        public NonRootURLClassloader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }
        
        @Override
        public Enumeration<URL> findResources(String name) throws IOException {
            return super.findResources("WEB-INF/classes/" + name);
        }
    }
}

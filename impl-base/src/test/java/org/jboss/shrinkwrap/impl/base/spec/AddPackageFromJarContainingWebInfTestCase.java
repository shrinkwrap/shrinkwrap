package org.jboss.shrinkwrap.impl.base.spec;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassA;
import org.junit.Test;

/**
 * @author Ken Finnigan
 */
public class AddPackageFromJarContainingWebInfTestCase extends AddPackageTestBase {

    @Override
    protected Archive<?> buildArchive() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "my.jar");
        jar.addClass(DummyClassA.class);
        jar.add(new StringAsset("stuff"), "WEB-INF/classes/stuff.txt");
        return jar;
    }

    @Test
    public void testAddPackage() {
        // This archive's classloaders should be the app classloader (without `donotchange`)
        // and an .war-based classloader including the `donotchange` within WEB-INF/classes/...

        WebArchive war = this.domain.getArchiveFactory().create(WebArchive.class);
        war.addPackage(DummyClassA.class.getPackage());
        Assert.assertNotNull( war.get( "/WEB-INF/classes/" + DummyClassA.class.getName().replace( '.', '/' ) + ".class" ) );
    }
}

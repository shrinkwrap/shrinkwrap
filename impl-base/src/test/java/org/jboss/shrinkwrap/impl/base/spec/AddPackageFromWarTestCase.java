package org.jboss.shrinkwrap.impl.base.spec;

import org.junit.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.spec.donotchange.DummyClassA;
import org.junit.Test;

/**
 * @author Bob McWhirter
 */
public class AddPackageFromWarTestCase extends AddPackageTestBase {

    @Override
    protected Archive<?> buildArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "my.war");
        war.addClass(DummyClassA.class);
        return war;
    }

    @Test
    public void testAddPackage() {
        // This archive's classloaders should be the app classloader (without `donotchange`)
        // and an .war-based classloader including the `donotchange` within WEB-INF/classes/...

        JavaArchive jar = this.domain.getArchiveFactory().create(JavaArchive.class);
        jar.addPackage("org.jboss.shrinkwrap.impl.base.spec.donotchange" );
        Assert.assertNotNull( jar.get( "/" + DummyClassA.class.getName().replace( '.', '/' ) + ".class" ) );
    }
}

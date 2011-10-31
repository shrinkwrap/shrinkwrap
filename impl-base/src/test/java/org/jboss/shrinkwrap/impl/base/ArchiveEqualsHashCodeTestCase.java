package org.jboss.shrinkwrap.impl.base;

import java.io.File;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

/**
 * Tests that verify {@link Archive} implement the semantics of equals and hashCode correctly.
 *
 * SHRINKWRAP-181
 *
 * @author <a href="mailto:chris.wash@gmail.com">Chris Wash</a>
 * @version $Revision: $
 *
 */
public class ArchiveEqualsHashCodeTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    // These two files have the same contents, used for testing equality.

    private static final String TEST_ZIP_1 = "org/jboss/shrinkwrap/impl/base/importer/test.zip";
    private static final String TEST_ZIP_2 = "org/jboss/shrinkwrap/impl/base/asset/test.zip";

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    @Test
    public void archiveEqualsShouldReturnTrueWhenNameAndContentsAreEqual() throws Exception {

        final File testFile = TestIOUtil.createFileFromResourceName(TEST_ZIP_1);
        final File testFile2 = TestIOUtil.createFileFromResourceName(TEST_ZIP_2);

        final Archive<?> archive = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile);
        final Archive<?> archive2 = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile2);

        Assert.assertEquals("Archives were not equal, but should be.", archive, archive2);

    }

    @Test
    public void archiveEqualsShouldReturnTrueWhenEqualByReference() throws Exception {

        // reference the same file
        final File testFile = TestIOUtil.createFileFromResourceName(TEST_ZIP_1);
        final File testFile2 = TestIOUtil.createFileFromResourceName(TEST_ZIP_1);

        final Archive<?> archive = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile);
        final Archive<?> archive2 = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile2);

        Assert.assertEquals("Archives were not equal, but should be.", archive, archive2);
    }

    @Test
    public void archiveEqualsShouldReturnFalseWhenArchivesContentsAreNotEqual() throws Exception {
        final String archiveName = "test.zip";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, archiveName);

        final File testFile = TestIOUtil.createFileFromResourceName(TEST_ZIP_1);
        final JavaArchive archive2 = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile);

        Assert
            .assertFalse("Archives were equal, but should not have been - contents differ.", archive.equals(archive2));

    }

    @Test
    public void archiveEqualsShouldReturnFalseWhenArchivesNamesAreNotEqual() throws Exception {

        final String archiveName = "test.war";
        final String archiveName2 = "test.jar";

        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, archiveName);
        final JavaArchive archive2 = ShrinkWrap.create(JavaArchive.class, archiveName2);

        Assert.assertFalse("Archives were equal, but should not have been - names differ.", archive.equals(archive2));

    }

    @Test
    public void enterpriseArchiveEqualsShouldReturnTrueWhenNameAndContentsAreEqual() throws Exception {

        final File testFile1 = TestIOUtil.createFileFromResourceName(TEST_ZIP_1);
        final File testFile2 = TestIOUtil.createFileFromResourceName(TEST_ZIP_2);

        final EnterpriseArchive ear1 = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, testFile1);
        final EnterpriseArchive ear2 = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, testFile2);

        Assert.assertEquals("EnterpriseArchive instances were not equal, but should be.", ear1, ear2);

    }

    @Test
    public void javaArchiveShouldEqualEnterpriseArchiveWhenNameAndContentsAreEqual() throws Exception {
        final File testFile1 = TestIOUtil.createFileFromResourceName(TEST_ZIP_1);
        final File testFile2 = TestIOUtil.createFileFromResourceName(TEST_ZIP_2);

        final JavaArchive jar = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile1);
        final EnterpriseArchive ear = ShrinkWrap.createFromZipFile(EnterpriseArchive.class, testFile2);

        Assert.assertEquals("JavaArchive and EnterpriseArchive were not equal, but should be.", jar, ear);

    }

    /**
     * Calls to hashCode with the same value should always hash to the same result.
     *
     * @throws Exception
     */
    @Test
    public void archiveHashCodeShouldBeIdempotent() throws Exception {
        final File testFile1 = TestIOUtil.createFileFromResourceName(TEST_ZIP_1);

        final JavaArchive jar = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile1);

        Assert.assertEquals("hashCode did not return consistent value for same instance", jar.hashCode(),
            jar.hashCode());
    }

}

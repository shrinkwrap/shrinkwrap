package org.jboss.shrinkwrap.impl.nio.file;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.logging.Logger;

import junit.framework.Assert;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.nio.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * ShrinkWrap implementation of {@link BasicFileAttributesView}; not all operations are supported
 * 
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class FileAttributesViewTestCase {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(FileAttributesTestCase.class.getName());

    private FileSystem fs;

    private JavaArchive archive;

    @Before
    public void createStore() throws IOException {
        // Setup
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        final FileSystem fs = ShrinkWrapFileSystems.newFileSystem(archive);

        // Set
        this.archive = archive;
        this.fs = fs;
    }

    @After
    public void closeFs() throws IOException {
        this.fs.close();
    }

    @Test
    public void getShrinkwrapAttributesView() {
        BasicFileAttributeView attributeView = getAttributesView("path");
        Assert.assertEquals("Attributes view wrong name", ShrinkWrapFileAttributeView.class.getSimpleName(),
            attributeView.name());
        Assert.assertTrue("Attribute view is not an instance of ShrinkWrapFileAttributeView",
            attributeView instanceof ShrinkWrapFileAttributeView);
    }

    @Test
    public void readAttributes() throws IOException {
        ShrinkWrapFileAttributeView attributeView = getAttributesView("path");
        Assert.assertNotNull("Attribute view should not be null", attributeView);

        BasicFileAttributes attributes = attributeView.readAttributes();
        // the attributes are tested in FileAttributesTestCase
        Assert.assertTrue("Atrributes are not instance of ShrinkWrapFileAttributes",
            attributes instanceof ShrinkWrapFileAttributes);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setTimes() throws IOException {
        BasicFileAttributeView attributeView = getAttributesView("path");
        FileTime fileTime = FileTime.fromMillis(0);
        attributeView.setTimes(fileTime, fileTime, fileTime);
    }

    private ShrinkWrapFileAttributeView getAttributesView(final String pathName) {
        archive.add(EmptyAsset.INSTANCE, pathName);
        return Files.getFileAttributeView(this.fs.getPath(pathName), ShrinkWrapFileAttributeView.class,
            (LinkOption) null);
    }
}

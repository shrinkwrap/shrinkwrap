package org.jboss.shrinkwrap.impl.base.nio2.file;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.nio2.file.ShrinkWrapFileSystems;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:mmatloka@gmail.com">Michal Matloka</a>
 */
public class FileAttributesViewTestCase {

    private FileSystem fs;

    private JavaArchive archive;

    @BeforeEach
    public void createStore() throws IOException {
        // Setup
        final String name = "test.jar";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, name);
        final FileSystem fs = ShrinkWrapFileSystems.newFileSystem(archive);

        // Set
        this.archive = archive;
        this.fs = fs;
    }

    @AfterEach
    public void closeFs() throws IOException {
        this.fs.close();
    }

    @Test
    public void getShrinkwrapAttributesView() {
        BasicFileAttributeView attributeView = getAttributesView("path");
        Assertions.assertEquals(ShrinkWrapFileAttributeView.class.getSimpleName(), attributeView.name(), "Attributes view wrong name");
        Assertions.assertInstanceOf(ShrinkWrapFileAttributeView.class, attributeView,
                "Attribute view is not an instance of ShrinkWrapFileAttributeView");
    }

    @Test
    public void readAttributes() {
        ShrinkWrapFileAttributeView attributeView = getAttributesView("path");
        Assertions.assertNotNull(attributeView, "Attribute view should not be null");

        BasicFileAttributes attributes = attributeView.readAttributes();
        // the attributes are tested in FileAttributesTestCase
        Assertions.assertInstanceOf(ShrinkWrapFileAttributes.class, attributes,
                "Atrributes are not instance of ShrinkWrapFileAttributes");
    }

    @Test
    public void setTimes() {
        BasicFileAttributeView attributeView = getAttributesView("path");
        FileTime fileTime = FileTime.fromMillis(0);
        Assertions.assertThrows(UnsupportedOperationException.class,
                () -> attributeView.setTimes(fileTime, fileTime, fileTime));
    }

    private ShrinkWrapFileAttributeView getAttributesView(final String pathName) {
        archive.add(EmptyAsset.INSTANCE, pathName);
        return Files.getFileAttributeView(this.fs.getPath(pathName), ShrinkWrapFileAttributeView.class,
            (LinkOption) null);
    }
}

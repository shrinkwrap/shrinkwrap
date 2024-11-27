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
package org.jboss.shrinkwrap.impl.base;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchiveFactory;
import org.jboss.shrinkwrap.api.ArchiveFormat;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Assignable;
import org.jboss.shrinkwrap.api.Configuration;
import org.jboss.shrinkwrap.api.ConfigurationBuilder;
import org.jboss.shrinkwrap.api.Domain;
import org.jboss.shrinkwrap.api.ExtensionLoader;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.UnknownExtensionTypeException;
import org.jboss.shrinkwrap.api.formatter.Formatter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.ResourceAdapterArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.impl.base.container.ContainerBase;
import org.jboss.shrinkwrap.impl.base.importer.ZipContentAssertionDelegate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests ensuring that the static entry point {@link ShrinkWrap} is working as contracted.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ShrinkWrapTestCase {

    // -------------------------------------------------------------------------------------||
    // Class Members ----------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Delegate for performing ZIP content assertions
     */
    private static final ZipContentAssertionDelegate delegate = new ZipContentAssertionDelegate();

    /**
     * The name of a simple TXT file
     */
    private static final String NAME_FILE_NON_ZIP = "nonzipfile.txt";

    // -------------------------------------------------------------------------------------||
    // Tests ------------------------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * Ensures we can create a new archive under the default {@link Domain}
     */
    @Test
    public void createNewArchiveUnderDefaultDomain() {
        final String archiveName = "test.war";
        final JavaArchive archive = ShrinkWrap.create(JavaArchive.class, archiveName);

        // Test
        Assertions.assertNotNull(archive, "A archive should have been created");
        Assertions.assertEquals(archiveName, archive.getName(), "Should have the same name as given input");
    }

    /**
     * Ensures that we can create isolated {@link Domain}s
     */
    @Test
    public void createIsolatedDomains() {
        // Make a couple domains
        final Domain domain1 = ShrinkWrap.createDomain();
        final Domain domain2 = ShrinkWrap.createDomain();

        // Ensure they exist
        Assertions.assertNotNull(domain1, "Domain should exist");
        Assertions.assertNotNull(domain2, "Domain should exist");

        // Ensure they're not equal
        Assertions.assertNotSame(domain1, domain2, "Creation of domains should return new instances");

        // Ensure the underlying configs are not equal
        Assertions.assertNotSame(domain1.getConfiguration(), domain2.getConfiguration(),
                "Creation of domains should have unique / isolated configurations");
    }

    /**
     * Ensures that we can create a new {@link Domain} with explicit {@link Configuration}
     */
    @Test
    public void createDomainWithExplicitConfiguration() {
        // Define configuration properties
        final ExecutorService service = Executors.newSingleThreadExecutor();
        final ExtensionLoader loader = new MockExtensionLoader();

        // Create a new domain using these config props in a config
        final Domain domain = ShrinkWrap.createDomain(new ConfigurationBuilder().executorService(service)
            .extensionLoader(loader).build());

        // Test
        Assertions.assertEquals(service, domain.getConfiguration().getExecutorService(),
                ExecutorService.class.getSimpleName() + " specified was not contained in resultant " + Domain.class.getSimpleName());
        Assertions.assertEquals(loader, domain.getConfiguration().getExtensionLoader(),
                ExtensionLoader.class.getSimpleName() + " specified was not contained in resultant " + Domain.class.getSimpleName());
    }

    /**
     * Ensures that we can create a new {@link Domain} with explicit {@link ConfigurationBuilder}
     */
    @Test
    public void createDomainWithExplicitConfigurationBuilder() {
        // Define configuration properties
        final ExecutorService service = Executors.newSingleThreadExecutor();
        final ExtensionLoader loader = new MockExtensionLoader();

        // Create a new domain using these config props in a builder
        final Domain domain = ShrinkWrap.createDomain(new ConfigurationBuilder().executorService(service)
            .extensionLoader(loader));

        // Test
        Assertions.assertEquals(service, domain.getConfiguration().getExecutorService(),
                ExecutorService.class.getSimpleName() + " specified was not contained in resultant " + Domain.class.getSimpleName());
        Assertions.assertEquals(loader, domain.getConfiguration().getExtensionLoader(),
                ExtensionLoader.class.getSimpleName() + " specified was not contained in resultant " + Domain.class.getSimpleName());
    }

    /**
     * SHRINKWRAP-246 Ensures that the user may supply an explicit {@link ClassLoader} to the {@link Domain}, and this
     * will be used when going via the {@link ArchiveFactory} to load archive extensions.
     */
    @Test
    public void serviceExtensionLoadingUsesExplicitDomainClassLoader() {

        // Define the custom extension to attempt to load
        final Class<? extends Assignable> assignable = CustomArchive.class;

        // First ensure that we cannot get at the desired extension via traditional
        // means (i.e. TCCL)
        try {
            ShrinkWrap.create(assignable);
        } catch (final UnknownExtensionTypeException uete) {
            // Expected
        }

        // Define the ClassLoaders to search to use our new custom archive impl
        final List<ClassLoader> classLoaders = new ArrayList<>();
        classLoaders.add(TestSecurityActions.getThreadContextClassLoader());
        classLoaders.add(new URLClassLoader(new URL[] {}) {
            @Override
            public InputStream getResourceAsStream(final String name) {

                final String thisClassName = this.getClass().getName()
                    .substring(0, this.getClass().getName().length() - 1);
                final String searchName = "META-INF/services/" + thisClassName + CustomArchive.class.getSimpleName();
                if (name.equals(searchName)) {

                    return new ByteArrayInputStream(("implementingClassName=" + thisClassName
                        + CustomArchiveImpl.class.getSimpleName() + "\nextension=.jar").getBytes());
                } else {
                    return super.getResourceAsStream(name);
                }

            }
        });

        // Make a configuration and get the ArchiveFactory for it
        final ConfigurationBuilder builder = new ConfigurationBuilder().classLoaders(classLoaders);
        final ArchiveFactory factory = ShrinkWrap.createDomain(builder).getArchiveFactory();

        // Now try to get the archive we asked for
        final Assignable archive = factory.create(assignable);
        Assertions.assertNotNull(archive, "Archive using custom extension available in explicit CL should have been loaded");
    }

    public interface CustomArchive extends Assignable {

    }

    public static class CustomArchiveImpl extends GenericArchiveImpl implements CustomArchive {

        public CustomArchiveImpl(final Archive<?> delegate) {
            super(delegate);
        }

    }

    /**
     * Ensures we cannot create a new {@link Domain} with null {@link Configuration} specified
     */
    @Test
    public void newDomainRequiresConfiguration() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ShrinkWrap.createDomain((Configuration) null));
    }

    /**
     * Ensures we cannot create a new {@link Domain} with null {@link ConfigurationBuilder} specified
     */
    @Test
    public void newDomainRequiresConfigurationBuilder() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> ShrinkWrap.createDomain((ConfigurationBuilder) null));
    }

    /**
     * Ensures all calls to get the default domain return the same reference
     */
    @Test
    public void getDefaultDomain() {
        // Get the default domain twice
        final Domain domain1 = ShrinkWrap.getDefaultDomain();
        final Domain domain2 = ShrinkWrap.getDefaultDomain();

        // Ensure they exist
        Assertions.assertNotNull(domain1, "Domain should exist");
        Assertions.assertNotNull(domain2, "Domain should exist");

        // Ensure they're not equal
        Assertions.assertSame(domain1, domain2,
                "Obtaining the default domain should always return the same instance (idempotent operation)");
    }

    /**
     * Ensures that we may add extension overrides via the {@link ExtensionLoader} through some new {@link Domain}
     *
     */
    @Test
    public void shouldBeAbleToAddOverride() {
        final Domain domain = ShrinkWrap.createDomain();
        domain.getConfiguration().getExtensionLoader().addOverride(JavaArchive.class, MockJavaArchiveImpl.class);
        final JavaArchive archive = domain.getArchiveFactory().create(JavaArchive.class, "test.jar");

        Assertions.assertEquals(MockJavaArchiveImpl.class, archive.getClass(),
                "Should have overridden normal JavaArchive impl");
    }

    @Test
    public void shouldCreateArchiveWithCorrectExtensionForJavaArchive() {
        JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class);
        String archiveExtension = javaArchive.getName().substring(javaArchive.getName().lastIndexOf("."));
        Assertions.assertEquals(".jar", archiveExtension, "JavaArchive should have proper extension");
    }

    @Test
    public void shouldCreateJavaArchiveWithGivenName() {
        String archiveName = "testArchive";
        JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, archiveName);
        Assertions.assertEquals(archiveName, javaArchive.getName(), "JavaArchive should have given name");
    }

    @Test
    public void shouldCreateArchiveWithCorrectExtensionForWebArchive() {
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class);
        String archiveExtension = webArchive.getName().substring(webArchive.getName().lastIndexOf("."));
        Assertions.assertEquals(".war", archiveExtension, "WebArchive should have proper extension");
    }

    @Test
    public void shouldCreateWebArchiveWithGivenName() {
        String archiveName = "testArchive";
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, archiveName);
        Assertions.assertEquals(archiveName, webArchive.getName(), "WebArchive should have given name");
    }

    @Test
    public void shouldCreateArchiveWithCorrectExtensionForEnterpriseArchive() {
        EnterpriseArchive enterpriseArchive = ShrinkWrap.create(EnterpriseArchive.class);
        String archiveExtension = enterpriseArchive.getName().substring(enterpriseArchive.getName().lastIndexOf("."));
        Assertions.assertEquals(".ear", archiveExtension, "EnterpriseArchive should have proper extension");
    }

    @Test
    public void shouldCreateEnterpriseArchiveWithGivenName() {
        String archiveName = "testArchive";
        EnterpriseArchive enterpriseArchive = ShrinkWrap.create(EnterpriseArchive.class, archiveName);
        Assertions.assertEquals(archiveName, enterpriseArchive.getName(), "EnterpriseArchive should have given name");
    }

    @Test
    public void shouldCreateArchiveWithCorrectExtensionForResourceAdapterArchive() {
        ResourceAdapterArchive resourceAdapterArchive = ShrinkWrap.create(ResourceAdapterArchive.class);
        String archiveExtension = resourceAdapterArchive.getName().substring(
            resourceAdapterArchive.getName().lastIndexOf("."));
        Assertions.assertEquals(".rar", archiveExtension, "ResourceAdapterArchive should have proper extension");
    }

    @Test
    public void shouldCreateResourceAdapterArchiveWithGivenName() {
        String archiveName = "testArchive";
        ResourceAdapterArchive resourceAdapterArchive = ShrinkWrap.create(ResourceAdapterArchive.class, archiveName);
        Assertions.assertEquals(archiveName, resourceAdapterArchive.getName(),
                "ResourceAdapterArchive should have given name");
    }

    /**
     * Ensures we can create a new Archive from a ZIP file via {@link ShrinkWrap} convenience class
     */
    @Test
    public void shouldBeAbleToImportZipFileViaShrinkWrap() throws Exception {
        // Get the test file
        final File testFile = delegate.getExistingResource();

        // Make a new archive via the default domain
        final JavaArchive archive = ShrinkWrap.createFromZipFile(JavaArchive.class, testFile);

        // Assert
        Assertions.assertNotNull(archive, "Should not return a null archive");
        Assertions.assertEquals(testFile.getName(), archive.getName(),
                "name of the archive imported from a ZIP file was not as expected");
        delegate.assertContent(archive, testFile);
    }

    /**
     * Ensures we can create a new Archive from a ZIP file via an {@link ArchiveFactory}
     */
    @Test
    public void shouldBeAbleToImportZipFileViaArchiveFactory() throws Exception {
        // Get the test file
        final File testFile = delegate.getExistingResource();

        // Make a new archive via the default domain
        final JavaArchive archive = ShrinkWrap.getDefaultDomain().getArchiveFactory()
            .createFromZipFile(JavaArchive.class, testFile);

        // Assert
        Assertions.assertNotNull(archive, "Should not return a null archive");
        Assertions.assertEquals(testFile.getName(), archive.getName(),
                "name of the archive imported from a ZIP file was not as expected");
        delegate.assertContent(archive, testFile);
    }

    /**
     * Ensures that attempting to import as ZIP from a non-ZIP file leads to {@link IllegalArgumentException}
     *
     * @throws Exception
     */
    @Test
    public void importFromNonZipFileThrowsException() throws Exception {
        final File nonZipFile = new File(TestSecurityActions.getThreadContextClassLoader()
                .getResource(NAME_FILE_NON_ZIP).toURI());

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShrinkWrap.createFromZipFile(JavaArchive.class, nonZipFile));
    }

    /**
     * Ensures that attempting to import as ZIP from null file leads to {@link IllegalArgumentException}
     *
     */
    @Test
    public void importFromNullFileThrowsException() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShrinkWrap.createFromZipFile(JavaArchive.class, null));
    }

    /**
     * Ensures that attempting to import as ZIP from a {@link File} that doesn't exist leads to
     * {@link IllegalArgumentException}
     *
     */
    @Test
    public void importFromNonexistentFileThrowsException() {
        final File file = new File("fileThatDoesntExist.tmp");
        Assertions.assertFalse(file.exists(), "Error in test setup, file should not exist: " + file.getAbsolutePath());
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> ShrinkWrap.createFromZipFile(JavaArchive.class, null));
    }

    /**
     * Ensures that creating a default name with no extension configured for a specified type results in
     * {@link UnknownExtensionTypeException}
     *
     * SHRINKWRAP-163
     *
     */
    @Test
    public void shouldThrowExceptionOnNoConfiguredMappingForType() {
        Assertions.assertThrows(UnknownExtensionTypeException.class,
                () -> ShrinkWrap.create(MockAssignable.class));
    }

    // -------------------------------------------------------------------------------------||
    // Internal Helper Members ------------------------------------------------------------||
    // -------------------------------------------------------------------------------------||

    /**
     * A Mock {@link ExtensionLoader} used only in testing reference equality
     *
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     * @version $Revision: $
     */
    private static class MockExtensionLoader implements ExtensionLoader {
        @Override
        public <T extends Assignable> T load(final Class<T> extensionClass, final Archive<?> baseArchive) {
            return null;
        }

        @Override
        public <T extends Assignable> ExtensionLoader addOverride(final Class<T> extensionClass,
            final Class<? extends T> extensionImplClass) {
            return null;
        }

        @Override
        public <T extends Assignable> String getExtensionFromExtensionMapping(final Class<T> extensionClass) {
            return null;
        }

        @Override
        public <T extends Archive<T>> ArchiveFormat getArchiveFormatFromExtensionMapping(Class<T> extensionClass) {
            return null;
        }

    }

    /**
     * Mock Archive wrapper implementation
     *
     * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
     * @version $Revision: $
     */
    public static class MockJavaArchiveImpl extends ContainerBase<JavaArchive> implements JavaArchive {

        public MockJavaArchiveImpl(Archive<?> archive) {
            super(JavaArchive.class, archive);
        }

        @Override
        protected ArchivePath getClassesPath() {
            return ArchivePaths.root();
        }

        @Override
        protected ArchivePath getLibraryPath() {
            return ArchivePaths.root();
        }

        @Override
        protected ArchivePath getManifestPath() {
            return ArchivePaths.root();
        }

        @Override
        protected ArchivePath getResourcePath() {
            return ArchivePaths.root();
        }

        @Override
        public String toString(final Formatter formatter) throws IllegalArgumentException {
            return formatter.format(this);
        }
    }

    /**
     * Used in testing {@link Assignable} types without the need to function
     *
     * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
     * @version $Revision: $
     */
    private static class MockAssignable implements Assignable {

        @Override
        public <TYPE extends Assignable> TYPE as(final Class<TYPE> clazz) {
            // NO-OP
            return null;
        }

    }

}

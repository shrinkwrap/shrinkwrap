# Shrinkwrap

ShrinkWrap is a Java library used to create Java archives such as JARs, WARs, EARs and RARs.
This is especially useful for integration testing, where you need to deploy specific configurations of your applications, such as [Arquillian](https://github.com/arquillian).

## Requirements

- **JDK**: Version 8 or newer.
- **Maven**: Version 3.2.5 or newer.

## Using Shrinkwrap

To include ShrinkWrap in your project, you can add a dependency on the `shrinkwrap-depchain` module in your project's `pom.xml`:

```xml
  <dependency>
    <groupId>org.jboss.shrinkwrap</groupId>
    <artifactId>shrinkwrap-depchain</artifactId>
    <version>${version.shrinkwrap}</version>
    <type>pom</type>
  </dependency>
```

The `ShrinkWrap` class is the entry point to the ShrinkWrap library, enabling users to create and manage archives and domains.
It provides static methods to create archives and domains with either default or custom configurations.

### Archives

`Archive` in ShrinkWrap represents a collection of resources, similar to a virtual filesystem.
Each archive supports adding `Node` under `ArchivePath` , where `Node` can be directory or `Asset`.

- `Node`: An entry in an `Archive`; may represent content or a directory
- `ArchivePath`: Location in an `Archive` under which a `Node` is stored.
- `Asset`: Content within a `Node`

```java
// Creates the simplest type which supports generic operations, with optional name.
GenericArchive genericArchive = ShrinkWrap.create(GenericArchive.class,"generic.jar");

// Creates JAR and adds TestClass and all inner classes to the JAR.
JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class).addClass(TestClass.class);

// Creates WAR and adds another JAR to WAR as a library to the archive.
WebArchive webArchive = ShrinkWrap.create(WebArchive.class).addAsLibrary(javaArchive);

// Creates EAR
EnterpriseArchive enterpriseArchive = ShrinkWrap.create(EnterpriseArchive.class);

// Creates RAR
ResourceAdapterArchive resourceAdapterArchive = ShrinkWrap.create(ResourceAdapterArchive.class);

// You can also create archives from ZIP files.
JavaArchive archive = ShrinkWrap.createFromZipFile(JavaArchive.class, new File("/home/user/Desktop/myPackage.jar"));
```

You can switch views of your archive using the as method from the Assignable interface.
For example, to add classes to a JAR file, use the JavaArchive view:

```java
genericArchive.as(JavaArchive.class).addClasses(String.class, Integer.class);
```

### Working With Archives

You can create and export JAR files for deployment to Java EE servers or for use in testing frameworks like Arquillian.

```java
// Create a new Java archive (JAR) using ShrinkWrap containing TestClass, StringAsset and a default generated MANIFEST.MF.
final JavaArchive archive = ShrinkWrap.create(JavaArchive.class)
    .addClass(TestClass.class)
    .add(new StringAsset("testContent"), "testResource")
    .addManifest();

// Export contents of the JAR to a target file
File target = new File("path/to/export/directory", "testArchive.jar");
archive.as(ZipExporter.class).exportTo(target);
```

With ShrinkWrap, you can also create a deployable web archive (WAR) file.
For instance, you can add resources and set the web.xml descriptor like this:

```java
// Initialize ShrinkWrap to create a WebArchive
WebArchive war = ShrinkWrap.create(WebArchive.class, "basic.war");

// Add resources to the WebArchive
war.addAsResource(URL resource, "index.html");
war.setWebXML(URL webxml);

// Export contents of the WAR to a target file
File basicWar = new File("path/to/export/directory", "basic.war");
war.as(ZipExporter.class).exportTo(basicWar, true);
```

Additionally, you can also export archives as `InputStream`, which allows for deployment without needing intermediate temporary files.
```java
final InputStream is = archive.as(ZipExporter.class).exportAsInputStream();
```

ShrinkWrap also provides creation of an enterprise EAR archive that includes both a WAR and a JAR, and then export it to a file.
```java
// Create a WebArchive (WAR) with the specified name and content
WebArchive war = ShrinkWrap.create(WebArchive.class, archiveName);
war.addAsWebResource(new StringAsset("testContent"), "page.html");

// Create a JavaArchive (JAR) with some example classes
JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "library.jar")
    .addClass(SomeClass.class)
    .addClass(AnotherClass.class);

// Create an EnterpriseArchive (EAR) and add the WAR and JAR to it
final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, subArchiveName);
ear.add(war, new BasicPath("/"), ZipExporter.class);
ear.add(jar, new BasicPath("/lib"), ZipExporter.class);

// Export the EAR to a target file
File target = new File("path/to/export/directory", "archive.ear");
new ZipExporterImpl(ear).exportTo(target, true);
```

### Assets

ShrinkWrap provides various types of `Assets` for different content sources, which can be added into an `Archive`, including nested archives, byte arrays, classes, files, strings, URLs, and empty content and more.

```java
JavaArchive myArchive = ShrinkWrap.create(JavaArchive.class, "myArchive.jar");

// Add a string asset
myArchive.add(new StringAsset("asset string"), "path/to/string.txt");

// Add a file asset
File file = new File("path/to/your/file.txt");
myArchive.add(new FileAsset(file), "file");

// Add an empty asset
myArchive.add(EmptyAsset.INSTANCE, "file");

// Add a byte array asset
byte[] byteArray = "This is a byte array".getBytes();
myArchive.add(new ByteArrayAsset(byteArray));
```

You can implement the `Asset` interface to supply any byte-based content as an `InputStream`.
For example, you can present a `DataSource` as an `Asset`:
```java
final DataSource dataSource = null; // Assume you have this
Asset asset = new Asset() {
  @Override
  public InputStream openStream() {
    try {
      return dataSource.getInputStream();
    } catch (final IOException e) {
      throw new RuntimeException(e);
    }
  }
};
```

### Domains

In ShrinkWrap, a `Domain` encapsulates a configuration context for creating archives.
Different domains can have isolated configurations, allowing you to manage multiple environments within a single application.


#### Creating Domains

You can create a new domain with a default configuration, a custom configuration, or a custom configuration builder.

The `Configuration` class in ShrinkWrap encapsulates all the settings and properties for a given `Domain`.
Each `Archive` created by the domain's `ArchiveFactory` will use this configuration internally.

```java
// Create a domain with default configuration
Domain defaultDomain = ShrinkWrap.createDomain();

// Create a domain with a custom configuration builder
Domain domain = ShrinkWrap.createDomain(new ConfigurationBuilder().executorService(service)
        .extensionLoader(loader));
```

#### Using Domains

Once you have a domain, you can create archives within that domain, which will use previously specified configuration.

```java
// Create an archive within the default domain
JavaArchive javaArchive = defaultDomain.getArchiveFactory().create(JavaArchive.class);

// Create an archive within a custom domain
JavaArchive customJavaArchive = customDomain.getArchiveFactory().create(JavaArchive.class, "customArchive.jar");

// Add multiple classes and a package to the custom archive. 
// The behavior is consistent with standard archives.
customJavaArchive.addClasses(MyClassOne.class, MyClassTwo.class)
                 .addPackages(true, MyClassThree.class.getPackage());
```

### Filters

ShrinkWrap provides filters to include or exclude specific content when creating archives.

```java
// Include all paths
Filter<ArchivePath> includeAll = Filters.includeAll();

// Include paths matching regex (e.g., all .class files)
Filter<ArchivePath> includeClassFiles = Filters.include(".*\\.class");

// Exclude paths matching regex (e.g., all .xml files)
Filter<ArchivePath> excludeXmlFiles = Filters.exclude(".*\\.xml");

// Include specific paths
Filter<ArchivePath> includePaths = Filters.includePaths("path/to/include", "another/path");

// Exclude specific paths
Filter<ArchivePath> excludePaths = Filters.excludePaths("path/to/exclude", "another/path/to/exclude");

// Apply filters to an archive
JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "myFilteredArchive.jar")
    .addPackages(true, Filters.include("com.example.myapp.*"))  // Include specific package
    .addAsResource(new FileAsset(new File("src/main/resources/config.xml")), "config.xml")  // Add specific resource
    .filter(Filters.exclude(".*\\.log"));  // Exclude all .log files
```

### Contributing

If you have suggestions for improvements or would like to contribute to ShrinkWrap, please visit our [issue tracker](https://issues.redhat.com/projects/SHRINKWRAP/issues) for the SHRINKWRAP project.




# Shrinkwrap

ShrinkWrap is a Java library to create Java archives such as JARs, WARs, EARs and RARs.
This is especially useful for integration testing, where you need to deploy specific configurations of your applications, such as [Arquillian](https://github.com/arquillian).

## Requirements

- **JDK**: Version 8 or newer.
- **Maven**: Version 2.2.0 or newer.

## Using Shrinkwrap

To include ShrinkWrap in your project, you can add a dependency on the `shrinkwrap-depchain` module in your Maven `pom.xml`:

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
- `ArchivePath`: Location in an `Archive` under which a `Node` lives
- `Asset`: Byte-based content within a `Node`

```java
// Creates simplest type which supports generic operations, with optional name.
GenericArchive genericArchive = ShrinkWrap.create(GenericArchive.class,"generic.jar");

// Creates JAR and adds TestClass and all inner classes to the JAR.
JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class).addClass(TestClass.class);

// Creates WAR and add another JAR to WAR as a library to the container.
WebArchive webArchive = ShrinkWrap.create(WebArchive.class).addAsLibrary(javaArchive);

// Creates EAR
EnterpriseArchive enterpriseArchive = ShrinkWrap.create(EnterpriseArchive.class);

// Creates RAR
ResourceAdapterArchive resourceAdapterArchive = ShrinkWrap.create(ResourceAdapterArchive.class);
```

#### Adding Content

ShrinkWrap provides various types of `Assets` for different content sources, which can be added into an `Archive`, including nested archives, byte arrays, classes, files, strings, URLs, and empty content and more.

```java
myArchive.add(myAsset,"path/to/content");
```

You can implement the Asset interface to supply any byte-based content as an InputStream. 
For example, you can present a DataSource as an Asset:
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

You can switch views of your archive using the as method from the Assignable interface.
For example, to add classes to a JAR file, use the JavaArchive view:

```java
myArchive.as(JavaArchive.class).addClasses(String.class, Integer.class);
```

#### Working with File Content

ShrinkWrap provides functionality to package file content.
Following example shows how to add all of the `.class` files in the current package.

```java
JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "myPackage.jar")
  .addPackage(this.getClass().getPackage());
```

Then, we can export the `JavaArchive` as ZIP, using property of `Assignable` interface, to view it as `ZipExporter`.

```java
archive.as(ZipExporter.class).exportTo(new File("/home/user/Desktop/myPackage.jar"), true);
```

Shrinkwrap allows exporting to `File`, `OutputStream` and `InputStream` and provides `TAR`, `TAR.GZ`, `ZIP` and `Exploded` exporters.

```java
// retrieves one of the exporter classes
final Class<? extends StreamExporter> exporter = this.getExporterClass();
// views the archive as exporter class and exports contents to target file
archive.as(exporter).exportTo(File target,  boolean overwrite);
```

ShrinkWrap can also obtain an `Archive` from a flat-file using one of the importers, which mirror their respective exporters.

```java
JavaArchive roundtrip = ShrinkWrap
  .create(ZipImporter.class, "myPackageRoundtrip.jar")
  .importFrom(new File("/home/user/Desktop/myPackage.jar"))
  .as(JavaArchive.class);
// or you can use more simple way for ZIP files
JavaArchive archive = ShrinkWrap.createFromZipFile(JavaArchive.class, new File("/home/user/Desktop/myPackage.jar"));
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

// Add content to the archive
customJavaArchive.addClass(MyClass.class);

// Export the archive
customJavaArchive.as(ZipExporter.class).exportTo(new File("/home/user/Desktop/customArchive.jar"), true);
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

### Issues and Improvements

If you encounter any issues or have suggestions for improving ShrinkWrap, please report them on our issue tracker at [SHRINKWRAP](https://issues.redhat.com/projects/SHRINKWRAP/issues) project.




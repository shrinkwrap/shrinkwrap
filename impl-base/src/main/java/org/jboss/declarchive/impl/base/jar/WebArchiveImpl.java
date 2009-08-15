//package org.jboss.declarchive.impl.base.jar;
//
//import java.util.logging.Logger;
//
//import org.jboss.declarchive.api.Archive;
//import org.jboss.declarchive.api.Path;
//import org.jboss.declarchive.api.jar.WebArchive;
//import org.jboss.declarchive.impl.base.ArchiveBaseSupport;
//import org.jboss.declarchive.impl.base.path.BasePath;
//import org.jboss.declarchive.impl.base.path.RelativePath;
//import org.jboss.declarchive.impl.base.resource.ClassloaderResource;
//
//public class WebArchiveImpl extends ArchiveBaseSupport<WebArchive> implements WebArchive
//{
//   //-------------------------------------------------------------------------------------||
//   // Class Members ----------------------------------------------------------------------||
//   //-------------------------------------------------------------------------------------||
//
//   /**
//    * Logger
//    */
//   private static final Logger log = Logger.getLogger(WebArchiveImpl.class.getName());
//
//   /**
//    * Path to the web resource inside of the Archive.
//    */
//   private static final Path PATH_WEB = new BasePath("WEB-INF"); 
//
//   /**
//    * Path to the manifest resources inside of the Archive.
//    */
//   private static final Path PATH_MANIFEST = new RelativePath(PATH_WEB, "META-INF"); 
//   
//   /**
//    * Path to the class resources inside of the Archive.
//    */
//   private static final Path PATH_CLASSES = new RelativePath(PATH_WEB, "classes");
//   
//   /**
//    * Path to the library resources inside of the Archive.
//    */
//   private static final Path PATH_LIBRARY = new RelativePath(PATH_WEB, "lib");
//
//   /**
//    * Path to the library resources inside of the Archive.
//    */
//   private static final Path PATH_RESOURCE = new BasePath("/");
//
//   //-------------------------------------------------------------------------------------||
//   // Instance Members -------------------------------------------------------------------||
//   //-------------------------------------------------------------------------------------||
//
//   //-------------------------------------------------------------------------------------||
//   // Constructor ------------------------------------------------------------------------||
//   //-------------------------------------------------------------------------------------||
//
//   /**
//    * Constructor
//    * 
//    * @param The underlying archive storage implementation
//    * to which the convenience methods of this archive
//    * will delegate
//    * @throws IllegalArgumentException If the delegate is not specified 
//    */
//   public WebArchiveImpl(final Archive<?> delegate)
//   {
//      super(delegate);
//   }
//   
//   //-------------------------------------------------------------------------------------||
//   // Required Implementations -----------------------------------------------------------||
//   //-------------------------------------------------------------------------------------||
//
//   /* (non-Javadoc)
//    * @see org.jboss.declarchive.impl.base.ArchiveBaseSupport#getManinfestPath()
//    */
//   @Override
//   protected Path getManinfestPath()
//   {
//      return PATH_MANIFEST;
//   }
//   
//   /* (non-Javadoc)
//    * @see org.jboss.declarchive.impl.base.ArchiveBaseSupport#getResourcePath()
//    */
//   @Override
//   protected Path getResourcePath()
//   {
//      return PATH_RESOURCE;
//   }
//   
//   /* (non-Javadoc)
//    * @see org.jboss.declarchive.impl.base.ArchiveBaseSupport#getClassesPath()
//    */
//   @Override
//   protected Path getClassesPath()
//   {
//      return PATH_CLASSES;
//   }
//
//   //-------------------------------------------------------------------------------------||
//   // Required Implementations - WebContainer      ---------------------------------------||
//   //-------------------------------------------------------------------------------------||
//
//   /* (non-Javadoc)
//    * @see org.jboss.declarchive.api.container.WebContainer#setWebXML(java.lang.String)
//    */
//   @Override
//   public WebArchive setWebXML(String resourceName)
//   {
//      return add(PATH_WEB, "web.xml", new ClassloaderResource(resourceName));
//   }
//}

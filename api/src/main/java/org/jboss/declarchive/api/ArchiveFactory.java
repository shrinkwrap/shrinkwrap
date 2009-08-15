package org.jboss.declarchive.api;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;

public class ArchiveFactory
{
   /**
    * Logger
    */
   private static final Logger log = Logger.getLogger(ArchiveFactory.class.getName());

   private static final Class<?>[] ARCHIVE_IMPL_CONSTRUCTOR_ARGUMENTS = new Class[]{String.class};
   
   private ArchiveFactory() {  }
   
   public static Archive createTmpArchive(String name) 
   {
      Class<? extends Archive> implClass = null;
      try 
      {
         implClass = getClass("org.jboss.declarchive.impl.jdkfile.TempFileArchiveImpl")
            .asSubclass(Archive.class);
      } 
      catch (ClassNotFoundException e) 
      {
         throw new RuntimeException(
               "Declarative Archives implementation \"JDK File\" not in classpath", 
               e);
      }
      return createArchive(
            name, 
            Archive.class, 
            implClass);
   }
   
   public static Archive createVirtualArchive(String name) 
   {
      Class<? extends Archive> implClass = null;
      try 
      {
         implClass = getClass("org.jboss.declarchive.impl.vfs.MemoryArchiveImpl")
            .asSubclass(Archive.class);
      } 
      catch (ClassNotFoundException e) 
      {
         throw new RuntimeException(
               "Declarative Archives implementation \"Virtual File System\" not in classpath", 
               e);
      }
      return createArchive(
            name, 
            Archive.class,
            implClass);
   }

   public static <T extends Archive> T createArchive(final String name, final Class<T> archiveType, final Class<? extends T> archiveImpl) 
   {
      if(archiveType == null) {
         throw new IllegalArgumentException("ArchiveType can not be null");
      }
      if(archiveImpl == null) {
         throw new IllegalArgumentException("ArchiveImpl can not be null");
      }
      
      Constructor<?> implConstructor = findConstructor(archiveImpl, ARCHIVE_IMPL_CONSTRUCTOR_ARGUMENTS); 
      
      Object archive = null;
      try 
      {
         archive = implConstructor.newInstance(name);
      } 
      catch (Exception e) {
         throw new RuntimeException("Error in creating new " + archiveImpl.getName(), e);
      }
      
      return archiveType.cast(archive);
   }
   
   private static Constructor<?> findConstructor(final Class<?> implClass, final Class<?>[] arguments) 
   {
      try 
      {
         return SecurityActions.getConstructor(implClass, arguments);
      } 
      catch (NoSuchMethodException e) 
      {
         throw new RuntimeException("Could not find constructor to be used in factory creation of a new "
               + Archive.class.getSimpleName(), e);
      }
   }
   
   /**
    * Obtains the class with the specified name from the TCCL
    *  
    * @param className
    * @return
    */
   static Class<?> getClass(final String className) throws ClassNotFoundException
   {
      final ClassLoader cl = SecurityActions.getThreadContextClassLoader();
      return Class.forName(className, false, cl);
   }
}

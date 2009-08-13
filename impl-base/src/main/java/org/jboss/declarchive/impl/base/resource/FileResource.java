package org.jboss.declarchive.impl.base.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.jboss.declarchive.spi.Resource;

/**
 * Loads any File.
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class FileResource implements Resource
{
   private File file;
   
   /**
    * Load the specified File. 
    * 
    * @param file The file to load
    * @throws IllegalArgumentException File can not be null
    * @throws IllegalArgumentException File must exist
    */
   public FileResource(File file) 
   {
      // Precondition check
      if (file == null)
      {
         throw new IllegalArgumentException("File must be specified");
      }
      if(!file.exists()) 
      {
         throw new IllegalArgumentException("File must exist");
      }
      this.file = file;
   }

   /**
    * Get the default name using File.getName();
    */
   @Override
   public String getDefaultName()
   {
      return file.getName();
   }
   
   /**
    * Opens a new FileInputStream for the given File.
    * 
    * Can throw a Runtime exception if the file has been deleted inbetween 
    * the FileResource was created and the stream is opened. 
    * 
    * @throws RuntimeException If the file is not found.
    */
   @Override
   public InputStream getStream()
   {
      try 
      {
         return new FileInputStream(file);
      }
      catch (FileNotFoundException e)   
      {
         throw new RuntimeException("Could not open file " + file, e);
      }
   }
}

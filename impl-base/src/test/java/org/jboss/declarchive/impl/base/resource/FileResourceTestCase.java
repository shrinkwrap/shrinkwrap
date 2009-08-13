package org.jboss.declarchive.impl.base.resource;

import java.io.File;
import java.io.InputStream;

import junit.framework.Assert;

import org.jboss.declarchive.spi.Resource;
import org.junit.Test;

/**
 * Test to ensure that we can use a File as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class FileResourceTestCase
{
   private static final String BASE_PATH = "src/test/resources/org/jboss/declarchive/impl/base/resource/";
   private static final String EXISTING_FILE = BASE_PATH + "Test.properties";
   private static final String NON_EXISTING_FILE = BASE_PATH + "NoFileShouldBePlacedHere.properties";
   
   @Test
   public void shouldBeAbleToReadFile() throws Exception 
   {
      Resource resource = new FileResource(new File(EXISTING_FILE));
      InputStream io = resource.getStream();
      
      Assert.assertNotNull(io);
      Assert.assertEquals(
            "Should be able to read the content of the resource",
            "declarch=true", TestUtils.convertToString(io));
   }
   
   @Test
   public void shouldBeAbleToReadDefaultName() throws Exception 
   {
      Resource resource = new FileResource(new File(EXISTING_FILE));
      Assert.assertEquals(
            "A File resource should use the file name as default name, not absolute path",
            "Test.properties", resource.getDefaultName());
   }
   
   @Test
   public void shouldThrowExceptionOnNullFile() throws Exception 
   {
      try 
      {
         new FileResource(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      } 
      catch (Exception e) 
      {
         Assert.assertEquals(
               "A null file argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void shouldThrowExceptionOnMissingFile() throws Exception 
   {
      try 
      {
         new FileResource(new File(NON_EXISTING_FILE));
         Assert.fail("Should have thrown IllegalArgumentException");
      } 
      catch (Exception e) 
      {
         Assert.assertEquals(
               "A non existing file should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }
}

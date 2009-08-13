package org.jboss.declarchive.impl.base.resource;

import java.io.InputStream;

import junit.framework.Assert;

import org.jboss.declarchive.spi.Resource;
import org.junit.Test;

/**
 * Test to ensure that we are can use a ClassLoader Resource as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class ClassloaderResourceTestCase
{
   private static final String EXISTING_RESOURCE = "org/jboss/declarchive/impl/base/resource/Test.properties";
   private static final String NON_EXISTING_RESOURCE = "org/jboss/declarchive/impl/base/resource/NoFileShouldBePlacedHere.properties";
   
   @Test
   public void shouldBeAbleToReadResource() throws Exception 
   {
      Resource resource = new ClassloaderResource(EXISTING_RESOURCE);
      InputStream io = resource.getStream();

      Assert.assertNotNull(io);
      Assert.assertEquals(
            "Should be able to read the content of the resource",
            "declarch=true", TestUtils.convertToString(io));
   }

   @Test
   public void shouldBeAbleToReadDefaultName() throws Exception 
   {
      Resource resource = new ClassloaderResource(EXISTING_RESOURCE);
      Assert.assertEquals(
            "A Classloader resource should use file name as default name, not absolute path", 
            "Test.properties", resource.getDefaultName());
   }

   @Test
   public void shouldThrowExceptionOnNullName() 
   {
      try
      {
         new ClassloaderResource(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      } 
      catch (Exception e)
      {
         Assert.assertEquals(
               "A null resourceName argument should result in a IllegalArgumentException", 
               IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void shouldThrowExceptionOnNullClassloader() 
   {
      try
      {
         new ClassloaderResource(EXISTING_RESOURCE, null);
         Assert.fail("Should have thrown IllegalArgumentException");
      } 
      catch (Exception e)
      {
         Assert.assertEquals(
               "A null classLoader argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }

   @Test
   public void shouldThrowExceptionOnMissingResource() 
   {
      try
      {
         new ClassloaderResource(NON_EXISTING_RESOURCE);
         Assert.fail("Should have thrown IllegalArgumentException");
      } 
      catch (Exception e)
      {
         Assert.assertEquals(
               "A resource that is not found in the classLoader should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }
}

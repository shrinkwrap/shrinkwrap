package org.jboss.declarchive.impl.base.resource;

import java.io.InputStream;

import junit.framework.Assert;

import org.jboss.declarchive.spi.Resource;
import org.junit.Test;

/**
 * Test to ensure that we can use a URL as a resource.
 * 
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class URLResourceTestCase
{
   private static final String EXISTING_RESOURCE = "org/jboss/declarchive/impl/base/resource/Test.properties";

   @Test
   public void shouldBeAbleToReadURL() throws Exception
   {
      Resource resource = new URLResource(
            Thread.currentThread()
               .getContextClassLoader().getResource(EXISTING_RESOURCE));

      InputStream io = resource.getStream();

      Assert.assertNotNull(io);
      Assert.assertEquals(
            "Should be able to read the content of the resource",
            "declarch=true", TestUtils.convertToString(io));
   }

   @Test
   public void shouldBeAbleToReadDefaultName() throws Exception 
   {
      Resource resource = new URLResource(
            Thread.currentThread().getContextClassLoader()
            .getResource(EXISTING_RESOURCE));
      
      Assert.assertEquals(
            "A URL resource should use the file name as default name, not absolute path",
            "Test.properties", resource.getDefaultName());
   }
   
   @Test
   public void shouldThrowExceptionOnNullURL() throws Exception
   {
      try
      {
         new URLResource(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      } 
      catch (Exception e)
      {
         Assert.assertEquals(
               "A null url argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }
}

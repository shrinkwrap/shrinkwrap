package org.jboss.declarchive.impl.base.resource;

import java.io.InputStream;

import org.jboss.declarchive.spi.Resource;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test to ensure that we are able to use Classes as Resources.
 *
 * https://jira.jboss.org/jira/browse/TMPARCH-5
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 *
 */
public class ClassResourceTestCase
{
   
   @Test
   public void shouldBeAbleToReadThisClass() throws Exception 
   {
      Resource resource = new ClassResource(ClassResourceTestCase.class);
      InputStream io = resource.getStream();

      Assert.assertNotNull(io);      
   }
   
   // TODO: add test to byte compare expected result?
   
   @Test
   public void shouldBeAbleToReadDefaultName() throws Exception 
   {
      Resource resource = new ClassResource(ClassResourceTestCase.class);
      Assert.assertEquals(
            "A Class resource should use class name + '.class' as default name",
            "org/jboss/declarchive/impl/base/resource/ClassResourceTestCase.class", 
            resource.getDefaultName());
   }

   @Test
   public void shouldThrowExceptionOnNullClass() throws Exception 
   {
      try 
      {
         new ClassResource(null);
         Assert.fail("Should have thrown IllegalArgumentException");
      } 
      catch (Exception e) 
      {
         Assert.assertEquals(
               "A null clazz argument should result in a IllegalArgumentException",
               IllegalArgumentException.class, e.getClass());
      }
   }
}

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
package org.jboss.shrinkwrap.impl.base.asset;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.impl.base.Validate;

/**
 * ServiceProviderAsset
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ServiceProviderAsset implements Asset
{
   private Class<?>[] providerImpls;

   /**
    * Creates a newline separated text file off the providerImpls class names.
    * 
    * @param providerImpls The Classes to use
    * @throws IllegalArgumentException if providerImpls is null or contain null values
    */
   public ServiceProviderAsset(Class<?>... providerImpls)
   {
      Validate.notNullAndNoNullValues(providerImpls, "ProviderImpls must be specified and can not contain null values");
      this.providerImpls = providerImpls;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.Asset#openStream()
    */
   @Override
   public InputStream openStream()
   {
      StringBuilder content = new StringBuilder();
      for(Class<?> providerImpl : providerImpls)
      {
         content.append(providerImpl.getName()).append('\n');
      }
      return new ByteArrayInputStream(content.toString().getBytes());
   }
}

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.dependencies.impl;

import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * @author <a href="mailto:kpiwko@redhat.com">Karel Piwko</a>
 * 
 */
public class ListenerFormatter extends Formatter
{
   /*
    * (non-Javadoc)
    * 
    * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
    */
   @Override
   public String format(LogRecord record)
   {
      StringBuilder sb = new StringBuilder();

      if (record.getLevel().intValue() > Level.FINE.intValue())
      {
         sb.append("[")
               .append(record.getLevel())
               .append("] ")
               .append(record.getMessage())
               .append("\n");
      }
      else
      {
         sb.append(record.getMessage());
      }

      return sb.toString();
   }

}

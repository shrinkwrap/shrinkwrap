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
package org.jboss.shrinkwrap.api.serialization;

import java.io.Serializable;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.Assignable;

/**
 * {@link Serializable} view of an {@link Archive}. This is the base interface for all {@link Serializable} views of an
 * {@link Archive}, and may be extended/implemented to define a custom wire protocol. <br />
 * <br />
 * ShrinkWrap will use a {@link ZipSerializableView} implementation when assigning archives to this type directly via
 * {@link Assignable#as(Class)}. However, consumers obtaining this type should not assume anything about the protocol
 * being used under the hood; it may be any subtype. <br />
 * <br />
 * May be reassigned back to a normal {@link Archive} view via {@link Assignable#as(Class)}.
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 * @version $Revision: $
 */
public interface SerializableView extends Assignable, Serializable {

}

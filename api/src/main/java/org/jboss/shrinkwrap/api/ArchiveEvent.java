/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.shrinkwrap.api;

import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * @author Davide D'Alto
 */
public class ArchiveEvent {

   private final Asset asset;

   private final ArchivePath path;

   private Asset handledAsset;

   public ArchiveEvent(ArchivePath path, Asset asset) {
      this.path = path;
      this.asset = asset;
      this.handledAsset = asset;
   }

   public Asset getAsset() {
      return asset;
   }

   public ArchivePath getPath() {
      return path;
   }

   public Asset getHandledAsset() {
      return handledAsset;
   }

   public void setHandledAsset(Asset handledAsset) {
      this.handledAsset = handledAsset;
   }
}

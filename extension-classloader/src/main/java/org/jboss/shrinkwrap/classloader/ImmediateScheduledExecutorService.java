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
package org.jboss.shrinkwrap.classloader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@link ScheduledExecutorService} implementation which ignores
 * all scheduling operations and immediately dispatches invocations
 * to an underlying {@link ExecutorService}
 *
 * @author <a href="mailto:andrew.rubinger@jboss.org">ALR</a>
 */
class ImmediateScheduledExecutorService implements ScheduledExecutorService
{
   //-------------------------------------------------------------------------------------||
   // Instance Members -------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Delegate for all operations
    */
   private final ExecutorService delegate;

   //-------------------------------------------------------------------------------------||
   // Constructors -----------------------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   /**
    * Creates a new instance using the specified delegate
    * @param delegate
    * @throws IllegalArgumentException If the delegate is not specified
    */
   ImmediateScheduledExecutorService(final ExecutorService delegate) throws IllegalArgumentException
   {
      // Precondition checks
      if (delegate == null)
      {
         throw new IllegalArgumentException("Delegate " + ExecutorService.class.getSimpleName() + " must be specified");
      }

      // Set
      this.delegate = delegate;
   }

   //-------------------------------------------------------------------------------------||
   // Required Implementations -----------------------------------------------------------||
   //-------------------------------------------------------------------------------------||

   private static class ImmediateScheduledFuture<V> implements ScheduledFuture<V>
   {

      /**
       * Delegate through which all {@link Future} contracts will
       * be fulfilled 
       */
      private final Future<V> delegate;

      ImmediateScheduledFuture(final Future<V> delegate)
      {
         assert delegate != null : "Delegate Future must be specified";
         this.delegate = delegate;
      }

      /**
       * Returns a delay of 0, always
       * @see java.util.concurrent.Delayed#getDelay(java.util.concurrent.TimeUnit)
       */
      public long getDelay(final TimeUnit unit)
      {
         // No delay
         return 0;
      }

      /**
       * Compare our delay of 0 to the incoming {@link Delayed}
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo(final Delayed o)
      {
         final TimeUnit unit = TimeUnit.SECONDS;
         return new Long(this.getDelay(unit)).compareTo(o.getDelay(unit));
      }

      /*
       * Delegate methods only below this marker
       */

      public boolean cancel(boolean mayInterruptIfRunning)
      {
         return delegate.cancel(mayInterruptIfRunning);
      }

      public V get() throws InterruptedException, ExecutionException
      {
         try
         {
            return this.delegate.get();
         }
         catch (final Exception e)
         {
            throw new ExecutionException(e);
         }
      }

      public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
      {
         return get();
      }

      public boolean isCancelled()
      {
         return this.delegate.isCancelled();
      }

      public boolean isDone()
      {
         return this.delegate.isDone();
      }

   }

   @SuppressWarnings("unchecked")
   public ScheduledFuture<?> schedule(final Runnable command, final long delay, final TimeUnit unit)
   {
      final Future<?> future = this.submit(command);
      return new ImmediateScheduledFuture(future);
   }

   public <V> ScheduledFuture<V> schedule(final Callable<V> callable, final long delay, final TimeUnit unit)
   {
      final Future<V> future = this.submit(callable);
      return new ImmediateScheduledFuture<V>(future);
   }

   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
   {
      return this.schedule(command, period, unit);
   }

   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit)
   {
      return this.schedule(command, delay, unit);
   }

   /*
    * Everything below this marker simply passes control along to the delegate
    */

   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
   {
      return delegate.awaitTermination(timeout, unit);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException
   {
      return delegate.invokeAll(tasks);
   }

   public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
         throws InterruptedException
   {
      return delegate.invokeAll(tasks, timeout, unit);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException
   {
      return delegate.invokeAny(tasks);
   }

   public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
         throws InterruptedException, ExecutionException, TimeoutException
   {
      return delegate.invokeAny(tasks, timeout, unit);
   }

   public boolean isShutdown()
   {
      return delegate.isShutdown();
   }

   public boolean isTerminated()
   {
      return delegate.isTerminated();
   }

   public void shutdown()
   {
      delegate.shutdown();
   }

   public List<Runnable> shutdownNow()
   {
      return delegate.shutdownNow();
   }

   public <T> Future<T> submit(Callable<T> task)
   {
      return delegate.submit(task);
   }

   public Future<?> submit(Runnable task)
   {
      return delegate.submit(task);
   }

   public <T> Future<T> submit(Runnable task, T result)
   {
      return delegate.submit(task, result);
   }

   public void execute(Runnable command)
   {
      delegate.execute(command);
   }
}

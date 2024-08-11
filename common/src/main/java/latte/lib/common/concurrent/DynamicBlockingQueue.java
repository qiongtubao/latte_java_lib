package latte.lib.common.concurrent;


import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */







public class DynamicBlockingQueue<T> implements BlockingQueue<T> {

    private final ConcurrentLinkedQueue<T> internalQueue;
    private final AtomicInteger sizeLimit;
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    public DynamicBlockingQueue(int initialCapacity) {
      internalQueue = new ConcurrentLinkedQueue<>();
      sizeLimit = new AtomicInteger(initialCapacity);
      lock = new ReentrantLock();
      notFull = lock.newCondition();
      notEmpty = lock.newCondition();
    }

    @Override
    public int remainingCapacity() {
      return sizeLimit.get() - internalQueue.size();
    }


  @Override
  public boolean add(T t) {
    lock.lock();
    try {
      return internalQueue.add(t);
    } finally {
      lock.unlock();
    }
  }

  @Override
    public boolean offer(T e) {
      try {
        return offer(e, 0, TimeUnit.MILLISECONDS);
      } catch (Exception ex) {
        return false;
      }
    }

  @Override
  public T remove() {
    lock.lock();
    try {
      return internalQueue.remove();
    } finally {
      lock.unlock();
    }
  }

  @Override
    public boolean offer(T e, long timeout, TimeUnit unit) throws InterruptedException {
      lock.lock();
      try {
        long nanos = unit.toNanos(timeout);
        while (internalQueue.size() == sizeLimit.get()) {
          if (nanos <= 0) {
            return false;
          }
          nanos = notFull.awaitNanos(nanos);
        }
        internalQueue.offer(e);
        notEmpty.signal();
        return true;
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void put(T e) throws InterruptedException {
      lock.lock();
      try {
        while (internalQueue.size() == sizeLimit.get()) {
          notFull.await();
        }
        internalQueue.offer(e);
        notEmpty.signal();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public T take() throws InterruptedException {
      lock.lock();
      try {
        while (internalQueue.isEmpty()) {
          notEmpty.await();
        }
        T e = internalQueue.poll();
        notFull.signal();
        return e;
      } finally {
        lock.unlock();
      }
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
      lock.lock();
      try {
        long nanos = unit.toNanos(timeout);
        while (true) {
          if (internalQueue.isEmpty()) {
            if (nanos <= 0) {
              return null;
            }
            nanos = notEmpty.awaitNanos(nanos);
          } else {
            T e = internalQueue.poll();
            notFull.signal();
            return e;
          }
        }
      } finally {
        lock.unlock();
      }
    }

  @Override
  public int drainTo(Collection<? super T> c) {
    lock.lock();
    try {
      int numMoved = 0;
      T e;
      while ((e = internalQueue.poll()) != null) {
        c.add(e);
        ++numMoved;
      }
      notFull.signalAll();
      return numMoved;
    } finally {
      lock.unlock();
    }
  }

  @Override
  public int drainTo(Collection<? super T> c, int maxElements) {
    lock.lock();
    try {
      int numMoved = 0;
      T e;
      while ((e = internalQueue.poll()) != null && numMoved < maxElements) {
        c.add(e);
        ++numMoved;
      }
      notFull.signalAll();
      return numMoved;
    } finally {
      lock.unlock();
    }
  }

    @Override
    public T poll() {
      lock.lock();
      try {
        return internalQueue.poll();
      } finally {
        lock.unlock();
      }
    }

  @Override
  public T element() {
    lock.lock();
    try {
      return internalQueue.element();
    } finally {
      lock.unlock();
    }
  }


  @Override
    public T peek() {
      lock.lock();
      try {
        return internalQueue.peek();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public int size() {
      lock.lock();
      try {
        return internalQueue.size();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean isEmpty() {
      lock.lock();
      try {
        return internalQueue.isEmpty();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean contains(Object o) {
      lock.lock();
      try {
        return internalQueue.contains(o);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public Iterator<T> iterator() {
      lock.lock();
      try {
        return internalQueue.iterator();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public Object[] toArray() {
      lock.lock();
      try {
        return internalQueue.toArray();
      } finally {
        lock.unlock();
      }
    }

    @Override
    public <E> E[] toArray(E[] a) {
      lock.lock();
      try {
        return internalQueue.toArray(a);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean remove(Object o) {
      lock.lock();
      try {
        return internalQueue.remove(o);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean containsAll(java.util.Collection<?> c) {
      lock.lock();
      try {
        return internalQueue.containsAll(c);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean addAll(java.util.Collection<? extends T> c) {
      lock.lock();
      try {
        if (c.size() + internalQueue.size() > sizeLimit.get()) {
          throw new IllegalStateException("Adding all elements would exceed the maximum capacity");
        }
        return internalQueue.addAll(c);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean removeAll(java.util.Collection<?> c) {
      lock.lock();
      try {
        return internalQueue.removeAll(c);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public boolean retainAll(java.util.Collection<?> c) {
      lock.lock();
      try {
        return internalQueue.retainAll(c);
      } finally {
        lock.unlock();
      }
    }

    @Override
    public void clear() {
      lock.lock();
      try {
        internalQueue.clear();
      } finally {
        lock.unlock();
      }
    }

    public synchronized void setSizeLimit(int newLimit) {
      lock.lock();
      try {
        sizeLimit.set(newLimit);
        notFull.signalAll();
      } finally {
        lock.unlock();
      }
    }

    public synchronized int getSizeLimit() {
      return sizeLimit.get();
    }

  }




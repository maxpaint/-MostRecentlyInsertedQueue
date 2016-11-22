package com.maxpaint;

import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentMostRecentlyInsertedQueue<T> extends MostRecentlyInsertedQueue<T> {

    private final ReentrantLock lock;

    public ConcurrentMostRecentlyInsertedQueue(Integer initialCapacity) {
        super(initialCapacity);
        this.lock = new ReentrantLock();
    }

    @Override
    public int size() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.size();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(T t) {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.offer(t);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T poll() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.poll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T peek() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return super.peek();
        } finally {
            lock.unlock();
        }

    }

    @Override
    public void clear() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            super.clear();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}

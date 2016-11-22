package com.maxpaint;

import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class MostRecentlyInsertedBlockingQueue<T> implements BlockingQueue<T> {

    private BlockingDeque<T> delegateQueue;
    private final Integer capacity;

    public MostRecentlyInsertedBlockingQueue(Integer initialCapacity) {
        this.capacity = initialCapacity;
        if (initialCapacity > 0) {
            this.delegateQueue = new LinkedBlockingDeque<T>(initialCapacity);
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return delegateQueue.iterator();
    }

    @Override
    public int size() {
        return delegateQueue.size();
    }

    @Override
    public boolean add(T t) {
        return delegateQueue.add(t);
    }

    @Override
    public boolean offer(T t) {
        if(size()==capacity){
            delegateQueue.removeFirst();
        }
        return delegateQueue.offer(t);
    }

    @Override
    public T poll() {
        return delegateQueue.poll();
    }

    @Override
    public T peek() {
        if(delegateQueue.isEmpty()){
            return null;
        }
        return delegateQueue.getFirst();
    }

    @Override
    public void clear() {
        delegateQueue = new LinkedBlockingDeque<T>(capacity);
    }

    @Override
    public synchronized  void put(T t) throws InterruptedException {
        if(size()==capacity)
            delegateQueue.removeFirst();
        delegateQueue.put(t);
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
        if(size()==capacity){
            delegateQueue.removeFirst();
        }
        return delegateQueue.offer(t, timeout, unit);
    }

    @Override
    public T take() throws InterruptedException {
        return delegateQueue.take();
    }

    @Override
    public T poll(long timeout, TimeUnit unit) throws InterruptedException {
        return delegateQueue.poll(timeout, unit);
    }

    @Override
    public int remainingCapacity() {
        return delegateQueue.remainingCapacity();
    }

    @Override
    public boolean remove(Object o) {
        return delegateQueue.remove(o);
    }

    @Override
    public boolean contains(Object o) {
        return delegateQueue.contains(o);
    }

    @Override
    public int drainTo(Collection<? super T> c) {
        return delegateQueue.drainTo(c);
    }

    @Override
    public int drainTo(Collection<? super T> c, int maxElements) {
        return delegateQueue.drainTo(c, maxElements);
    }

    @Override
    public T remove() {
        return null;
    }

    @Override
    public T element() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Object[] toArray() {
        return delegateQueue.toArray();
    }

    @Override
    public <T1> T1[] toArray(T1[] a) {
        return null;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends T> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }
}

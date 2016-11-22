package com.maxpaint;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MostRecentlyInsertedQueue<T> extends AbstractQueue<T> {

    private Integer capacity;
    private List<T> list;

    public MostRecentlyInsertedQueue(Integer initialCapacity) {
        this.capacity = initialCapacity;
        if (initialCapacity > 0) {
            this.list = new ArrayList<T>(initialCapacity);
        } else {
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean offer(T t) {
        if(size()==capacity){
            list.remove(0);
            ((ArrayList)list).trimToSize();
        }
        return list.add(t);
    }

    @Override
    public T poll() {
        T removed = list.remove(0);
        ((ArrayList)list).trimToSize();
        return removed;
    }

    @Override
    public T peek() {
        if(list.isEmpty()){
            return null;
        }
        return list.get(0);
    }

    @Override
    public void clear() {
        list = new ArrayList<T>();
    }

    @Override
    public String toString() {
        return list.toString();
    }
}

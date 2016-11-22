package com.maxpaint;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ConcurrentMostRecentlyInsertedQueueTest {
    private ConcurrentMostRecentlyInsertedQueue<Integer> queue = new ConcurrentMostRecentlyInsertedQueue<>(3);

    @Before
    public void setUp() throws Exception {
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);
    }

    @Test
    public void testSize() throws Exception {
        assertTrue(String.format("Queue size expected 3, but actual is %s", queue.size()), queue.size() == 3);
    }

    @Test
    public void testOffer() throws Exception {
        Integer[] expectedArray = {2, 3, 4};
        queue.offer(4);
        assertTrue(String.format("Queue contents expected [2, 3, 4], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }

    @Test
    public void testPoll() throws Exception {
        Integer[] expectedArray = {2, 3};
        int poll = queue.poll();
        assertEquals(String.format("Queue size expected 2, but actual is %s", queue.size()), 2, queue.size());
        assertEquals(String.format("Queue poll expected 2, but actual is %s", poll), 1, poll);
        assertTrue(String.format("Queue contents expected [3, 4], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }

    @Test
    public void testPeek() throws Exception {
        queue.offer(4);
        queue.offer(5);
        assertEquals(String.format("Queue peek expected 3, but actual is %s", queue.peek()), (Integer) 3, queue.peek());
    }

    @Test
    public void testOrder() throws Exception {
        queue = new ConcurrentMostRecentlyInsertedQueue<>(3);
        queue.offer(3);
        queue.offer(1);
        queue.offer(2);

        Integer[] expectedArray = {3, 1, 2};
        assertTrue(String.format("Queue contents expected [3, 1, 2], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCapacity1() throws Exception {
        queue = new ConcurrentMostRecentlyInsertedQueue<>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCapacity2() throws Exception {
        queue = new ConcurrentMostRecentlyInsertedQueue<>(-1);
    }

    @Test
    public void commonTest() throws Exception {
        queue = new ConcurrentMostRecentlyInsertedQueue<>(3);

        queue.offer(1); // queue.size(): 1, contents (head -> tail): [ 1 ]
        Integer[] expectedArray1 = {1};
        assertTrue(String.format("Queue contents expected [1], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray1));

        queue.offer(2); // queue.size(): 2, contents (head -> tail): [ 1, 2 ]
        Integer[] expectedArray2 = {1, 2};
        assertTrue(String.format("Queue contents expected [1, 2], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray2));

        queue.offer(3); // queue.size(): 3, contents (head -> tail): [ 1, 2, 3 ]
        Integer[] expectedArray3 = {1, 2, 3};
        assertTrue(String.format("Queue contents expected [1, 2, 3], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray3));

        queue.offer(4); // queue.size(): 3, contents (head -> tail): [ 2, 3, 4 ]
        Integer[] expectedArray4 = {2, 3, 4};
        assertTrue(String.format("Queue contents expected [2, 3, 4], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray4));

        queue.offer(5); // queue.size(): 3, contents (head -> tail): [ 3, 4, 5 ]
        Integer[] expectedArray5 = {3, 4, 5};
        assertTrue(String.format("Queue contents expected [3, 4, 5], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray5));

        int poll1 = queue.poll(); // queue.size(): 2, contents (head -> tail): [ 4, 5 ], poll1 = 3
        Integer[] expectedArray6 = {4, 5};
        assertEquals(String.format("Queue poll expected 3, but actual is %s", poll1), 3, poll1);
        assertTrue(String.format("Queue contents expected [4, 5], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray6));

        int poll2 = queue.poll(); // queue.size(): 1, contents (head -> tail): [ 5 ], poll2 = 4
        Integer[] expectedArray7 = {5};
        assertEquals(String.format("Queue poll expected 4, but actual is %s", poll1), 4, poll2);
        assertTrue(String.format("Queue contents expected [5], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray7));

        queue.clear(); // queue.size(): 0, contents (head -> tail): [ ]
        assertTrue(String.format("Queue size expected 0, but actual is %s", queue.size()), queue.size() == 0);
    }

    @Test
    public void testConcurrentOffer() throws Exception {
        int poolSize = Runtime.getRuntime().availableProcessors();
        if (poolSize == 1) {
            poolSize++;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(poolSize);
        CountDownLatch countDownLatch = new CountDownLatch(poolSize);
        AtomicInteger atomicInteger = new AtomicInteger();

        for (int i = 0; i < poolSize; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < 100000; j++) {
                    queue.offer(atomicInteger.incrementAndGet());
                }
                countDownLatch.countDown();
            });
        }

        countDownLatch.await();
        executorService.shutdown();

        Integer[] expectedArray = {poolSize * 100000 - 2, poolSize * 100000 - 1, poolSize * 100000};
        assertEquals(String.format("Queue size expected 3, but actual is %s", queue.size()), 3, queue.size());
        assertTrue(String.format("Queue contents expected [%s, %s, %s], but actual is %s",
                poolSize * 100000 - 2, poolSize * 100000 - 1, poolSize * 100000,
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }
}

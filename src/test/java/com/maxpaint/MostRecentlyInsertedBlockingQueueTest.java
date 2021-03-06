package com.maxpaint;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class MostRecentlyInsertedBlockingQueueTest {
    private MostRecentlyInsertedBlockingQueue<Integer> queue = new MostRecentlyInsertedBlockingQueue<>(3);

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

    @Test(timeout = 10)
    public void testPut() throws Exception {
        // if queue will be blocked test will fail on timeout
        Integer[] expectedArray = {2, 3, 4};
        queue.put(4);
        assertTrue(String.format("Queue contents expected [2, 3, 4], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }

    @Test
    public void testOffer() throws Exception {
        Integer[] expectedArray = {2, 3, 4};
        queue.offer(4);
        assertTrue(String.format("Queue contents expected [2, 3, 4], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }

    @Test
    public void testTake() throws Exception {
        Integer[] expectedArray = {2, 3};
        int head = queue.take();
        assertEquals(String.format("Queue size expected 2, but actual is %s", queue.size()), 2, queue.size());
        assertEquals(String.format("Queue head expected 2, but actual is %s", head), 1, head);
        assertTrue(String.format("Queue contents expected [3, 4], but actual is %s",
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
    public void testRemainingCapacity() throws Exception {
        queue.poll();
        assertEquals(String.format("Queue remaining capacity expected 1, but actual is %s",
                queue.remainingCapacity()), 1, queue.remainingCapacity());
    }

    @Test
    public void testRemainingCapacityEmpty() throws Exception {
        queue.clear();
        assertEquals(String.format("Queue remaining capacity expected 3, but actual is %s",
                queue.remainingCapacity()), 3, queue.remainingCapacity());
    }

    @Test
    public void testDrainTo() throws Exception {
        Integer[] expectedArray = {1, 2, 3};
        List<Integer> drainedList = new ArrayList<>();
        int numberOfDrainedElements = queue.drainTo(drainedList);

        assertEquals(String.format("Queue size expected 0, but actual is %s", queue.size()), 0, queue.size());
        assertEquals(String.format("Queue remaining capacity expected 3, but actual is %s",
                queue.remainingCapacity()), 3, queue.remainingCapacity());
        assertEquals(String.format("Number of drained elements expected 3, but actual is %s",
                numberOfDrainedElements), 3, numberOfDrainedElements);
        assertTrue(String.format("Drained list contents expected [1, 2, 3], but actual is %s",
                Arrays.toString(drainedList.toArray())), Arrays.equals(drainedList.toArray(), expectedArray));
    }

    @Test
    public void testDrainToWithNumber() throws Exception {
        Integer[] expectedDrainArray = {1, 2};
        Integer[] expectedQueueArray = {3};
        List<Integer> drainedList = new ArrayList<>();
        int numberOfDrainedElements = queue.drainTo(drainedList, 2);

        assertEquals(String.format("Queue size expected 1, but actual is %s", queue.size()), 1, queue.size());
        assertEquals(String.format("Queue remaining capacity expected 2, but actual is %s",
                queue.remainingCapacity()), 2, queue.remainingCapacity());
        assertEquals(String.format("Number of drained elements expected 2, but actual is %s",
                numberOfDrainedElements), 2, numberOfDrainedElements);
        assertTrue(String.format("Drained list contents expected [1, 2], but actual is %s",
                Arrays.toString(drainedList.toArray())), Arrays.equals(drainedList.toArray(), expectedDrainArray));
        assertTrue(String.format("Queue list contents expected [3], but actual is %s",
                Arrays.toString(drainedList.toArray())), Arrays.equals(queue.toArray(), expectedQueueArray));
    }

    @Test(timeout = 10)
    public void testOfferTimeout() throws Exception {
        // if queue will be blocked test will fail on timeout
        Integer[] expectedArray = {2, 3, 4};
        queue.offer(4, 1000, TimeUnit.SECONDS);
        assertTrue(String.format("Queue contents expected [2, 3, 4], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }

    @Test
    public void testPoll1() throws Exception {
        Integer[] expectedArray = {2, 3};
        int poll = queue.poll(100, TimeUnit.MILLISECONDS);
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
        queue = new MostRecentlyInsertedBlockingQueue<>(3);
        queue.offer(3);
        queue.offer(1);
        queue.offer(2);

        Integer[] expectedArray = {3, 1, 2};
        assertTrue(String.format("Queue contents expected [3, 1, 2], but actual is %s",
                Arrays.toString(queue.toArray())), Arrays.equals(queue.toArray(), expectedArray));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCapacity1() throws Exception {
        queue = new MostRecentlyInsertedBlockingQueue<>(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCapacity2() throws Exception {
        queue = new MostRecentlyInsertedBlockingQueue<>(-1);
    }

    @Test
    public void commonTest() throws Exception {
        queue = new MostRecentlyInsertedBlockingQueue<>(3);

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

    @Test
    public void testConcurrentPut() throws Exception {
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
                    try {
                        queue.put(atomicInteger.incrementAndGet());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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

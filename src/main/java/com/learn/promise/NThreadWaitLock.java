package com.learn.promise;

import java.util.concurrent.ExecutorService;

import static java.lang.Thread.currentThread;
import static java.util.concurrent.Executors.newCachedThreadPool;

public class NThreadWaitLock {

    private static final ExecutorService THREAD_POOL = newCachedThreadPool();
    private static final String LOCK = "LOCK";

    private static void waitForLock() {
        System.out.println("Thread (" + currentThread().getId() + ") wait start");

        // in the N (waiting) threads
        synchronized (LOCK) {
            try {
                LOCK.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Thread (" + currentThread().getId() + ") wait end");
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 10; i++) {
            THREAD_POOL.execute(NThreadWaitLock::waitForLock);
        }

        Thread.sleep(3000);

        // in the Single (holding) thread, to wake up everyone
        synchronized (LOCK) {
            LOCK.notifyAll();
        }

        // clean-up
        Thread.sleep(500);
        THREAD_POOL.shutdown();
    }
}

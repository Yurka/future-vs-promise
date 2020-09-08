package com.learn.promise;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import static java.lang.System.out;
import static java.lang.Thread.sleep;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class FutureSimpleGet {
    private static final ExecutorService THREAD_POOL = newFixedThreadPool(8);

    private static Double verySlowBlockingCalculation() {
        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 1.0;
    }

    public static void main(String[] args) {
        FutureTask<Double> ft = new FutureTask<>(FutureSimpleGet::verySlowBlockingCalculation);

        // We wait, for 3.1 second and no more
        try {
            THREAD_POOL.execute(ft);
            Double result = ft.get(3100, MILLISECONDS);  // 1 second timeout

            // continue to do other work, with result
            out.println("result is " + result);
        } catch (Exception e) {
            e.printStackTrace();
            ft.cancel(true);
        }

        // We wait, till we get the result.
        ft = new FutureTask<>(FutureSimpleGet::verySlowBlockingCalculation);
        try {
            THREAD_POOL.execute(ft);

            while (!ft.isDone()) {
                // while we wait, we can go work on something else
                sleep(200);
                out.println("waiting, not giving up ");
            }

            Double result = ft.get();  // this will return immediately
            out.println("result is " + result);
        } catch (Exception e) {
            e.printStackTrace();
            ft.cancel(true);
        }

        // clean-up
        THREAD_POOL.shutdown();
    }
}

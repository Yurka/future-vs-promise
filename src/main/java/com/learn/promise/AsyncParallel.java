package com.learn.promise;

import java.security.SecureRandom;
import java.util.concurrent.*;

import static java.lang.System.out;
import static java.util.concurrent.CompletableFuture.supplyAsync;

public class AsyncParallel {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private static SecureRandom secureRandom = new SecureRandom();

    static {
        secureRandom.setSeed(System.currentTimeMillis());
    }

    private static void waitForAWhile() {
        try {
            long sleepTimeInMilli = 1000L + 100L * secureRandom.nextInt(10);  // 1-2 seconds
            Thread.sleep(sleepTimeInMilli);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }
    }

    private static String slowRequest1() {
        waitForAWhile();
        return "1";
    }

    private static String slowRequest2() {
        waitForAWhile();
        return "2";
    }

    private static String slowRequest3() {
        waitForAWhile();
        return "3";
    }

    public static void main(String[] args) throws Exception {
        // blocking version
        String s1 = slowRequest1();
        String s2 = slowRequest2();
        String s3 = slowRequest3();
        out.println(s1 + s2 + s3);

        // completionService version
        final ExecutorCompletionService<String> completionService = new ExecutorCompletionService<>(THREAD_POOL);
        completionService.submit(AsyncParallel::slowRequest1);
        completionService.submit(AsyncParallel::slowRequest2);
        completionService.submit(AsyncParallel::slowRequest3);

        int numFetched = 0;
        StringBuilder buf = new StringBuilder();
        while (numFetched < 3) {
            try {
                final Future<String> future = completionService.take();
                buf.append(future.get());
                ++numFetched;
            } catch (InterruptedException ie) {
                break;
            } catch (Throwable e) {
                ++numFetched;
            }
        }
        out.println(buf.toString());

        // promise version
        CompletableFuture<String> cf1 = supplyAsync(AsyncParallel::slowRequest1);
        CompletableFuture<String> cf2 = supplyAsync(AsyncParallel::slowRequest2);
        CompletableFuture<String> cf3 = supplyAsync(AsyncParallel::slowRequest3);

        CompletableFuture.allOf(cf1, cf2, cf3).thenApply(v -> {
            out.println(cf1.join() + cf2.join() + cf3.join());
            return null;
        });
        out.println("to prove it is async, this line should be printed before the result from above is printed");

        // clean-up
        Thread.sleep(3000);
        THREAD_POOL.shutdown();
    }
}

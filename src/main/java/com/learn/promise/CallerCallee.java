package com.learn.promise;

import java.util.concurrent.*;

import static java.lang.System.out;
import static java.lang.Thread.sleep;

public class CallerCallee {
    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    private String slowCalculation() {
        out.println("slow routine started.");
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "slow_calc_result";
    }

    // Callee / Implementor
    public Future<String> asyncSlowFetch() {
        CompletableFuture<String> promise = new CompletableFuture<>();
        THREAD_POOL.execute(() -> {
            String result = slowCalculation();
            promise.complete(result);
        });
        return promise;
    }

    // Caller / Consumer
    public void someMethod() throws ExecutionException, InterruptedException {
        Future<String> f = asyncSlowFetch();
        String result = f.get();
        out.println("caller thread got result back: " + result);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        new CallerCallee().someMethod();
        // clean-up
        THREAD_POOL.shutdown();
    }
}

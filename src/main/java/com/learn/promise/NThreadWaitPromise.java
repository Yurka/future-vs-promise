package com.learn.promise;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NThreadWaitPromise {
	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();
	private static final CompletableFuture<String> PROMISE = new CompletableFuture<>();

	private static void waitForPromise() {
		System.out.println("Thread ("+Thread.currentThread().getId()+") wait start");
		try {
			// in the N (waiting) threads
			PROMISE.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Thread ("+Thread.currentThread().getId()+") wait end");
	}

	public static void main(String[] args) throws Exception {
		for (int i=0; i<10; i++) {
			THREAD_POOL.execute( ()->waitForPromise() );
		}

		Thread.sleep(3000);
		// in the Single (holding) thread, to wake up everyone
		PROMISE.complete("WAKE UP!!!");

		// clean-up
		Thread.sleep(500);
		THREAD_POOL.shutdown();
	}
}

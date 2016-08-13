package com.learn.promise;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CallerCallee {
	private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

	private String slow_calculattion() {
		System.out.println("slow routine started.");
		try { Thread.sleep(5000); } catch (InterruptedException e) { e.printStackTrace(); }
		return "slow_calc_result";
	}

	// Callee / Implementor
	public Future<String> asyncSlowFetch() {
		CompletableFuture<String> promise = new CompletableFuture<>();
		THREAD_POOL.execute( ()-> {
			String result = slow_calculattion();
			promise.complete(result);
		} );
		return promise;
	}

	// Caller / Consumer
	public void someMethod() throws Exception{
		Future<String> f = asyncSlowFetch();
		String result = f.get();
		System.out.println( "caller thread got result back: " + result );
	}

	public static void main(String[] args) throws Exception {
		new CallerCallee().someMethod();
		// clean-up
		THREAD_POOL.shutdown();
	}
}

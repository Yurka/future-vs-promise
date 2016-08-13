package com.learn.promise;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class FutureSimpleGet {
	private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(8);

	private static Double very_slow_blocking_calculation() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		return 1.0;
	}

	public static void main(String[] args) {
		FutureTask<Double> ft = new FutureTask<Double>( ()->very_slow_blocking_calculation() );

		// We wait, for 3.1 second and no more
		try {
			THREAD_POOL.execute(ft);
			Double result = ft.get(3100, TimeUnit.MILLISECONDS);  // 1 second timeout
			// continue to do other work, with result
			System.out.println("result is "+result);
		} catch (Exception e) {
			e.printStackTrace();
			ft.cancel(true);
		}

		// We wait, till we get the result.
		ft = new FutureTask<Double>( ()->very_slow_blocking_calculation() );
		try {
			THREAD_POOL.execute(ft);
			while( !ft.isDone() ) {
				// while we wait, we can go work on something else
				Thread.sleep(200);
				System.out.println("waiting, not giving up ");;
			}
			Double result = ft.get();  // this will return immediately
			System.out.println("result is "+result);
		} catch (Exception e) {
			e.printStackTrace();
			ft.cancel(true);
		}

		// clean-up
		THREAD_POOL.shutdown();
	}
}

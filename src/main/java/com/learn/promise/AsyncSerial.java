package com.learn.promise;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AsyncSerial {

	private static String slow_request_1() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		return "result from request 1";
	}

	private static Integer slow_request_2(final String input) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		return input==null ? 0 : input.length();
	}

	private static String slow_request_3(int input) {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) { e.printStackTrace(); }
		return "the length of request 1 result ="+input;
	}

	private static String slow_request_1_withCB(
			final BiFunction<String, Function<Integer, String>, String> cb1,
			Function<Integer, String> cb2) {
		String request1Output = slow_request_1();
		return cb1.apply( request1Output, cb2 );
	}

	private static String slow_request_2_withCB(final String input, final Function<Integer, String> callback) {
		int request2Output = slow_request_2(input);
		return callback.apply( request2Output );
	}

	private static String slow_request_3_withCB(int input) {
		String request3Output = slow_request_3(input);
		return request3Output;
	}


	public static void main(String[] args) throws Exception {
		// blocking version
		String result1 = slow_request_1();
		int result2 = slow_request_2(result1);
		String result3 = slow_request_3(result2);
		System.out.println( result3 );

		// callback version
		System.out.println(
			slow_request_1_withCB(AsyncSerial::slow_request_2_withCB, AsyncSerial::slow_request_3_withCB)
		);

		// promise version
		CompletableFuture.supplyAsync( AsyncSerial::slow_request_1 )
				.thenApply( AsyncSerial::slow_request_2 )
				.thenApply( AsyncSerial::slow_request_3 )
				.thenAccept( System.out::println );
		System.out.println("to prove it is async, this line should be printed before the result from above is printed");

		// clean-up
		Thread.sleep(6200);
	}
}

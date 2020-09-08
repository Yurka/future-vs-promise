package com.learn.promise;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.Function;

public class AsyncSerial {

    private static String slowRequest1() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "result from request 1";
    }

    private static Integer slowRequest2(final String input) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return input == null ? 0 : input.length();
    }

    private static String slowRequest3(int input) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "the length of request 1 result =" + input;
    }

    private static String slowRequest1withCB(
        final BiFunction<String, Function<Integer, String>, String> cb1,
        Function<Integer, String> cb2) {
        String request1Output = slowRequest1();
        return cb1.apply(request1Output, cb2);
    }

    private static String slowRequest2withCB(final String input, final Function<Integer, String> callback) {
        int request2Output = slowRequest2(input);
        return callback.apply(request2Output);
    }

    private static String slowRequest3withCB(int input) {
        String request3Output = slowRequest3(input);
        return request3Output;
    }


    public static void main(String[] args) throws Exception {
        // blocking version
        String result1 = slowRequest1();
        int result2 = slowRequest2(result1);
        String result3 = slowRequest3(result2);
        System.out.println(result3);

        // callback version
        System.out.println(
            slowRequest1withCB(AsyncSerial::slowRequest2withCB, AsyncSerial::slowRequest3withCB)
        );

        // promise version
        CompletableFuture.supplyAsync(AsyncSerial::slowRequest1)
            .thenApply(AsyncSerial::slowRequest2)
            .thenApply(AsyncSerial::slowRequest3)
            .thenAccept(System.out::println);
        System.out.println("to prove it is async, this line should be printed before the result from above is printed");

        // clean-up
        Thread.sleep(6200);
    }
}

package com.miskevich.threadpool.data;

import java.util.concurrent.Callable;

public class MyCallableTask implements Callable {

    public static volatile int counter = 1;

    @Override
    public Object call() throws Exception {
        System.out.println("I'm callable number " + counter);
        counter++;
        return counter;
    }
}

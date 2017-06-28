package com.miskevich.threadpool.data;

public class MyRunnableForCallableTask implements Runnable {

    public static volatile int counter = 1;

    @Override
    public void run() {
        System.out.println("I'm runnableForCallable number " + counter);
        counter++;
    }
}

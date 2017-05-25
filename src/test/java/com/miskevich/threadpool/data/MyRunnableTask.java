package com.miskevich.threadpool.data;

public class MyRunnableTask implements Runnable {

    public static volatile int counter = 1;

    @Override
    public void run() {
        System.out.println("I'm number " + counter);
        counter++;
    }
}

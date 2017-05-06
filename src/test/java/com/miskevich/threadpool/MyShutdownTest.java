package com.miskevich.threadpool;

public class MyShutdownTest implements Runnable{

    public static volatile int counter = 1;

    @Override
    public void run() {
        System.out.println("I'm number " + counter);
        counter++;
    }
}

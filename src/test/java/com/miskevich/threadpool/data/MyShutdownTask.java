package com.miskevich.threadpool.data;

public class MyShutdownTask implements Runnable{

    public static volatile int counter = 1;

    @Override
    public void run() {
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("I'm number " + counter);
        counter++;
    }
}

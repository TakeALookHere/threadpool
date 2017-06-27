package com.miskevich.threadpool.data;

import java.util.concurrent.atomic.AtomicInteger;

public class MySlowTask implements Runnable{

    public static AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void run() {

        System.out.println("I'm number " + counter.incrementAndGet());
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

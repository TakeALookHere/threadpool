package com.miskevich.threadpool;

import java.util.concurrent.Future;

public interface RunnableFuture<T> extends Runnable, Future<T> {

    void run();
}

package com.miskevich.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class FutureTask<T> implements RunnableFuture<T> {

    private Callable<T> callable;
    private Object result;

    public FutureTask(Callable<T> callable) {
        this.callable = callable;
    }

    public void run() {
        //TODO Callable.call()
        Callable<T> task = callable;
        try {
            result = task.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        return null;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }
}

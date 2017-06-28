package com.miskevich.threadpool;

import org.testng.annotations.Test;

import java.util.concurrent.Callable;

public class TestNulls {

    @Test(expectedExceptionsMessageRegExp = "Task can't be NULL", expectedExceptions = NullPointerException.class)
    public void testSubmitNullCallable() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5);
        Callable<String> task = null;
        threadPoolExecutor.submit(task);
    }

    @Test(expectedExceptionsMessageRegExp = "Task can't be NULL", expectedExceptions = NullPointerException.class)
    public void testSubmitNullRunnable() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5);
        Runnable task = null;
        threadPoolExecutor.submit(task);
    }


}

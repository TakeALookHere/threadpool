package com.miskevich.threadpool;

import org.testng.annotations.Test;

import java.util.concurrent.Callable;

public class TestNulls {

    @Test(expectedExceptionsMessageRegExp = "Task can't be NULL", expectedExceptions = NullPointerException.class)
    void testSubmitNull() {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5);
        Callable<String> task = null;
        threadPoolExecutor.submit(task);
    }
}

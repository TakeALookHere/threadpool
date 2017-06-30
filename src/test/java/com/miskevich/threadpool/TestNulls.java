package com.miskevich.threadpool;

import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
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

    private boolean testMe(int a, List<Integer> list){
        boolean check = true;
        while (true){
            if(a <= 0){
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                if(a < list.get(i)){
                    check = false;
                    break;
                }
                if(i == list.size() - 1 && check){
                    return true;
                }
            }

        }
    }

    @Test
    public void testTestMe(){
        List<Integer> list = new ArrayList<>();
        list.add(7);
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        System.out.println(testMe(6, list));
    }


}

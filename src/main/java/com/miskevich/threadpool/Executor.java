package com.miskevich.threadpool;

public interface Executor {

    void execute(Runnable command);
}

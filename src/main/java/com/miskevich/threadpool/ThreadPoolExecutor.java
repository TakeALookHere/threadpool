package com.miskevich.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolExecutor implements Executor {
    private final BlockingQueue<Runnable> workQueue;
    private volatile boolean isStopped = false;

    public ThreadPoolExecutor(int aliveThreads) {
        this.workQueue = new LinkedBlockingQueue<>(aliveThreads);

        for (int i = 0; i < aliveThreads; i++) {
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();

            List<ThreadPoolTaskExecutor> threads = new ArrayList<>();
            threads.add(threadPoolTaskExecutor);

            new Thread(threadPoolTaskExecutor).start();
        }
    }

    public void execute(Runnable task) {
        synchronized (workQueue){
            if (task == null){
                throw new NullPointerException("Task can't be NULL");
            }
            if(isStopped){
                throw new IllegalStateException("Thread pool is stopped");
            }

            try {
                while (0 == workQueue.remainingCapacity()){
                    workQueue.wait();
                }
                workQueue.put(task);
                workQueue.notifyAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);            }
        }
    }

    public synchronized void shutdown(){
        isStopped = true;
    }

    public synchronized List<Runnable> shutdownNow(){
        shutdown();
        List<Runnable> notRunTasks = new ArrayList<>();
        workQueue.drainTo(notRunTasks);
        return notRunTasks;
    }

    private class ThreadPoolTaskExecutor implements Runnable{
        @Override
        public void run() {
            Runnable task;
            while (!isStopped){
                synchronized (workQueue){
                    while (workQueue.isEmpty()){
                        try {
                            workQueue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    task = workQueue.poll();
                    workQueue.notifyAll();
                }
                task.run();
            }
        }
    }
}

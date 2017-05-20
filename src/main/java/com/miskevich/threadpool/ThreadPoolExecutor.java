package com.miskevich.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolExecutor implements Executor {
    private final BlockingQueue<Runnable> workQueue;
    private final List<ThreadPoolTaskExecutor> threads = new ArrayList<>();
    private volatile boolean isStopped = false;

    public ThreadPoolExecutor(int aliveThreads) {
        this.workQueue = new LinkedBlockingQueue<>(aliveThreads);

        for (int i = 0; i < aliveThreads; i++) {
            threads.add(new ThreadPoolTaskExecutor(workQueue));
        }

        for (ThreadPoolTaskExecutor thread : threads){
            new Thread(thread).start();
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
                while (threads.size() == workQueue.size()){
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
        private final BlockingQueue<Runnable> workQueue;

        ThreadPoolTaskExecutor(BlockingQueue<Runnable> workQueue) {
            this.workQueue = workQueue;
        }

        @Override
        public void run() {
            while (!isStopped){
                synchronized (workQueue){
                    while (workQueue.isEmpty()){
                        try {
                            workQueue.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Runnable task = workQueue.poll();
                    task.run();
                    workQueue.notifyAll();
                }
            }
        }
    }
}

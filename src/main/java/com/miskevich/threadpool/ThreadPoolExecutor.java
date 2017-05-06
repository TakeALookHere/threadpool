package com.miskevich.threadpool;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadPoolExecutor implements Executor{
    private final BlockingQueue<Runnable> workQueue;
    private final List<ThreadPoolTaskExecutor> threads = new ArrayList<>();
    private boolean isStopped = false;

    public ThreadPoolExecutor(int aliveThreads) {
        this.workQueue = new LinkedBlockingQueue<>(aliveThreads);

        for (int i = 0; i < aliveThreads; i++) {
            threads.add(new ThreadPoolTaskExecutor(workQueue));
        }

        for (ThreadPoolTaskExecutor thread : threads){
            new Thread(thread).start();
        }
    }

    public synchronized void execute(Runnable task) {
        if (task == null){
            throw new NullPointerException();
        }
        if(isStopped){
            throw new IllegalStateException("Thread pool is stopped");
        }

        try {
            workQueue.put(task);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void shutdown(){
        isStopped = true;
        for (ThreadPoolTaskExecutor thread : threads){
            thread.stop();
        }
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
                while (null != workQueue.peek()){
                    Runnable task = workQueue.poll();
                    task.run();
                }
            }
        }

        private synchronized void stop() {
            Thread.currentThread().interrupt();
        }
    }
}

package com.miskevich.threadpool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolExecutor implements ExecutorService {
    private final BlockingQueue<Runnable> workQueue;
    private volatile boolean isStopped = false;

    public ThreadPoolExecutor(int aliveThreads) {
        workQueue = new LinkedBlockingQueue<>(aliveThreads);

        for (int i = 0; i < aliveThreads; i++) {
            ThreadPoolTaskWorker threadPoolTaskWorker = new ThreadPoolTaskWorker();
            new Thread(threadPoolTaskWorker).start();
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

    public boolean isShutdown() {
        //TODO
        return false;
    }

    public boolean isTerminated() {
        //TODO
        return false;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        //TODO
        return false;
    }

    public <T> Future<T> submit(Callable<T> task) {
        //TODO
        return null;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        //TODO
        return null;
    }

    public Future<?> submit(Runnable task) {
        //TODO
        return null;
    }

    //optional
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return null;
    }

    //optional
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    //optional
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return null;
    }

    //optional
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return null;
    }

    private class ThreadPoolTaskWorker implements Runnable{
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

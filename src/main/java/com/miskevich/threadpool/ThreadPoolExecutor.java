package com.miskevich.threadpool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadPoolExecutor extends AbstractExecutorService implements ExecutorService {
    private final BlockingQueue<Runnable> workQueue;
    private volatile boolean isStopped = false;
    private final List<Thread> threads = new ArrayList<>();
    private final ReentrantLock mainLock = new ReentrantLock();

    public ThreadPoolExecutor(int aliveThreads) {
        workQueue = new LinkedBlockingQueue<>(aliveThreads);

        for (int i = 0; i < aliveThreads; i++) {
            ThreadPoolTaskWorker threadPoolTaskWorker = new ThreadPoolTaskWorker();
            Thread thread = new Thread(threadPoolTaskWorker);
            threads.add(thread);
            thread.start();

        }
    }

    public void execute(Runnable task) {
        synchronized (workQueue) {
            if (task == null) {
                throw new NullPointerException("Task can't be NULL");
            }
            if (isStopped) {
                throw new IllegalStateException("Thread pool is stopped");
            }

            try {
                while (0 == workQueue.remainingCapacity()) {
                    workQueue.wait();
                }
                workQueue.put(task);
                workQueue.notifyAll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void shutdown() {
        isStopped = true;
    }

    public synchronized List<Runnable> shutdownNow() {
        shutdown();
        List<Runnable> notRunTasks = new ArrayList<>();
        workQueue.drainTo(notRunTasks);
        return notRunTasks;
    }

    private class ThreadPoolTaskWorker implements Runnable {
        @Override
        public void run() {
            Runnable task;
            while (!isStopped) {
                synchronized (workQueue) {
                    while (workQueue.isEmpty()) {
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

    public boolean isShutdown() {
        return isStopped;
    }

    public boolean isTerminated() {
        if (!isStopped) {
            return false;
        }
        for (Thread thread : threads) {
            if (!"TERMINATED".equals(thread.getState().toString())) {
                return false;
            }
        }
        return true;
    }

    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        long nanos = unit.toNanos(timeout);
        final ReentrantLock mainLock = this.mainLock;
        boolean isTerminated = false;
        boolean check = true;
        mainLock.lock();
        try{
//            while (isTerminated || nanos > 0) {

                if (nanos <= 0) {
                    return false;
                }
                for (int i = 0; i < threads.size(); i++) {
                    System.out.println(threads.get(i).getState());
                    if (!"TERMINATED".equals(threads.get(i).getState().toString())) {
                        System.out.println(threads.get(i).getState());
                        check = false;
                        break;
                    }
                    if(i == threads.size() - 1 && check){
                        isTerminated = true;
                    }
                }
            //}
        }finally {
            mainLock.unlock();
        }
        return true;
    }

    public <T> Future<T> submit(Callable<T> task) {
        if (task == null) {
            throw new NullPointerException("Task can't be NULL");
        }
        RunnableFuture<T> futureTask = newTaskFor(task);
        execute(futureTask);
        return futureTask;
    }

    public <T> Future<T> submit(Runnable task, T result) {
        if (task == null) {
            throw new NullPointerException("Task can't be NULL");
        }
        RunnableFuture<T> futureTask = newTaskFor(task, result);
        execute(futureTask);
        return futureTask;
    }

    public Future<?> submit(Runnable task) {
        if (task == null) {
            throw new NullPointerException("Task can't be NULL");
        }
        RunnableFuture<Void> futureTask = newTaskFor(task, null);
        execute(futureTask);
        return futureTask;
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
}

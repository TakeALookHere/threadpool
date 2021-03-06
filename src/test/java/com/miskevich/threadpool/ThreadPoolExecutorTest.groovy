package com.miskevich.threadpool

import com.miskevich.threadpool.data.*
import org.testng.annotations.Test

import java.util.concurrent.*

class ThreadPoolExecutorTest extends GroovyTestCase {

    @Test
    void testExecuteNull() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        String message = shouldFail(NullPointerException) {
            threadPoolExecutor.execute(null)
        }
        assert message == "Task can't be NULL"
        threadPoolExecutor.shutdownNow()
    }

    @Test
    void testExecuteStopped() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        threadPoolExecutor.shutdown()
        String message = shouldFail(IllegalStateException) {
            threadPoolExecutor.execute(new MyRunnableTask())
        }
        assert message == "Thread pool is stopped"
        threadPoolExecutor.shutdownNow()
    }

    @Test
    void testExecute() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyRunnableTask())
        }

        assertFalse(MyRunnableTask.counter == 1)
        threadPoolExecutor.shutdownNow()
    }

    @Test
    void testShutdownNow() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyShutdownTask())
        }

        def notRunTasks = threadPoolExecutor.shutdownNow()
        assertTrue(notRunTasks.size() != 0)
    }

    static void main(String[] args) {
        def start = System.currentTimeMillis()

        def threadPoolExecutor = new ThreadPoolExecutor(5)
        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MySlowTask())
        }

        while (20 != MySlowTask.counter) {
            Thread.sleep(100)
        }

        def duration = System.currentTimeMillis() - start
        println duration
        println MySlowTask.counter
        assertTrue(duration < 12000)
        threadPoolExecutor.shutdown()
    }

    @Test
    void testSubmitCallable() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.submit(new MyCallableTask())
        }
    }

    @Test
    void testSubmitRunnableWithResult() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        int result = 55

        for (int i = 0; i < 20; i++) {
            Future<Integer> future = threadPoolExecutor.submit(new MyRunnableForCallableTask(), result)
            assertEquals(55, future.get())
        }
    }

    @Test
    void testSubmitRunnable() throws ExecutionException, InterruptedException {
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            Future<?> future = threadPoolExecutor.submit(new MyRunnableForCallableTask())
            assertEquals(null, future.get())
        }
    }

    @Test
    void testIsShutdownFalse() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        assertFalse(threadPoolExecutor.isShutdown())
    }

    @Test
    void testIsShutdownTrue() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        threadPoolExecutor.shutdown()
        assertTrue(threadPoolExecutor.isShutdown())
    }

    @Test
    void testIsTerminatedFalse() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        assertFalse(threadPoolExecutor.isTerminated())
    }

    @Test
    void testIsTerminatedTrue() {
        def threadPoolExecutor = new ThreadPoolExecutor(2)
        for (int i = 0; i < 6; i++) {
            threadPoolExecutor.execute(new MyShutdownTask())
        }
        threadPoolExecutor.shutdown()
        Thread.sleep(11000)
        assertTrue(threadPoolExecutor.isTerminated())
    }

    @Test
    void testAwaitTerminationTimeoutOccurs() {
        def threadPoolExecutor = new ThreadPoolExecutor(2)
        for (int i = 0; i < 6; i++) {
            threadPoolExecutor.execute(new MyShutdownTask())
        }
        threadPoolExecutor.shutdown()
        assertFalse(threadPoolExecutor.awaitTermination(5000, TimeUnit.MILLISECONDS))
    }

    @Test
    void testAwaitTerminationWorkFinished() {
        def threadPoolExecutor = new ThreadPoolExecutor(2)
        for (int i = 0; i < 6; i++) {
            threadPoolExecutor.execute(new MyShutdownTask())
        }
        threadPoolExecutor.shutdown()
        assertTrue(threadPoolExecutor.awaitTermination(11000, TimeUnit.MILLISECONDS))
    }
}

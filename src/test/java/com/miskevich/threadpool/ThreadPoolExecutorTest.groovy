package com.miskevich.threadpool

import org.testng.annotations.Test

class ThreadPoolExecutorTest extends GroovyTestCase {

    @Test(priority = 1)
    void testExecuteCountOfThreads() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        def activeCount = Thread.activeCount()
        assert activeCount == 5 + 2
        threadPoolExecutor.shutdownNow()
    }

    @Test(priority = 2)
    void testExecuteNull() {
        Thread.sleep(100)
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        String message = shouldFail(NullPointerException){
            threadPoolExecutor.execute(null)
        }
        assert message == "Task can't be NULL"
        threadPoolExecutor.shutdownNow()
    }

    @Test(priority = 3)
    void testExecuteStopped() {
        Thread.sleep(100)
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        threadPoolExecutor.shutdown()
        String message = shouldFail(IllegalStateException){
            threadPoolExecutor.execute(new MyRunnableTest())
        }
        assert message == "Thread pool is stopped"
        threadPoolExecutor.shutdownNow()
    }

    @Test(priority = 4)
    void testExecute() {
        Thread.sleep(100)
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyRunnableTest())
        }

        assertFalse(MyRunnableTest.counter == 1)
        threadPoolExecutor.shutdownNow()
    }

    @Test(priority = 5)
    void testShutdown() {
        Thread.sleep(100)
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        def activeCountBeforeShutdown = Thread.activeCount()
        assert activeCountBeforeShutdown == 5 + 2

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyRunnableTest())
        }

        threadPoolExecutor.shutdown()
        Thread.sleep(1000)

        def activeCountAfterShutdown = Thread.activeCount()
        assert activeCountAfterShutdown == 2
    }

    @Test(priority = 6)
    void testShutdownNow() {
        Thread.sleep(100)
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyShutdownTest())
        }

        def notRunTasks = threadPoolExecutor.shutdownNow()
        assertEquals(notRunTasks.size(), 21 - MyShutdownTest.counter)
    }
}

package com.miskevich.threadpool

import com.miskevich.threadpool.data.MyRunnableTest
import com.miskevich.threadpool.data.MyShutdownTest
import org.testng.annotations.Test

class ThreadPoolExecutorTest extends GroovyTestCase {

    @Test
    void testExecuteNull() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        String message = shouldFail(NullPointerException){
            threadPoolExecutor.execute(null)
        }
        assert message == "Task can't be NULL"
        threadPoolExecutor.shutdownNow()
    }

    @Test
    void testExecuteStopped() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)
        threadPoolExecutor.shutdown()
        String message = shouldFail(IllegalStateException){
            threadPoolExecutor.execute(new MyRunnableTest())
        }
        assert message == "Thread pool is stopped"
        threadPoolExecutor.shutdownNow()
    }

    @Test
    void testExecute() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyRunnableTest())
        }

        assertFalse(MyRunnableTest.counter == 1)
        threadPoolExecutor.shutdownNow()
    }

    @Test
    void testShutdownNow() {
        def threadPoolExecutor = new ThreadPoolExecutor(5)

        for (int i = 0; i < 20; i++) {
            threadPoolExecutor.execute(new MyShutdownTest())
        }

        def notRunTasks = threadPoolExecutor.shutdownNow()
        assertEquals(notRunTasks.size(), 21 - MyShutdownTest.counter)
    }
}

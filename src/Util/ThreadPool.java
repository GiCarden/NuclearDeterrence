package Util;

import java.util.LinkedList;

/**
 *  Code created by David Brackeen
 *  Copyright (c) 2003, David Brackeen
 *
 *  A thread pool is a group of a limited number of threads that
 *  are used to execute tasks.
 */
public class ThreadPool extends ThreadGroup {

    private boolean isAlive;
    private LinkedList taskQueue;
    private int threadID;
    private static int threadPoolID;

    // Creates a new ThreadPool
    public ThreadPool(int numThreads) {

        super("ThreadPool-" + (threadPoolID++));

        setDaemon(true);

        isAlive = true;

        taskQueue = new LinkedList();

        for (int i=0; i<numThreads; i++) { new PooledThread().start(); }
    }

    // Run Task
    public synchronized void runTask(Runnable task) {

        /*
         Requests a new task to run. This method returns
         immediately, and the task executes on the next available
         idle thread in this ThreadPool.
         <p>Tasks start execution in the order they are received.
         @param task The task to run. If null, no action is taken.
         @throws IllegalStateException if this ThreadPool is
         already closed.
         */

        if (!isAlive) { throw new IllegalStateException(); }

        if (task != null) { taskQueue.add(task); notify(); }
    }

    // Get Task
    protected synchronized Runnable getTask() throws InterruptedException {
        while (taskQueue.size() == 0) {

            if (!isAlive) { return null; }
            wait();
        }
        return (Runnable)taskQueue.removeFirst();
    }

    // Close
    public synchronized void close() {

        /*
         Closes this ThreadPool and returns immediately. All
         threads are stopped, and any waiting tasks are not
         executed. Once a ThreadPool is closed, no more tasks can
         be run on this ThreadPool.
         */

        if(isAlive) { isAlive = false; taskQueue.clear(); interrupt(); }
    }

    // Closes this ThreadPool and waits for all running threads to finish. Any waiting tasks are executed
    public void join() {

        // notify all waiting threads that this ThreadPool is no longer alive
        synchronized (this) {

            isAlive = false;
            notifyAll();
        }

        // wait for all threads to finish
        Thread[] threads = new Thread[activeCount()];

        int count = enumerate(threads);

        for (int i = 0; i < count; i++) {

            try {
                threads[i].join();
            } catch (InterruptedException ex) { }
        }
    }

    // Thread Started
    protected void threadStarted() {

        // do nothing

        /*
         Signals that a PooledThread has started. This method
         does nothing by default; subclasses should override to do
         any thread-specific startup tasks.
         */
    }

    // Thread Stopped
    protected void threadStopped() {

        // do nothing

        /*
         Signals that a PooledThread has stopped. This method
         does nothing by default; subclasses should override to do
         any thread-specific cleanup tasks.
         */
    }

    // A PooledThread is a Thread in a ThreadPool group, designed to run tasks (Runnable)
    private class PooledThread extends Thread {

        public PooledThread()
        {
            super(ThreadPool.this, "PooledThread-" + (threadID++));
        }

        public void run() {

            // signal that this thread has started
            threadStarted();

            while(!isInterrupted()) {

                // get a task to run
                Runnable task = null;

                try {
                    task = getTask();
                } catch (InterruptedException ex) { }

                // if getTask() returned null or was interrupted, close this thread.
                if (task == null) { break; }

                // run the task, and eat any exceptions it throws
                try {
                    task.run();
                } catch (Throwable t) {
                    uncaughtException(this, t);
                }
            }
            // signal that this thread has stopped
            threadStopped();
        }

    } // End Inner Class.

} // End of Class.
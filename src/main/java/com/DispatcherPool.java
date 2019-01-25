package com;

import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DispatcherPool {
    final int threadNumber;

    private ExecutorService threadPool;

    private Dispatcher[] dispatchers;

    int currentIndex = 0;

    DispatcherPool(int threadNumber) {
        System.out.println("DispatcherPool");
        this.threadNumber = threadNumber;
        threadPool = Executors.newFixedThreadPool(threadNumber);
        dispatchers = new Dispatcher[threadNumber];
    }

    void register(SelectableChannel channel, int ops, Object att) {

        dispatchers[currentIndex].register(channel, ops, att);
        currentIndex = (++currentIndex) % 8;
    }

    void run(){
        try {
            for ( int i = 0; i < threadNumber; ++i) {
               // System.out.println("start dispatcher thread");
                dispatchers[i] = new Dispatcher();
                threadPool.execute(dispatchers[i]);

            }
        } catch (IOException ex ){
            System.out.println("DispatcherPool ex: " + ex.getMessage());
        }

    }
}

package com.shinemo.smartgrid.utils;

import java.util.concurrent.*;

public class ThreadPoolUtil {

    public static ExecutorService pool = new ThreadPoolExecutor(
            20,
            20,
            0L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(10000),
            new ThreadFactory() {
                @Override
                public Thread newThread(Runnable r) {
                    return new Thread(r);
                }
            },
            new ThreadPoolExecutor.AbortPolicy());

    public static Executor getApiLogPool() {
        return pool;
    }

}

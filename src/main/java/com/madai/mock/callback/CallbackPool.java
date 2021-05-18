package com.madai.mock.callback;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CallbackPool {

    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static void execute(Runnable runnable) {
        pool.execute(runnable);
    }
}

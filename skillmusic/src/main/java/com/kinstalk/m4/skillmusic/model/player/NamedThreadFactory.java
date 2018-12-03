package com.kinstalk.m4.skillmusic.model.player;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by libin on 2016/11/1.
 */

public class NamedThreadFactory implements ThreadFactory {
    private final AtomicInteger mCounter = new AtomicInteger();
    private static final String THREAD_NAME_PATTERN = "%s-%d";
    private String mName;

    public NamedThreadFactory(String name) {
        mName = name;
    }

    @Override
    public Thread newThread(Runnable r) {
        final String threadName = String.format(THREAD_NAME_PATTERN,
                mName, mCounter.incrementAndGet());
        return new Thread(r, threadName);
    }
}

/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kinstalk.m4.common.usecase;

import android.os.Handler;
import android.os.Looper;

import com.kinstalk.m4.common.utils.QLog;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Executes asynchronous tasks using a {@link ThreadPoolExecutor}.
 * <p/>
 * See also {@link Executors} for a list of factory methods to create common
 * {@link java.util.concurrent.ExecutorService}s for different scenarios.
 */
public class UseCaseThreadPoolScheduler implements UseCaseScheduler {

    public static final int POOL_SIZE = 2;
    public static final int MAX_POOL_SIZE = 4;
    public static final int TIMEOUT = 30;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    ThreadPoolExecutor mThreadPoolExecutor;

    public UseCaseThreadPoolScheduler() {
        mThreadPoolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                TimeUnit.SECONDS, new LinkedBlockingQueue(),
                new NamedThreadFactory(UseCaseThreadPoolScheduler.class.getSimpleName()));
    }

    public UseCaseThreadPoolScheduler(int poolSize, int poolMaxSize, long keepAliveTime,
                                      TimeUnit unit, String name) {
        mThreadPoolExecutor = new ThreadPoolExecutor(poolSize, poolMaxSize, keepAliveTime,
                unit, new LinkedBlockingQueue(), new NamedThreadFactory(name));
    }

    @Override
    public void execute(Runnable runnable) {
        QLog.v(this, "execute");
        mThreadPoolExecutor.execute(runnable);
    }

    @Override
    public <Q extends UseCase.RequestValue, P extends UseCase.ResponseValue> void notifyResponse(
            final Q request, final P response, final UseCase.UseCaseCallback<Q, P> useCaseCallback) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                useCaseCallback.onResponse(request, response);
            }
        });
    }
}

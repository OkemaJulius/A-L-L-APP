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


import com.kinstalk.m4.common.utils.QLog;

import java.util.concurrent.TimeUnit;

/**
 * Runs {@link UseCase}s using a {@link UseCaseScheduler}.
 */
public class UseCaseHandler {

    private static UseCaseHandler INSTANCE;

    private final UseCaseScheduler mForegroundScheduler;
    private final UseCaseScheduler mBackgroundScheduler;

    public UseCaseHandler(UseCaseScheduler foregroundScheduler,
                          UseCaseScheduler backgroundScheduler) {
        mForegroundScheduler = foregroundScheduler;
        mBackgroundScheduler = backgroundScheduler;
    }

    public static UseCaseHandler getInstance() {
        if (INSTANCE == null) {
            // Use single thread pool
            INSTANCE = new UseCaseHandler(new UseCaseThreadPoolScheduler(1, 1, 0, TimeUnit.SECONDS,
                    "ForegroundUseCase"), new UseCaseThreadPoolScheduler(1, 1, 0, TimeUnit.SECONDS,
                    "BackgroundUseCase"));
        }
        return INSTANCE;
    }

    public <T extends UseCase.RequestValue, R extends UseCase.ResponseValue> void execute(
            final UseCase<T, R> useCase, T values, UseCase.UseCaseCallback<T, R> callback) {
        QLog.v(this, "execute");
        useCase.setRequestValues(values);
        useCase.setUseCaseCallback(new UiCallbackWrapper(callback, this));
        if (useCase.isForeground()) {
            mForegroundScheduler.execute(new MyRunnable(useCase));
        } else {
            mBackgroundScheduler.execute(new MyRunnable(useCase));
        }
    }

    private static class MyRunnable<T extends UseCase.RequestValue,
            R extends UseCase.ResponseValue> implements Runnable {
        UseCase<T, R> mUseCase;

        public MyRunnable(UseCase<T, R> useCase) {
            mUseCase = useCase;
        }

        @Override
        public void run() {
            QLog.v(this, "Runnable execute");

            if (mUseCase != null) {
                mUseCase.run();
            }
        }
    }

    public <Q extends UseCase.RequestValue, P extends UseCase.ResponseValue> void notifyResponse(
            final Q request, final P response, final UseCase.UseCaseCallback<Q, P> useCaseCallback) {
        mForegroundScheduler.notifyResponse(request, response, useCaseCallback);
    }

    private static final class UiCallbackWrapper<Q extends UseCase.RequestValue,
            P extends UseCase.ResponseValue> implements
            UseCase.UseCaseCallback<Q, P> {
        private final UseCase.UseCaseCallback<Q, P> mCallback;
        private final UseCaseHandler mUseCaseHandler;

        public UiCallbackWrapper(UseCase.UseCaseCallback<Q, P> callback,
                                 UseCaseHandler useCaseHandler) {
            mCallback = callback;
            mUseCaseHandler = useCaseHandler;
        }

        @Override
        public void onResponse(Q request, P response) {
            mUseCaseHandler.notifyResponse(request, response, mCallback);
        }
    }
}

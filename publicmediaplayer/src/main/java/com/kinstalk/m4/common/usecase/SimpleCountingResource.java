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

import android.support.annotation.NonNull;

import java.util.concurrent.atomic.AtomicLong;

public final class SimpleCountingResource {
    public final static long COUNTING_ID_BASE = 2000;
    private final String mResourceName;

    private final AtomicLong mCounter;

    /**
     * Creates a SimpleCountingIdlingResource
     *
     * @param resourceName the resource name this resource should report to Espresso.
     */
    public SimpleCountingResource(@NonNull String resourceName, long startValue) {
        mResourceName = resourceName;
        mCounter = new AtomicLong(startValue);
    }

    public String getName() {
        return mResourceName;
    }

    /**
     * Increments the count of in-flight transactions to the resource being monitored.
     */
    public void increment() {
        mCounter.getAndIncrement();
    }

    public synchronized long next() {
        return mCounter.getAndIncrement();
    }
}

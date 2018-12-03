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

public class RequestValueResource {

    private static final String RESOURCE = "REQUEST_INDEX";
    private static final long REQUEST_VALUE_BASE = 2000;

    private static final SimpleCountingResource DEFAULT_INSTANCE =
            new SimpleCountingResource(RESOURCE,
                    (long) (Math.random() * (double) REQUEST_VALUE_BASE) + REQUEST_VALUE_BASE);

    public static void increment() {
        DEFAULT_INSTANCE.increment();
    }

    public static synchronized long next() {
        return DEFAULT_INSTANCE.next();
    }
}

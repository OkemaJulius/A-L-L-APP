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

package com.kinstalk.m4.common.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.UseCase;


/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 */
public abstract class MusicBaseCase<Q extends UseCase.RequestValue, P extends UseCase.ResponseValue> extends UseCase<Q, P> {
    protected Context mContext;

    public MusicBaseCase(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * data passed to a request.
     */
    public static abstract class RequestValue extends UseCase.RequestValue {
        public RequestValue() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("MusicBaseCase.RequestValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }

    /**
     * data in a response.
     */
    public static abstract class ResponseValue extends UseCase.ResponseValue {
        private static int MUSIC_CONTROL_BASE_ERR = 0x100;

        public ResponseValue() {
            super();
        }

        public ResponseValue(int error) {
            this();
            mError = error;
        }

        @Override
        public String toString() {
            return "ResponseValue{" +
                    "} " + super.toString();
        }
    }
}

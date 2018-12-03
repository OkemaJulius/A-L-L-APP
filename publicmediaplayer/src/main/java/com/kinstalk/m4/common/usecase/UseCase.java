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

import android.content.Context;


/**
 * Use cases are the entry points to the domain layer.
 *
 * @param <Q> the request type
 * @param <P> the response type
 */
public abstract class UseCase<Q extends UseCase.RequestValue, P extends UseCase.ResponseValue> {

    private Q mRequestValues;

    private boolean mForeground = true;

    private UseCaseCallback<Q, P> mUseCaseCallback;

    public Q getRequestValues() {
        return mRequestValues;
    }

    public void setRequestValues(Q requestValues) {
        mRequestValues = requestValues;
    }

    public UseCaseCallback<Q, P> getUseCaseCallback() {
        return mUseCaseCallback;
    }

    public boolean isForeground() {
        return mForeground;
    }

    public void setForeground(boolean foreground) {
        mForeground = foreground;
    }

    public void setUseCaseCallback(UseCaseCallback<Q, P> useCaseCallback) {
        mUseCaseCallback = useCaseCallback;
    }

    void run() {
        executeUseCase(mRequestValues);
    }

    protected abstract void executeUseCase(Q requestValues);

    public interface UseCaseCallback<P, Q> {
        void onResponse(P request, Q response);
    }

    /**
     * data passed to a request.
     */
    public static abstract class RequestValue {
        private long mRequestIndex = RequestValueResource.next();

        public long getRequestIndex() {
            return mRequestIndex;
        }

        public abstract UseCase getUseCase(Context context);

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("UseCase.RequestValue {");
            sb.append("mRequestIndex=" + mRequestIndex);
            sb.append("}");
            return sb.toString();
        }
    }

    /**
     * data received from a request.
     */
    public static abstract class ResponseValue {
        public static int NO_ERROR = 0;
        public static int ERROR_NULL_RESULT = 1;

        protected int mError = NO_ERROR;
        private RequestValue mRequest;

        public boolean isError() {
            return mError != NO_ERROR;
        }

        public int getError() {
            return mError;
        }

        public void setError(int error) {
            mError = error;
        }

        public RequestValue getRequest() {
            return mRequest;
        }

        public void setRequest(RequestValue request) {
            this.mRequest = request;
        }

        @Override
        public String toString() {
            return "UseCase.ResponseValue{" +
                    "mError=" + mError +
                    ", mRequest=" + mRequest +
                    '}';
        }
    }
}

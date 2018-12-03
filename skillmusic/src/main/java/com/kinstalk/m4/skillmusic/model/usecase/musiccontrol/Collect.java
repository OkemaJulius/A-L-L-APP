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

package com.kinstalk.m4.skillmusic.model.usecase.musiccontrol;

import android.content.Context;

import com.kinstalk.m4.common.usecase.musiccontrol.MusicBaseCase;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.skillmusic.model.entity.SongInfo;
import com.kinstalk.m4.skillmusic.ui.source.QAIMusicConvertor;


/**
 * Marks a task as Music Collect.
 */
public class Collect extends MusicBaseCase<Collect.RequestValue, Collect.ResponseValue> {
    public Collect(Context context) {
        super(context);
    }

    @Override
    protected void executeUseCase(Collect.RequestValue requestValues) {
        if (requestValues == null) {
            QLog.w(this, "executeUseCase, null parameter - " + requestValues);
        } else {
            SongInfo songInfo = requestValues.getSongInfo();
            QLog.w(this, "executeUseCase songInfo:" + songInfo);
            if (null != songInfo) {
                QAIMusicConvertor.getInstance().setFavorite(songInfo.getPlayId(), requestValues.isCollect());
            }

            getUseCaseCallback().onResponse(requestValues, new ResponseValue());
        }

    }

    public static final class RequestValue extends MusicBaseCase.RequestValue {
        private boolean mCollect;
        private SongInfo mSongInfo;

        public RequestValue(SongInfo songInfo, boolean collect) {
            super();
            mCollect = collect;
            mSongInfo = songInfo;
        }

        public boolean isCollect() {
            return mCollect;
        }

        public SongInfo getSongInfo() {
            return mSongInfo;
        }

        public Collect getUseCase(Context context) {
            return new Collect(context);
        }
    }


    public static final class ResponseValue extends MusicBaseCase.ResponseValue {
        public ResponseValue() {
            super();
        }

        public ResponseValue(int error) {
            super(error);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Collect.ResponseValue {");
            sb.append("super=" + super.toString());
            sb.append("}");
            return sb.toString();
        }
    }
}

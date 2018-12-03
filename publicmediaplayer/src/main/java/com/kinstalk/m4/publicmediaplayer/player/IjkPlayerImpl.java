package com.kinstalk.m4.publicmediaplayer.player;

import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;

import com.danikula.videocache.CacheListener;
import com.kinstalk.m4.common.utils.QLog;

import java.io.File;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static android.content.ContentResolver.SCHEME_FILE;


public class IjkPlayerImpl extends IPlayerImpl implements CacheListener {
    private IjkMediaPlayer mRealPlayer;
    private int mCachePercent = 0;
    private boolean isOnPrepared = false;

    private IjkPlayerImpl() {
        mRealPlayer = new IjkMediaPlayer();
        mRealPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mRealPlayer.setOnCompletionListener(new OnCompletionListenerWithVersion() {
            @Override
            public void onCompletion(IMediaPlayer mediaPlayer) {
                if (mediaPlayer != mRealPlayer) {
                    QLog.w(IjkPlayerImpl.this, "onCompletion: ignore old player callback");
                    return;
                }
                QLog.w(IjkPlayerImpl.this, "onCompletion");
                mOnCompletionListener.onCompletion(IjkPlayerImpl.this);
            }
        });
        mRealPlayer.setOnPreparedListener(new OnPreparedListenerWithVersion() {
            @Override
            public void onPrepared(IMediaPlayer mediaPlayer) {
                if (mediaPlayer != mRealPlayer) {
                    QLog.w(IjkPlayerImpl.this, "onPrepared: ignore old player callback");
                    return;
                }
                isOnPrepared = true;
                QLog.w(IjkPlayerImpl.this, "onPrepared");
                mOnPreparedListener.onPrepared(IjkPlayerImpl.this);
            }
        });
        mRealPlayer.setOnErrorListener(new OnErrorListenerWithVersion() {
            @Override
            public boolean onError(IMediaPlayer mediaPlayer, int i, int i1) {
                if (mediaPlayer != mRealPlayer) {
                    QLog.w(IjkPlayerImpl.this, "onError: ignore old player callback");
                    return false;
                }
                QLog.w(IjkPlayerImpl.this, "onError: code - " + i);
                return mOnErrorListener.onError(IjkPlayerImpl.this, i, i1);
            }
        });
        mRealPlayer.setOnSeekCompleteListener(new OnSeekListenerWithVersion() {
            @Override
            public void onSeekComplete(IMediaPlayer mediaPlayer) {
                if (mediaPlayer != mRealPlayer) {
                    QLog.w(IjkPlayerImpl.this, "onSeekComplete: ignore old player callback");
                }
                QLog.w(IjkPlayerImpl.this, "onSeekComplete: done");
                mOnSeekCompleteListener.onSeekComplete(IjkPlayerImpl.this);
            }
        });
        mRealPlayer.setOnBufferingUpdateListener(new OnBufferingListenerWithVersion() {
            @Override
            public void onBufferingUpdate(IMediaPlayer mp, int percent) {
                if (mp != mRealPlayer) {
                    QLog.w(IjkPlayerImpl.this, "onBufferingUpdate: ignore old player callback");
                }
                QLog.d(IjkPlayerImpl.this, "onBufferingUpdate: percent - " + percent);
                mCachePercent = percent;
            }
        });
    }

    public static synchronized IPlayerImpl getInstance() {
        if (mInstance == null) {
            mInstance = new IjkPlayerImpl();
        }
        return mInstance;
    }

    @Override
    public synchronized void release() {
        QLog.d(this, "release");
        if (mRealPlayer != null) {
            mRealPlayer.release();
            mRealPlayer = null;
            mInstance = null;
            isOnPrepared = false;

            QLog.d(this, "release done!");
        } else {
            QLog.w(this, "release: already released before");
        }
    }

    @Override
    public void seekTo(long msec) {
        QLog.d(this, "seekTo: msec - " + msec);
        if (mRealPlayer != null) {
            try {
                mRealPlayer.seekTo((int) msec);
                QLog.d(this, "seekTo done!");
            } catch (IllegalStateException e) {
                QLog.e(this, e, "seekTo: ignore");
            }
        } else {
            QLog.w(this, "seekTo: null player");
        }

    }

    @Override
    public void start() {
        QLog.d(this, "start");
        if (mRealPlayer != null) {
            try {
                mRealPlayer.start();
                QLog.d(this, "start done!");
            } catch (IllegalStateException e) {
                e.printStackTrace();
                QLog.e(this, e, "start: ignore");
                throw new AndroidRuntimeException(e);
            }
        } else {
            QLog.w(this, "start: null player");
        }
    }

    @Override
    public void pause() {
        QLog.d(this, "pause");
        if (mRealPlayer != null) {
            if (mRealPlayer.isPlaying()) {
                mRealPlayer.pause();
                QLog.d(this, "pause done!");
            }
        } else {
            QLog.w(this, "pause: null player");
        }
    }

    @Override
    public void stop() {
        QLog.d(this, "stop");
        if (mRealPlayer != null) {
            if (mRealPlayer.isPlaying()) {
                mRealPlayer.stop();
                mRealPlayer.reset();
                QLog.d(this, "stop done!");
            }
        } else {
            QLog.w(this, "stop: null player");
        }
    }

    @Override
    public int getDuration() {
        if (mRealPlayer != null) {
            if (isOnPrepared) {
                long pos = mRealPlayer.getDuration();
                QLog.d(this, "getDuration pos:" + pos);
                return (int) pos;
            } else {
                QLog.w(this, "getDuration not onPrepared");
            }
        } else {
            QLog.w(this, "getDuration: null player");
        }
        return 0;
    }

    @Override
    public int getCachePercent() {
        if (mRealPlayer != null) {
            return mCachePercent;
        } else {
            QLog.w(this, "getCachePercent: null player");
        }
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (mRealPlayer != null) {
            if (isOnPrepared) {
                long pos = mRealPlayer.getCurrentPosition();
                QLog.d(this, "getCurrentPosition pos:" + pos);
                return (int) pos;
            } else {
                QLog.w(this, "getCurrentPosition not onPrepared");
            }
        } else {
            QLog.w(this, "getCurrentPosition: null player");
        }
        return 0;
    }

    @Override
    public void setDataSource(String dataSource) {
        mCachePercent = 0;
        isOnPrepared = false;
        if (!TextUtils.isEmpty(dataSource)) {
            Uri uri = Uri.parse(dataSource);
            if (uri != null) {
                if (TextUtils.isEmpty(uri.getScheme())
                        || TextUtils.equals(uri.getScheme(), SCHEME_FILE)) {
                    mCachePercent = 100;
                }
            }
        }

        String playUrl = dataSource;
//        if (dataSource.toLowerCase().contains(".m3u8".toLowerCase())) {
//            playUrl = dataSource;
//        } else {
//            playUrl = SongFileCacheController.getInstance().getRequestUrl(
//                    dataSource);
//        }

        QLog.d(this, "setDataSource, playUrl:" + playUrl);
        SongFileCacheController.getInstance().getCacheServer().registerCacheListener(this, dataSource);
        try {
            if (mRealPlayer != null) {
                QLog.d(this, "setDataSource, isPlaying:" + mRealPlayer.isPlaying());
                if (mRealPlayer.isPlaying()) {
                    mRealPlayer.stop();
                }
                mRealPlayer.reset();
                mRealPlayer.setDataSource(playUrl);
                mRealPlayer.prepareAsync();
                QLog.d(this, "setDataSource done!");
            } else {
                QLog.w(this, "setDataSource: null player");
            }
        } catch (Exception e) {
            QLog.e(this, e, "setDataSource: error");
            e.printStackTrace();
            throw new AndroidRuntimeException(e);
        }
    }

    @Override
    public boolean canRecovery() {
        return true;
    }

    @Override
    public void onCacheAvailable(File cacheFile, String url, int percentsAvailable) {
        QLog.d(this, "onCacheAvailable percentsAvailable:" + percentsAvailable);
    }

    private static abstract class OnCompletionListenerWithVersion implements IjkMediaPlayer.OnCompletionListener {
        public OnCompletionListenerWithVersion() {
            super();
        }
    }

    private static abstract class OnPreparedListenerWithVersion implements IjkMediaPlayer.OnPreparedListener {
        public OnPreparedListenerWithVersion() {
            super();
        }
    }

    private static abstract class OnErrorListenerWithVersion implements IjkMediaPlayer.OnErrorListener {
        public OnErrorListenerWithVersion() {
            super();
        }
    }

    private static abstract class OnSeekListenerWithVersion implements IjkMediaPlayer.OnSeekCompleteListener {
        public OnSeekListenerWithVersion() {
            super();
        }
    }

    private static abstract class OnBufferingListenerWithVersion implements IjkMediaPlayer.OnBufferingUpdateListener {
        public OnBufferingListenerWithVersion() {
            super();
        }
    }
}

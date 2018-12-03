package com.kinstalk.m4.publicmediaplayer.player;

import java.io.IOException;


public abstract class IPlayerImpl {
    protected static IPlayerImpl mInstance;

    protected OnSeekCompleteListener mOnSeekCompleteListener;
    protected OnErrorListener mOnErrorListener;
    protected OnPreparedListener mOnPreparedListener;
    protected OnCompletionListener mOnCompletionListener;

    public abstract void release();

    public abstract void seekTo(long msec);

    public abstract void start();

    public abstract void pause();

    public abstract void stop();

    public abstract int getDuration();

    public abstract int getCachePercent();

    public abstract int getCurrentPosition();

    public abstract void setDataSource(String dataSource) throws IOException;

    public boolean canRecovery() {
        return false;
    }

    protected IPlayerImpl() {
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener var1) {
        this.mOnSeekCompleteListener = var1;
    }

    public void setOnErrorListener(OnErrorListener var1) {
        this.mOnErrorListener = var1;
    }

    public void setOnPreparedListener(OnPreparedListener var1) {
        this.mOnPreparedListener = var1;
    }

    public void setOnCompletionListener(OnCompletionListener var1) {
        this.mOnCompletionListener = var1;
    }

    public interface OnErrorListener {
        boolean onError(IPlayerImpl player, int errCode, int extra);
    }

    public interface OnSeekCompleteListener {
        void onSeekComplete(IPlayerImpl player);
    }

    public interface OnCompletionListener {
        void onCompletion(IPlayerImpl player);
    }

    public interface OnPreparedListener {
        void onPrepared(IPlayerImpl player);
    }
}

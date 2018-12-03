package com.kinstalk.m4.publicmediaplayer.resource;

/**
 * Created by libin on 2016/9/29.
 */

public class PriorityDelayTask<T> implements Comparable<PriorityDelayTask<T>> {
    public static final long PRIORITY_FOREGROUND = Long.MAX_VALUE / 2;
    public static final long PRIORITY_BACKGROUND = 0;

    public static final long BACKGROUND_DELAY = 5 * 1000;

    // 0 means run task now
    private long mScheduledAt;
    // Big value means high mPriority
    private long mPriority;
    private T mTask;
    private Object mExtra;
    private long mTaskTimestamp;
    private boolean mClearCache;

    public PriorityDelayTask(long scheduledAt, long priority, T task, Object extra, long taskTimestamp, boolean clearCache) {
        mScheduledAt = scheduledAt;
        this.mPriority = priority;
        mTask = task;
        mExtra = extra;
        mTaskTimestamp = taskTimestamp;
        mClearCache = clearCache;
    }

    public Object getExtra() {
        return mExtra;
    }

    public void setExtra(Object extra) {
        mExtra = extra;
    }

    public boolean isClearCache() {
        return mClearCache;
    }

    public void setClearCache(boolean clearCache) {
        mClearCache = clearCache;
    }

    public long getScheduledAt() {
        return mScheduledAt;
    }

    public long getPriority() {
        return mPriority;
    }

    public void setPriority(long priority) {
        this.mPriority = priority;
    }

    public T getTask() {
        return mTask;
    }

    public long getTaskTimestamp() {
        return mTaskTimestamp;
    }

    @Override
    public int compareTo(PriorityDelayTask<T> another) {
        long timestampDelta = (this.getScheduledAt() - another.getScheduledAt());
        long priorityDelta = (another.mPriority - this.mPriority);
        long taskTimestampDelta = another.getTaskTimestamp() - this.getTaskTimestamp();
        return (int) (timestampDelta != 0 ? timestampDelta :
                (priorityDelta != 0 ? priorityDelta : taskTimestampDelta));
    }

    @Override
    public String toString() {
        return "PriorityDelayTask{" +
                "mScheduledAt=" + mScheduledAt +
                ", mPriority=" + mPriority +
                ", mTask=" + mTask +
                ", mExtra=" + mExtra +
                ", mTaskTimestamp=" + mTaskTimestamp +
                ", mClearCache=" + mClearCache +
                '}';
    }
}

package com.kinstalk.m4.publicmediaplayer.resource;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;

import com.kinstalk.m4.common.usecase.NamedThreadFactory;
import com.kinstalk.m4.common.utils.QLog;
import com.kinstalk.m4.publicmediaplayer.player.RetryManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by libin on 2016/9/28.
 */

public abstract class KeyedResourcePoolBase<K, V> implements KeyedPoolInterface<K, V> {
    private final static int THREAD_POOL = 1;
    private final static int MAX_THREAD_POLL = 3;
    private final static long IDLE_TIME_OUT = 5;
    protected final Object mLock = new Object();
    private final ConcurrentHashMap<K, Set<CallbackMoreInfo>> mKeyedCallbacks =
            new ConcurrentHashMap<K, Set<CallbackMoreInfo>>(1, 0.9f, 1);
    private final ConcurrentHashMap<K, Boolean> mLoadingStatus =
            new ConcurrentHashMap<K, Boolean>(MAX_THREAD_POLL, 0.9f, 1);
    private final ConcurrentHashMap<K, RetryManager> mErrorRetryManager =
            new ConcurrentHashMap<K, RetryManager>(1, 0.9f, 1);
    private final Set<Listener> mListeners = Collections.newSetFromMap(
            new ConcurrentHashMap<Listener, Boolean>(1, 0.9f, 1));
    ThreadPoolExecutor mThreadPoolExecutor;
    private int mCapacity;
    private int mLimitPerKey;
    private KeyedResCache<K, V> mKeyedResCache = null;
    private long mRequestNum = 0;
    private long mCacheHitNum = 0;
    private long mLoadingHitNum = 0;
    private boolean mOnetimeCache;
    private Handler mRetryHandler;
    private String mRetryPolicy = "max_retries=10,1000,2000,5000";
    private PriorityTaskQueue<K> mPendingFetchTask;

    public KeyedResourcePoolBase(int capacity, int limitPerKey, boolean onetimeCache, Looper loop) {
        mCapacity = capacity;
        mLimitPerKey = limitPerKey;
        mOnetimeCache = onetimeCache;
        mKeyedResCache = createResCache(capacity, limitPerKey);
        mThreadPoolExecutor = new ThreadPoolExecutor(THREAD_POOL, MAX_THREAD_POLL, IDLE_TIME_OUT,
                TimeUnit.SECONDS, new LinkedBlockingQueue(), new NamedThreadFactory(getName()));
        mRetryHandler = new PendingTaskHandler(loop);
        mPendingFetchTask = new PriorityTaskQueue<>();
    }

    protected abstract KeyedResCache<K, V> createResCache(int capacity, int limitPerKey);

    protected abstract DataLoadResult<K, V> fetchValue(K key, Object extra);

    private void addCallback(K key, KeyedResourcePool.Callback callback, int max, long priority) {
        synchronized (mLock) {
            Set<CallbackMoreInfo> callbackSet = mKeyedCallbacks.get(key);
            if (callbackSet == null) {
                callbackSet = Collections.newSetFromMap(
                        new ConcurrentHashMap<CallbackMoreInfo, Boolean>(1, 0.9f, 1));
                mKeyedCallbacks.put(key, callbackSet);
            }
            callbackSet.add(new CallbackMoreInfo(callback, max, priority));
        }
    }

    @Override
    public void prefetchResource(K key) {
        prefetchResource(key, null);
    }

    @Override
    public void prefetchResource(K key, Object extra) {
        boolean needFetch = true;
        synchronized (mLock) {
            if (mKeyedResCache.isFull(key)) {
                QLog.d(this, "prefetchResource, cache full, just return!");
                needFetch = false;
            } else {
                Boolean loading = mLoadingStatus.get(key);
                if (loading != null && loading.booleanValue()) {
                    QLog.d(this, "prefetchResource, is loading, just return!");
                    needFetch = false;
                }
            }
            if (needFetch) {
                mErrorRetryManager.remove(key);
                tryToFetch(key, extra, 0, PriorityDelayTask.PRIORITY_BACKGROUND, false, SystemClock.uptimeMillis());
            }
        }
    }

    @Override
    public void getResource(K key, KeyedResourcePool.Callback callback) {
        getResource(key, null, 1, callback);
    }

    @Override
    public void getResource(K key, Object extra, KeyedResourcePool.Callback callback) {
        getResource(key, extra, 1, callback);
    }

    @Override
    public void getResource(K key, int max, KeyedResourcePool.Callback callback) {
        getResourceInternal(key, null, max, mOnetimeCache, PriorityDelayTask.PRIORITY_FOREGROUND, false, callback, false);
    }

    @Override
    public void getResource(K key, Object extra, int max, KeyedResourcePool.Callback callback) {
        getResourceInternal(key, extra, max, mOnetimeCache, PriorityDelayTask.PRIORITY_FOREGROUND, false, callback, false);
    }

    @Override
    public void getResource(K key, int max, KeyedResourcePool.Callback callback, boolean clearCache) {
        getResourceInternal(key, null, max, mOnetimeCache, PriorityDelayTask.PRIORITY_FOREGROUND, false, callback, clearCache);
    }

    @Override
    public void getResource(K key, Object extra, int max, KeyedResourcePool.Callback callback, boolean clearCache) {
        getResourceInternal(key, extra, max, mOnetimeCache, PriorityDelayTask.PRIORITY_FOREGROUND, false, callback, clearCache);
    }

    @Override
    public void remove(K key) {
        synchronized (mLock) {
            mKeyedResCache.remove(key);
        }
    }

    private void getResourceInternal(K key, Object extra, int max, boolean remove, long priority, boolean notifyAfterFetch,
                                     KeyedResourcePool.Callback callback, boolean clearCache) {
        boolean hasKey = false;
        Collection<V> values = null;
        synchronized (mLock) {
            if (!notifyAfterFetch) mRequestNum++;
            QLog.d(this, "getResourceInternal: clearCache - " + clearCache);
            hasKey = mKeyedResCache.containsKey(key);
            QLog.d(this, "getResourceInternal: hasKey - " + hasKey);
            hasKey = hasKey && (!clearCache);
            if (hasKey) {
                if (!notifyAfterFetch) mCacheHitNum++;
                values = mKeyedResCache.get(key, extra, max, remove);
                if (remove) {
                    if (!notifyAfterFetch) {
                        mErrorRetryManager.remove(key);
                    }
                    tryToFetch(key, extra, 0, PriorityDelayTask.PRIORITY_BACKGROUND, clearCache, SystemClock.uptimeMillis());
                }
            } else {
                addCallback(key, callback, max, priority);
                Boolean loading = mLoadingStatus.get(key);
                if (loading != null && loading.booleanValue()) {
                    if (!notifyAfterFetch) mLoadingHitNum++;
                    QLog.d(this, "getResource, is loading, just return!");
                } else {
                    if (!notifyAfterFetch) {
                        mErrorRetryManager.remove(key);
                    }
                    tryToFetch(key, extra, 0, priority, clearCache, SystemClock.uptimeMillis());
                }
            }
            if (!notifyAfterFetch) logStatistic();
        }
        if (hasKey) {
            QLog.d(this, "Return cached resource, key - " + key + " values - " + values);
            callback.onResponse(key, new DataLoadResult(DataLoadResultCode.RESULT_OK, key, values));
        }
    }

    @Override
    public Collection<V> getResourceSnapshot(K key, int max) {
        Collection<V> values;
        synchronized (mLock) {
            values = mKeyedResCache.get(key, null, max, false);
        }
        return values;
    }

    @Override
    public V getFirstSnapshot(K key) {
        Collection<V> values = getResourceSnapshot(key, 1);
        if (values != null) {
            Iterator<V> iterator = values.iterator();
            if (iterator.hasNext()) {
                return iterator.next();
            }
        }
        return null;
    }

    @Override
    public void update(K key, Collection<V> values) {
        mKeyedResCache.update(key, values);
    }

    @Override
    public void updateOnlyOne(K key, V value) {
        ArrayList<V> values = new ArrayList<>(1);
        values.add(value);
        update(key, values);
    }

    private void logStatistic() {
        try {
            QLog.d(this, "Hit rate - %.0f%%, Cache hit rate - %.0f%%, Loading hit rate - %.0f%%",
                    (float) (mLoadingHitNum + mCacheHitNum) * 100f / (float) mRequestNum,
                    (float) (mCacheHitNum) * 100f / (float) mRequestNum,
                    (float) (mLoadingHitNum) * 100f / (float) mRequestNum);
        } catch (Exception e) {
            mRequestNum = 0;
            mLoadingHitNum = 0;
            mCacheHitNum = 0;
        }
    }

    private boolean notifyPendingCallback(K key, Object extra, DataLoadResultCode resultCode) {
        boolean notified = false;
        Set<CallbackMoreInfo> callbackSet = null;
        synchronized (mLock) {
            callbackSet = mKeyedCallbacks.remove(key);
        }
        if (callbackSet != null) {
            notified = true;
            Iterator<CallbackMoreInfo> iterator = callbackSet.iterator();
            while (iterator.hasNext()) {
                CallbackMoreInfo callbackDetail = iterator.next();
                KeyedResourcePool.Callback callback = callbackDetail.mCallback;
                if (callback != null) {
                    if (resultCode != null && resultCode.isError()) {
                        QLog.d(this, "Notify Callback with error and no more try. resultCode - "
                                + resultCode);
                        callback.onResponse(key, new DataLoadResult<K, V>(resultCode, key, (V) null));
                    } else {
                        getResourceInternal(key, extra, callbackDetail.mLimit, mOnetimeCache,
                                callbackDetail.mPriority, true, callback, false);
                    }
                }
            }
        }
        return notified;
    }

    private void tryToFetch(final K key, Object extra, long delay, long priority, boolean clearCache, long taskTimestamp) {
        QLog.d(this, "tryToFetch, key - " + key + ", extra - " + extra
                        + " delay - %d, priority - %d, clearCache - %b, taskTimestamp - %d",
                delay, priority, clearCache, taskTimestamp);
        if (priority < PriorityDelayTask.PRIORITY_FOREGROUND && delay < PriorityDelayTask.BACKGROUND_DELAY) {
            delay += PriorityDelayTask.BACKGROUND_DELAY;
        }
        long now = SystemClock.uptimeMillis();
        long timeStamp = (delay == 0 ? 0 : (now + delay));
        PriorityDelayTask<K> task = new PriorityDelayTask<>(timeStamp, priority, key, extra, taskTimestamp, clearCache);
        mRetryHandler.removeMessages(PendingTaskHandler.EVENT_CONSUME_PENDING);
        synchronized (mLock) {
            mPendingFetchTask.offer(task);
        }
        mRetryHandler.sendEmptyMessage(PendingTaskHandler.EVENT_CONSUME_PENDING);
    }

    private void onValueFetched(K key, DataLoadResult<K, V> values, final PriorityDelayTask<K> task) {
        boolean needNotify = false;
        synchronized (mLock) {
            QLog.d(this, "onValueFetched, key - " + key + " values - " + values + " task - " + task);
            if (values == null || task == null) {
                return;
            }
            synchronized (mKeyedResCache) {
                if (key != null && values != null && values.getResult() != null
                        && values.getResult().size() > 0) {
                    QLog.d(this, "Insert to cache map, key - " + key + " values - " + values);
                    if (task.isClearCache()) {
                        QLog.d(this, "onValueFetched: need remove cache of key - " + key);
                        mKeyedResCache.remove(key);
                    }
                    mKeyedResCache.add(key, values.getResult());
                    needNotify = true;
                }
            }
            mLoadingStatus.remove(key);
        }
        if (needNotify) {
            // Try to notify callback with new result!
            notifyPendingCallback(key, task.getExtra(), null);
        }
        if (values.getCode().isError() && !values.getCode().allowLoadMore()) {
            QLog.d(this, "Error and can't load more, code - " + values.getCode());
            notifyPendingCallback(key, task.getExtra(), values.getCode());
        } else {
            if (values.getCode() == DataLoadResultCode.RESULT_OK) {
                synchronized (mLock) {
                    mErrorRetryManager.remove(key);
                    prefetchResource(key);
                }
            } else {
                RetryManager rm = mErrorRetryManager.get(key);
                if (rm == null) {
                    rm = new RetryManager();
                    rm.configure(mRetryPolicy);
                    mErrorRetryManager.put(key, rm);
                }
                if (rm.isRetryNeeded()) {
                    rm.increaseRetryCount();
                    long delay = rm.getRetryTimer();
                    tryToFetch(key, task.getExtra(), delay, task.getPriority(), false, task.getTaskTimestamp());
                } else {
                    QLog.d(this, "No more retry");
                }
            }
        }
    }

    private void executeFetchTask(final PriorityDelayTask<K> task) {
        mThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if (task == null) {
                    QLog.w(this, "executeFetchTask: task is null.");
                    return;
                }
                final K key = task.getTask();
                QLog.d(this, "executeFetchTask, key - " + key);
                if (key == null) {
                    QLog.d(this, "executeFetchTask, ignore empty key - " + key);
                    return;
                }
                synchronized (mLock) {
                    Boolean loading = mLoadingStatus.get(key);
                    if (loading != null && loading.booleanValue()) {
                        QLog.d(this, "executeFetchTask, already loading, ignore key - " + key);
                        return;
                    }
                    boolean isFull = mKeyedResCache.isFull(key);
                    boolean clearCache = task.isClearCache();
                    QLog.d(this, "executeFetchTask: isFull - %b, clearCache - %b", isFull, clearCache);
                    if (isFull && !clearCache) {
                        QLog.d(this, "executeFetchTask, already full, ignore key - " + key);
                        return;
                    }
                    mLoadingStatus.put(key, true);
                }
                DataLoadResult<K, V> values = fetchValue(key, task.getExtra());
                notifyListeners(key, values == null ? null : values.getResult());
                QLog.d(KeyedResourcePoolBase.this, "executeFetchTask, fetched, values - " + values);
                onValueFetched(key, values, task);
            }
        });
    }

    private void notifyListeners(K key, Collection<V> values) {
        for (Listener listener : mListeners) {
            listener.onItemAdded(key, values);
        }
    }

    public abstract static class KeyedResCache<K, V> {
        private final int mKeyCapacity;
        private final int mLimitPerKey;

        public KeyedResCache(int keyCapacity, int limitPerKey) {
            mKeyCapacity = keyCapacity;
            mLimitPerKey = limitPerKey;
        }

        public int getKeyCapacity() {
            return mKeyCapacity;
        }

        public int getLimitPerKey() {
            return mLimitPerKey;
        }

        public abstract boolean containsKey(K key);

        public abstract Collection<V> get(K key, Object extra, int max, boolean remove);

        public abstract void add(K key, Collection<V> values);

        public void add(K key, V value) {
            ArrayList<V> values = new ArrayList<>(1);
            values.add(value);
            add(key, values);
        }

        public abstract boolean isFull(K key);

        public abstract void remove(K key);

        public abstract void update(K key, Collection<V> values);

        public void update(K key, V value) {
            ArrayList<V> values = new ArrayList<>(1);
            values.add(value);
            update(key, values);
        }
    }

    private class CallbackMoreInfo {
        Callback<K, V> mCallback;
        int mLimit;
        long mPriority;

        public CallbackMoreInfo(Callback<K, V> callback, int limit, long priority) {
            mCallback = callback;
            mLimit = limit;
            mPriority = priority;
        }
    }

    private class PendingTaskHandler extends Handler {
        private final static int EVENT_CONSUME_PENDING = 0;

        public PendingTaskHandler(Looper looper) {
            super(looper);
        }

        private void consumeTaskQueue() {
            synchronized (mLock) {
                long now = SystemClock.uptimeMillis();
                Iterator<PriorityDelayTask<K>> iterator = mPendingFetchTask.iterator();
                while (iterator.hasNext()) {
                    PriorityDelayTask<K> task = iterator.next();
                    if (task.getScheduledAt() <= now) {
                        iterator.remove();
                        QLog.d(this, "consumeTaskQueue, task - " + task);
                        executeFetchTask(task);
                    } else {
                        long delay = task.getScheduledAt() - now;
                        QLog.d(this, "schedule next task after delay - " + delay);
                        sendEmptyMessageDelayed(EVENT_CONSUME_PENDING, delay);
                        return;
                    }
                }
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case EVENT_CONSUME_PENDING:
                    consumeTaskQueue();
                    break;
                default:
                    QLog.w(this, "Unknown msg - " + msg);
                    break;
            }
        }
    }

    @Override
    public void addListener(Listener listener) {
        mListeners.remove(listener);
        mListeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    protected String getName() {
        return "ResourcePoolBase";
    }
}

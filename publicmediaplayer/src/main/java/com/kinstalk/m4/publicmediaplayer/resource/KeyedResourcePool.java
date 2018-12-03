package com.kinstalk.m4.publicmediaplayer.resource;

import android.os.Looper;

import com.kinstalk.m4.common.utils.QLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by libin on 2016/9/26.
 */

public abstract class KeyedResourcePool<K, V> extends KeyedResourcePoolBase<K, V> {

    private final static int DEFAULT_LIMIT_PER_KEY = 1;

    public KeyedResourcePool(int capacity, Looper looper) {
        this(capacity, DEFAULT_LIMIT_PER_KEY, looper);
    }

    public KeyedResourcePool(int capacity, boolean onetimeCache, Looper looper) {
        this(capacity, DEFAULT_LIMIT_PER_KEY, onetimeCache, looper);
    }

    public KeyedResourcePool(int capacity, int limitPerKey, Looper looper) {
        super(capacity, limitPerKey, true, looper);
    }

    public KeyedResourcePool(int capacity, int limitPerKey, boolean onetimeCache, Looper looper) {
        super(capacity, limitPerKey, onetimeCache, looper);
    }

    @Override
    protected KeyedResCache createResCache(int capacity, int limitPerKey) {
        return new MemoryKeyedResCache<K, V>(capacity, limitPerKey);
    }

    public static class MemoryKeyedResCache<K, V> extends KeyedResCache<K, V> {
        protected FIFOLinkedHashMap<K, ArrayBlockingQueue<V>> mKeyedResHashMap;

        public MemoryKeyedResCache(int keyCapacity, int limitPerKey) {
            super(keyCapacity, limitPerKey);
            mKeyedResHashMap = new FIFOLinkedHashMap<K, ArrayBlockingQueue<V>>(getKeyCapacity());
        }

        @Override
        public boolean containsKey(K key) {
            ArrayBlockingQueue<V> values = mKeyedResHashMap.get(key);
            return values != null && values.size() > 0;
        }

        @Override
        public Collection<V> get(K key, Object extra, int max, boolean remove) {
            QLog.d(this, "get, key - " + key + " extra - " + extra
                    + " max - %d, remove - %b", max, remove);
            return fetch(key, max, remove);
        }

        @Override
        public void update(K key, Collection<V> values) {
            if (values == mKeyedResHashMap.get(key)) {
                return;
            }
            mKeyedResHashMap.remove(key);
            add(key, values);
        }

        @Override
        public void add(K key, Collection<V> values) {
            if (values == null || values.size() == 0) {
                QLog.w(this, "ignore add since values is empty!");
                return;
            }
            ArrayBlockingQueue<V> cacheValues = mKeyedResHashMap.get(key);
            if (cacheValues == null) {
                cacheValues = new ArrayBlockingQueue<V>(getLimitPerKey());
                mKeyedResHashMap.put(key, cacheValues);
            }
            try {
                cacheValues.addAll(values);
            } catch (Exception e) {
                if (cacheValues.size() == getLimitPerKey()) {
                    QLog.w(this, "ignore add error since cache is full!");
                } else {
                    QLog.e(this, e, "error in addAll!");
                }
            }
        }

        @Override
        public boolean isFull(K key) {
            ArrayBlockingQueue<V> values = mKeyedResHashMap.get(key);
            return (values != null) && (values.size() >= getLimitPerKey());
        }

        @Override
        public void remove(K key) {
            mKeyedResHashMap.remove(key);
        }

        private Collection<V> fetch(K key, int max, boolean remove) {
            ArrayBlockingQueue<V> values = mKeyedResHashMap.get(key);
            Collection<V> result = null;
            if (values != null) {
                if (values.size() <= max) {
                    QLog.d(this, "fetch, return size - " + values.size());
                    if (remove) {
                        result = mKeyedResHashMap.remove(key);
                    } else {
                        result = values;
                    }
                } else {
                    result = new ArrayList<V>(max);
                    int i = 0;
                    Iterator<V> iterator = values.iterator();
                    while (iterator.hasNext() && i < max) {
                        i++;
                        result.add(iterator.next());
                        if (remove) {
                            iterator.remove();
                        }
                    }
                    QLog.d(this, "fetch, return size - " + i);
                }
            } else {
                QLog.d(this, "fetch, return null");
            }
            return result;
        }
    }

    private static class FIFOLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
        int mCapacityLimit;

        public FIFOLinkedHashMap(int capacityLimit) {
            super(capacityLimit + 1);
            mCapacityLimit = capacityLimit;
        }

        @Override
        public V put(K key, V value) {
            V forReturn = super.put(key, value);
            if (super.size() > mCapacityLimit) {
                removeEldest();
            }

            return forReturn;
        }

        private void removeEldest() {
            Iterator<K> iterator = this.keySet().iterator();
            if (iterator.hasNext()) {
                this.remove(iterator.next());
            }
        }
    }

}

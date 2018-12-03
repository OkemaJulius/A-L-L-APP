package com.kinstalk.m4.publicmediaplayer.resource;

import java.util.Collection;

/**
 * Created by libin on 2016/10/14.
 */

public interface KeyedPoolInterface<K, V> {
    void prefetchResource(K key);

    void prefetchResource(K key, Object extra);

    void getResource(K key, KeyedResourcePool.Callback callback);

    void getResource(K key, Object extra, KeyedResourcePool.Callback callback);

    void getResource(K key, int max, KeyedResourcePool.Callback callback);

    void getResource(K key, Object extra, int max, KeyedResourcePool.Callback callback);

    void getResource(K key, int max, KeyedResourcePool.Callback callback, boolean clearCache);

    void getResource(K key, Object extra, int max, KeyedResourcePool.Callback callback,
                     boolean clearCache);

    Collection<V> getResourceSnapshot(K key, int max);

    void addListener(Listener listener);

    void removeListener(Listener listener);

    V getFirstSnapshot(K key);

    void update(K key, Collection<V> values);

    void updateOnlyOne(K key, V value);

    void remove(K key);

    interface Callback<K, V> {
        void onResponse(K key, DataLoadResult<K, V> result);
    }

    interface Listener<K, V> {
        void onItemAdded(K key, Collection<V> v);
    }
}

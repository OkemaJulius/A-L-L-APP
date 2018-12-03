package com.kinstalk.m4.publicmediaplayer.resource;


import com.kinstalk.m4.common.utils.QLog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Created by libin on 2016/9/29.
 */


public class DataLoadResult<K, V> {
    private DataLoadResultCode mCode;
    private K mRequestId;
    private Collection<V> mResult;
    private Object mExtra;

    public DataLoadResult(DataLoadResultCode code) {
        mCode = code;
        mResult = null;
        mRequestId = null;
    }

    public DataLoadResult(DataLoadResultCode code, K requestId, Collection<? extends V> result) {
        mCode = code;
        if (result != null) {
            mResult = new ArrayList<>(result.size());
            Iterator<? extends V> iterator = result.iterator();
            while (iterator.hasNext()) {
                mResult.add(iterator.next());
            }
        } else {
            mResult = null;
        }
        mRequestId = requestId;
    }

    public DataLoadResult(DataLoadResultCode code, Collection<? extends V> result) {
        this(code, null, result);
    }

    public DataLoadResult(DataLoadResultCode code, K requestId, V result) {
        mCode = code;
        if (result != null) {
            ArrayList<V> resultList = new ArrayList<>(1);
            resultList.add(result);
            mResult = resultList;
        } else {
            mResult = null;
        }
        mRequestId = requestId;
    }

    public DataLoadResult(DataLoadResultCode code, V result) {
        this(code, null, result);
    }

    public V getFirstResult() {
        QLog.d(this, "getFirstResult: mResult - " + mResult);
        if (mResult != null) {
            Iterator<V> iterator = mResult.iterator();
            if (iterator.hasNext()) {
                V result = iterator.next();
                QLog.d(this, "getFirstResult: result - " + result);
                return result;
            } else {
                QLog.i(this, "getFirstResult: no next");
            }
        }
        return null;
    }

    public K getRequestId() {
        return mRequestId;
    }

    public void setRequestId(K requestId) {
        mRequestId = requestId;
    }

    public DataLoadResultCode getCode() {
        return mCode;
    }

    public void setCode(DataLoadResultCode code) {
        mCode = code;
    }

    public Collection<V> getResult() {
        return mResult;
    }

    public void setResult(Collection<V> result) {
        mResult = result;
    }

    public Object getExtra() {
        return mExtra;
    }

    public void setExtra(Object extra) {
        mExtra = extra;
    }

    @Override
    public String toString() {
        return "DataLoadResult{" +
                "mCode=" + mCode +
                ", mRequestId=" + mRequestId +
                ", mResult=" + mResult +
                ", mExtra=" + mExtra +
                '}';
    }
}

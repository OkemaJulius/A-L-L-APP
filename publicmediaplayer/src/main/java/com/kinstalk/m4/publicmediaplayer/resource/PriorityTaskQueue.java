package com.kinstalk.m4.publicmediaplayer.resource;


import com.kinstalk.m4.common.utils.QLog;

import java.util.Iterator;
import java.util.PriorityQueue;

/**
 * Created by libin on 2016/9/29.
 */

public class PriorityTaskQueue<T> extends PriorityQueue<PriorityDelayTask<T>> {
    @Override
    public boolean offer(PriorityDelayTask<T> o) {
        if (o == null || o.getTask() == null) {
            throw new NullPointerException("o == null || o.getTask() == null");
        }
        Iterator<PriorityDelayTask<T>> iterator = iterator();
        boolean needOffer = true;
        while (iterator.hasNext()) {
            PriorityDelayTask<T> task = iterator.next();
            if (task.getTask().equals(o.getTask())) {
                if (task.compareTo(o) <= 0) {
                    QLog.d(this, "offer, don't need offer o - %s since there is high task - %s",
                            o.toString(), task.toString());
                    if (o.getPriority() > task.getPriority()) {
                        QLog.d(this, "Set higher priority");
                        task.setPriority(o.getPriority());
                    }
                    if (o.isClearCache()) {
                        task.setClearCache(o.isClearCache());
                    }
                    needOffer = false;
                } else {
                    iterator.remove();
                }
            }
        }
        if (needOffer) {
            return super.offer(o);
        } else {
            return true;
        }
    }
}

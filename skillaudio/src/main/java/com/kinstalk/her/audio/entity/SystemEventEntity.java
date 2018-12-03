package com.kinstalk.her.audio.entity;

/**
 * Created by lipeng on 18/1/19.
 */

public class SystemEventEntity {
    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_CONTINUE = 2;
    public static final int ACTION_STOP = 3;
    public static final int ACTION_EXIT = 4;

    private int action;

    public SystemEventEntity(int action) {
        this.action = action;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }
}

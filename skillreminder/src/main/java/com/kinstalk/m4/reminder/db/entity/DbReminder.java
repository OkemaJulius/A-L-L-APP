package com.kinstalk.m4.reminder.db.entity;

/**
 * Created by mamingzhang on 2017/10/23.
 */

public class DbReminder {
    private int reminderId;
    private long time;
    private String content;

    private boolean display;

    public int getReminderId() {
        return reminderId;
    }

    public void setReminderId(int id) {
        this.reminderId = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }
}

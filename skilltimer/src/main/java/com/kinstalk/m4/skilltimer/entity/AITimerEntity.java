package com.kinstalk.m4.skilltimer.entity;

/**
 * Created by mamingzhang on 2018/2/9.
 */

public class AITimerEntity {
    private int code;

    private String engine;
    private String service;

    private int playtts;
    private String text;

    private int duration;
    private String unit;
    private String operation;

    private String voiceID;

    public int getCode() {
        return code;
    }

    public String getEngine() {
        return engine;
    }

    public String getService() {
        return service;
    }

    public int getPlaytts() {
        return playtts;
    }

    public String getText() {
        return text;
    }

    public int getDuration() {
        return duration;
    }

    public String getUnit() {
        return unit;
    }

    public String getOperation() {
        return operation;
    }

    public String getVoiceID() {
        return voiceID;
    }
}

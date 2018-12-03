package com.kinstalk.m4.publichttplib;

/**
 * Created by pop on 17/4/17.
 */

public class HttpResult<T> {
    private int c;

    private String m;

    private T d;

    public int getC() {
        return c;
    }

    public void setC(int c) {
        this.c = c;
    }

    public String getM() {
        return m;
    }

    public void setM(String m) {
        this.m = m;
    }

    public T getD() {
        return d;
    }

    public void setD(T d) {
        this.d = d;
    }
}

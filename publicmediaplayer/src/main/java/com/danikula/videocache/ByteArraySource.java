package com.danikula.videocache;

import java.io.ByteArrayInputStream;

/**
 * Simple memory based {@link Source} implementation.
 *
 * @author Alexey Danilov (danikula@gmail.com).
 */
public class ByteArraySource implements Source {

    private final byte[] data;
    private ByteArrayInputStream arrayInputStream;

    public ByteArraySource(byte[] data) {
        this.data = data;
    }

    @Override
    public int read(byte[] buffer) {
        return arrayInputStream.read(buffer, 0, buffer.length);
    }

    @Override
    public long length() {
        return data.length;
    }

    @Override
    public void open(long offset) {
        arrayInputStream = new ByteArrayInputStream(data);
        arrayInputStream.skip(offset);
    }

    @Override
    public void close() {
    }
}


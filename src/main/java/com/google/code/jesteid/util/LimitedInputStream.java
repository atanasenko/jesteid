package com.google.code.jesteid.util;

import java.io.IOException;
import java.io.InputStream;

public class LimitedInputStream extends InputStream {

    private InputStream in;
    private int limit;
    
    public LimitedInputStream(InputStream in, int limit) {
        this.in = in;
        this.limit = limit;
    }
    
    public int read() throws IOException {
        if(limit <= 0) return -1;
        int b = in.read();
        if(b != -1) limit--;
        return b;
    }
    
    public int read(byte[] b) throws IOException {
        if(limit <= 0) return -1;
        int l = in.read(b, 0, limit);
        limit -= l;
        return l;
    }
    
    public int read(byte[] b, int off, int len) throws IOException {
        if(limit <= 0) return -1;
        int l = in.read(b, off, len < limit ? len : limit);
        limit -= l;
        return l;
    }
    
    public int available() throws IOException {
        return limit;
    }
    
}

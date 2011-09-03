package com.google.code.jesteid.util;

import java.io.IOException;
import java.io.OutputStream;

public class LengthOutputStream extends OutputStream {

    private int length;

    public void write(int b) throws IOException {
        length++;
    }
    
    public void write(byte[] b) throws IOException {
        length += b.length;
    }
    
    public void write(byte[] b, int off, int len) throws IOException {
        if(b.length < off + len) {
            throw new IOException("off + len > data.length");
        }
        length += len;
    }
    
    public int getLength() {
        return length;
    }

}

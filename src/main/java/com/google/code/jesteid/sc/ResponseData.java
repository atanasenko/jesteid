package com.google.code.jesteid.sc;

public class ResponseData {
    
    private byte[] data;
    private int sw;
    
    public ResponseData(byte[] data, int sw) {
        this.data = data;
        this.sw = sw;
    }

    public byte[] getData() {
        return data;
    }

    public int getSW() {
        return sw;
    }
    
    public int getSW1() {
        return (sw >> 8) & 0xff;
    }
    
    public int getSW2() {
        return sw & 0xff;
    }
    
    
}

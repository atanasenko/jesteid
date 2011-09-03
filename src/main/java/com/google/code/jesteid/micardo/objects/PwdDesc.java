package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.iso8825.TLVList;

public class PwdDesc {
    private TLVList data;
    
    public PwdDesc(TLVList data) {
        this.data = data;
    }

    public TLVList getData() {
        return data;
    }
    
    public String toString() {
        return data.toString();
    }
    
    public static PwdDesc decode(TLVList list) {
        return new PwdDesc(list);
    }
}

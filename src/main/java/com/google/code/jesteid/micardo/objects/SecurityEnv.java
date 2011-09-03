package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.iso8825.TLVList;
import com.google.code.jesteid.micardo.CRTType;

public class SecurityEnv {
    
    private int se;
    private TLVList data;
    
    public SecurityEnv(int se, TLVList data) {
        this.se = se;
        this.data = data;
    }

    public int getSe() {
        return se;
    }
    
    public CRT getCRT(CRTType type) {
        return CRT.decode(type, data);
    }

    public TLVList getData() {
        return data;
    }
    
    public String toString() {
        return "SE# " + se + ": " + data;
    }
    
}

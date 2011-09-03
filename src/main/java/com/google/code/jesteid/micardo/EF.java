package com.google.code.jesteid.micardo;

import com.google.code.jesteid.util.BinUtils;

public class EF extends FSEntry {
    
    private final Integer fid;

    EF(DF parent, int fid) {
        super(parent);
        this.fid = fid;
    }

    public Integer getFid() {
        return fid;
    }
    
    public boolean isDir() {
        return false;
    }
    
    public String toString() {
        return (getParent() != null ? getParent() + "/" : "") + BinUtils.toHex(getFid(), 4);
    }
}

package com.google.code.jesteid.micardo;

import com.google.code.jesteid.util.BinUtils;

public class DF extends FSEntry {
    
    private final Integer fid;
    private final String name;

    DF(DF parent, Integer fid, String name) {
        super(parent);
        this.fid = fid;
        this.name = name;
    }
    
    public Integer getFid() {
        return fid;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isNamed() {
        return fid == null;
    }
    
    public boolean isDir() {
        return true;
    }
    
    public DF dir(int fid) {
        return new DF(this, fid, null);
    }
    
    public DF dir(String name) {
        return new DF(this, null, name);
    }
    
    public EF file(int fid) {
        return new EF(this, fid);
    }
    
    public String toString() {
        return (getParent() != null ? getParent() + "/" : "") + 
                (isNamed() ? getName() : BinUtils.toHex(getFid(), 4));
    }

}

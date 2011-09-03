package com.google.code.jesteid.micardo;

public abstract class FSEntry {
    
    public static final DF MF = new DF(null, Micardo.EF_MF, null);
    
    private final DF parent;
    
    FSEntry(DF parent) {
        this.parent = parent;
    }
    
    public DF getParent() {
        return parent;
    }
    
    public abstract Integer getFid();
    
    public abstract boolean isDir();
    
}

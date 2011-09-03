package com.google.code.jesteid.micardo;

public enum CRTType {
    /**
     * Key usage is restricted to digital signatures
     */
    DST(0xb6),
    
    /**
     * Key usage is restricted to cryptographic checksums
     */
    CCT(0xb4),
    
    /**
     * Key usage is restricted to confidentiality
     */
    CT(0xb8),
    
    /**
     * Key usage is restricted to authentication
     */
    AT(0xa4);
    
    private int tag;
    
    CRTType(int tag) {
        this.tag = tag;
    }
    
    public int getTag() {
        return tag;
    }
}

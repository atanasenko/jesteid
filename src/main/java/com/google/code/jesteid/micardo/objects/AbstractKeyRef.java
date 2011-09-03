package com.google.code.jesteid.micardo.objects;

public abstract class AbstractKeyRef {
    
    /*
     * Page 61 of Micardo doc
     */
    
    private int keyID;
    private int keyVersion;
    
    protected AbstractKeyRef(int keyID, int keyVersion) {
        this.keyID = keyID;
        this.keyVersion = keyVersion;
    }

    public int getKeyID() {
        return keyID;
    }

    public int getKeyVersion() {
        return keyVersion;
    }
    
    public abstract byte[] encode();
    
}

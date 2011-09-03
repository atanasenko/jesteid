package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.util.BinUtils;

public class KeyRef extends AbstractKeyRef {

    private int keyEF;
    
    public KeyRef(int keyID, int keyVersion, int keyEF) {
        super(keyID, keyVersion);
        this.keyEF = keyEF;
    }
    
    public int getKeyEF() {
        return keyEF;
    }
    
    public String toString() {
        return BinUtils.toHex(getKeyID(), 2) + ":" + BinUtils.toHex(getKeyVersion(), 2) + (keyEF != -1 ? "/" + BinUtils.toHex(keyEF, 4) : "");
    }
    
    public byte[] createRecord(byte[] keyData) {
        byte[] keyRef = getBytes(false);
        byte[] data = new byte[keyRef.length + keyData.length];
        System.arraycopy(keyRef, 0, data, 0, keyRef.length);
        System.arraycopy(keyData, 0, data, 2, keyData.length);
        return data;
    }
    
    private byte[] getBytes(boolean includeEF) {
        if(keyEF == -1 || !includeEF) {
            return new byte[]{ (byte)getKeyID(), (byte)getKeyVersion() };
        } else {
            byte[] data = new byte[]{ (byte)getKeyID(), (byte)getKeyVersion(), 0, 0 };
            BinUtils.toBytes(keyEF, data, 2, 2);
            return data;
        }
    }
    
    public byte[] encode() {
        return getBytes(true);
    }
    
    public static KeyRef decode(byte[] data) {
        int keyID = BinUtils.toInt(data[0]);
        int keyVersion = BinUtils.toInt(data[1]);
        int keyEF = -1;
        if(data.length > 2) {
            keyEF = (int) BinUtils.fromBytes(data, 2, 2);
        }
        
        return new KeyRef(keyID, keyVersion, keyEF);
    }
}

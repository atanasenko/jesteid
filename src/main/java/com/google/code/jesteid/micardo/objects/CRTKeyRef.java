package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.util.BinUtils;

public class CRTKeyRef extends AbstractKeyRef {
    
    public enum Search {
        GLOBAL(0),
        DF(0x80);
        
        private int value;
        
        private Search(int value) {
            this.value = value;
        }

        public static Search get(int value) {
            
            for(Search search: values()) {
                if(value == search.value) {
                    return search;
                }
            }
            
            return null;
        }
    }
    
    private Search search;
    
    public CRTKeyRef(Search search, int keyID, int keyVersion) {
        super(keyID, keyVersion);
        this.search = search;
    }
    
    public Search getSearch() {
        return search;
    }
    
    public boolean isEmpty() {
        return search == null && getKeyID() == -1 && getKeyVersion() == -1;
    }

    public String toString() {
        if(isEmpty()) return "Empty key";
        return search + " " + BinUtils.toHex(getKeyID(), 2) + ":" + BinUtils.toHex(getKeyVersion(), 2);
    }
    
    public byte[] encode() {
        if(isEmpty()) {
            return new byte[0];
        } else {
            return new byte[]{ (byte) search.value, (byte)getKeyID(), (byte)getKeyVersion() };
        }
    }
    
    public static CRTKeyRef decode(byte[] data) {
        if(data.length == 0) {
            return null;
        }
        return new CRTKeyRef(Search.get(BinUtils.toInt(data[0])), BinUtils.toInt(data[1]), BinUtils.toInt(data[2]));
    }

}

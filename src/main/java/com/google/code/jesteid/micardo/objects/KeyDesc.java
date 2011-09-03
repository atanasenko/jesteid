package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.iso8825.TLV;
import com.google.code.jesteid.iso8825.TLVList;
import com.google.code.jesteid.util.BinUtils;

public class KeyDesc {
    
    /*
     * Page 70 of Micardo doc
     */
    
    private static final int TAG_KEYID_JOINT  = 0x83;
    private static final int TAG_KEYID_LOCAL = 0x93;
    private static final int TAG_KEY1 = 0xc0; //
    private static final int TAG_KEY2 = 0xc1;
    private static final int TAG_KEY3 = 0xc2;
    
    private final boolean local;
    private final KeyRef keyRef;
    private final int keyLength;
    
    public KeyDesc(boolean local, KeyRef keyRef, int keyLength) {
        this.local = local;
        this.keyRef = keyRef;
        this.keyLength = keyLength;
    }
    
    public boolean isLocal() {
        return local;
    }
    
    public KeyRef getKeyReference() {
        return keyRef;
    }
    
    public int getKeyLength() {
        return keyLength;
    }
    
    public String toString() {
        return "Key " + keyRef + 
                (local ? " (local)" : " (joint)") + 
                ", Length: " + keyLength;
    }

    public static KeyDesc decode(TLVList list) {
        
        boolean local = true;
        KeyRef keyRef = null;
        int keyLength = -1;
        
        for(TLV tlv: list) {
            // skip counters, ignore key usage
            byte[] data;
            
            switch((int)tlv.getTag().getTag()) {
            case TAG_KEYID_JOINT:
                local = false;
                //$FALL-THROUGH$
            case TAG_KEYID_LOCAL:
                keyRef = KeyRef.decode(tlv.getData());
                break;
            case TAG_KEY1:
            case TAG_KEY2:
            case TAG_KEY3:
                data = tlv.getData();
                keyLength = BinUtils.toInt(data[1]); // first byte is always 0x81
                break;
            }
        }
        
        return new KeyDesc(local, keyRef, keyLength);
    }
}

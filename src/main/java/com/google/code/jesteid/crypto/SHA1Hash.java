package com.google.code.jesteid.crypto;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Hash {
    
    private static final byte[] HASH_PREFIX = new byte[] {
        0x30, 0x21, 0x30, 0x09, 
        0x06, 0x05, 0x2B, 0x0E, 
        0x03, 0x02, 0x1A, 0x05, 
        0x00, 0x04, 0x14
    };
    
    private byte[] data;
    
    public SHA1Hash(byte[] data) {
        readData(data);
    }

    public SHA1Hash(InputStream in) throws IOException {
        readData(in);
    }
    
    private MessageDigest getSHA1() {
        try {
            return MessageDigest.getInstance("SHA1");
        } catch(NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error creating hash instance", e);
        }
    }
    
    private void readData(InputStream in) throws IOException {
        MessageDigest sha1 = getSHA1();
        
        byte[] buf = new byte[1024];
        int l;
        while((l = in.read(buf)) != -1) {
            sha1.update(buf, 0, l);
        }
        
        setData(sha1.digest());
    }
    
    private void readData(byte[] in) {
        setData(getSHA1().digest(in));
    }
    
    private void setData(byte[] sha1hash) {
        byte[] result = new byte[HASH_PREFIX.length + sha1hash.length];
        System.arraycopy(HASH_PREFIX, 0, result, 0, HASH_PREFIX.length);
        System.arraycopy(sha1hash, 0, result, HASH_PREFIX.length, sha1hash.length);
        
        this.data = result;
    }
    
    public byte[] getHashBytes() {
        return data;
    }
}

package com.google.code.jesteid.iso8825;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.util.BinUtils;

public class PrimitiveTLV extends TLV {
    
    protected byte[] data;

    public PrimitiveTLV(long tag) {
        super(tag);
    }

    public PrimitiveTLV(TLVTag tag) {
        super(tag);
    }

    public boolean isConstructed() {
        return false;
    }
    
    public void setData(byte[] data) {
        setData(data, 0, data == null ? 0 : data.length);
    }
    
    public void setData(byte[] data, int offset, int length) {
        
        if(offset > data.length) {
            throw new IllegalArgumentException("offset > data.length");
        }
        
        if(offset + length > data.length) {
            throw new IllegalArgumentException("offset + length > data.length");
        }
        
        if(length >= 0x10000) {
            throw new IllegalArgumentException(length + " > 65535");
        }
        
        this.data = new byte[length];
        System.arraycopy(data, offset, this.data, 0, length);
    }
    
    public byte[] getData() {
        byte[] d = new byte[data.length];
        System.arraycopy(data, 0, d, 0, d.length);
        return d;
    }
    
    public void getData(OutputStream out) throws IOException {
        if(data != null) out.write(data);
    }
    
    public TLVLength getLength() {
        return new TLVLength(data == null ? 0 : data.length);
    }

    void encodeData(OutputStream out) throws IOException {
        getData(out);
    }

    void decodeData(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] b = new byte[256];
        int l;
        while((l = in.read(b)) != -1) {
            out.write(b, 0, l);
        }
        setData(out.toByteArray());
    }
    
    @Override
    public String toString() {
        return getTag() + ": " + BinUtils.getDump(data);
    }
    
}

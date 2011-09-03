package com.google.code.jesteid.iso8825;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.util.BinUtils;
import com.google.code.jesteid.util.LimitedInputStream;

public class TLVLength {

    public static final TLVLength INDEFINITE = new TLVLength(-1);
    
    private static final TLV EOC = new PrimitiveTLV(0);
    
    private static final int MASK_LONG_FORM = 0x80;
    
    private long length;
    
    public TLVLength(long length) {
        this.length = length;
    }
    
    TLVLength() {
        
    }
    
    public long getLength() {
        return length;
    }
    
    public boolean isIndefinite() {
        return length == -1;
    }
    
    void decode(InputStream in) throws IOException {
        
        int firstByte = in.read();
        if(firstByte == -1) {
            throw new EOFException();
        }
        if((firstByte & MASK_LONG_FORM) == 0x80) {
            // long form
            int byteCount = firstByte & ~MASK_LONG_FORM;
            if(byteCount > 8) throw new IllegalArgumentException("TLVLength does not support lengths > 64bits");
            
            length = BinUtils.fromBytes(new LimitedInputStream(in, byteCount));
            
        } else {
            // short form
            length = firstByte;
        }
        
    }
    
    void decodeTLVData(TLV tlv, InputStream in) throws IOException {
        
        if(isIndefinite()) {
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // read until `00 00` is encountered
            int l = 0;
            int n;
            while((n = in.read()) != -1) {
                if(n == 0) {
                    l++;
                    if(l == 2) {
                        break;
                    }
                } else {
                    while(l > 0) {
                        out.write(0);
                        l--;
                    }
                    out.write(n);
                }
            }
            if(l < 2 && n == -1) {
                throw new IllegalArgumentException("Error reading EOC");
            }
            tlv.decodeData(new ByteArrayInputStream(out.toByteArray()));
            
        } else {
            
            tlv.decodeData(new LimitedInputStream(in, (int)length));
            
        }
        
    }
    
    byte[] encode() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            encode(out);
        } catch(IOException e) {
            // impossible
            throw new IllegalStateException(e);
        }
        return out.toByteArray();
    }
    
    void encode(OutputStream out) throws IOException {
        if(isIndefinite()) {
            
            out.write(MASK_LONG_FORM);
            
        } else {
            
            if(length < MASK_LONG_FORM) {
                out.write((int)length);
            } else {
                int byteCount = BinUtils.getByteCount(length);
                out.write(byteCount | MASK_LONG_FORM);
                BinUtils.toBytes(length, out);
            }
            
        }
    }
    
    private void encodeEnd(OutputStream out) throws IOException {
        if(isIndefinite()) {
            EOC.encode(out, TLV.getEncodingRules());
        }
    }
    
    void encodeTLVData(TLV tlv, OutputStream out) throws IOException {
        encode(out);
        tlv.encodeData(out);
        encodeEnd(out);

    }
    
    public int hashCode() {
        return (int)length;
    }
    
    public boolean equals(Object o) {
        if(o instanceof TLVLength) {
            TLVLength l = (TLVLength) o;
            return l.length == length;
        }
        return false;
    }
    
}

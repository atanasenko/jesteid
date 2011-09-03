package com.google.code.jesteid.iso8825;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This class implements ISO-8825-1:2002
 * 
 * @author sleepless
 *
 */
public abstract class TLV {
    
    protected TLVTag tag;
    
    protected TLV(long tag) {
        this.tag = new TLVTag(tag);
    }
    
    protected TLV(TLVTag tag) {
        this.tag = tag;
    }
    
    public TLVTag getTag() {
        return tag;
    }
    
    public abstract byte[] getData();
    
    public abstract boolean isConstructed();
    
    public abstract TLVLength getLength();
    
    public static TLV decode(byte[] encoded, int offset, EncodingRules er) {
        try {
            return decode(new ByteArrayInputStream(encoded, offset, encoded.length - offset), er);
        } catch(IOException e) {
            // should not happen
            throw new IllegalStateException(e);
        }
    }
    
    public static TLV decode(InputStream in, EncodingRules er) throws IOException {
        
        setEncodingRules(er);
        
        TLVTag tag = new TLVTag();
        try {
            tag.decode(in);
        } catch(EOFException e) {
            // no more data
            return null;
        }
        
        TLV tlv;
        if(tag.isConstructed()) {
            // constructed
            tlv = new ConstructedTLV(tag);
        } else {
            // primitive
            tlv = new PrimitiveTLV(tag);
        }

        TLVLength l = new TLVLength();
        l.decode(in);
        l.decodeTLVData(tlv, in);
        
        return tlv;
    }
    
    public static TLVList decodeList(byte[] encoded, int offset, EncodingRules er) {
        try {
            return decodeList(new ByteArrayInputStream(encoded, offset, encoded.length - offset), er);
        } catch(IOException e) {
            // should not happen
            throw new IllegalStateException(e);
        }
    }
    
    public static TLVList decodeList(InputStream in, EncodingRules er) throws IOException {
        TLVList tlvs = new TLVList();
        TLV tlv;
        while((tlv = TLV.decode(in, er)) != null) {
            tlvs.add(tlv);
        }
        return tlvs;
    }
    
    abstract void decodeData(InputStream in) throws IOException;
    
    public byte[] encode(EncodingRules er) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            encode(out, er);
        } catch(IOException e) {
            // impossible
            throw new IllegalStateException(e);
        }
        return out.toByteArray();
    }
    
    public void encode(OutputStream out, EncodingRules er) throws IOException {
        setEncodingRules(er);
        
        tag.encode(out);
        TLVLength l = getLength();
        l.encodeTLVData(this, out);
    }
    
    abstract void encodeData(OutputStream out) throws IOException;
    
    private static final ThreadLocal<EncodingRules> ER = new ThreadLocal<EncodingRules>();
    
    static void setEncodingRules(EncodingRules er) {
        ER.set(er);
    }
    
    static EncodingRules getEncodingRules() {
        return ER.get();
    }
}

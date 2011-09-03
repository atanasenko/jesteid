package com.google.code.jesteid.iso8825;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.util.LengthOutputStream;

public class ConstructedTLV extends TLV {
    
    private TLVList contents;
    
    public ConstructedTLV(TLVTag tag) {
        super(tag);
        contents = new TLVList();
    }

    public ConstructedTLV(long tag) {
        super(tag);
        contents = new TLVList();
    }
    
    public boolean isConstructed() {
        return true;
    }
    
    public byte[] getData() {
        return null;
    }
    
    public void add(TLV tlv) {
        contents.add(tlv);
    }
    
    public TLVList getContents() {
        return contents;
    }
    
    public TLVLength getLength() {
        if(TLV.getEncodingRules() == EncodingRules.CER) {
            return TLVLength.INDEFINITE;
        }
        
        return calcLength();
    }
    
    private TLVLength calcLength() {
        LengthOutputStream o = new LengthOutputStream();
        try {
            encodeData(o);
        } catch(IOException e) {
            // should never happen
            throw new IllegalStateException(e);
        }
        return new TLVLength(o.getLength());
    }

    void decodeData(InputStream in) throws IOException {
        TLV tlv;
        while((tlv = TLV.decode(in, getEncodingRules())) != null) {
            add(tlv);
        }
    }

    void encodeData(OutputStream out) throws IOException {
        for(TLV tlv: getContents()) {
            tlv.encode(out, TLV.getEncodingRules());
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getTag()).append(":").append(contents.toString());
        return sb.toString();
    }
    
}

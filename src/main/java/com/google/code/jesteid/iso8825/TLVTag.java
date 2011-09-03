package com.google.code.jesteid.iso8825;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.util.BinUtils;

public class TLVTag {
    
    public enum TagClass {
        UNIVERSAL(0),
        APPLICATION(0x40),
        CONTEXT(0x80),
        PRIVATE(0xc0);
        
        private final int mask;
        
        private TagClass(int mask) {
            this.mask = mask;
        }
        
        public static TagClass getTagClass(int tagFirstByte) {
            
            int value = tagFirstByte & 0xc0;
            
            if(value == PRIVATE.mask) {
                return PRIVATE;
            } else if(value == CONTEXT.mask) {
                return CONTEXT;
            } else if(value == APPLICATION.mask) {
                return APPLICATION;
            }
            return UNIVERSAL;
        }
    }
    
    private static final int MASK_CONSTRUCTED = 0x20;
    private static final int MASK_MULTIBYTE_FIRST = 0x1f;
    private static final int MASK_MULTIBYTE = 0x80;
    private static final int MASK_MULTIBYTE_VALUE = 0x7f;
    
    private TagClass tagClass;
    private boolean constructed;
    private long number;
    private long tag;
    
    public TLVTag(TagClass tagClass, boolean constructed, long number) {
        // reserve 1 byte for leading byte
        if(BinUtils.getByteCount(number) > 7) {
            throw new IllegalArgumentException("TLVTag does not support tags > 64bit");
        }
        this.tagClass = tagClass;
        this.constructed = constructed;
        this.number = number;
        this.tag = BinUtils.fromBytes(encode());
    }
    
    public TLVTag(long value) {
        decode(value);
    }
    
    TLVTag() {
        
    }
    
    public TagClass getTagClass() {
        return tagClass;
    }
    
    public boolean isConstructed() {
        return constructed;
    }
    
    public long getNumber() {
        return number;
    }
    
    public long getTag() {
        return tag;
    }
    
    void decode(long value) {
        try {
            decode(new ByteArrayInputStream(BinUtils.toBytes(value)));
        } catch(IOException e) {
            // impossible
            throw new IllegalStateException(e);
        }
    }
    
    void decode(InputStream in) throws IOException {
        
        int firstByte = in.read();
        if(firstByte == -1) {
            throw new EOFException();
        }
        
        tagClass = TagClass.getTagClass(firstByte);
        constructed = (firstByte & MASK_CONSTRUCTED) == MASK_CONSTRUCTED;
        
        number = firstByte & MASK_MULTIBYTE_FIRST;
        int l = 1;
        boolean next = number == MASK_MULTIBYTE_FIRST;
        if(next) {
            number = 0;
            l = 0;
            while(next) {
                l++;
                
                // reserve 1 byte for leading byte
                if(l > 7) throw new IllegalArgumentException("TLVTag does not support tags > 64bit");
                
                long n = in.read();
                if(n == -1) {
                    throw new EOFException("Misformatted tag");
                }
                
                next = (n & MASK_MULTIBYTE) == MASK_MULTIBYTE;
                number = (number << 8) | (n & MASK_MULTIBYTE_VALUE);
            }
        }
        
        tag = BinUtils.fromBytes(encode());
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
        
        byte firstByte = (byte)(tagClass.mask | (constructed ? MASK_CONSTRUCTED : 0));
        
        if(number < MASK_MULTIBYTE_FIRST) {
            
            // simple
            out.write(firstByte | (int)number);
            
        } else {
            
            // multibyte
            int byteCount = BinUtils.getByteCount(number);
            out.write(firstByte | MASK_MULTIBYTE_FIRST);
            for(int i = 0; i < byteCount; i++) {
                int n = (int)((number >>> ((byteCount - i - 1) * 8)) & 0xff);
                if(i < byteCount - 1) {
                    n |= MASK_MULTIBYTE;
                }
                out.write(n);
            }
            
        }
    }
    
    public String toString() {
        return Long.toHexString(getTag());
    }
    
    public String toDebugString() {
        return 
            "Tag " + toString() + 
            " (" + tagClass + 
            ", " + (constructed ? "constructed" : "primitive") +
            ", " + Long.toHexString(number) + ")";
    }
    
    public int hashCode() {
        return (int)number;
    }
    
    public boolean equals(Object o) {
        if(o instanceof TLVTag) {
            TLVTag t = (TLVTag)o;
            return t.tagClass == tagClass 
                && t.constructed == constructed 
                && t.number == number;
        }
        return false;
    }

}

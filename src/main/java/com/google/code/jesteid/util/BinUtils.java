package com.google.code.jesteid.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import sun.misc.HexDumpEncoder;

public class BinUtils {
    
    public static final void dump(byte[] data){
        try {
            dump(data, System.out);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public static final void dump(byte[] data, Appendable out) throws IOException {
        
        HexDumpEncoder hex = new HexDumpEncoder();
        out.append(hex.encode(data)).append('\n');
        /*
        if(data.length == 0 ) {
            return;
        }
        
        int l = data.length;
        if(data.length % 0x10 > 0) 
            l += 0x10 - (data.length % 0x10);
        
        for(int i = 0; i < l; i++) {
            if(i % 0x10 == 0) {
                out.append(toHex((byte)i, 8) + " | ");
            }
            
            if(i >= data.length) {
                out.append("   ");
            } else {
                out.append(toHex(data[i], 2) + " ");
            }
            if(i % 0x10 == 7) {
                out.append(" ");
            }
            
            if(i % 0x10 == 0x10 - 1) {
                // print actual data
                out.append(" | ");
                for(int j = i - 0x10 + 1; j <= i; j++) {
                    if(j >= data.length) {
                        out.append(" ");
                    } else {
                        out.append(getChar(data[j]));
                    }
                    if(j % 0x10 == 7) {
                        out.append(" ");
                    }
                }
                out.append("\n");

            }
        }
        */
        //out.println();
    }
    /*
    private static final char getChar(byte b) {
        return Character.isLetterOrDigit(b) ? ((char)b) : '.';
    }
    */
    
    
    public static String getDump(byte[] data)
    {
        StringBuilder sb = new StringBuilder();
        try {
            dump(data, sb);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return sb.toString();
    }
    
    private static final String pad(String s, char c, int tl) {
        StringBuilder sb = new StringBuilder();
        for(int i = tl - s.length(); i > 0; i--) {
            sb.append(c);
        }
        sb.append(s);
        return sb.toString();
    }
    
    public static final int toInt(byte b) {
        return b < 0 ? b + 256 : b;
    }
    
    public static byte toByte(int i) {
        return (byte)i;
    }
    
    public static final String toHex(int i, int l) {
        return toHex((byte)i, l);
    }
    public static final String toHex(byte b, int l) {
        int i = b; 
        if(i < 0) i += 256;
        
        return pad(Integer.toHexString(i).toUpperCase(), '0', l);
    }
    
    /*
    public static final String toHex(byte[] array) {
        return toHex(array, "");
    }
    
    public static final String toHex(byte[] array, String sep) {
        
        StringBuilder sb = new StringBuilder();
        boolean f = true;
        for(byte b: array) {
            if(f) f = false;
            else sb.append(sep);
            sb.append(toHex(b, 2));
        }
        return sb.toString();
    }
    */
    
    public static byte[] fromHex(String hex) {
        return fromHex(hex, "\\s");
    }
    
    public static byte[] fromHex(String hex, String sepPattern) {
        
        hex = hex.replaceAll(sepPattern, "");
        
        int sl = hex.length();
        int l = sl / 2 + (sl % 2);
        
        byte[] array = new byte[l];
        
        for(int i = 0; i < l; i++) {
            String b = hex.substring(i * 2, (Math.min(i+1, l) * 2));
            array[i] = toByte(Integer.valueOf(b, 16));
        }
        
        return array;
    }
    
    public static final int getByteCount(long value) {
        int bitCount = 64 - Long.numberOfLeadingZeros(value);
        int byteCount = (bitCount / 8) + ((bitCount % 8) > 0 ? 1 : 0);
        return byteCount == 0 ? 1 : byteCount;
    }
    
    public static final byte[] toBytes(long value) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            toBytes(value, out);
        } catch(IOException e) {
            // impossible
            throw new IllegalStateException(e);
        }
        return out.toByteArray();
    }
    
    public static final void toBytes(long value, OutputStream out) throws IOException {
        int byteCount = getByteCount(value);
        
        for(int i = 0; i < byteCount; i++) {
            out.write((byte)((value >>> ((byteCount - i - 1) * 8)) & 0xff)); 
        }
    }
    
    public static void toBytes(int value, byte[] data, int offset, int length) {
        int byteCount = getByteCount(value);
        int max = length == -1 ? byteCount : Math.min(byteCount,  length);
        for(int i = 0; i < max; i++) {
            data[offset + i] = (byte)((value >>> ((byteCount - i - 1) * 8)) & 0xff); 
        }
    }
    
    public static final long fromBytes(byte[] data) {
        return fromBytes(data, 0, data.length);
    }
    
    public static final long fromBytes(byte[] data, int offset, int length) {
        if(data.length < length) {
            throw new IllegalArgumentException("data.length < length");
        }
        try {
            return fromBytes(new ByteArrayInputStream(data, offset, length));
        } catch(IOException e) {
            // impossible
            throw new IllegalStateException(e);
        }
        
    }
    
    public static final long fromBytes(InputStream in) throws IOException {
        
        long value = 0;
        int n;
        int l = 0;
        while((n = in.read()) != -1) {
            if(l >= 8) {
                throw new IllegalArgumentException("Can only decode up to 8 bytes");
            }
            value = (value << 8) | n;
            l++;
        }
        return value;
    }

    /*
    public static final long fromBytes(ByteSource src) {
        LongConstructor c = CON.get().init();
        
        try {
            src.provideBytes(c);
        } catch(IOException e) {
            // should not happen
            throw new IllegalStateException(e);
        }
        
        return c.getValue();
    }
    
    public static interface ByteSource {
        
        void provideBytes(OutputStream out) throws IOException;
        
    }
    
    private static final ThreadLocal<LongConstructor> CON = new ThreadLocal<BitUtils.LongConstructor>() {
        protected LongConstructor initialValue() {
            return new LongConstructor();
        }  
    };

    private static class LongConstructor extends OutputStream {
        
        private int count;
        private long value;

        public LongConstructor init() {
            count = 8;
            value = 0L;
            return this;
        }

        public void write(int b) throws IOException {
            if(count <= 0) {
                throw new IllegalStateException("Can only decode up to 8 bytes");
            }
            
            if(b < 0) b += 256;
            value = (value << 8) | b;
            count--;
        }
        
        public long getValue() {
            return value;
        }
        
    }
    */
    
}

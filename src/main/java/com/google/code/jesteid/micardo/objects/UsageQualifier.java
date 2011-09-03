package com.google.code.jesteid.micardo.objects;

public class UsageQualifier {
    
    /**
     * External and mutual authentication (AT)
     */
    public boolean bit8;
    
    /**
     * Data authentication (DST), 
     * data confidentiality (CT), 
     * internal and mutual authentication (AT)
     */
    public boolean bit7;
    
    /**
     * Secure messaging for response to message (CCT, CT)
     */
    public boolean bit6;
    
    /**
     * Secure messaging for command message (CCT, CT)
     */
    public boolean bit5;
    
    /**
     * User authentication (AT)
     */
    public boolean bit4;
    
    public static UsageQualifier decode(int data) {
        UsageQualifier q = new UsageQualifier();
        q.bit8 = (data & 0x80) != 0;
        q.bit7 = (data & 0x40) != 0;
        q.bit6 = (data & 0x20) != 0;
        q.bit5 = (data & 0x10) != 0;
        q.bit4 = (data & 0x8) != 0;
        return q;
    }
    
    public int encode() {
        int data = 0;
        if(bit8) data |= 0x80;
        if(bit7) data |= 0x40;
        if(bit6) data |= 0x20;
        if(bit5) data |= 0x10;
        if(bit4) data |= 0x8;
        return data;
    }
}

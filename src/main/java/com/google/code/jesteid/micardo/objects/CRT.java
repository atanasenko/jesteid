package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.iso8825.ConstructedTLV;
import com.google.code.jesteid.iso8825.PrimitiveTLV;
import com.google.code.jesteid.iso8825.TLV;
import com.google.code.jesteid.iso8825.TLVList;
import com.google.code.jesteid.micardo.CRTType;

public class CRT {
    
    /*
     * Page 125 of Micardo doc
     */
    
    private static final int TAG_UQ = 0x95;
    private static final int TAG_KEYREF = 0x83;
    
    private CRTType type;
    private UsageQualifier usageQ;
    private CRTKeyRef keyRef;
    
    public CRT(CRTType type, UsageQualifier usageQ, CRTKeyRef keyRef) {
        
        this.type = type;
        this.usageQ = usageQ;
        this.keyRef = keyRef;
    }

    public CRTType getType() {
        return type;
    }

    public UsageQualifier getUsageQualifier() {
        return usageQ;
    }

    public CRTKeyRef getKeyReference() {
        return keyRef;
    }
    
    public TLV encodeKeyReference() {
        PrimitiveTLV tlv = new PrimitiveTLV(TAG_KEYREF);
        if(keyRef != null) tlv.setData(keyRef.encode());
        return tlv;
    }

    public static CRT decode(CRTType type, TLVList data) {
        ConstructedTLV crtTLV = (ConstructedTLV) data.getByTag(type.getTag());
        TLV uqTLV = crtTLV.getContents().getByTag(TAG_UQ);
        
        return new CRT(type, UsageQualifier.decode(uqTLV.getData()[0]), 
                CRTKeyRef.decode(crtTLV.getContents().getByTag(TAG_KEYREF).getData()));
    }
    
}

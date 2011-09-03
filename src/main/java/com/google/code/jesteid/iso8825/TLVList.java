package com.google.code.jesteid.iso8825;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TLVList extends AbstractList<TLV> {
    
    private List<TLV> tlvs;
    
    public TLVList() {
        tlvs = new ArrayList<TLV>();
    }
    
    public TLV get(int index) {
        return tlvs.get(index);
    }
    
    public TLV getByTag(long tag) {
        for(TLV tlv: this) {
            if(tlv.getTag().getTag() == tag) {
                return tlv;
            }
        }
        return null;
    }
    
    public List<TLV> getListByTag(long tag) {
        List<TLV> tlvs = null;
        for(TLV tlv: this) {
            if(tlv.getTag().getTag() == tag) {
                if(tlvs == null) {
                    tlvs = new ArrayList<TLV>();
                }
                tlvs.add(tlv);
            }
        }
        if(tlvs == null) {
            return Collections.emptyList();
        }
        return tlvs;
    }

    public int size() {
        return tlvs.size();
    }
    
    @Override
    public boolean add(TLV e) {
        return tlvs.add(e);
    }
    
    @Override
    public void add(int index, TLV element) {
        tlvs.add(index, element);
    }
    
    @Override
    public TLV set(int index, TLV element) {
        return tlvs.set(index, element);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder().append("{");
        boolean first = true;
        for(TLV tlv: tlvs) {
            if(!first) sb.append(",");
            else first = false;
            sb.append("\n").append(tlv);
        }
        return sb.append("}").toString();
    }
}

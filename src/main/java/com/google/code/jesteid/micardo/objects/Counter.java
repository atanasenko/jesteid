package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.iso8825.TLV;
import com.google.code.jesteid.iso8825.TLVList;
import com.google.code.jesteid.util.BinUtils;

public class Counter {
    private int retryCounterInitial;
    private int retryCounter;
    private int usageCounterInitial;
    private int usageCounter;
    
    public Counter(int retryCounterInitial, int retryCounter, int usageCounterInitial, int usageCounter) {
        this.retryCounterInitial = retryCounterInitial;
        this.retryCounter = retryCounter;
        this.usageCounterInitial = usageCounterInitial;
        this.usageCounter = usageCounter;
    }

    public Integer getRetryCounter() {
        return retryCounter;
    }
    
    public Integer getRetryCounterInitial() {
        return retryCounterInitial;
    }
    
    public Integer getUsageCounter() {
        return usageCounter;
    }
    
    public Integer getUsageCounterInitial() {
        return usageCounterInitial;
    }
    
    public String toString() {
        return "Retry " + getRetryCounter() + "/" + getRetryCounterInitial() +
                ", Usage " + getUsageCounter() + "/" + getUsageCounterInitial();
    }

    public static Counter decode(TLVList data) {
        return new Counter(
                getCounter(data, 0x80),
                getCounter(data, 0x90),
                getCounter(data, 0x81),
                getCounter(data, 0x91));
    }

    private static int getCounter(TLVList data, int tag) {
        TLV tlv = data.getByTag(tag);
        return tlv == null ? -1 : (int) BinUtils.fromBytes(tlv.getData());
    }
    
}

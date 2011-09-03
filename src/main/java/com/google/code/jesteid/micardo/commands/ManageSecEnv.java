package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.iso8825.EncodingRules;
import com.google.code.jesteid.micardo.CRTType;
import com.google.code.jesteid.micardo.objects.CRT;
import com.google.code.jesteid.micardo.objects.UsageQualifier;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class ManageSecEnv implements ICommand<Void>{
    
    /*
     * Page 171 of Micardo doc
     */
    
    private static final int INS = 0x22;
    
    private static final int P1_SET_NEG_MASK = ~0x8; // bit4 is rfu
    private static final int P1_SET_POS_MASK = 0x1; // bit1 set
    private static final int P1_RESTORE = 0xf3; // 11110011

    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .error("67 00", "Lc is not equal to the length of the command data, Lc is missing or is superfluous, Le is missing or is superfluous")
                .error("69 85", "Chip card contains no data from which to derive a key, referenced key cannot be used")
                .error("6A 80", "Incorrect command data")
                .error("6A 86", "P1 or P2 possesses an illegal value")
                .error("6A 88", "Referenced key not found")
                .error("6E 00", "CLA and INS are inconsistent");
                
    private int se = -1;
    private UsageQualifier usageQ;
    private CRTType crt;
    private byte[] data;
    
    /**
     * RESTORE
     * @param se Security Environment #
     */
    public ManageSecEnv(int se) {
        this.se = se;
    }
    
    /**
     * SET provided CRT
     * @param crt
     */
    public ManageSecEnv(CRT crt) {
        this(crt.getUsageQualifier(), crt.getType(), crt.encodeKeyReference().encode(EncodingRules.DER));
    }
    /**
     * SET
     * @param usageQ
     * @param tag tag of crt component
     * @param data command data
     */
    public ManageSecEnv(UsageQualifier usageQ, CRTType crt, byte[] data) {
        this.usageQ = usageQ;
        this.crt = crt;
        this.data = data;
    }

    @Override
    public Void execute(ISmartCardChannel channel) throws CardException {
        
        ResponseData response;
        if(se == -1) {
            response = set(channel);
        } else {
            response = restore(channel);
        }
        
        err.processResponse(response);
        
        return null;
    }

    private ResponseData restore(ISmartCardChannel channel) throws CardException {
        
        return channel.transmit(new CommandData(0, INS, P1_RESTORE, se));
    }

    private ResponseData set(ISmartCardChannel channel) throws CardException {
        
        int p1 = usageQ.encode();
        p1 &= P1_SET_NEG_MASK;
        p1 |= P1_SET_POS_MASK;
        
        return channel.transmit(new CommandData(0, INS, p1, crt.getTag(), data));
    }

}

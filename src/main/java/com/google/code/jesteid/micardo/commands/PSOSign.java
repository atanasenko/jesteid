package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class PSOSign extends PerformSecOp<byte[]> {

    private static final int TAG_RESPONSE = 0x9e;
    private static final int TAG_COMMAND = 0x9a;

    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .error("64 00", "Hardware fault during the operation, inconsistencies in EF_KeyD, inconsistent or incorrect data")
                .error("65 81", "Error encountered while writing retry counter or usage counter")
                .error("67 00", "Response data has incorrect length, Lc is not equal to the length of the command data, Le is smaller than Lr")
                .error("69 82", "Access conditions for the key not fulfilled")
                .error("69 83", "Retry counter is zero")
                .error("69 84", "Usage counter is zero")
                .error("69 85", "Usage conditions for the key not fulfilled, referenced key cannot be used")
                .error("69 88", "DSI has incorrect length, incorrect data objects for secure messaging")
                .error("6A 88", "Key or hash function not found, referenced key not found");
            
    private final byte[] hash;
    
    public PSOSign(byte[] hash) {
        this.hash = hash;
    }

    protected int getResponseTag() {
        return TAG_RESPONSE;
    }

    protected int getCommandTag() {
        return TAG_COMMAND;
    }

    @Override
    public byte[] execute(ISmartCardChannel channel) throws CardException {
        
        ResponseData res = sendPSO(channel, false, hash, 0, hash.length);
        
        err.processResponse(res);
        
        return res.getData();
    }

}

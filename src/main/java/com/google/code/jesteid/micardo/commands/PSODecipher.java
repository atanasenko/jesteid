package com.google.code.jesteid.micardo.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.micardo.KeyAlgorithm;
import com.google.code.jesteid.sc.CommandException;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class PSODecipher extends PerformSecOp<Void> {

    private static final int TAG_RES = 0x80;
    private static final int TAG_CMD = 0x86;
    
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
                .error("69 88", "Contents of the cryptogram are not correct, incorrect data objects for secure messaging")
                .error("6A 88", "Referenced key not found");
    
    private final InputStream in;
    private final OutputStream out;
    private final KeyAlgorithm alg;
    
    public PSODecipher(InputStream in, OutputStream out, KeyAlgorithm alg) {
        this.in = in;
        this.out = out;
        this.alg = alg;
    }

    @Override
    protected int getResponseTag() {
        return TAG_RES;
    }

    @Override
    protected int getCommandTag() {
        return TAG_CMD;
    }
    
    @Override
    public Void execute(ISmartCardChannel channel) throws CardException {
        
        byte[] d = new byte[alg.getKeySize()+1];
        d[0] = (byte) alg.getPaddingIndicator();
        int l;
        try {
            while((l = in.read(d, 1, d.length-1)) != -1) {
                ResponseData res = sendPSO(channel, false, d, 0, l+1); // TODO 0x80 as Le ?
                
                err.processResponse(res);
                
                out.write(GetResponse.getData(channel, res));
            }
            
        } catch(IOException e) {
            throw new CommandException("Request/Response IO error", e);
        }
        
        return null;
    }

}

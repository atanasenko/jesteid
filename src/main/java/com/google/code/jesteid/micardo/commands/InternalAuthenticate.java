package com.google.code.jesteid.micardo.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.CommandException;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;
import com.google.code.jesteid.util.IOUtils;

public class InternalAuthenticate implements ICommand<Void>{

    private static final int INS = 0x88;
    private static final int P2_LOCAL_MASK = 0x80;

    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .warning("63 CX", "Repeated attempts were necessary: \\X")
                .error("68 51", "Write error")
                .error("67 00", "Lc is missing or incorrect, Le is missing, Le is smaller than Lr")
                .error("69 00", "Missing key reference")
                .error("69 82", "Security condition for K0.ICC not fulfilled")
                .error("69 83", "Retry counter has expired")
                .error("69 84", "Usage counter has expired")
                .error("69 85", "Incorrect algorithm ID or second key reference is missing, incorrect key length, referenced key cannot be used")
                .error("69 98", "Error encountered while deriving the session key")
                .error("6A 86", "P1 or P2 possesses an illegal value")
                .error("6A 88", "Referenced key not found")
                .error("6E 00", "CLA and INS are inconsistent");
                
    private final InputStream in;
    private final OutputStream out;
    private final int keyID;
    private final boolean global;
    
    public InternalAuthenticate(InputStream in, OutputStream out) {
        this(in, out, 0);
    }
    
    public InternalAuthenticate(InputStream in, OutputStream out, int keyID) {
        this(in, out, keyID, true);
    }
    
    public InternalAuthenticate(InputStream in, OutputStream out, int keyID, boolean global) {
        this.in = in;
        this.out = out;
        this.keyID = keyID;
        this.global = global;
    }

    @Override
    public Void execute(ISmartCardChannel channel) throws CardException {
        
        int p2 = keyID;
        if(!global) p2 |= P2_LOCAL_MASK;
        
        byte[] refData;
        try {
            refData = IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new CommandException("Error reading from command input", e);
        }
        
        ResponseData res = channel.transmit(new CommandData(0, INS, 0x00, p2, refData));
        byte[] result = GetResponse.getData(channel, res);
        
        err.processResponse(res);
        
        try {
            out.write(result);
        } catch (IOException e) {
            throw new CommandException("Error writing to command output", e);
        }
        return null;
    }
}

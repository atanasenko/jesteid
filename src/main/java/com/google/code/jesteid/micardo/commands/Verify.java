package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.PINVerificationException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class Verify implements ICommand<Void>{

    private static final int INS = 0x20;
    private static final int P2_LOCAL_MASK = 0x80;
    
    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .error("64 00", "Inconsistent or data is incorrect")
                .error("65 81", "Write error")
                .error("66 12", "Parity error in the cryptographic algorithm")
                .error("67 00", "Response data has incorrect length, Lc is missing or incorrect, Le is superfluous")
                .error("69 82", "Security conditions are not fulfilled")
                .error("69 83", "Retry counter has expired")
                .error("69 84", "Usage counter has expired")
                .error("69 85", "Referenced password cannot be used")
                .error("69 88", "Incorrect data objects for secure messaging")
                .error("6A 80", "Password has incorrect transport format")
                .error("6A 83", "Record not found")
                .error("6A 86", "P1 or P2 has an illegal value")
                .error("6A 88", "Referenced password not found")
                .error("6E 00", "CLA and INS are inconsistent")
                ;
    
    
    private final int pwdID;
    private final byte[] pwd;

    private final boolean global;

    public Verify(int pwdID, byte[] pwd) {
        this(pwdID, pwd, true);
    }
    
    public Verify(int pwdID, byte[] pwd, boolean global) {
        this.pwdID = pwdID;
        this.pwd = pwd;
        this.global = global;
    }

    @Override
    public Void execute(ISmartCardChannel channel) throws CardException {
        int p2 = pwdID;
        if(!global) p2 |= P2_LOCAL_MASK;
        ResponseData res = channel.transmit(new CommandData(0, INS, 0x00, p2, pwd));
        if(res.getSW1() == 0x63) {
            throw new PINVerificationException("Pin verification failed", res.getSW2() == 0 ? -1 : res.getSW2() - 0xC0);
        }
        
        err.processResponse(res);
        
        return null;
    }
}

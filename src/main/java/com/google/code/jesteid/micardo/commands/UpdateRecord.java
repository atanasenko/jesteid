package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class UpdateRecord implements ICommand<Void>{

    private static final int INS = 0xdc;
    private static final int CURRENT_EF = 0;
    private static final int ABS_ADDRESSING = 0x4;

    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .warning("63 CX", "Repeated write attempts were necessary")
                .error("64 00", "Referenced EF is deactivated, inconsistent or incorrect data")
                .error("65 81", "Write error")
                .error("67 00", "Response data has incorrect length, command data is missing, or Lc is not equal to the length of the command data, or the data length is not equal to the fixed record length, Le is superfluous")
                .error("69 81", "Referenced EF is not formatted")
                .error("69 82", "Security conditions are not fulfilled")
                .error("69 86", "Command not allowed, no EF selected")
                .error("69 88", "Incorrect data objects for secure messaging")
                .error("6A 82", "File not found via SFI")
                .error("6A 83", "Referenced record does not exist")
                .error("6A 84", "Too much data for the EF")
                .error("6A 86", "Parameters P1 - P2 incorrect")
                .error("6E 00", "CLA and INS are inconsistent");
    
    private final int sfi;
    private final int rec;
    private final byte[] data;

    public UpdateRecord(int rec, byte[] data) {
        this(CURRENT_EF, rec, data);
    }
    public UpdateRecord(int sfi, int rec, byte[] data) {
        this.sfi = sfi;
        this.rec = rec;
        this.data = data;
    }

    @Override
    public Void execute(ISmartCardChannel channel) throws CardException {
        int p2 = (sfi << 3) | ABS_ADDRESSING;
        ResponseData res = channel.transmit(new CommandData(0, INS, rec, p2, data));
        
        err.processResponse(res);
        
        return null;
    }
    
}

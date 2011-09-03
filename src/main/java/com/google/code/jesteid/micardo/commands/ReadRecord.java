package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class ReadRecord implements ICommand<byte[]>{
    
    /*
     * page 184 of Micardo doc
     */
    
    private static final int INS = 0xb2;
    private static final int CURRENT_EF = 0;
    private static final int ABS_ADDRESSING = 0x4;
    
    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .warning("62 82", "Length of the response data is smaller than Le")
                .warning("62 81", "Record and its checksum are inconsistent")
                .error("64 00", "EF to be read out is deactivated, inconsistent or incorrect data")
                .error("67 00", "EF to be read out is deactivated, inconsistent or incorrect data")
                .error("69 81", "EF to be read out is not formatted")
                .error("69 82", "Security conditions not fulfilled")
                .error("69 86", "Command not allowed, no EF is selected")
                .error("69 88", "Incorrect data objects for secure messaging")
                .error("6a 82", "File not found via SFI")
                .error("6a 83", "Referenced record does not exist")
                .error("6a 86", "Parameters P1 - P2 incorrect")
                .error("6e 00", "CLA and INS are inconsistent");
    
    private final int sfi;
    private final int record;
    private final int size;
    
    public ReadRecord(int record, int size) {
        this(CURRENT_EF, record, size);
    }

    public ReadRecord(int sfi, int record, int size) {
        this.sfi = sfi;
        this.record = record;
        this.size = size;
    }

    @Override
    public byte[] execute(ISmartCardChannel channel) throws CardException {
        int p2 = (sfi << 3) | ABS_ADDRESSING;
        ResponseData response = channel.transmit(new CommandData(0, INS, record, p2, size));
        
        err.processResponse(response);
        
        return response.getData();
    }

}

package com.google.code.jesteid.micardo.commands;

import java.io.ByteArrayOutputStream;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.BinUtils;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class ReadBinary implements ICommand<byte[]>{
    
    /*
     * Page 182 of Micardo doc
     */
    
    private static final int INS = 0xb0;
    
    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .warning("62 82", "Length of the response data is less than Le")
                .warning("62 81", "User data and checksum are inconsistent")
                .error("64 00", "EF to be read out is deactivated, inconsistent or incorrect data")
                .error("67 00", "Response data has incorrect length, Lc is superfluous, Le is missing")
                .error("69 81", "EF to be read out is not transparent")
                .error("69 82", "Security conditions are not fulfilled")
                .error("69 86", "Command not allowed, no EF is selected")
                .error("69 88", "Incorrect data objects for secure messaging")
                .error("6a 82", "File not found via SFI")
                .error("6a 86", "Parameters P1 - P2 incorrect")
                .error("6b 00", "Offset is outside the EF")
                .error("6e 00", "CLA and INS are inconsistent");
    
    private final int sfi;
    private final int size;

    private final int offset;
    
    public ReadBinary(int size) {
        this(0, size);
    }
    
    public ReadBinary(int offset, int size) {
        this(-1, offset, size);
    }
    
    public ReadBinary(int sfi, int offset, int size) {
        this.sfi = sfi;
        this.offset = offset;
        this.size = size;
        
        if(sfi == -1 && offset > 0x7fff) {
            
            // most significant bit must be 0
            throw new IllegalArgumentException("Invalid offset " + offset);
            
        } else if(sfi != -1) {
            
            if(offset > 0xff) {
                // only 1 byte of offset
                throw new IllegalArgumentException("Invalid offset " + offset + " for sfi " + BinUtils.toHex(sfi, 2));
            }
            
            if(size > (0xfe + 0xff)) {
                throw new IllegalArgumentException("Unable to read binary of size " + size + " for sfi " + BinUtils.toHex(sfi, 2));
            }
            
        }
    }

    public byte[] execute(ISmartCardChannel channel) throws CardException {
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        int offset = this.offset;
        int size = offset + this.size;
        while(offset < size) {
            int readSize = 0xfe;
            if(readSize > size - offset) {
                readSize = size - offset;
            }
            
            ResponseData res;
            if(sfi == -1) {
                res = readBinary(channel, offset, readSize);
            } else {
                res = readBinary(channel, 0x8000 | (sfi << 16) | offset, readSize);
            }
            
            byte[] d = GetResponse.getData(channel, res);
            out.write(d, 0, d.length);
            offset += d.length;
        }
        
        return out.toByteArray();
    }

    private ResponseData readBinary(ISmartCardChannel channel, int offset, int size) throws CardException {
        int offMsb = (offset >> 8) & 0xff;
        int offLsb = offset & 0xff;
        ResponseData response = channel.transmit(new CommandData(0, INS, offMsb, offLsb, size));
        err.processResponse(response);
        return response;
    }
    
}

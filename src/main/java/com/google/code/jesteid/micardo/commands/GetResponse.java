package com.google.code.jesteid.micardo.commands;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.CommandException;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.BinUtils;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class GetResponse implements ICommand<byte[]>{

    /*
     * Page 168 of Micardo doc
     */
    
    private static final int INS = 0xc0;
    
    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                // 61 XX
                .error("67 00", "Lc is superfluous, Le is missing")
                .error("69 85", "There is no data to be fetched")
                .error("6a 86", "P1 or P2 possesses an illegal value")
                .error("6e 00", "CLA and INS are inconsistent");
                // 6C XX
    
    public static byte[] getData(ISmartCardChannel channel, ResponseData response) 
            throws CardException {
        if(response.getSW() == 0x9000) {
            return response.getData();
        }
        if(response.getSW1() == 0x61) {
            return new GetResponse(response.getSW2()).execute(channel);
        }
        return null;
    }
    
    private final int size;

    public GetResponse(int size) {
        this.size = size;
    }
    
    @Override
    public byte[] execute(ISmartCardChannel channel) throws CardException {
        ResponseData response = getResponse(channel, size);
        
        if(response.getSW1() == 0x61) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                getResponseData(channel, response, out);
            } catch (IOException e) {
                // improbable
                throw new IllegalStateException(e);
            }
            return out.toByteArray();
        }
        // SW == 0x9000
        return response.getData();
    }
    
    private ResponseData getResponse(ISmartCardChannel channel, int size) throws CardException {
        ResponseData response = channel.transmit(new CommandData(0, INS, 0x00, 0x00, size));
        
        if(response.getSW1() == 0x61) {
            
            // will be handled later
            
        } else if(response.getSW1() == 0x6c) {
            
            throw new CommandException(BinUtils.toHex(response.getSW(), 4) + ": Send same command with Le = " + BinUtils.toHex(response.getSW2(), 2));
            
        } else {
            
            // remaining errors 
            err.processResponse(response);
            
        }
        
        return response;
    }
    
    private void getResponseData(ISmartCardChannel channel, ResponseData response, OutputStream out) 
            throws CardException, IOException {
        
        out.write(response.getData());
        
        if(response.getSW1() == 0x61) {
            // there is more
            ResponseData newResponse = getResponse(channel, response.getSW2());
            getResponseData(channel, newResponse, out);
        }
    }
}

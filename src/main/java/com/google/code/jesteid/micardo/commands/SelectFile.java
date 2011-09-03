package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.iso8825.EncodingRules;
import com.google.code.jesteid.iso8825.TLV;
import com.google.code.jesteid.micardo.DF;
import com.google.code.jesteid.micardo.FSEntry;
import com.google.code.jesteid.micardo.objects.FCP;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public class SelectFile implements ICommand<FCP>{
    
    /*
     * Page 193 of micardo doc
     */
    
    private static final int INS = 0xa4;
    private static final int SELECT_MF = 0x0;
    private static final int SELECT_DF = 0x1;
    private static final int SELECT_EF = 0x2;
    
    public static final int RETURN_NONE = 0xC;
    public static final int RETURN_FMD = 0x8;
    public static final int RETURN_FCP = 0x4;
    public static final int RETURN_FCP_FMD = 0x0;

    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .warning("62 83", "File has been deactivated")
                .error("64 00", "File header and checksum are inconsistent")
                .error("67 00", "Lc is not equal to the length of the command data, Le is missing or is superfluous, Le is smaller than Lr")
                .error("6A 80", "P1=´00´ yet FID is not ´3F00´")
                .error("6A 82", "File not found")
                .error("6A 86", "P1 or P2 possesses an illegal value")
                .error("6A 87", "Lc is missing or is superfluous")
                .error("6E 00", "CLA and INS are inconsistent");
    
    private final FSEntry entry;
    private final int ret;
    
    public SelectFile(FSEntry entry, int ret)
    {
        this.entry = entry;
        this.ret = ret;
    }

    @Override
    public FCP execute(ISmartCardChannel channel) throws CardException {
        
        ResponseData response = select(channel, entry, true);
        
        err.processResponse(response);
        
        TLV tlv = TLV.decode(response.getData(), 0, EncodingRules.DER);
        return FCP.forTLV(tlv);
    }
    
    private ResponseData select(ISmartCardChannel channel, FSEntry f, boolean last) throws CardException {
        if(f == FSEntry.MF) {
            return select(channel, SELECT_MF, last ? ret : RETURN_NONE);
        }
        
        select(channel, f.getParent(), false);
        
        if(f.isDir()) {
            DF df = (DF) f;
            if(df.isNamed()) {
                return select(channel, SELECT_DF, last ? ret : RETURN_NONE, df.getName());
            }
            return select(channel, SELECT_DF, last ? ret : RETURN_NONE, f.getFid());
        } else {
            return select(channel, SELECT_EF, last ? ret : RETURN_NONE, f.getFid());
        }
    }
    
    private ResponseData select(ISmartCardChannel channel, int sel, int ret) throws CardException {
        return channel.transmit(new CommandData(0, INS, sel, ret));
    }

    private ResponseData select(ISmartCardChannel channel, int sel, int ret, int fid) throws CardException {
        return channel.transmit(new CommandData(0, INS, sel, ret, 
                new byte[]{(byte)((fid >> 8)&0xff), (byte)(fid&0xff)}));
    }
    
    private ResponseData select(ISmartCardChannel channel, int sel, int ret, String name) throws CardException {
        return channel.transmit(new CommandData(0, INS, sel, ret, name.getBytes()));
    }
    
}

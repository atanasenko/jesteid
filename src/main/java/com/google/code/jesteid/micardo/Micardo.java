package com.google.code.jesteid.micardo;

import java.util.ArrayList;
import java.util.List;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.iso8825.EncodingRules;
import com.google.code.jesteid.iso8825.TLV;
import com.google.code.jesteid.micardo.commands.ReadRecord;
import com.google.code.jesteid.micardo.commands.SelectFile;
import com.google.code.jesteid.micardo.objects.Counter;
import com.google.code.jesteid.micardo.objects.FCP;
import com.google.code.jesteid.micardo.objects.KeyDesc;
import com.google.code.jesteid.micardo.objects.PwdDesc;
import com.google.code.jesteid.micardo.objects.SecurityEnv;
import com.google.code.jesteid.sc.ISmartCardChannel;

public class Micardo {
    
    public static final int EF_MF   = 0x3fff;
    
    public static final int EF_Pwd  = 0x0012;
    public static final int EF_PwdD = 0x0015;
    public static final int EF_PwdC = 0x0016;
    
    public static final int EF_Key  = 0x0010;
    public static final int EF_KeyD = 0x0013;
    
    public static final int EF_Rule = 0x0030;
    public static final int EF_SE   = 0x0033;
    public static final int EF_TIN  = 0x2fe4;
    public static final int EF_ATR  = 0x2f01;
    public static final int EF_RFU  = 0xffff;

    private ISmartCardChannel sc;
    
    public Micardo(ISmartCardChannel sc) {
        this.sc = sc;
    }
    
    public Counter getKeyCounter(DF dir, int rec) throws CardException {
        return getCounter(dir.file(EF_KeyD), rec);
    }
    
    public Counter getPwdCounter(DF dir, int rec) throws CardException {
        return getCounter(dir.file(EF_PwdC), rec);
    }
    
    private Counter getCounter(EF file, int rec) throws CardException {
        FCP fcp = new SelectFile(file, SelectFile.RETURN_FCP).execute(sc);
        int l = fcp.getFileDescriptor().maxRecordLength;
        
        byte[] data = new ReadRecord(rec, l).execute(sc);
        return Counter.decode(TLV.decodeList(data, 0, EncodingRules.DER));
    }
    
    public List<PwdDesc> getPwdDescriptors() throws CardException {
        return getPwdDescriptors(FSEntry.MF);
    }
    
    public List<PwdDesc> getPwdDescriptors(DF dir) throws CardException {
        FCP fcp = new SelectFile(dir.file(Micardo.EF_PwdD), SelectFile.RETURN_FCP).execute(sc);
        int r = fcp.getFileDescriptor().maxRecords;
        int l = fcp.getFileDescriptor().maxRecordLength;
        
        List<PwdDesc> pwds = new ArrayList<PwdDesc>();
        
        for(int i = 0; i < r; i++) {
            byte[] data = new ReadRecord(i+1, l).execute(sc);
            pwds.add(new PwdDesc(TLV.decodeList(data, 0, EncodingRules.DER)));
        }
        
        return pwds;
    }
    
    public List<KeyDesc> getKeyDescriptors() throws CardException {
        return getKeyDescriptors(FSEntry.MF);
    }
    
    public List<KeyDesc> getKeyDescriptors(DF dir) throws CardException {
        FCP fcp = new SelectFile(dir.file(Micardo.EF_KeyD), SelectFile.RETURN_FCP).execute(sc);
        int r = fcp.getFileDescriptor().maxRecords;
        int l = fcp.getFileDescriptor().maxRecordLength;
        
        List<KeyDesc> pwds = new ArrayList<KeyDesc>();
        
        for(int i = 0; i < r; i++) {
            byte[] data = new ReadRecord(i+1, l).execute(sc);
            pwds.add(KeyDesc.decode(TLV.decodeList(data, 0, EncodingRules.DER)));
        }
        
        return pwds;
    }
    
    public List<SecurityEnv> getSecurityEnvironments(DF dir) throws CardException {
        
        FCP fcp = new SelectFile(dir.file(Micardo.EF_SE), SelectFile.RETURN_FCP).execute(sc);
        int r = fcp.getFileDescriptor().maxRecords;
        int l = fcp.getFileDescriptor().maxRecordLength;
        
        List<SecurityEnv> envs = new ArrayList<SecurityEnv>();
        
        for(int i = 0; i < r; i++) {
            byte[] data = new ReadRecord(i+1, l).execute(sc);
            envs.add(new SecurityEnv(data[0], TLV.decodeList(data, 1, EncodingRules.DER)));
        }
        
        return envs;
    }
    
    public SecurityEnv getSecurityEnvironment(DF dir, int se) throws CardException {
        for(SecurityEnv env: getSecurityEnvironments(dir)) {
            if(env.getSe() == se) {
                return env;
            }
        }
        return null;
    }
    
}

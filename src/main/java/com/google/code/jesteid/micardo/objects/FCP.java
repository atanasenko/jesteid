package com.google.code.jesteid.micardo.objects;

import com.google.code.jesteid.iso8825.ConstructedTLV;
import com.google.code.jesteid.iso8825.TLV;
import com.google.code.jesteid.util.BinUtils;

public class FCP {
    
    /*
     * Page 36 of micardo doc
     */
    
    private static final long TAG_FCP = 0x62;
    //private static final long TAG_FMD = 0x64;
    private static final long TAG_FCP_FMD = 0x6f;
    
    private static final long TAG_FILE_DESC = 0x82;
    private static final long TAG_FID = 0x83;
    private static final long TAG_NAME = 0x84;
    private static final long TAG_MEMORY_SPACE = 0x85;
    private static final long TAG_SHORT_ID = 0x88;
    private static final long TAG_LCSI = 0x8a; // lifecycle status integer
    private static final long TAG_ARR = 0xa1; // access rule reference
    
    private ConstructedTLV data;
    private int fid;
    private EntryType entryType;
    private FileDescriptor fileDescriptor;
    private String dirName;
    private Integer sfi;
    private int maxSpace;
    private boolean deactivated;
    
    private FCP(TLV data) {
        this.data = (ConstructedTLV) data;
        decode();
    }
    
    public static FCP forTLV(TLV tlv) {
        if(tlv == null || !tlv.isConstructed()) {
            return null;
        }
        long tag = tlv.getTag().getTag();
        if(tag == TAG_FCP || tag == TAG_FCP_FMD) {
            return new FCP(tlv);
        }
        
        return null;
    }
    
    private void decode() {
        for(TLV tlv: data.getContents()) {
            if(tlv.getTag().getTag() == TAG_FILE_DESC) {
                
                byte[] data = tlv.getData();
                if(data.length == 1) {
                    
                    if(data[0] == 0x38) entryType = EntryType.DIRECTORY;
                    else if(data[0] == 0x1) entryType = EntryType.TRANSPARENT_DATA_FIELD;
                    else throw new IllegalStateException("Unsupported file type " + BinUtils.toHex(data[0], 2));
                    
                } else {
                    entryType = EntryType.FORMATTED_DATA_FIELD;
                    fileDescriptor = new FileDescriptor(
                            (data[0] & 0x20) > 0,
                            (data[0] & 0x10) > 0,
                            FileType.forCode(data[0] & 0x7),
                            (int) BinUtils.fromBytes(data, 2, 2),
                            data[4]
                    );
                }
                
            } else if(tlv.getTag().getTag() == TAG_FID) {
                
                fid = (int)BinUtils.fromBytes(tlv.getData());
                
            } else if(tlv.getTag().getTag() == TAG_NAME) {
                
                dirName = new String(tlv.getData());
                
            } else if(tlv.getTag().getTag() == TAG_MEMORY_SPACE) {
                
                maxSpace = (int)BinUtils.fromBytes(tlv.getData());
                
            } else if(tlv.getTag().getTag() == TAG_SHORT_ID) {
                
                sfi = tlv.getData()[0] & 0x7;
                
            } else if(tlv.getTag().getTag() == TAG_LCSI) {
                
                deactivated = tlv.getData()[0] == 0x4;
                
            } else if(tlv.getTag().getTag() == TAG_ARR) {
                
                // we don't use it yet
                
            }
        }
    }
    
    public int getFid() {
        return fid;
    }

    public EntryType getEntryType() {
        return entryType;
    }

    public FileDescriptor getFileDescriptor() {
        return fileDescriptor;
    }

    public String getDirName() {
        return dirName;
    }

    public Integer getSfi() {
        return sfi;
    }

    public int getMaxSpace() {
        return maxSpace;
    }

    public boolean isDeactivated() {
        return deactivated;
    }

    public String toString() {
        return BinUtils.toHex(fid, 4) + "::" + entryType + 
                (entryType == EntryType.FORMATTED_DATA_FIELD ? "{ ShortID: " + sfi + ", " + fileDescriptor + " }" : "") +
                (entryType == EntryType.DIRECTORY ? "{ Name: " + dirName + " }": "") +
                ", MaxSpace: " + maxSpace +
                (deactivated ? ", Deactivated" : "") +
                "";
    }
    
    public enum EntryType {
        DIRECTORY,
        TRANSPARENT_DATA_FIELD,
        FORMATTED_DATA_FIELD;
    }
    
    public enum FileType {
        LINEAR_CONSTANT,
        LINEAR_VARIABLE,
        CYCLIC;

        public static FileType forCode(int i) {
            if(i == 0x2) return LINEAR_CONSTANT;
            if(i == 0x4) return LINEAR_VARIABLE;
            if(i == 0x6) return CYCLIC;
            throw new IllegalArgumentException("Unknown file type " + BinUtils.toHex(i, 2));
        }
    }
    
    public static class FileDescriptor {
        public final boolean timeOptimizedAccess;
        public final boolean locallyUsable;
        public final FileType fileType;
        public final int maxRecordLength;
        public final int maxRecords;
        
        public FileDescriptor(boolean timeOptimizedAccess, boolean locallyUsable, FileType fileType,
                int maxRecordLength, int maxRecords) {
            this.timeOptimizedAccess = timeOptimizedAccess;
            this.locallyUsable = locallyUsable;
            this.fileType = fileType;
            this.maxRecordLength = maxRecordLength;
            this.maxRecords = maxRecords;
        }
        
        public String toString() {
            return "Access: " + (timeOptimizedAccess ? "time-optimized" : "standard") +
                    ", Usability: " + (locallyUsable ? "locally" : "jointly") + 
                    ", Type: " + fileType +
                    ", MaxRecords: " + maxRecords + 
                    ", MaxRecordBytes: " + maxRecordLength;
        }
    }
}

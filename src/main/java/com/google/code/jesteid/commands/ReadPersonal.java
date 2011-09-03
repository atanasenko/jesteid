package com.google.code.jesteid.commands;

import java.nio.charset.Charset;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.PersonalData;
import com.google.code.jesteid.impl.Esteid;
import com.google.code.jesteid.micardo.commands.ReadRecord;
import com.google.code.jesteid.micardo.commands.SelectFile;
import com.google.code.jesteid.micardo.objects.FCP;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;

public class ReadPersonal implements ICommand<PersonalData>{
    
    private static final int PERSONAL_REC_SUR_NAME        = 0;
    private static final int PERSONAL_REC_GIVEN_NAME_1    = 1;
    private static final int PERSONAL_REC_GIVEN_NAME_2    = 2;
    private static final int PERSONAL_REC_SEX             = 3;
    private static final int PERSONAL_REC_CITIZENSHIP     = 4;
    private static final int PERSONAL_REC_DATE_OF_BIRTH   = 5;
    private static final int PERSONAL_REC_PERSONAL_CODE   = 6;
    private static final int PERSONAL_REC_DOCUMENT_NUMBER = 7;
    private static final int PERSONAL_REC_DATE_OF_EXPIRY  = 8;
    private static final int PERSONAL_REC_PLACE_OF_BIRTH  = 9;
    private static final int PERSONAL_REC_DATE_OF_ISSUE   = 10;
    private static final int PERSONAL_REC_RIGHT_TYPE      = 11;
    private static final int PERSONAL_REC_MEMO_1          = 12;
    private static final int PERSONAL_REC_MEMO_2          = 13;
    private static final int PERSONAL_REC_MEMO_3          = 14;
    private static final int PERSONAL_REC_MEMO_4          = 15;

    @Override
    public PersonalData execute(ISmartCardChannel channel) throws CardException {
        
        // get info
        FCP fcp = 
                new SelectFile(Esteid.PERSONAL_FILE, SelectFile.RETURN_FCP)
                    .execute(channel);
        
        // read records
        Charset cs = Charset.forName("ISO-8859-1");
        int l = fcp.getFileDescriptor().maxRecords;
        int recSize = fcp.getFileDescriptor().maxRecordLength;
        PersonalData pdata = new PersonalData();
        
        String[] records = new String[l];
        
        for(int i = 0; i < l; i++) {
            byte[] recData = new ReadRecord(i+1, recSize).execute(channel);
            String record;
            if(recData.length == 0 || (recData.length == 1 && recData[0] == 0)) {
                record = null;
            } else {
                record = new String(recData, cs);
            }
            records[i] = record;
        }
        
        pdata.setIdCode(records[PERSONAL_REC_PERSONAL_CODE]);
        pdata.setGivenName(records[PERSONAL_REC_GIVEN_NAME_1]);
        pdata.setGivenName2(records[PERSONAL_REC_GIVEN_NAME_2]);
        pdata.setSurName(records[PERSONAL_REC_SUR_NAME]);
        pdata.setSex(records[PERSONAL_REC_SEX]);
        pdata.setDateOfBirth(records[PERSONAL_REC_DATE_OF_BIRTH]);
        pdata.setPlaceOfBirth(records[PERSONAL_REC_PLACE_OF_BIRTH]);
        pdata.setCitizenship(records[PERSONAL_REC_CITIZENSHIP]);
        pdata.setRightType(records[PERSONAL_REC_RIGHT_TYPE]);
        pdata.setDocumentNumber(records[PERSONAL_REC_DOCUMENT_NUMBER]);
        pdata.setDateOfIssue(records[PERSONAL_REC_DATE_OF_ISSUE]);
        pdata.setDateOfExpiry(records[PERSONAL_REC_DATE_OF_EXPIRY]);
        pdata.setMemo1(records[PERSONAL_REC_MEMO_1]);
        pdata.setMemo2(records[PERSONAL_REC_MEMO_2]);
        pdata.setMemo3(records[PERSONAL_REC_MEMO_3]);
        pdata.setMemo4(records[PERSONAL_REC_MEMO_4]);
        
        return pdata;
    }
    

}

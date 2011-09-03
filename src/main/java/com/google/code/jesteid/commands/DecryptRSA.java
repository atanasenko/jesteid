package com.google.code.jesteid.commands;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.Password;
import com.google.code.jesteid.IPasswordProvider;
import com.google.code.jesteid.impl.Esteid;
import com.google.code.jesteid.micardo.CRTType;
import com.google.code.jesteid.micardo.KeyAlgorithm;
import com.google.code.jesteid.micardo.commands.ManageSecEnv;
import com.google.code.jesteid.micardo.commands.PSODecipher;
import com.google.code.jesteid.micardo.commands.Verify;
import com.google.code.jesteid.micardo.objects.CRT;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;

public class DecryptRSA implements ICommand<Void> {
    
    private final InputStream in;
    private final OutputStream out;
    private final IPasswordProvider codeProvider;
    private final Esteid esteid;

    public DecryptRSA(InputStream in, OutputStream out, IPasswordProvider codeProvider, Esteid esteid) {
        this.in = in;
        this.out = out;
        this.codeProvider = codeProvider;
        this.esteid = esteid;
    }

    @Override
    public Void execute(ISmartCardChannel channel) throws CardException {
        
        CRT ctCRT = esteid.getActiveCRT(CRTType.DST); // decrypting with sign private key
        // esteid dir remains selected after reading EF_SE
        //new SelectFile(EsteidCard.ESTEID_DIR, SelectFile.RETURN_NONE).execute(channel);
        
        new ManageSecEnv(Esteid.SE6).execute(channel);
        
        // remove auth key reference
        new ManageSecEnv(new CRT(CRTType.AT, ctCRT.getUsageQualifier(), null)).execute(channel);
        // remove sign key reference
        new ManageSecEnv(new CRT(CRTType.DST, ctCRT.getUsageQualifier(), null)).execute(channel);
        // set active sign key as confidentiality reference
        new ManageSecEnv(new CRT(CRTType.CT, ctCRT.getUsageQualifier(), ctCRT.getKeyReference())).execute(channel);
        
        new Verify(Esteid.PWDID_PIN2, codeProvider.providePassword(Password.PIN2)).execute(channel);
        
        new PSODecipher(in, out, KeyAlgorithm.RSA_1024).execute(channel);
        
        // TODO decrypt
        
        return null;
    }

}

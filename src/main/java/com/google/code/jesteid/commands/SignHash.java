package com.google.code.jesteid.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.Password;
import com.google.code.jesteid.IPasswordProvider;
import com.google.code.jesteid.crypto.SHA1Hash;
import com.google.code.jesteid.impl.Esteid;
import com.google.code.jesteid.micardo.commands.ManageSecEnv;
import com.google.code.jesteid.micardo.commands.PSOSign;
import com.google.code.jesteid.micardo.commands.SelectFile;
import com.google.code.jesteid.micardo.commands.Verify;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;

public class SignHash implements ICommand<byte[]> {

    private final SHA1Hash hash;
    private final IPasswordProvider codeProvider;

    public SignHash(SHA1Hash hash, IPasswordProvider codeProvider) {
        this.hash = hash;
        this.codeProvider = codeProvider;
    }
    
    @Override
    public byte[] execute(ISmartCardChannel channel) throws CardException {
        
        new SelectFile(Esteid.ESTEID_DIR, SelectFile.RETURN_NONE).execute(channel);
        new ManageSecEnv(Esteid.SE1).execute(channel);
        new Verify(Esteid.PWDID_PIN2, codeProvider.providePassword(Password.PIN2)).execute(channel);
        return new PSOSign(hash.getHashBytes()).execute(channel);
        
    }

}

package com.google.code.jesteid.commands;

import java.io.InputStream;
import java.io.OutputStream;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.Password;
import com.google.code.jesteid.IPasswordProvider;
import com.google.code.jesteid.impl.Esteid;
import com.google.code.jesteid.micardo.commands.InternalAuthenticate;
import com.google.code.jesteid.micardo.commands.ManageSecEnv;
import com.google.code.jesteid.micardo.commands.SelectFile;
import com.google.code.jesteid.micardo.commands.Verify;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;

public class SSLChallenge implements ICommand<Void>{

    private final InputStream in;
    private final OutputStream out;
    private final IPasswordProvider codeProvider;

    public SSLChallenge(InputStream in, OutputStream out, IPasswordProvider codeProvider) {
        this.in = in;
        this.out = out;
        this.codeProvider = codeProvider;
    }
    
    @Override
    public Void execute(ISmartCardChannel channel) throws CardException {
        
        new SelectFile(Esteid.ESTEID_DIR, SelectFile.RETURN_NONE).execute(channel);
        new ManageSecEnv(Esteid.SE1).execute(channel);
        new Verify(Esteid.PWDID_PIN1, codeProvider.providePassword(Password.PIN1)).execute(channel);
        new InternalAuthenticate(in, out).execute(channel);
        
        return null;
    }

}

package com.google.code.jesteid.commands;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.CertType;
import com.google.code.jesteid.impl.Esteid;
import com.google.code.jesteid.micardo.EF;
import com.google.code.jesteid.micardo.commands.ReadBinary;
import com.google.code.jesteid.micardo.commands.SelectFile;
import com.google.code.jesteid.micardo.objects.FCP;
import com.google.code.jesteid.micardo.objects.FCP.EntryType;
import com.google.code.jesteid.sc.CommandException;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;

public class ReadCertificate implements ICommand<X509Certificate> {

    private final CertType certType;

    public ReadCertificate(CertType certType) {
        this.certType = certType;
    }

    @Override
    public X509Certificate execute(ISmartCardChannel channel) throws CardException {
        
        EF file = certType == CertType.AUTH ? Esteid.AUTH_CERT_FILE : Esteid.SIGN_CERT_FILE;
        
        FCP fcp = 
                new SelectFile(file, SelectFile.RETURN_FCP).execute(channel);
        
        // ensure it is a transparent field
        if(fcp.getEntryType() != EntryType.TRANSPARENT_DATA_FIELD) {
            throw new IllegalArgumentException("File " + file + " is not " + EntryType.TRANSPARENT_DATA_FIELD);
        }
        
        
        // read binary data
        byte[] certData = new ReadBinary(fcp.getMaxSpace()).execute(channel);
        
        // build certificate
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X509");
            return (X509Certificate)cf.generateCertificate(new ByteArrayInputStream(certData));
        } catch(CertificateException e) {
            throw new CommandException("Error creating certificate object", e);
        }
    }

}

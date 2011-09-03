package com.google.code.jesteid.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.CertType;
import com.google.code.jesteid.CounterType;
import com.google.code.jesteid.IPasswordProvider;
import com.google.code.jesteid.IEsteid;
import com.google.code.jesteid.PersonalData;
import com.google.code.jesteid.commands.DecryptRSA;
import com.google.code.jesteid.commands.ReadCertificate;
import com.google.code.jesteid.commands.ReadCounter;
import com.google.code.jesteid.commands.ReadPersonal;
import com.google.code.jesteid.commands.SSLChallenge;
import com.google.code.jesteid.commands.SignHash;
import com.google.code.jesteid.crypto.SHA1Hash;
import com.google.code.jesteid.micardo.CRTType;
import com.google.code.jesteid.micardo.DF;
import com.google.code.jesteid.micardo.EF;
import com.google.code.jesteid.micardo.FSEntry;
import com.google.code.jesteid.micardo.Micardo;
import com.google.code.jesteid.micardo.objects.CRT;
import com.google.code.jesteid.micardo.objects.Counter;
import com.google.code.jesteid.micardo.objects.KeyRef;
import com.google.code.jesteid.micardo.objects.SecurityEnv;
import com.google.code.jesteid.micardo.objects.UsageQualifier;
import com.google.code.jesteid.sc.ISmartCard;

public class Esteid implements IEsteid {
    
    public static final byte[] ESTEID_HISTORICAL_BYTES = "EstEID ver 1.0".getBytes();
    
    public static final DF ESTEID_DIR = FSEntry.MF.dir(0xeeee);
    public static final EF PERSONAL_FILE = ESTEID_DIR.file(0x5044);
    public static final EF AUTH_CERT_FILE = ESTEID_DIR.file(0xaace);
    public static final EF SIGN_CERT_FILE = ESTEID_DIR.file(0xddce);
    public static final EF KEY_COUNTER_FILE  = FSEntry.MF.file(Micardo.EF_KeyD);
    
    public static final int KEY_REC_PASSWORD1 = 1;
    public static final int KEY_REC_PASSWORD2 = 2;
    
    public static final KeyRef KEY_REF_PASSWORD1 = new KeyRef(0x04, 0, -1);
    public static final KeyRef KEY_REF_PASSWORD2 = new KeyRef(0x05, 0, -1);
    
    public static final int PWDID_PIN1 = 1;
    public static final int PWDID_PIN2 = 2;

    public static final int SE0 = 0;
    public static final int SE1 = 1;
    public static final int SE6 = 6;
    
    public static final UsageQualifier KEY_USAGE = UsageQualifier.decode(0x40);

    private final ISmartCard smartCard;
    
    
    private PersonalData personalData;
    private Map<CertType, X509Certificate> certificates;
    
    
    public Esteid(ISmartCard smartCard) throws CardException {
        if(!Arrays.equals(smartCard.getHistoricalBytes(), ESTEID_HISTORICAL_BYTES)) {
            throw new CardException("Not an EstEID card");
        }
        
        this.smartCard = smartCard;
    }
    
    public PersonalData getPersonalData() throws CardException {
        
        if(personalData == null) {
            personalData = new ReadPersonal().execute(smartCard.getChannel());
        }
        
        return personalData;
        
    }
    
    public X509Certificate getCertificate(CertType certType) throws CardException {
        
        if(certificates == null) {
            certificates = new EnumMap<CertType, X509Certificate>(CertType.class);
        }
        X509Certificate cert = certificates.get(certType);
        if(cert == null) {
            cert = new ReadCertificate(certType).execute(smartCard.getChannel());
            certificates.put(certType, cert);
        }
        
        return cert;
    }
    
    public Counter readCounter(CounterType counterType) throws CardException {
        
        return new ReadCounter(counterType).execute(smartCard.getChannel());
        
    }
    
    public byte[] sslChallenge(byte[] data, IPasswordProvider codeProvider) throws CardException {
        
        return sslChallenge(data, 0, data.length, codeProvider);
        
    }
    
    public byte[] sslChallenge(byte[] data, int offset, int length, IPasswordProvider codeProvider) throws CardException {
        ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        sslChallenge(in, out, codeProvider);
        
        return out.toByteArray();
    }
        
    public void sslChallenge(InputStream in, OutputStream out, IPasswordProvider codeProvider) throws CardException {
        
        new SSLChallenge(in, out, codeProvider).execute(smartCard.getChannel());
        
    }
    
    public byte[] decryptRSA(byte[] data, IPasswordProvider codeProvider) throws CardException {
        
        return decryptRSA(data, 0, data.length, codeProvider);
        
    }
    
    public byte[] decryptRSA(byte[] data, int offset, int length, IPasswordProvider codeProvider) throws CardException {
        ByteArrayInputStream in = new ByteArrayInputStream(data, offset, length);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        decryptRSA(in, out, codeProvider);
        
        return out.toByteArray();
    }
        
    public void decryptRSA(InputStream in, OutputStream out, IPasswordProvider codeProvider) throws CardException {
        
        new DecryptRSA(in, out, codeProvider, this).execute(smartCard.getChannel());
        
    }
    
    public byte[] signHash(SHA1Hash hash, IPasswordProvider codeProvider) throws CardException {
        
        return new SignHash(hash, codeProvider).execute(smartCard.getChannel());
        
    }
    
    public CRT getActiveCRT(CRTType type) throws CardException {
        Micardo micardo = new Micardo(smartCard.getChannel());
        SecurityEnv env = micardo.getSecurityEnvironment(ESTEID_DIR, Esteid.SE0);
        return env.getCRT(type);

    }
    
}

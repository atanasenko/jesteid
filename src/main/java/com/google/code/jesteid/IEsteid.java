package com.google.code.jesteid;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;

import com.google.code.jesteid.crypto.SHA1Hash;
import com.google.code.jesteid.micardo.objects.Counter;

public interface IEsteid {

    PersonalData getPersonalData() throws CardException;

    X509Certificate getCertificate(CertType certType) throws CardException;

    Counter readCounter(CounterType counterType) throws CardException;

    byte[] sslChallenge(byte[] data, IPasswordProvider codeProvider) throws CardException;

    byte[] sslChallenge(byte[] data, int offset, int length, IPasswordProvider codeProvider) 
            throws CardException;

    void sslChallenge(InputStream in, OutputStream out, IPasswordProvider codeProvider) 
            throws CardException;

    byte[] decryptRSA(byte[] data, IPasswordProvider codeProvider) throws CardException;

    byte[] decryptRSA(byte[] data, int offset, int length, IPasswordProvider codeProvider) 
            throws CardException;

    void decryptRSA(InputStream in, OutputStream out, IPasswordProvider codeProvider) 
            throws CardException;

    byte[] signHash(SHA1Hash hash, IPasswordProvider codeProvider) throws CardException;

}
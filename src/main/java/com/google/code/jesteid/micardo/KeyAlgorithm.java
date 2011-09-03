package com.google.code.jesteid.micardo;

public enum KeyAlgorithm {
    DES(1, 0x10),
    DES3(1, 0x30),
    RSA_768(0, 0x60),
    RSA_1024(0, 0x80);
    
    private final int paddingIndicator;
    private final int keySize;

    private KeyAlgorithm(int paddingIndicator, int keySize) {
        this.paddingIndicator = paddingIndicator;
        this.keySize = keySize;
    }

    public int getPaddingIndicator() {
        return paddingIndicator;
    }

    public int getKeySize() {
        return keySize;
    }
    
}

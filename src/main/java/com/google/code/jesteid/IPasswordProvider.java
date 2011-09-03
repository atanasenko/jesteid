package com.google.code.jesteid;

public interface IPasswordProvider {
    
    byte[] providePassword(Password key);
    
}

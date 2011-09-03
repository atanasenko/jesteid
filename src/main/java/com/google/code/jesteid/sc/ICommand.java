package com.google.code.jesteid.sc;

import com.google.code.jesteid.CardException;

public interface ICommand<T> {

    T execute(ISmartCardChannel channel) throws CardException;
    
}

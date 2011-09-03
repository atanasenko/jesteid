package com.google.code.jesteid.sc;

import com.google.code.jesteid.CardException;

public interface ISmartCardChannel {
    
    ResponseData transmit(CommandData cmd) throws CardException;
    
}

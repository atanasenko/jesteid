package com.google.code.jesteid.sc;

import com.google.code.jesteid.CardException;

public interface ISmartCard {

    String getTerminalName();

    void beginExclusive() throws CardException;

    void endExclusive() throws CardException;

    void disconnect() throws CardException;

    ISmartCardChannel getChannel();
    
    byte[] getHistoricalBytes();
}
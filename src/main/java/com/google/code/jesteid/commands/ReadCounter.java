package com.google.code.jesteid.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.CounterType;
import com.google.code.jesteid.impl.Esteid;
import com.google.code.jesteid.micardo.FSEntry;
import com.google.code.jesteid.micardo.Micardo;
import com.google.code.jesteid.micardo.objects.Counter;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;

public class ReadCounter implements ICommand<Counter> {

    private final CounterType counterType;

    public ReadCounter(CounterType counterType) {
        this.counterType = counterType;
    }

    @Override
    public Counter execute(ISmartCardChannel channel) throws CardException {
        Micardo micardo = new Micardo(channel);
        switch(counterType){
        case PIN1:      return micardo.getPwdCounter(FSEntry.MF, 1);
        case PIN2:      return micardo.getPwdCounter(FSEntry.MF, 2);
        case PUK:       return micardo.getPwdCounter(FSEntry.MF, 3);
        
        case PASSWORD1: return micardo.getKeyCounter(FSEntry.MF, 5);
        case PASSWORD2: return micardo.getKeyCounter(FSEntry.MF, 6);
        
        case KEY_SIGN1: return micardo.getKeyCounter(Esteid.ESTEID_DIR, 1);
        case KEY_SIGN2: return micardo.getKeyCounter(Esteid.ESTEID_DIR, 2);
        case KEY_AUTH1: return micardo.getKeyCounter(Esteid.ESTEID_DIR, 3);
        case KEY_AUTH2: return micardo.getKeyCounter(Esteid.ESTEID_DIR, 4);
        default:        return null;
        }
    }
}

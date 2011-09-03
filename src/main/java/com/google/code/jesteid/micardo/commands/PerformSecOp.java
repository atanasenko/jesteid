package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.ErrorCodeProcessor;

public abstract class PerformSecOp<T> implements ICommand<T> {
    
    private static final int INS = 0x2a;
    private static final int CLA_SUBCOMMAND_MASK = 0x10;
    
    private static final ErrorCodeProcessor err = 
            new ErrorCodeProcessor()
                .success("90 00")
                .error("6A 86", "Illegal combination of P1 and P2 for PSO commands");
                
    protected abstract int getResponseTag();
    
    protected abstract int getCommandTag();
    
    protected ResponseData sendPSO(ISmartCardChannel channel, boolean subCommand, byte[] cmdData, int offset, int length) throws CardException {
        int cla = 0;
        if(subCommand) cla |= CLA_SUBCOMMAND_MASK;
        ResponseData res = channel.transmit(new CommandData(cla, INS, getResponseTag(), getCommandTag(), cmdData, offset, length));
        
        err.processResponse(res);
        
        return res;
    }

}

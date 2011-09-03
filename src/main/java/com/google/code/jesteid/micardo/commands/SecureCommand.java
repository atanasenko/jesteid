package com.google.code.jesteid.micardo.commands;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ICommand;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;

public class SecureCommand<T> implements ICommand<T> {
    
    private ICommand<T> command;
    
    public SecureCommand(ICommand<T> command) {
        this.command = command;
    }

    public T execute(ISmartCardChannel channel) throws CardException {
        
        return command.execute(new SecureChannel(channel));
    }
    
    private CommandData wrapCommand(CommandData cmd) {
        // TODO
        return cmd;
    }

    private ResponseData unwrapResponse(ResponseData res) {
        // TODO
        return res;
    }

    private class SecureChannel implements ISmartCardChannel {
        
        private ISmartCardChannel channel;
        
        SecureChannel(ISmartCardChannel channel) {
            this.channel = channel;
        }

        public ResponseData transmit(CommandData cmd) throws CardException {
            
            cmd = wrapCommand(cmd);
            ResponseData res = channel.transmit(cmd);
            return unwrapResponse(res);
        }
        
    }

}

package com.google.code.jesteid.sc;

import com.google.code.jesteid.CardException;

public class CommandException extends CardException {
    
    private static final long serialVersionUID = 1L;
    
    public CommandException(String message) {
        super(message);
    }

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }


}

package com.google.code.jesteid;

public class CardException extends Exception {
    
    private static final long serialVersionUID = 1L;

    public CardException(Throwable cause) {
        super(cause);
    }
    
    public CardException(String message) {
        super(message);
    }
    
    public CardException(String message, Throwable cause) {
        super(message, cause);
    }
    
}

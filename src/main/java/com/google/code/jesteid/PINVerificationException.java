package com.google.code.jesteid;

import com.google.code.jesteid.sc.CommandException;

public class PINVerificationException extends CommandException {

    private static final long serialVersionUID = 1L;
    
    private int retries;
    
    public PINVerificationException(String cause, int retries) {
        super(cause);
        this.retries = retries;
    }

    
    /**
     * Returns the number of allowed subsequent retries.
     * If this information is not available, -1 is returned
     * @return number of retries or -1
     */
    public int getRetries(){
        return retries;
    }
}

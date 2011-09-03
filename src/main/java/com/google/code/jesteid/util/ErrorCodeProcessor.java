package com.google.code.jesteid.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.code.jesteid.sc.CommandException;
import com.google.code.jesteid.sc.ResponseData;

public class ErrorCodeProcessor {
    
    private Condition success;
    private List<Condition> warnings;
    private List<Condition> errors;
    
    public ErrorCodeProcessor success(String code) {
        if(success != null) throw new IllegalStateException("Only one success code allowed");
        success = new Condition(code, null);
        return this;
    }
    
    public ErrorCodeProcessor error(String code, String message) {
        
        if(errors == null) errors = new LinkedList<ErrorCodeProcessor.Condition>();
        errors.add(new Condition(code, message));
        return this;
    }
    
    public ErrorCodeProcessor warning(String code, String message) {
        
        if(warnings == null) warnings = new LinkedList<ErrorCodeProcessor.Condition>();
        warnings.add(new Condition(code, message));
        return this;
    }
    
    public List<Warning> processResponse(ResponseData response) throws CommandException {
        
        int code = response.getSW();
        
        if(success.matches(code)) return null;

        List<Warning> ws = null;
        
        if(errors != null) {
            for(Condition c: errors) {
                if(c.matches(code)) {
                    throw new CommandException(c.getMessage(code));
                }
            }
        }
        
        boolean processed = false;
        if(warnings != null) {
            for(Condition c: warnings) {
                if(c.matches(code)) {
                    if(ws == null) ws = new ArrayList<Warning>();
                    ws.add(new Warning(c.getMessage(code)));
                    System.out.println("Warning: " + c.getMessage(code));
                    processed = true;
                }
            }
        }
        
        if(!processed) {
            throw new CommandException("Unknown response code: " + BinUtils.toHex(code, 4));
        }
        
        return ws;
    }
    
    private class Condition {
        
        private CodeCondition code;
        private String message;
        
        Condition(String code, String message) {
            this.message = message;
            
            code = code.replaceAll("\\s", "").toUpperCase();
            
            int xPos = 0;
            int x = -1;
            int len = 0;
            while(xPos >= 0) {
                xPos = code.indexOf('X', xPos);
                if(xPos != -1) {
                    if(x == -1) x = xPos;
                    len++;
                    xPos++;
                }
            }
            
            if(len > 0) {
                code = code.replace("X", "0");
            }
            
            byte[] bytes = BinUtils.fromHex(code);
            int n = (int) BinUtils.fromBytes(bytes);
            
            if(x == -1) {
                this.code = new CodeValue(n);
            } else {
                int mask = ((1 << (len*4)) - 1) << ((code.length() - len - x) * 4);
                this.code = new MaskValue(n, mask);
            }
        }
        
        boolean matches(int code) {
            return this.code.matches(code);
        }
        
        String getMessage(int code) {
            return "SW=" + BinUtils.toHex(code, 4) + ": " + message.replaceAll("\\\\X", String.valueOf(this.code.getReference(code)));
        }
        
        public String toString()
        {
            return code.toString();
        }
    }
    
    private interface CodeCondition {
        boolean matches(int value);
        int getReference(int value);
    }
    
    private class CodeValue implements CodeCondition {
        private final int value;
        
        CodeValue(int value) {
            this.value = value;
        }
        
        public boolean matches(int value) {
            return this.value == value;
        }
        
        public int getReference(int value) {
            return value;
        }
        
        public String toString() {
            return BinUtils.toHex(value, 4);
        }
    }
    
    private class MaskValue implements CodeCondition {
        private final int value;
        private final int mask;
                
        MaskValue(int value, int mask) {
            this.value = value;
            this.mask = mask;
        }
        
        public boolean matches(int value) {
            return (value & ~mask) == this.value;
        }
        
        public int getReference(int value) {
            return (value & mask) >> Integer.numberOfTrailingZeros(mask);
        }
        
        public String toString() {
            return BinUtils.toHex(value, 4);
        }
    }
}

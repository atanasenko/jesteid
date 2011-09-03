package com.google.code.jesteid.micardo;

public final class CommandClass {
    
    public static enum SecureMessaging {
        NO,
        YES,
        MAC;
    }
    
    private static final CommandClass NO_SM = new CommandClass(false, 0);
    private static final CommandClass SM = new CommandClass(false, 0x8);
    private static final CommandClass SM_MAC = new CommandClass(false, 0xc);
    
    private static final CommandClass NO_SM_CHAINED = new CommandClass(true, 0);
    private static final CommandClass SM_CHAINED = new CommandClass(true, 0x8);
    private static final CommandClass SM_MAC_CHAINED = new CommandClass(true, 0xc);
    
    private int cla;
    
    CommandClass(boolean chained, int sm) {
        cla = (chained ? 0x10 : 0) + sm;
    }
    
    public int getCla() {
        return cla;
    }
    
    public static CommandClass get(boolean chained, SecureMessaging sm) {
        switch(sm) {
        case NO:
            return chained ? NO_SM_CHAINED : NO_SM;
        case YES:
            return chained ? SM_CHAINED : SM;
        case MAC:
            return chained ? SM_MAC_CHAINED : SM_MAC;
        }
        return null;
    }
}

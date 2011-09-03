package com.google.code.jesteid.sc.smartcardio;

import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;

import com.google.code.jesteid.CardException;
import com.google.code.jesteid.sc.CommandData;
import com.google.code.jesteid.sc.ISmartCard;
import com.google.code.jesteid.sc.ISmartCardChannel;
import com.google.code.jesteid.sc.ResponseData;
import com.google.code.jesteid.util.BinUtils;

public class SmartCard implements ISmartCard, ISmartCardChannel {
    
    private CardTerminal cardTerminal;
    private Card card;
    private CardChannel cardChannel;
    
    public SmartCard(CardTerminal cardTerminal, Card card) {
        this.cardTerminal = cardTerminal;
        this.card = card;
        cardChannel = card.getBasicChannel();
    }
    
    public byte[] getHistoricalBytes() {
        return card.getATR().getHistoricalBytes();
    }
    
    public String getTerminalName() {
        return cardTerminal.getName();
    }
    
    public void beginExclusive() throws CardException {
        try {
            card.beginExclusive();
        } catch (javax.smartcardio.CardException e) {
            throw new CardException(e);
        }
    }
    
    public void endExclusive() throws CardException {
        try {
            card.endExclusive();
        } catch (javax.smartcardio.CardException e) {
            throw new CardException(e);
        }
    }
    
    public ResponseData transmit(CommandData cmd) throws CardException {
        return transmit(cmd, true);
    }
    
    public ResponseData transmit(CommandData cmd, boolean printRequest) throws CardException {
        
        if(!(cmd instanceof CommandData)) {
            throw new IllegalArgumentException("CommandData should be created with this smartcard");
        }
        
        CommandAPDU apdu = createCommandAPDU(cmd);
        
        System.out.println(">>> " + apdu + ": CLA=" + BinUtils.toHex(apdu.getCLA(), 2) + ", INS=" + BinUtils.toHex(apdu.getINS(), 2) + ", P1=" + BinUtils.toHex(apdu.getP1(), 2) + ", P2=" + BinUtils.toHex(apdu.getP2(), 2) + ", Ne=" + BinUtils.toHex(apdu.getNe(), 2));
        if(printRequest) {
            BinUtils.dump(apdu.getData());
        } else {
            System.out.println("!!! Request data hidden !!!");
        }
        
        try {
            ResponseAPDU res = cardChannel.transmit(apdu);
            System.out.println("<<< " + res);
            BinUtils.dump(res.getData());
            return createResponse(res);
        } catch(javax.smartcardio.CardException e) {
            throw new CardException("Error transmitting data", e);
        }
    }
    
    public void disconnect() throws CardException {
        try {
            card.disconnect(false);
        } catch (javax.smartcardio.CardException e) {
            throw new CardException(e);
        }
    }
    
    public ISmartCardChannel getChannel() {
        return this;
    }
    
    private CommandAPDU createCommandAPDU(CommandData cmd) {
        byte[] data = cmd.getData();
        int le = cmd.getLe();
        if(le == -1) {
        
            if(data == null || data.length == 0) {
                return new CommandAPDU(cmd.getCla(), cmd.getIns(), cmd.getP1(), cmd.getP2());
            }
            return new CommandAPDU(cmd.getCla(), cmd.getIns(), cmd.getP1(), cmd.getP2(), 
                    data, cmd.getOffset(), cmd.getLimit());
            
        } else {
            
            if(data == null || data.length == 0) {
                return new CommandAPDU(cmd.getCla(), cmd.getIns(), cmd.getP1(), cmd.getP2(), le);
            }
            return new CommandAPDU(cmd.getCla(), cmd.getIns(), cmd.getP1(), cmd.getP2(), 
                    data, cmd.getOffset(), cmd.getLimit(), le);
            
        }
        
    }

    private ResponseData createResponse(ResponseAPDU res) {
        return new ResponseData(res.getData(), res.getSW());
    }

}

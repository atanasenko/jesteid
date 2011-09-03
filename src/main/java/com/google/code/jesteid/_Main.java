package com.google.code.jesteid;

import java.io.ByteArrayOutputStream;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.smartcardio.Card;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.CardTerminals.State;
import javax.smartcardio.TerminalFactory;
import javax.swing.JOptionPane;

import com.google.code.jesteid.crypto.SHA1Hash;
import com.google.code.jesteid.impl.Esteid;
import com.google.code.jesteid.iso8825.EncodingRules;
import com.google.code.jesteid.iso8825.TLV;
import com.google.code.jesteid.micardo.Micardo;
import com.google.code.jesteid.micardo.commands.ReadRecord;
import com.google.code.jesteid.micardo.commands.SelectFile;
import com.google.code.jesteid.micardo.objects.Counter;
import com.google.code.jesteid.micardo.objects.FCP;
import com.google.code.jesteid.micardo.objects.KeyDesc;
import com.google.code.jesteid.micardo.objects.PwdDesc;
import com.google.code.jesteid.micardo.objects.SecurityEnv;
import com.google.code.jesteid.sc.ISmartCard;
import com.google.code.jesteid.sc.smartcardio.SmartCard;
import com.google.code.jesteid.util.BinUtils;

public class _Main {
    
    private static final IPasswordProvider PASSWORD_PROVIDER = new IPasswordProvider() {
        
        private Map<Password, byte[]> passwords = new EnumMap<Password, byte[]>(Password.class);
        
        @Override
        public byte[] providePassword(Password pwd) {
            
            byte[] data = passwords.get(pwd);
            if(data == null) {
                String s = JOptionPane.showInputDialog(pwd);
                if(s == null) System.exit(0);
                data = s.getBytes();
                passwords.put(pwd, data);
            }
            return data;
        }
    };

    public static void main(String[] args) throws Exception {
        
        
        TerminalFactory tf = TerminalFactory.getInstance("PC/SC", null);
        CardTerminals terminals = tf.terminals();
        
        cycle:
        while (true) {
            for (CardTerminal t: terminals.list(State.CARD_PRESENT)) {
                // examine Card in terminal, return if it matches
                if(t.isCardPresent()) {
                    Card c = t.connect("T=0");
                    SmartCard sc = new SmartCard(t, c);
                    doStuff(sc);
                    c.disconnect(false);
                    break cycle;
                }
            }
            terminals.waitForChange();
        }
        
    }
    
    private static void doStuff(ISmartCard sc) throws Exception {
        
        IEsteid esteid = new Esteid(sc);
        //testKeyD(sc);
        //testMicardo(sc);
        testPublic(esteid);
        //testChallenge(esteid);
        //testDecrypt(esteid);
        //testSign(esteid);
    }
    
    public static void testMicardo(ISmartCard sc) throws Exception {
        Micardo micardo = new Micardo(sc.getChannel());
        List<PwdDesc> pwdDescs = micardo.getPwdDescriptors();
        List<KeyDesc> keyDescs = micardo.getKeyDescriptors(Esteid.ESTEID_DIR);
        List<SecurityEnv> secEnvs = micardo.getSecurityEnvironments(Esteid.ESTEID_DIR);
        
        System.out.println(pwdDescs);
        System.out.println(keyDescs);
        System.out.println(secEnvs);
    }
    
    public static void testPublic(IEsteid ec) throws Exception {
        X509Certificate authCert = ec.getCertificate(CertType.AUTH);
        X509Certificate signCert = ec.getCertificate(CertType.SIGN);
        PersonalData personal = ec.getPersonalData();
        Map<CounterType, Counter> counters = new EnumMap<CounterType, Counter>(CounterType.class);
        for(CounterType ct: CounterType.values()){
            counters.put(ct, ec.readCounter(ct));
        }
        System.out.println(authCert);
        System.out.println(signCert);
        System.out.println(personal);
        System.out.println(counters);
    }
    
    public static void testKeyD(ISmartCard sc) throws Exception {
        FCP fcp = new SelectFile(Esteid.ESTEID_DIR.file(Micardo.EF_KeyD), SelectFile.RETURN_FCP).execute(sc.getChannel());
        int r = fcp.getFileDescriptor().maxRecords;
        int l = fcp.getFileDescriptor().maxRecordLength;
        
        for(int i = 0; i < r; i++) {
            byte[] data = new ReadRecord(i+1, l).execute(sc.getChannel());
            System.out.println(TLV.decodeList(data, 0, EncodingRules.DER));
        }
    }
    
    public static void testChallenge(IEsteid ec) throws Exception {
        X509Certificate ac = ec.getCertificate(CertType.AUTH);
        
        byte[] enc = ec.sslChallenge("I can see what you see n".getBytes(), PASSWORD_PROVIDER);
        System.out.println("Encrypted:");
        BinUtils.dump(enc);
        
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.DECRYPT_MODE, ac.getPublicKey());
        byte[] dec = c.doFinal(enc);
        System.out.println("Decrypted:");
        BinUtils.dump(dec);
        
        boolean valid = "I can see what you see n".equals(new String(dec));
        System.out.println("Valid: " + valid);
        if(!valid) throw new IllegalStateException("Invalid result");
    }
    
    public static void testDecrypt(IEsteid ec) throws Exception {
        
        String poem = 
                "I can see what you see not,\n" +
                "Vision milky then eyes rot.\n" + 
                "When you turn they will be gone,\n" + 
                "Whispering their hidden song.\n" + 
                "Then you see what cannot be,\n" + 
                "Shadows move where light should be\n" +
                "Out of darkness, out of mind,\n" + 
                "Cast down into the halls of the blind";
        
        X509Certificate ac = ec.getCertificate(CertType.SIGN);
        
        Cipher c = Cipher.getInstance("RSA");
        c.init(Cipher.ENCRYPT_MODE, ac.getPublicKey());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] data = poem.getBytes();
        int l = 0;
        while(l < data.length) {
            int r = Math.min(117, data.length - l);
            out.write(c.doFinal(data, l, r));
            l += r;
        }
        byte[] enc = out.toByteArray();
        System.out.println("Encrypted:");
        BinUtils.dump(enc);
        byte[] dec = ec.decryptRSA(enc, 0, enc.length, PASSWORD_PROVIDER);
        System.out.println("Decrypted:");
        BinUtils.dump(dec);
        
        boolean valid = poem.equals(new String(dec));
        System.out.println("Valid: " + valid);
        if(!valid) throw new IllegalStateException("Invalid result");
    }
    
    public static void testSign(IEsteid ec) throws Exception {
        byte[] b = "I can see what you see not".getBytes();
        BinUtils.dump(b);
        
        byte[] sig = ec.signHash(new SHA1Hash(b), PASSWORD_PROVIDER);
        
        System.out.println("Signature:");
        BinUtils.dump(sig);

        X509Certificate sc = ec.getCertificate(CertType.SIGN);
        Signature s = Signature.getInstance("SHA1withRSA");
        s.initVerify(sc.getPublicKey());
        s.update(b);
        boolean valid = s.verify(sig);
        System.out.println("Valid: " + valid);
        if(!valid) throw new IllegalStateException("Invalid result");
        
    }


}

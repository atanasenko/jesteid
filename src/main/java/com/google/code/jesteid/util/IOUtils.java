package com.google.code.jesteid.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class IOUtils {
    
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        byte[] buf = new byte[1024];
        int l;
        while((l = in.read(buf)) != -1) {
            out.write(buf, 0, l);
        }
        
        return out.toByteArray();
    }
    
}

package edu.agh.idziak;


import java.io.Closeable;
import java.io.IOException;

public class Utils {
    public static void closeQuietly(Closeable c){
        try {
            c.close();
        } catch (IOException e) {
            // hush
        }
    }

    public static void checkNotNull(Object... objs){
        for(Object o : objs){
            if(o==null)
                throw new IllegalArgumentException("Null passed");
        }

    }

    public static String colorAsHex(int r, int g, int b){
        return "0x"
                + makeHex(r)
                + makeHex(g)
                + makeHex(b);
    }

    private static String makeHex(int n){
        String hex = Integer.toHexString(n);
        if(hex.length()==1)
            hex = "0" + hex;
        return hex;
    }
}

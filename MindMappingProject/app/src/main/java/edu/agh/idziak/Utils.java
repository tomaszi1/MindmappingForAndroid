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
                throw new IllegalArgumentException("Null not allowed");
        }

    }
}

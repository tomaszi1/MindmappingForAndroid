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
}

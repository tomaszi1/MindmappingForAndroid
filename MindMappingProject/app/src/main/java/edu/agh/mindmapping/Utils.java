package edu.agh.mindmapping;

import java.io.Closeable;
import java.io.IOException;

public class Utils {
    public static final String APP_KEY = "tk1x1qw474b5jv2";
    public static final String APP_SECRET = "mafsewwrvxne3v6";

	public static void closeQuietly(Closeable closeable){
		try {
			if (closeable != null)
				closeable.close();
		} catch (IOException e) {
			// quiet
		}
	}
}

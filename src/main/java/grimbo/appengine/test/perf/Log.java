package grimbo.appengine.test.perf;

import java.io.PrintWriter;

import javax.servlet.ServletContext;

/**
 * VERY simple logging utility to avoid loading Log4J, etc.
 */
public class Log {
    private static ServletContext context;

    public static synchronized void setContext(ServletContext context) {
        if (null != Log.context) {
            return;
        }
        Log.context = context;
    }

    public static void log(String s, long time) {
        context.log(time + ": " + s);
    }

    public static void log(PrintWriter w, String s, long time) {
        w.println(time + ": " + s);
    }
}

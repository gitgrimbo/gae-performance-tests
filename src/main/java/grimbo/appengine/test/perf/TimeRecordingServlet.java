package grimbo.appengine.test.perf;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class TimeRecordingServlet extends HttpServlet {
    private static int nextId = 0;
    private long creationTime;
    private String name;
    private String logPrefix;
    private int id;
    TimeRecorder timeRecorder;

    public TimeRecordingServlet() {
        super();

        creationTime = System.currentTimeMillis();

        id = getNextId();

        name = getClass().getSimpleName();
        logPrefix = name + "." + id + ". ";
    }

    private static synchronized int getNextId() {
        return ++nextId;
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        long start = System.currentTimeMillis();

        super.init(config);

        long end = System.currentTimeMillis();

        timeRecorder = TimeRecordingListener.getTimeRecorder(config.getServletContext());

        record(logPrefix + "creation.start", creationTime);
        record(logPrefix + "init.start", start);
        record(logPrefix + "init.end", end);
    }

    @Override
    public void destroy() {
        long start = System.currentTimeMillis();

        record(logPrefix + "destroy.start=", start);

        timeRecorder = null;

        super.destroy();
    }

    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        long start = System.currentTimeMillis();
        record(logPrefix + "doGet.start", start);

        outputTimeRecordingsJson(req, resp, timeRecorder.getRecordings().iterator());

        long end = System.currentTimeMillis();
        record(logPrefix + "doGet.end", end);
    }

    public static void outputTimeRecordingsHtml(HttpServletRequest req, HttpServletResponse resp,
            Iterator<TimeRecorder.Recording> recordings) throws IOException {
        resp.setContentType("text/html");

        PrintWriter w = resp.getWriter();

        w.println("<body>");
        w.println("<pre id=timings>");

        for (Iterator<TimeRecorder.Recording> it = recordings; it.hasNext();) {
            TimeRecorder.Recording r = it.next();
            w.println(r.time + ": " + r.msg);
        }

        w.println("</pre>");
        w.println("<script src='//ajax.googleapis.com/ajax/libs/jquery/1.9.0/jquery.min.js'></script>");
        w.println("<script src='/timings.js'></script>");
        w.println("</body>");
    }

    public static void outputTimeRecordingsJson(HttpServletRequest req, HttpServletResponse resp,
            Iterator<TimeRecorder.Recording> recordings) throws IOException {
        resp.setContentType("application/json");

        PrintWriter w = resp.getWriter();

        // keep this method simple to avoid loading extra classes which may effect results
        w.print("{ \"recordings\": [");
        for (Iterator<TimeRecorder.Recording> it = recordings; it.hasNext();) {
            TimeRecorder.Recording r = it.next();
            String msg = (null == r.msg) ? "" : r.msg;
            msg = msg.replace("\"", "\\\"");
            w.print("{ \"time\": " + r.time + ", \"msg\": \"" + msg + "\" }");
            if (it.hasNext()) {
                w.print(",");
            }
        }
        w.print("] }");
    }

    private void record(String msg, long time) {
        timeRecorder.add(time, msg);
        Log.log(msg, time);
    }
}
